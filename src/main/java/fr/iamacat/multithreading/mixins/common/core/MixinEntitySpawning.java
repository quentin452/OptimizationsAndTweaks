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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.network.internal.EntitySpawnHandler;
import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(EntitySpawnHandler.class)
public abstract class MixinEntitySpawning {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private static final int NUM_CPUS = MultithreadingandtweaksMultithreadingConfig.numberofcpus;

    private final ConcurrentLinkedQueue<Entity> spawnQueue = new ConcurrentLinkedQueue<>();
    private final AtomicInteger batchSize = new AtomicInteger(BATCH_SIZE);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        SharedThreadPool.getExecutorService();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(World world, CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinEntitySpawning
            && world.getTotalWorldTime() % 10 == 0) {
            spawnEntitiesInQueue(world);
        }
    }

    private void spawnEntitiesInQueue(World world) {
        List<Entity> entities = new ArrayList<>(BATCH_SIZE);
        int numEntities = batchSize.getAndSet(0);
        for (Entity entity : spawnQueue) {
            if (numEntities == 0) {
                break;
            }
            entities.add(entity);
            numEntities--;
        }
        spawnQueue.removeAll(entities);
        int numThreads = Math.min(NUM_CPUS, entities.size());
        if (numThreads > 1) {
            List<List<Entity>> partitions = Lists.partition(entities, (entities.size() + numThreads - 1) / numThreads);
            ExecutorService executorService = SharedThreadPool.getExecutorService();
            List<Future<?>> futures = new ArrayList<>(numThreads);
            for (List<Entity> partition : partitions) {
                futures.add(executorService.submit(() -> {
                    for (Entity entity : partition) {
                        world.spawnEntityInWorld(entity);
                    }
                }));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (Entity entity : entities) {
                world.spawnEntityInWorld(entity);
            }
        }
    }

    @Redirect(
        method = "renderEntities",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/Render;doRender(Lnet/minecraft/entity/Entity;DDDFF)V"))
    private void redirectDoRenderEntities(Render render, Entity entity, double x, double y, double z, float yaw,
        float partialTicks) {
        // Don't render entities during mob spawning
        if (!spawnQueue.isEmpty()) {
            render.doRender(entity, x, y, z, yaw, partialTicks);
        } else {
            render.doRender(entity, x, y, z, yaw, partialTicks);
        }
    }

    @Final
    public void close() {
        Runtime.getRuntime()
            .addShutdownHook(
                new Thread(
                    () -> {
                        SharedThreadPool.getExecutorService()
                            .shutdown();
                    }));
    }
}
