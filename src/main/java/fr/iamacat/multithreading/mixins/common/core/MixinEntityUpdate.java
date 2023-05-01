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

    public synchronized WorldServer getWorldServer() {
        return world.get();
    }

    private void addEntitiesToUpdateQueue(Collection<Entity> entities) {
        for (Entity entity : entities) {
            loadedEntities.putIfAbsent(entity.getEntityId(), entity);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            ConcurrentHashMap<Integer, Entity> entitiesToUpdate = new ConcurrentHashMap<>(loadedEntities);
            loadedEntities.clear();
            ExecutorService executorService = SharedThreadPool.getExecutorService();
            if (!executorService.isShutdown()) {
                int availableProcessors = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
                executorService.execute(() -> {
                    entitiesToUpdate.values().parallelStream().forEach(entity -> {
                        try {
                            if (entity != null) {
                                entity.onEntityUpdate();
                            }
                        } catch (Exception e) {
                            // Handle the exception in a specific way, such as logging the error and continuing with the program.
                            e.printStackTrace();
                        }
                    });
                });
            }
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
