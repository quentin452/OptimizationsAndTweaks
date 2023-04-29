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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.Multithreaded;

@Mixin(BlockLiquid.class)
public abstract class MixinLiquidTick {

    public MixinLiquidTick() {
        tickQueue = new LinkedBlockingQueue<>(1000);
    }
    private BlockingQueue<ChunkCoordinates> tickQueue;
    private ExecutorService tickExecutorService;

    private static final Comparator<ChunkCoordinates> DISTANCE_COMPARATOR = new Comparator<ChunkCoordinates>() {

        @Override
        public int compare(ChunkCoordinates o1, ChunkCoordinates o2) {
            return Double.compare(o1.getDistanceSquared(0, 0, 0), o2.getDistanceSquared(0, 0, 0));
        }
    };
    private static final int BATCH_SIZE = 15;

    public abstract void func_149813_h(int x, int y, int z);

    @Inject(method = "liquidtick", at = @At("RETURN"))
    private void onUpdateTick(World worldObj, CallbackInfo ci) {
        // Use the 'world' parameter instead of 'worldObj'
        if (!MultithreadingandtweaksConfig.enableMixinliquidTick) {
            // Add liquid blocks that need to be ticked to the queue
            for (int x = -64; x <= 64; x++) {
                for (int z = -64; z <= 64; z++) {
                    for (int y = 0; y <= 255; y++) {
                        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
                        if (worldObj.getChunkProvider()
                            .chunkExists(pos.posX >> 4, pos.posZ >> 4)) {
                            if (worldObj.getBlock(pos.posX, pos.posY, pos.posZ) instanceof BlockLiquid) {
                                tickQueue.offer(pos);
                            }
                        }
                    }
                }
            }
            // Initialize tick executor service with daemon threads
            tickExecutorService = new ThreadPoolExecutor(
                2, // Minimum number of threads
                6, // Maximum number of threads
                60L, // Thread idle time before termination
                TimeUnit.SECONDS, // Time unit for idle time
                new LinkedBlockingQueue<>(1000), // Blocking queue for tasks
                new ThreadFactory() {

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = Executors.defaultThreadFactory()
                            .newThread(r);
                        thread.setDaemon(true);
                        return thread;
                    }
                });
            // Process liquid blocks in batches using executor service
            while (!tickQueue.isEmpty()) {
                List<ChunkCoordinates> batch = new ArrayList<>();
                int batchSize = Math.max(Math.min(tickQueue.size(), BATCH_SIZE), 1);
                for (int i = 0; i < batchSize; i++) {
                    batch.add(tickQueue.poll());
                }
                if (!batch.isEmpty()) {
                    tickExecutorService.submit(new Runnable() {

                        @Override
                        public void run() {
                            for (ChunkCoordinates pos : batch) {
                                func_149813_h(pos.posX, pos.posY, pos.posZ);
                            }
                        }

                    });
                }
            }
            // Shutdown tick executor service after it is used
            tickExecutorService.shutdown();
        }
    }
}
