package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(EntityLiving.class)
public abstract class MixinEntityAITask {

    private final ConcurrentMap<Class<? extends Entity>, ConcurrentMap<Integer, Entity>> entityMap = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "Entity-AI-" + threadNumber.getAndIncrement());
            }
        }
    );

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

    private void updateEntityMap(ConcurrentMap<Integer, Entity> entitiesToAIUpdate) {
        List<Entity> entities = new ArrayList<>(entitiesToAIUpdate.values());
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
                        if (livingEntity.tasks.taskEntries.size() > 0) {
                            for (Object taskEntryObj : livingEntity.tasks.taskEntries) {
                                if (taskEntryObj instanceof EntityAITasks.EntityAITaskEntry) {
                                    EntityAITasks.EntityAITaskEntry taskEntry = (EntityAITasks.EntityAITaskEntry) taskEntryObj;
                                    if (taskEntry.action instanceof EntityAIBase) {
                                        try {
                                            EntityAIBase aiBase = (EntityAIBase) taskEntry.action;
                                            aiBase.startExecuting();
                                        } catch (Exception e) {
                                            // Handle the exception appropriately
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
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
    public void shutdownExecutorService() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
