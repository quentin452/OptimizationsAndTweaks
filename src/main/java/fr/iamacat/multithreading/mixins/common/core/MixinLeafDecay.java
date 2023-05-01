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
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;

import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = BlockLeavesBase.class, priority = 900)
public abstract class MixinLeafDecay {

    private final List<BlockPos> decayQueue = new ArrayList<>();
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    private final int numberOfCPUs = MultithreadingandtweaksMultithreadingConfig.numberofcpus;

    @Inject(method = "updateLeaves", at = @At("RETURN"))
    private void onUpdateLeaves(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinLeafDecay) {
            // Add leaf blocks that need to decay to the queue
            WorldServer worldServer = (WorldServer) world;
            ChunkProviderServer chunkProvider = worldServer.theChunkProviderServer;

            List<Chunk> loadedChunks = new ArrayList<>(chunkProvider.loadedChunks);

            loadedChunks.parallelStream()
                .forEach(chunk -> {
                    IntStream.range(chunk.xPosition * 16, chunk.xPosition * 16 + 16)
                        .parallel()
                        .forEach(i -> {
                            IntStream.range(0, 256)
                                .parallel()
                                .forEach(j -> {
                                    IntStream.range(chunk.zPosition * 16, chunk.zPosition * 16 + 16)
                                        .parallel()
                                        .forEach(k -> {
                                            Block block = world.getBlock(i, j, k);
                                            if (block instanceof BlockLeaves) {
                                                decayQueue.add(new BlockPos(i, j, k));
                                            }
                                        });
                                });
                        });
                });
        }

        int queueSize = decayQueue.size();
        int numThreads = Math.min(queueSize, numberOfCPUs);
        List<List<BlockPos>> batches = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            int startIndex = i * BATCH_SIZE;
            int endIndex = Math.min(startIndex + BATCH_SIZE, queueSize);
            List<BlockPos> batch = new ArrayList<>(decayQueue.subList(startIndex, endIndex));
            batches.add(batch);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>(numThreads);
        for (List<BlockPos> batch : batches) {
            futures.add(
                CompletableFuture.runAsync(
                    () -> processLeafBlocks(new ArrayList<>(batch), world),
                    SharedThreadPool.getExecutorService()));
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to process leaf blocks", e);
        }

        decayQueue.clear();
    }

    public void processLeafBlocks(List<BlockPos> batch, World world) {
        List<BlockPos> neighborPositions = new ArrayList<>();
        for (BlockPos pos : batch) {
            try {
                Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
                if (block instanceof BlockLeaves) {
                    if (shouldDecay(pos, world)) {
                        world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                        getNeighborPositions(pos, neighborPositions);
                        for (BlockPos neighbor : neighborPositions) {
                            int chunkX = neighbor.getX() >> 4;
                            int chunkZ = neighbor.getZ() >> 4;
                            Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                            if (chunk != null && chunk.isChunkLoaded) {
                                Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                                if (neighborBlock instanceof BlockLeaves) {
                                    decayQueue.add(neighbor.toImmutable());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LogManager.getLogger()
                    .error("Failed to process leaf block at " + pos, e);
            }
        }
    }

    public boolean shouldDecay(BlockPos pos, World world) {
        Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        if (block instanceof BlockLeaves) {
            return !((BlockLeaves) block).shouldCheckWeakPower(world, pos.getX(), pos.getY(), pos.getZ(), 1);
        }
        return false;
    }

    public List<BlockPos> getNeighborPositions(BlockPos pos, List<BlockPos> neighborPositions) {
        neighborPositions.clear();
        for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
            for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
                for (int z = pos.getZ() - 1; z <= pos.getZ() + 1; z++) {
                    if (x != pos.getX() || y != pos.getY() || z != pos.getZ()) {
                        neighborPositions.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return neighborPositions;
    }

}
