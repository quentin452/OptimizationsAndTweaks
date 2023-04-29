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
import java.util.Queue;
import java.util.Random;
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

    private static final int BATCH_SIZE = 15;
    private final int availableProcessors = Runtime.getRuntime().availableProcessors();
    private final int threadNumber = availableProcessors;
    private final int batchSize = (BATCH_SIZE / 2) / availableProcessors;
    private final BlockingQueue<ChunkCoordinates> tickQueue = new LinkedBlockingQueue<>();
    private final ExecutorService tickExecutorService = Executors.newFixedThreadPool(threadNumber);
    private CompletableFuture<Void> tickFuture = CompletableFuture.completedFuture(null);

    public MixinLiquidTick() {
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        synchronized (world) {
            // Access and modify shared state in the World object here
        }
    }

    private void processBatch(List<ChunkCoordinates> batch, World world) {
        tickFuture = tickFuture.thenComposeAsync((Void) -> CompletableFuture.runAsync(() -> {
            batch.parallelStream().forEach(pos -> {
                try {
                    updateTick(world, pos.posX, pos.posY, pos.posZ, world.rand);
                } catch (Exception e) {
                    // Log or handle the exception appropriately
                    e.printStackTrace();
                }
            });
        }, tickExecutorService), tickExecutorService).whenComplete((result, throwable) -> {
            if (throwable != null) {
                // Log or handle the exception appropriately
                throwable.printStackTrace();
            }
            tickFuture = CompletableFuture.completedFuture(null);
        });
    }
    private void processQueue(World world) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadNumber, threadNumber, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ChunkCoordinates pos = tickQueue.take();
                List<ChunkCoordinates> batch = new ArrayList<>();
                batch.add(pos);
                tickQueue.drainTo(batch, batchSize - 1);
                executor.execute(() -> processBatch(batch, world));
            }
        } catch (InterruptedException e) {
            // Log or handle the exception appropriately
            e.printStackTrace();
        } finally {
            executor.shutdown();
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
                                tickQueue.put(pos);
                            }
                        }
                    }
                }
            }
            processQueue(world);
        }
    }
}
