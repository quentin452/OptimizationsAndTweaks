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

import net.minecraft.crash.CrashReport;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = WorldServer.class, priority = 998)
public abstract class MixinChunkPopulating {

    private final CopyOnWriteArrayList<Chunk> chunksToPopulate = new CopyOnWriteArrayList<>();
    private final ConcurrentSkipListSet<Chunk> chunksInProgress = new ConcurrentSkipListSet<>();
    private static final int MAX_CHUNKS_PER_TICK = 20; // Lowered from 50 to reduce server lag spikes
    private static final int NUM_THREADS = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
    private final CountDownLatch latch = new CountDownLatch(MAX_CHUNKS_PER_TICK);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        SharedThreadPool.getExecutorService();
    }

    @Inject(
        method = "Lnet/minecraft/world/WorldServer;<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/ExecutorService;Lnet/minecraft/world/storage/ISaveHandler;Lnet/minecraft/world/storage/WorldInfo;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;Lnet/minecraft/crash/CrashReport;Lnet/minecraft/util/ReportedException;)V",
        at = @At("RETURN"))
    private void onInitialize(MinecraftServer server, ExecutorService executorService, ISaveHandler saveHandler,
                              WorldInfo info, WorldProvider provider, Profiler profiler, CrashReport report, CallbackInfo ci) {
        // Only load necessary chunks
        for (Object chunk : ((ChunkProviderServer) provider.createChunkGenerator()).loadedChunks) {
            if (chunk instanceof Chunk && !((Chunk) chunk).isTerrainPopulated && ((Chunk) chunk).isChunkLoaded) {
                Chunk chunkToAdd = (Chunk) chunk;
                if (!chunksInProgress.contains(chunkToAdd)) {
                    chunksToPopulate.add(chunkToAdd);
                    chunksInProgress.add(chunkToAdd);
                }
            }
        }
    }
    @Inject(method = "populate", at = @At("HEAD"))
    private void onPopulate(Chunk chunk, IChunkProvider chunkProvider, IChunkProvider chunkProvider1, CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinChunkPopulating) {
            int count = 0;
            List<Chunk> chunksToProcess = new ArrayList<>();
            for (Chunk chunkToPopulate : chunksToPopulate) {
                if (count >= MAX_CHUNKS_PER_TICK) {
                    break;
                }
                if (!chunkToPopulate.isTerrainPopulated) {
                    chunksToProcess.add(chunkToPopulate);
                    count++;
                }
            }
            chunksToPopulate.removeAll(chunksToProcess);
            int numChunks = chunksToProcess.size();
            int numBatches = (numChunks + MAX_CHUNKS_PER_TICK - 1) / MAX_CHUNKS_PER_TICK;
            CountDownLatch batchLatch = new CountDownLatch(numBatches);
            ExecutorService executorService = SharedThreadPool.getExecutorService();
            BlockingQueue<Chunk> priorityQueue = new PriorityBlockingQueue<>(numChunks, (c1, c2) -> {
                int dist1 = Math.abs(c1.xPosition - chunk.xPosition) + Math.abs(c1.zPosition - chunk.zPosition);
                int dist2 = Math.abs(c2.xPosition - chunk.xPosition) + Math.abs(c2.zPosition - chunk.zPosition);
                return Integer.compare(dist1, dist2);
            });
            for (Chunk batchChunk : chunksToProcess) {
                priorityQueue.offer(batchChunk);
            }
            while (!priorityQueue.isEmpty()) {
                List<Chunk> batchChunks = new ArrayList<>(MAX_CHUNKS_PER_TICK);
                priorityQueue.drainTo(batchChunks, MAX_CHUNKS_PER_TICK);
                executorService.execute(() -> {
                    for (Chunk batchChunk : batchChunks) {
                        if (batchChunk.isChunkLoaded) {
                            batchChunk.isTerrainPopulated = true;
                            batchChunk.populateChunk(
                                chunkProvider,
                                chunkProvider1,
                                batchChunk.xPosition,
                                batchChunk.zPosition);
                        }
                        chunksInProgress.remove(batchChunk);
                        latch.countDown();
                    }
                    batchLatch.countDown();
                });
                chunksInProgress.addAll(batchChunks);
            }
            try {
                batchLatch.await();
            } catch (InterruptedException e) {
                // Handle exception appropriately
            }
        }
    }
}
