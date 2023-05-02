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
            List<Chunk> chunksToProcess = new ArrayList<>();
            for (Chunk chunkToPopulate : chunksToPopulate) {
                if (!chunkToPopulate.isTerrainPopulated && chunkToPopulate.isChunkLoaded) {
                    chunksToProcess.add(chunkToPopulate);
                }
            }
            chunksToPopulate.removeAll(chunksToProcess);
            int numChunks = chunksToProcess.size();
            if (numChunks > 0) {
                int numThreads = Math.min(numChunks, NUM_THREADS);
                int numChunksPerThread = (numChunks + numThreads - 1) / numThreads;
                CountDownLatch batchLatch = new CountDownLatch(numThreads);
                ExecutorService executorService = SharedThreadPool.getExecutorService();
                for (int i = 0; i < numThreads; i++) {
                    int startIndex = i * numChunksPerThread;
                    int endIndex = Math.min(startIndex + numChunksPerThread, numChunks);
                    List<Chunk> batchChunks = chunksToProcess.subList(startIndex, endIndex);
                    executorService.execute(() -> {
                        for (Chunk batchChunk : batchChunks) {
                            batchChunk.isTerrainPopulated = true;
                            batchChunk.populateChunk(
                                chunkProvider,
                                chunkProvider1,
                                batchChunk.xPosition,
                                batchChunk.zPosition);
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
}
