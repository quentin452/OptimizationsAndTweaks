package fr.iamacat.multithreading.mixins.common.core;

import static fr.iamacat.multithreading.MultithreadingLogger.LOGGER;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.batching.BatchedEntity;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(EntityLiving.class)
public abstract class MixinEntityAITask {

    private EntityLivingBase entityliv;
    private final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final ConcurrentLinkedQueue<Entity> batchedUpdates = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isBatchProcessing = new AtomicBoolean(false);

    private final ConcurrentMap<Class<? extends Entity>, ConcurrentMap<Integer, Entity>> entityMap = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    public World getWorld() {
        return entityliv.worldObj;
    }

    private ConcurrentMap<Integer, Entity> getEntityMapForClass(Class<? extends Entity> entityClass) {
        return entityMap.computeIfAbsent(entityClass, k -> new ConcurrentHashMap<>());
    }


    private void batchedMoveEntities() {
        LOGGER.error("Failed to batch move entities");
    }

    @Inject(method = "updateEntityTask", at = @At("HEAD"))
    private void updateEntityTask(Entity entity, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityAITask) {
            this.getWorld()
                .updateEntity(entity);
            executorService.submit(() -> {
                try {
                    if (entity instanceof EntityLiving) {
                        EntityLiving livingEntity = (EntityLiving) entity;
                        Field tasksField = EntityAITasks.class.getDeclaredField("taskEntries");
                        tasksField.setAccessible(true);
                        EntityAITasks entityAITasks = (EntityAITasks) tasksField.get(livingEntity.tasks);
                        List<EntityAITasks.EntityAITaskEntry> taskEntries = new ArrayList<>(entityAITasks.taskEntries);
                        taskEntries.parallelStream()
                            .filter(taskEntry -> taskEntry.action != null)
                            .forEach(taskEntry -> {
                                try {
                                    EntityAIBase aiBase = taskEntry.action;
                                    aiBase.startExecuting();
                                    entityAITasks.taskEntries.remove(taskEntry); // Cancel vanilla EntityAITask
                                } catch (Exception e) {
                                    // Handle the exception appropriately
                                    e.printStackTrace();
                                }
                            });
                    }
                } catch (Exception e) {
                    // Handle the exception appropriately
                    e.printStackTrace();
                }
            });
        }
    }

    @Inject(method = "onEntityRemoved", at = @At("RETURN"))
    private void onEntityRemoved(Entity entity, CallbackInfo ci) {
        if (entity instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving) entity;

            ConcurrentMap<Integer, Entity> entityMap = getEntityMapForClass(livingEntity.getClass());
            entityMap.remove(entity.getEntityId());
        }
        // Check if the executorService is shutdown before shutting it down
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private void executeBatchedAIUpdates(EntityLiving livingEntity) {
        try {
            EntityAITasks entityAITasks = livingEntity.tasks;
            if (entityAITasks != null) {
                entityAITasks.onUpdateTasks();
            }
        } catch (Exception e) {
            // handle exception
        }
    }

    private void updateAITasksParallel(BatchedEntity batch) {
        executorService.submit(() -> {
            try {
                batch.entities.parallelStream()
                    .forEach(entity -> {
                        if (entity instanceof EntityLiving) {
                            EntityLiving livingEntity = (EntityLiving) entity;
                            executeBatchedAIUpdates(livingEntity);
                        }
                    });
            } catch (Exception e) {
                // Handle exception
            }
        });
    }

    @Inject(method = "onUpdateTasks", at = @At("HEAD"), cancellable = true)
    private void batchOnUpdateTasks(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityAITask) {
            // Run batched updates
            processBatchedUpdates();
            // Cancel vanilla method
            ci.cancel();
        }
    }

    @Inject(
        method = "onUpdate",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/EntityAITasks;onUpdateTasks()V"),
        cancellable = true)
    private void cancelVanillaOnUpdateTasks(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void onUpdate(CallbackInfo ci) {

        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityAITask) {

            EntityLivingBase entity = entityliv;

            float strafe = (float) entity.motionX;
            float forward = (float) entity.motionZ;

            moveEntityWithHeading(strafe, forward);

        }

    }

    public void processBatchedUpdates() {
        if (!isBatchProcessing.compareAndSet(false, true)) {
            return; // Already processing a batch
        }

        while (batchedUpdates.size() >= BATCH_SIZE) {
            BatchedEntity batchedEntity = populateBatch();
            updateAITasksParallel(batchedEntity);
            batchedMoveEntities();
        }

        isBatchProcessing.set(false);
    }

    private BatchedEntity populateBatch() {
        BatchedEntity batchedEntity = new BatchedEntity();
        for (int i = 0; i < BATCH_SIZE; i++) {
            Entity entity = batchedUpdates.poll();
            if (entity instanceof EntityLivingBase) {
                batchedEntity.entities.add((EntityLivingBase) entity);
            } else {
                break; // No more entities in the batch
            }
        }
        return batchedEntity;
    }

    private void customMoveEntityWithHeading(EntityLivingBase entity, float strafe, float forward) {
        World world = entity.worldObj;

        double x = entity.posX + strafe;
        double y = entity.posY;
        double z = entity.posZ + forward;

        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);

        // Mettez à jour l'entité dans le monde
        world.updateEntityWithOptionalForce(entity, false);

        // Appliquer le mouvement
        entity.motionX = strafe;
        entity.motionZ = forward;

        // Désactiver le saut
        entity.setJumping(false);
    }

    @ModifyVariable(method = "updateEntityTask", at = @At("HEAD"), ordinal = 0)
    private Entity modifyLivingEntity(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            entityliv = (EntityLivingBase) entity;
        }
        return entity;
    }

    public void moveEntityWithHeading(float moveStrafing, float moveForward) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityAITask) {
            if (batchedUpdates.contains(entityliv)) {
                // Move is part of a batch update
                customMoveEntityWithHeading(entityliv, moveStrafing, moveForward);
            } else {
                // Regular move, execute on the current thread
                entityliv.moveEntityWithHeading(moveStrafing, moveForward);
            }
        } else {
            // Regular move, execute on the current thread
            entityliv.moveEntityWithHeading(moveStrafing, moveForward);
        }
    }

    public void shutdownExecutorService() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
