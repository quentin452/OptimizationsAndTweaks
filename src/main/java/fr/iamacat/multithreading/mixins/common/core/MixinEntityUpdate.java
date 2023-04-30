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

    private final ConcurrentLinkedQueue<Entity> entitiesToUpdate = new ConcurrentLinkedQueue<>();
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;;
    private int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final AtomicReference<WorldServer> world = new AtomicReference<>((WorldServer) (Object) this);
    private final CopyOnWriteArrayList<Entity> loadedEntities = new CopyOnWriteArrayList<>();

    protected MixinEntityUpdate() {}

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        int availableProcessors = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
        SharedThreadPool.getExecutorService();
        loadedEntities.addAll(getWorldServer().loadedEntityList);
    }

    public synchronized WorldServer getWorldServer() {
        return world.get();
    }

    private void addEntitiesToUpdateQueue(Collection<Entity> entities) {
        entitiesToUpdate.addAll(entities);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            List<Entity> entitiesToUpdateBatch = new ArrayList<>();

            int count = 0;
            while (!entitiesToUpdate.isEmpty() && count < MAX_ENTITIES_PER_TICK) {
                Entity entity = entitiesToUpdate.poll();
                entitiesToUpdateBatch.add(entity);
                count++;
            }

            if (!entitiesToUpdateBatch.isEmpty()) {
                SharedThreadPool.getExecutorService()
                    .execute(() -> {
                        for (int i = 0; i < entitiesToUpdateBatch.size(); i += BATCH_SIZE) {
                            List<Entity> batch = entitiesToUpdateBatch
                                .subList(i, Math.min(i + BATCH_SIZE, entitiesToUpdateBatch.size()));
                            try {
                                for (Entity entity : batch) {
                                    entity.onEntityUpdate();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            }
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        SharedThreadPool.getExecutorService()
            .shutdown();
    }
}
