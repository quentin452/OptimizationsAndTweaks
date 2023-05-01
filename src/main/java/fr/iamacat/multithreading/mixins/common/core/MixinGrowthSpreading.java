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
import java.util.PriorityQueue;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockReed;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = WorldServer.class, priority = 998)
public abstract class MixinGrowthSpreading {

    private int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;

    private PriorityQueue<ChunkCoordinates> growthQueue = new PriorityQueue<>(
        1000,
        Comparator.comparingInt(o -> o.posY));

    // Define a getter method for the world object
    public WorldServer getWorldServer() {
        return (WorldServer) (Object) this;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        SharedThreadPool.getExecutorService();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinGrowthSpreading
            && getWorldServer().getTotalWorldTime() % 10 == 0) {
            if (getWorldServer().getSaveHandler()
                .getWorldDirectory()
                .exists()) {
                batchSize = 1;
                growthQueue.clear();
            } else {
                addBlocksToGrowthQueue();
                processBlocksInGrowthQueue();
            }
        }
    }

    private void addBlocksToGrowthQueue() {
        for (int x = -64; x <= 64; x++) {
            for (int z = -64; z <= 64; z++) {
                for (int y = 0; y <= 255; y++) {
                    ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
                    Block block = getWorldServer().getBlock(pos.posX, pos.posY, pos.posZ);
                    if (block instanceof BlockCrops || block instanceof BlockReed) {
                        growthQueue.add(pos);
                    }
                }
            }
        }
    }

    // Process blocks in growth queue using executor service
    private void processBlocksInGrowthQueue() {
        ExecutorService executor = Executors
            .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);
        while (!growthQueue.isEmpty()) {
            List<ChunkCoordinates> batch = new ArrayList<>();
            int batchSize = Math.min(growthQueue.size(), this.batchSize);
            for (int i = 0; i < batchSize; i++) {
                batch.add(growthQueue.poll());
            }
            if (!batch.isEmpty()) {
                executor.submit(() -> {
                    batch.forEach(pos -> {
                        Block block = getWorldServer().getBlock(pos.posX, pos.posY, pos.posZ);
                        getWorldServer().markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
                        getWorldServer().notifyBlocksOfNeighborChange(pos.posX, pos.posY, pos.posZ, block);
                    });
                });
            }
        }
        SharedThreadPool.getExecutorService().shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // Handle interruption
        }
    }
}
