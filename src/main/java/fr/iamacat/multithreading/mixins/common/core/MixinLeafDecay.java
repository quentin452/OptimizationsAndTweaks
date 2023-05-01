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

import com.falsepattern.lib.compat.BlockPos;
import fr.iamacat.multithreading.SharedThreadPool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = BlockLeavesBase.class, priority = 900)
public abstract class MixinLeafDecay {

    private final List<BlockPos> decayQueue = new ArrayList<>();
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    private int numberOfCPUs = MultithreadingandtweaksMultithreadingConfig.numberofcpus;

    private final List<BlockPos> neighborPositions = new ArrayList<>(27);

    @Inject(method = "updateLeaves", at = @At("RETURN"))
    private void onUpdateLeaves(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinLeafDecay) {
            // Add leaf blocks that need to decay to the queue
            WorldServer worldServer = (WorldServer) world;
            ChunkProviderServer chunkProvider = worldServer.theChunkProviderServer;

            List<Chunk> loadedChunks = new ArrayList<>(chunkProvider.loadedChunks);

            loadedChunks.parallelStream().forEach(chunk -> {
                IntStream.range(chunk.xPosition * 16, chunk.xPosition * 16 + 16).parallel().forEach(i -> {
                    IntStream.range(0, 256).parallel().forEach(j -> {
                        IntStream.range(chunk.zPosition * 16, chunk.zPosition * 16 + 16).parallel().forEach(k -> {
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
        int numBatches = (queueSize + BATCH_SIZE - 1) / BATCH_SIZE;
        List<CompletableFuture<Void>> futures = new ArrayList<>(numBatches);
        for (int i = 0; i < numBatches; i++) {
            int startIndex = i * BATCH_SIZE;
            int endIndex = Math.min(startIndex + BATCH_SIZE, queueSize);
            List<BlockPos> batch = decayQueue.subList(startIndex, endIndex);
            futures.add(CompletableFuture.runAsync(() -> processLeafBlocks(batch, world),  SharedThreadPool.getExecutorService()));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void processLeafBlocks(List<BlockPos> batch, World world) {
        for (BlockPos pos : batch) {
            try {
                Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
                if (block == Blocks.leaves) {
                    if (shouldDecay(pos, world)) {
                        world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                        getNeighborPositions(pos, neighborPositions);
                        for (BlockPos neighbor : neighborPositions) {
                            Chunk chunk = world.getChunkFromBlockCoords(neighbor.getX(), neighbor.getZ());
                            if (chunk != null && chunk.isChunkLoaded) {
                                Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                                if (neighborBlock == Blocks.leaves) {
                                    decayQueue.add(neighbor.toImmutable());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LogManager.getLogger().error("Failed to process leaf block at " + pos, e);
            }
        }
    }

    public boolean shouldDecay(BlockPos pos, World world) {
        for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
            for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
                for (int z = pos.getZ() - 1; z <= pos.getZ() + 1; z++) {
                    if (x == pos.getX() && y == pos.getY() && z == pos.getZ()) {
                        continue;
                    }
                    Block block = world.getBlock(x, y, z);
                    if (block == Blocks.leaves) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static final BlockPos[] NEIGHBOR_OFFSETS = new BlockPos[27];
    static {
        int index = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    NEIGHBOR_OFFSETS[index++] = new BlockPos(x, y, z);
                }
            }
        }
    }
    private List<BlockPos> getNeighborPositions(BlockPos pos, List<BlockPos> neighborPositions) {
        List<BlockPos> neighbors = new ArrayList<>(NEIGHBOR_OFFSETS.length);
        for (BlockPos offset : NEIGHBOR_OFFSETS) {
            BlockPos neighborPos = pos.add(offset);
            neighbors.add(neighborPos);
        }
        return neighbors;
    }
}

