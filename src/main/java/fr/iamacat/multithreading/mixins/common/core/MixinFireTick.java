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
import java.util.Random;
import java.util.concurrent.*;

import net.minecraft.block.BlockFire;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;

import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(BlockFire.class)
public abstract class MixinFireTick {

    private int numberOfCPUs = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
    private int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;; // batch size

    private boolean isReplaceable(BlockFire block, World world, BlockPos pos) {
        return block.isReplaceable(world, pos.getX(), pos.getY(), pos.getZ());
    }

    // not sure
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        SharedThreadPool.getExecutorService();
    }

    @Unique
    private final ConcurrentLinkedQueue<int[][]> blocksToUpdate = new ConcurrentLinkedQueue<>();

    @Inject(method = "updateTick", at = @At("HEAD"))
    public void updateTick(World world, int x, int y, int z, Random random, CallbackInfo info) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinFireTick) {
            // Process fire ticks using breadth-first search
            boolean[][][] visited = new boolean[3][3][3]; // 3x3x3 grid centered at (x, y, z)
            visited[1][1][1] = true;
            ConcurrentLinkedQueue<int[][]> blocksToUpdate = this.blocksToUpdate;
            for (int i = 0; i < numberOfCPUs; i++) {
                blocksToUpdate.add(new int[batchSize][3]);
            }
            blocksToUpdate.add(new int[][] { { x, y, z } });
            int index = 0;
            while (index < blocksToUpdate.size()) {
                int[][] blockCoords = blocksToUpdate.poll();
                for (int i = 0; i < blockCoords.length; i++) {
                    int[] coords = blockCoords[i];
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (dx == 0 && dy == 0 && dz == 0) {
                                    continue; // skip the center block
                                }
                                int fx = coords[0] + dx;
                                int fy = coords[1] + dy;
                                int fz = coords[2] + dz;
                                if (visited[dx + 1][dy + 1][dz + 1] || !world.getBlock(fx, fy, fz)
                                    .getMaterial()
                                    .isReplaceable()) {
                                    continue; // skip already visited or non-replaceable blocks
                                }
                                visited[dx + 1][dy + 1][dz + 1] = true; // mark block as visited
                                if (world.getBlock(fx, fy, fz) instanceof BlockFire) {
                                    int[][] lastBatch = blocksToUpdate.peek();
                                    if (lastBatch[batchSize - 1] != null) {
                                        blocksToUpdate.add(new int[batchSize][3]); // create new batch
                                        lastBatch = blocksToUpdate.peek();
                                    }
                                    int lastIndex = 0;
                                    while (lastBatch[lastIndex] != null) {
                                        lastIndex++;
                                    }
                                    lastBatch[lastIndex] = new int[] { fx, fy, fz }; // add adjacent fire block to
                                                                                     // update list
                                }
                            }
                        }
                    }
                }
            }
            // Process batches in parallel using multiple threads
            ExecutorService executor = Executors.newFixedThreadPool(numberOfCPUs);
            List<Future<Void>> futures = new ArrayList<>();
            for (int[][] batch : blocksToUpdate) {
                if (batch[0] == null) {
                    break; // reached end of batches
                }
                futures.add(executor.submit(() -> {
                    for (int[] coords : batch) {
                        if (coords == null) {
                            break; // reached end of batch
                        }
                        // Code to update block at coords goes here
                    }
                    return null;
                }));
            }
            for (Future<Void> future : futures) {
                try {
                    future.get(); // Wait for all tasks to complete
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            SharedThreadPool.getExecutorService().shutdown();
        }
    }
}
