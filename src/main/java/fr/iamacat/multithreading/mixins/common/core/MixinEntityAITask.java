package fr.iamacat.multithreading.mixins.common.core;

import static fr.iamacat.multithreading.MultithreadingLogger.LOGGER;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(EntityLiving.class)
public abstract class MixinEntityAITask {

    private final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final ConcurrentLinkedQueue<Entity> batchedUpdates = new ConcurrentLinkedQueue<>();

    private final ConcurrentMap<Class<? extends Entity>, ConcurrentMap<Integer, Entity>> entityMap = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    public WorldServer getWorldServer() {
        return (WorldServer) (Object) this;
    }

    private ConcurrentMap<Integer, Entity> getEntityMapForClass(Class<? extends Entity> entityClass) {
        return entityMap.computeIfAbsent(entityClass, k -> new ConcurrentHashMap<>());
    }

    public void updateEntities() {
        entityMap.values()
            .parallelStream()
            .forEach(this::updateEntityMap);
    }

    private void batchedMoveEntities(List<Entity> batch) {
        try {
            // Parallel stream entities
        } catch (Exception e) {
            LOGGER.error("Failed to batch move entities", e);
            // Log exception appropriately
        }
    }

    private void updateEntityMap(ConcurrentMap<Integer, Entity> entitiesToAIUpdate) {
        List<Entity> entities = new CopyOnWriteArrayList<>(entitiesToAIUpdate.values());
        entities.parallelStream()
            .forEach(entity -> {
                entity.onEntityUpdate();
                if (entity instanceof EntityAgeable) {
                    EntityAgeable ageable = (EntityAgeable) entity;
                    if (ageable.isChild()) {
                        int growingAge = ageable.getGrowingAge();
                        if (growingAge < 0) {
                            growingAge++;
                            ageable.setGrowingAge(growingAge);
                        }
                    }
                }
            });

        // Remove dead entities from the map
        entitiesToAIUpdate.keySet()
            .parallelStream()
            .filter(key -> entitiesToAIUpdate.get(key).isDead)
            .forEach(entitiesToAIUpdate::remove);
    }

    @Inject(method = "updateEntityTask", at = @At("HEAD"))
    private void updateEntityTask(Entity entity, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityAITask) {
            this.getWorldServer()
                .updateEntity(entity);
            executorService.submit(() -> {
                try {
                    if (entity instanceof EntityLiving) {
                        EntityLiving livingEntity = (EntityLiving) entity;
                        Field tasksField = EntityAITasks.class.getDeclaredField("tasks");
                        tasksField.setAccessible(true);
                        EntityAITasks entityAITasks = (EntityAITasks) tasksField.get(livingEntity.tasks);
                        List<EntityAITasks.EntityAITaskEntry> taskEntries = new ArrayList<>(entityAITasks.taskEntries);
                        taskEntries.parallelStream()
                            .filter(taskEntry -> taskEntry.action instanceof EntityAIBase)
                            .forEach(taskEntry -> {
                                try {
                                    EntityAIBase aiBase = (EntityAIBase) taskEntry.action;
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

    private void updateAITasksParallel(List<Entity> batch, CallbackInfo ci) {
        executorService.submit(() -> {
            try {
                batch.parallelStream()
                    .forEach(entity -> {
                        if (entity instanceof EntityLiving) {
                            EntityLiving livingEntity = (EntityLiving) entity;
                            EntityAITasks entityAITasks = livingEntity.tasks;
                            if (entityAITasks != null) {
                                entityAITasks.onUpdateTasks(); // Call the batched onUpdateTasks
                            }
                        }
                    });

                batch.forEach(entity -> { ci.cancel(); });
            } catch (Exception e) {
                // Handle exception
            }
        });
    }

    @Inject(method = "onUpdateTasks", at = @At("HEAD"), cancellable = true)
    private void batchOnUpdateTasks(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityAITask) {
            // Run batched updates
            processBatchedUpdates(ci);
            // Cancel vanilla method
            ci.cancel();
        }
    }

    @Inject(
        method = "onUpdate",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/EntityAITasks;onUpdateTasks()V"))
    private void cancelVanillaOnUpdateTasks(CallbackInfo ci) {
        ci.cancel();
    }

    public void processBatchedUpdates(CallbackInfo ci) {
        while (batchedUpdates.size() >= BATCH_SIZE) {
            List<Entity> batch = new ArrayList<>();

            // Populate batch...
            updateAITasksParallel(batch, ci);

            batchedMoveEntities(batch);
        }
    }

    private void batchMoveWithHeading(List<EntityLivingBase> entities, float moveStrafing, float moveForward) {
        executorService.submit(() -> {
            try {
                entities.parallelStream()
                    .forEach(entity -> { entity.moveEntityWithHeading(moveStrafing, moveForward); });
            } catch (Exception e) {
                // Handle exception
            }
        });
    }

    public void moveEntityWithHeading(float moveStrafing, float moveForward) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityAITask) {
            if (batchedUpdates.contains(this)) {
                // Move is part of a batch update
                batchMoveWithHeading(
                    Collections.singletonList((EntityLivingBase) (Object) this),
                    moveStrafing,
                    moveForward);
            } else {
                // Regular move, execute on the current thread
                ((EntityLivingBase) (Object) this).moveEntityWithHeading(moveStrafing, moveForward);
            }
        } else {
            // Regular move, execute on the current thread
            ((EntityLivingBase) (Object) this).moveEntityWithHeading(moveStrafing, moveForward);
        }
    }

    public void shutdownExecutorService() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
