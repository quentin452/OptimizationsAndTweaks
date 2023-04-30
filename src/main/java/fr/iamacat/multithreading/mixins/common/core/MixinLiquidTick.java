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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(BlockLiquid.class)
public abstract class MixinLiquidTick {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;;

    private ExecutorService executorService;
    private LinkedBlockingQueue<List<ChunkCoordinates>> batchQueue;
    private Map<ChunkCoordinates, Material> blockMaterialMap;

    public MixinLiquidTick() {
        executorService = Executors.newFixedThreadPool(
            MultithreadingandtweaksMultithreadingConfig.numberofcpus,
            r -> new Thread(r, "liquidTick-" + r.hashCode()));
        batchQueue = new LinkedBlockingQueue<>();
        blockMaterialMap = new ConcurrentHashMap<>();
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        // Access and modify shared state in the World object here
        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
        Material currentMaterial = null;
        synchronized (blockMaterialMap) {
            currentMaterial = blockMaterialMap.get(pos);
        }
        Material newMaterial = world.getBlock(x, y, z)
            .getMaterial();
        if (newMaterial == Material.water || newMaterial == Material.lava) {
            if (currentMaterial != newMaterial) {
                synchronized (blockMaterialMap) {
                    blockMaterialMap.put(pos, newMaterial);
                }
                addToBatch(pos);
            }
        } else {
            synchronized (blockMaterialMap) {
                blockMaterialMap.remove(pos);
            }
        }
    }

    private void addToBatch(ChunkCoordinates pos) {
        try {
            List<ChunkCoordinates> batch = batchQueue.peek();
            if (batch == null || batch.size() >= BATCH_SIZE) {
                batch = new ArrayList<>(BATCH_SIZE);
                batchQueue.put(batch);
            }
            batch.add(pos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private void processBatch(List<ChunkCoordinates> batch, World world) {
        if (batch == null || batch.isEmpty()) {
            return;
        }
        lock.readLock()
            .lock();
        try {
            for (ChunkCoordinates pos : batch) {
                updateTick(world, pos.posX, pos.posY, pos.posZ, world.rand);
            }
        } finally {
            lock.readLock()
                .unlock();
        }
    }

    @Inject(method = "liquidTick", at = @At("RETURN"))
    private void onLiquidTick(World world, CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinliquidTick) {
            int batchSize = BATCH_SIZE;
            int numThreads = Math.max(
                1,
                Runtime.getRuntime()
                    .availableProcessors() - 1);
            ((ThreadPoolExecutor) executorService).setMaximumPoolSize(numThreads);
            List<ChunkCoordinates> liquidPositions = new ArrayList<>();
            for (int x = -64; x <= 64; x++) {
                for (int z = -64; z <= 64; z++) {
                    for (int y = 0; y <= 255; y++) {
                        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
                        if (world.getChunkProvider()
                            .chunkExists(pos.posX >> 4, pos.posZ >> 4)) {
                            if (world.getBlock(pos.posX, pos.posY, pos.posZ)
                                .getMaterial() == Material.water
                                || world.getBlock(pos.posX, pos.posY, pos.posZ)
                                    .getMaterial() == Material.lava) {
                                liquidPositions.add(pos);
                            }
                        }
                    }
                }
            }
            int numPositions = liquidPositions.size();
            int numBatches = (int) Math.ceil((double) numPositions / batchSize);
            List<CompletableFuture<Void>> taskBatch = new ArrayList<>(numBatches);
            ReadWriteLock lock = new ReentrantReadWriteLock();
            for (int i = 0; i < numBatches; i++) {
                int startIndex = i * batchSize;
                int endIndex = Math.min(startIndex + batchSize, numPositions);
                List<ChunkCoordinates> batch = liquidPositions.subList(startIndex, endIndex);
                taskBatch.add(CompletableFuture.runAsync(() -> {
                    lock.readLock()
                        .lock();
                    try {
                        processBatch(batch, world);
                    } finally {
                        lock.readLock()
                            .unlock();
                    }
                }, executorService));
            }
            CompletableFuture.allOf(taskBatch.toArray(new CompletableFuture[0]))
                .join();
            executorService.shutdown();
        }
    }
}
