package fr.iamacat.multithreading.mixins.common.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.world.*;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.ChunkCoordIntPair;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(WorldServer.class)
public abstract class MixinWorldTick {
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("World-Tick-%d")
            .build());

    private final ReentrantLock lock = new ReentrantLock();

    @Inject(method = "updatechunks", at = @At("HEAD"))
    private void onUpdateChunks(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinWorldTick) {
            // Update chunks asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    updateChunks((WorldServer) (Object) this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executorService);
        }
    }

    private void updateChunks(WorldServer world) throws Exception {
        // Get the chunk loader and the number of loaded chunks
        ChunkProviderServer chunkProvider = (ChunkProviderServer) world.getChunkProvider();
        IChunkLoader chunkLoader = chunkProvider.currentChunkLoader;
        int loadedChunkCount = chunkProvider.getLoadedChunkCount();

        // Divide the chunks into batches
        int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;
        List<ChunkCoordIntPair> chunkCoords = new ArrayList<>();
        for (int i = 0; i < loadedChunkCount; i++) {
            chunkCoords.add(new ChunkCoordIntPair(i / 16, i % 16));
        }
        List<List<ChunkCoordIntPair>> batches = Lists.partition(chunkCoords, batchSize);

        // Update each batch of chunks in parallel
        List<CompletableFuture<Void>> futures = batches.parallelStream().map(batch -> CompletableFuture.runAsync(() -> {
            synchronized (world) {
                for (ChunkCoordIntPair chunkCoord : batch) {
                    Chunk chunk = chunkProvider.provideChunk(chunkCoord.chunkXPos, chunkCoord.chunkZPos);
                    if (chunk != null) {
                        synchronized (chunk) {
                            synchronized (chunkLoader) {
                                chunkLoader.saveExtraChunkData(world, chunk);
                            }
                        }
                    }
                }
            }
        }, Executors.newCachedThreadPool())).collect(Collectors.toList());

        // Wait for all the futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

}
