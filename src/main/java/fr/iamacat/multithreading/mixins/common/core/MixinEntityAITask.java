package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = WorldServer.class, priority = 901)
public abstract class MixinEntityAITask {

    private int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final ConcurrentMap<Class<? extends Entity>, ConcurrentMap<Integer, Entity>> entityMap = new ConcurrentHashMap<>();

    private final ExecutorService executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(1000),
        new ThreadFactoryBuilder().setNameFormat("Entity-AI-%d")
            .build(),
        new ThreadPoolExecutor.CallerRunsPolicy());

    public WorldServer getWorldServer() {
        return (WorldServer) (Object) this;
    }

    private ConcurrentMap<Integer, Entity> getEntityMapForClass(Class<? extends Entity> entityClass) {
        return entityMap.computeIfAbsent(entityClass, k -> new ConcurrentHashMap<>());
    }

    public void updateEntities() {
        entityMap.values()
            .forEach(this::updateEntityMap);
    }

    private void updateEntityMap(ConcurrentMap<Integer, Entity> entitiesToAIUpdate) {
        List<Entity> entities = new ArrayList<>(entitiesToAIUpdate.values());
        for (int i = 0; i < entities.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, entities.size());
            List<Entity> batch = entities.subList(i, end);
            batch.forEach(entity -> {
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
            for (Iterator<Map.Entry<Integer, Entity>> it = entitiesToAIUpdate.entrySet()
                .iterator(); it.hasNext();) {
                Map.Entry<Integer, Entity> entry = it.next();
                if (entry.getValue().isDead) {
                    it.remove();
                }
            }
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void onUpdateEntity(Entity entity, CallbackInfo ci) {
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
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
