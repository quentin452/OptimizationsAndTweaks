package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = WorldServer.class, priority = 998)
public abstract class MixinChunkPopulating {

    private final ConcurrentLinkedQueue<Chunk> chunksToPopulate = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Chunk, Boolean> chunksInProgress = new ConcurrentHashMap<>();

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Chunk-Populator-%d")
            .build());

    @Inject(
        method = "Lnet/minecraft/world/WorldServer;<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/ExecutorService;Lnet/minecraft/world/storage/ISaveHandler;Lnet/minecraft/world/storage/WorldInfo;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;Lnet/minecraft/crash/CrashReport;Lnet/minecraft/util/ReportedException;)V",
        at = @At("RETURN"))
    private void onInitialize(MinecraftServer server, ExecutorService executorService, ISaveHandler saveHandler,
        WorldInfo info, WorldProvider provider, Profiler profiler, CrashReport report, CallbackInfo ci) {
        // Only load necessary chunks
        for (Object chunk : ((ChunkProviderServer) provider.createChunkGenerator()).loadedChunks) {
            if (chunk instanceof Chunk && !((Chunk) chunk).isTerrainPopulated && ((Chunk) chunk).isChunkLoaded) {
                Chunk chunkToAdd = (Chunk) chunk;
                if (chunksInProgress.putIfAbsent(chunkToAdd, true) == null) {
                    chunksToPopulate.add(chunkToAdd);
                }
            }
        }
    }

    @Inject(method = "populate", at = @At("TAIL"))
    private void onPopulate(IChunkProvider chunkProvider, IChunkProvider chunkProvider1, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinChunkPopulating) {
            List<Chunk> chunksToProcess = new ArrayList<>();
            chunksToPopulate.poll();
            int numChunks = chunksToProcess.size();
            if (numChunks > 0) {
                int numThreads = Runtime.getRuntime()
                    .availableProcessors();
                int numChunksPerThread = (numChunks + numThreads - 1) / numThreads;
                List<List<Chunk>> batches = chunksToProcess.stream()
                    .collect(
                        Collectors.groupingByConcurrent(chunk -> (chunksToProcess.indexOf(chunk) / numChunksPerThread)))
                    .values()
                    .stream()
                    .collect(Collectors.toList());
                CountDownLatch batchLatch = new CountDownLatch(batches.size());
                for (List<Chunk> batchChunks : batches) {
                    executorService.execute(() -> {
                        try {
                            batchChunks.forEach(batchChunk -> {
                                try {
                                    batchChunk.isTerrainPopulated = true;
                                    batchChunk.populateChunk(
                                        chunkProvider,
                                        chunkProvider1,
                                        batchChunk.xPosition,
                                        batchChunk.zPosition);
                                } finally {
                                    chunksInProgress.remove(batchChunk);
                                }
                            });
                        } catch (Exception e) {
                            // Handle exception appropriately
                        } finally {
                            batchLatch.countDown();
                        }
                    });
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
