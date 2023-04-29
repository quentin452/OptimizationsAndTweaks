package fr.iamacat.multithreading.mixins.common.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.iamacat.multithreading.Multithreaded;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(value = WorldServer.class, priority = 999)
public abstract class MixinEntityAITask {
    private int availableProcessors;
    private ThreadPoolExecutor executorService;
    private int maxPoolSize;

    // Store the entities to be updated in a thread-safe map
    private ConcurrentHashMap<Integer, Entity> entitiestoAIupdate = new ConcurrentHashMap<>();

    // Define a getter method for the world object
    public WorldServer getWorldServer() {
        return (WorldServer) (Object) this;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        availableProcessors = Runtime.getRuntime().availableProcessors();
        maxPoolSize = Math.max(availableProcessors * 2, 1);
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setNameFormat("Mob-Spawner-" + this.hashCode() + "-%d");
        executorService = new ThreadPoolExecutor(
            0,
            maxPoolSize,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), // use a LinkedBlockingQueue instead of a SynchronousQueue
            builder.build(),
            new ThreadPoolExecutor.AbortPolicy()
        );
        executorService.allowCoreThreadTimeOut(true);
        executorService.setThreadFactory(builder.build());
        executorService.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!MultithreadingandtweaksConfig.enableMixinEntityAITask) {
            // Dynamically set the maximum pool size
            int newMaxPoolSize = Math.max(availableProcessors, 1);
            if (newMaxPoolSize != maxPoolSize) {
                maxPoolSize = newMaxPoolSize;
                executorService.setMaximumPoolSize(maxPoolSize);
            }
        }

        // Update all entities in the map
        for (Entity entity : entitiestoAIupdate.values()) {
            entity.onEntityUpdate();
        }
        // Clear the map of entities to be updated for the next tick
        entitiestoAIupdate.clear();
    }

    public void updateEntities() {
        // Update all entities in the map
        for (Entity entity : entitiestoAIupdate.values()) {
            entity.onEntityUpdate();
        }

        // Remove dead entities from the map
        for (Iterator<Map.Entry<Integer, Entity>> it = entitiestoAIupdate.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Integer, Entity> entry = it.next();
            if (entry.getValue().isDead) {
                it.remove();
            }
        }
    }
    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void onUpdateEntity(Entity entity, CallbackInfo ci) {
        // Update the entity immediately
        this.getWorldServer().updateEntity(entity);

        // Add the entity's AI task to the executor service
        if (entity instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving) entity;
            if (livingEntity.tasks.taskEntries.size() > 0) {
                for (Object taskEntryObj : livingEntity.tasks.taskEntries) {
                    if (taskEntryObj instanceof EntityAITasks.EntityAITaskEntry) {
                        EntityAITasks.EntityAITaskEntry taskEntry = (EntityAITasks.EntityAITaskEntry) taskEntryObj;
                        if (taskEntry.action instanceof EntityAIBase) {
                            executorService.submit(() -> {
                                try {
                                    taskEntry.action.startExecuting(); // start the task
                                    while (taskEntry.action.shouldExecute()) {
                                        taskEntry.action.updateTask(); // run the task
                                    }
                                    taskEntry.action.resetTask(); // reset the task
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
            }

            // Add the entity to the map to be updated later
            entitiestoAIupdate.putIfAbsent(entity.getEntityId(), entity);

            // Add a check to remove the entity from the queue if it is dead
            if (entity.isDead) {
                entitiestoAIupdate.remove(entity);
            }
        }
    }
}
