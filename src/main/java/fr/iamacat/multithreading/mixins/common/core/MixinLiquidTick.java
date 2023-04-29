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

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;


@Mixin(BlockLiquid.class)
public abstract class MixinLiquidTick {
    private final CompletableFuture<Void> tickFuture = new CompletableFuture<>();
    private static final int BATCH_SIZE = 15;
    private final int corePoolSize = Runtime.getRuntime().availableProcessors();
    private final int maximumPoolSize = corePoolSize * 2;
    private final long keepAliveTime = 60L;
    private final TimeUnit unit = TimeUnit.SECONDS;
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private final ExecutorService tickExecutorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

    public MixinLiquidTick() {
    }

    public synchronized void updateTick(World world, int x, int y, int z, Random random){
        // Access and modify shared state in the World object here
    }

    private final Queue<List<ChunkCoordinates>> batchQueue = new ConcurrentLinkedQueue<>();
    private void processBatch(List<ChunkCoordinates> batch, World world) {
        batch.forEach(pos -> {
            try {
                updateTick(world, pos.posX, pos.posY, pos.posZ, world.rand);
            } catch (Exception e) {
                // Log or handle the exception appropriately
                e.printStackTrace();
            }
        });
    }
    private void processQueue(World world) {
        while (!Thread.currentThread().isInterrupted()) {
            List<ChunkCoordinates> batch = new ArrayList<>();
            workQueue.drainTo(Collections.singleton(batch), BATCH_SIZE);
            if (!batch.isEmpty()) {
                List<Runnable> runnableBatch = new ArrayList<>();
                for (ChunkCoordinates pos : batch) {
                    Runnable task = () -> {
                        // Perform actions on the ChunkCoordinates object here
                    };
                    runnableBatch.add(task);
                }
                List<ChunkCoordinates> chunkBatch = new ArrayList<>();
                for (Runnable task : runnableBatch) {
                    // Convert each Runnable object to a ChunkCoordinates object here
                }
                batchQueue.offer(chunkBatch);
            }
        }
    }
    @Inject(method = "liquidTick", at = @At("RETURN"))
    private void onLiquidTick(World world, CallbackInfo ci) throws InterruptedException {
        if (!MultithreadingandtweaksConfig.enableMixinliquidTick) {
            for (int x = -64; x <= 64; x++) {
                for (int z = -64; z <= 64; z++) {
                    for (int y = 0; y <= 255; y++) {
                        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
                        if (world.getChunkProvider().chunkExists(pos.posX >> 4, pos.posZ >> 4)) {
                            if (world.getBlock(pos.posX, pos.posY, pos.posZ).getMaterial() == Material.water || world.getBlock(pos.posX, pos.posY, pos.posZ).getMaterial() == Material.lava) {
                                if (workQueue.size() < BATCH_SIZE * corePoolSize) {
                                    Runnable task = () -> {
                                        // Perform actions on the ChunkCoordinates object here
                                    };
                                    workQueue.put(task);
                                }
                            }
                        }
                    }
                }
            }
            tickExecutorService.submit(() -> {
                try {
                    processQueue(world);
                    batchQueue.forEach(batch -> tickExecutorService.submit(() -> processBatch(batch, world)));
                    tickFuture.complete(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
