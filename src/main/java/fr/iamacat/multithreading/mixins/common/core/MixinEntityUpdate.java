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

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(value = WorldServer.class, priority = 1000)
public abstract class MixinEntityUpdate {

    private final ConcurrentLinkedQueue<Entity> entitiesToUpdate = new ConcurrentLinkedQueue<>();
    private static final int MAX_ENTITIES_PER_TICK = 50;
    private ThreadPoolExecutor executorService;
    private final AtomicReference<WorldServer> world = new AtomicReference<>((WorldServer) (Object) this);
    private final CopyOnWriteArrayList<Entity> loadedEntities = new CopyOnWriteArrayList<>();

    protected MixinEntityUpdate() {}

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        int availableProcessors = MultithreadingandtweaksConfig.numberofcpus;
        executorService = new ThreadPoolExecutor(
            availableProcessors,
            availableProcessors,
            0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(MAX_ENTITIES_PER_TICK),
            r -> new Thread(
                r,
                "Entity-Update-Thread-" + Thread.currentThread()
                    .getId()));
        executorService.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
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
        if (!MultithreadingandtweaksConfig.enableMixinEntityUpdate) {
            List<Entity> entitiesToUpdateBatch = new ArrayList<>();

            int count = 0;
            while (!entitiesToUpdate.isEmpty() && count < MAX_ENTITIES_PER_TICK) {
                Entity entity = entitiesToUpdate.poll();
                entitiesToUpdateBatch.add(entity);
                count++;
            }

            if (!entitiesToUpdateBatch.isEmpty()) {
                executorService.execute(() -> {
                    for (Entity entity : entitiesToUpdateBatch) {
                        try {
                            entity.onEntityUpdate();
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
        executorService.shutdown();
    }
}
