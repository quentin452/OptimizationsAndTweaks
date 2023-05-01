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

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = WorldServer.class, priority = 1000)
public abstract class MixinEntityUpdate {

    private int numberOfCPUs = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final AtomicReference<WorldServer> world = new AtomicReference<>((WorldServer) (Object) this);
    private final ConcurrentHashMap<Integer, Entity> loadedEntities = new ConcurrentHashMap<>();

    protected MixinEntityUpdate() {}

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        WorldServer worldServer = (WorldServer) (Object) this;
        world.set(worldServer);
        SharedThreadPool.getExecutorService();
        addEntitiesToUpdateQueue(worldServer.loadedEntityList);
    }

    private synchronized void addEntitiesToUpdateQueue(Collection<Entity> entities) {
        loadedEntities.putAll(
            entities.stream()
                .collect(Collectors.toConcurrentMap(Entity::getEntityId, Function.identity(), (e1, e2) -> e1)));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            ConcurrentHashMap<Integer, Entity> entitiesToUpdate = new ConcurrentHashMap<>(loadedEntities);
            loadedEntities.clear();

            int numBatches = (entitiesToUpdate.size() + MAX_ENTITIES_PER_TICK - 1) / MAX_ENTITIES_PER_TICK; // round up
                                                                                                            // division
            List<List<Entity>> batches = new ArrayList<>(numBatches);
            Iterator<Entity> iter = entitiesToUpdate.values()
                .iterator();
            for (int i = 0; i < numBatches; i++) {
                List<Entity> batch = new ArrayList<>(MAX_ENTITIES_PER_TICK);
                for (int j = 0; j < MAX_ENTITIES_PER_TICK && iter.hasNext(); j++) {
                    batch.add(iter.next());
                }
                batches.add(batch);
            }

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfCPUs);
            for (List<Entity> batch : batches) {
                executorService.execute(() -> {
                    for (Entity entity : batch) {
                        try {
                            if (entity != null) {
                                entity.onEntityUpdate();
                            }
                        } catch (Exception e) {
                            // Handle the exception in a specific way, such as logging the error and continuing with the
                            // program.
                            e.printStackTrace();
                        }
                    }
                });
            }
            executorService.shutdown();
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        ExecutorService executorService = SharedThreadPool.getExecutorService();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
