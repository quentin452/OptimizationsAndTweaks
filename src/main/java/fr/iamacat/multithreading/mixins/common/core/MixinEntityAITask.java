/*
 * FalseTweaks
 * Copyright (C) 2022 FalsePattern
 * All Rights Reserved
 * The above copyright notice, this permission notice and the word "SNEED"
 * shall be included in all copies or substantial portions of the Software.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package fr.iamacat.multithreading.mixins.common.core;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(value = WorldServer.class, priority = 901)
public abstract class MixinEntityAITask {

    private int maxPoolSize = Math.max(MultithreadingandtweaksConfig.numberofcpus, 1);

    private ThreadPoolExecutor executorService;

    // Store the entities to be updated in a thread-safe map
    private final ConcurrentHashMap<Integer, Entity> entitiesToAIUpdate = new ConcurrentHashMap<>();

    // Define a getter method for the world object
    public WorldServer getWorldServer() {
        return (WorldServer) (Object) this;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setNameFormat("Mob-Spawner-" + this.hashCode() + "-%d");
        executorService = new ThreadPoolExecutor(
            maxPoolSize, // fix the maximumPoolSize bug
            maxPoolSize,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), // use a LinkedBlockingQueue instead of a SynchronousQueue
            builder.build(),
            new ThreadPoolExecutor.DiscardOldestPolicy()); // use DiscardOldestPolicy to handle rejected tasks
        executorService.allowCoreThreadTimeOut(true);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!MultithreadingandtweaksConfig.enableMixinEntityAITask) {
            // Dynamically set the maximum pool size
            int newMaxPoolSize = Math.max(
                Runtime.getRuntime()
                    .availableProcessors() * 2,
                1);
            if (newMaxPoolSize != maxPoolSize) {
                maxPoolSize = newMaxPoolSize;
                executorService.setMaximumPoolSize(maxPoolSize);
            }
        }
    }

    public void updateEntities() {
        // Update all entities in the map
        synchronized (entitiesToAIUpdate) { // synchronize the map access
            for (Entity entity : entitiesToAIUpdate.values()) {
                entity.onEntityUpdate();
            }

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
        // Update the entity immediately
        this.getWorldServer()
            .updateEntity(entity);

        // Add the entity's AI task to the executor service
        if (entity instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving) entity;
            if (livingEntity.tasks.taskEntries.size() > 0) {
                for (Object taskEntryObj : livingEntity.tasks.taskEntries) {
                    if (taskEntryObj instanceof EntityAITasks.EntityAITaskEntry) {
                        EntityAITasks.EntityAITaskEntry taskEntry = (EntityAITasks.EntityAITaskEntry) taskEntryObj;
                        if (taskEntry.action instanceof EntityAIBase) {
                            try {
                                executorService.execute(() -> {
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
                            } catch (RejectedExecutionException e) {
                                // Discard the oldest task and try again
                                executorService.getQueue()
                                    .poll();
                                executorService.execute(() -> {
                                    try {
                                        taskEntry.action.startExecuting(); // start the task
                                        while (taskEntry.action.shouldExecute()) {
                                            taskEntry.action.updateTask(); // run the task
                                        }
                                        taskEntry.action.resetTask(); // reset the task
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                });
                            }
                        }
                    }
                }
            }

            // Add the entity to the thread-safe map to be updated later
            entitiesToAIUpdate.putIfAbsent(entity.getEntityId(), entity);
        } else {
            // Remove the entity from the map if it is dead or not an instance of EntityLiving
            entitiesToAIUpdate.remove(entity.getEntityId());
        }
    }
}
