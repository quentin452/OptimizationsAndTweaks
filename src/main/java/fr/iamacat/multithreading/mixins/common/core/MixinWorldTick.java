package fr.iamacat.multithreading.mixins.common.core;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinWorldTick {

    private final Object lock = new Object();
    private static final int updatechunkatonce = 10;
    private final ConcurrentLinkedQueue<Chunk> chunksToUpdate = new ConcurrentLinkedQueue<>();
    private final Map<ChunkCoordIntPair, Chunk> loadedChunks = new ConcurrentHashMap<>();
    private final Map<ChunkCoordIntPair, CompletableFuture<Chunk>> loadingChunks = new ConcurrentHashMap<>();
    private final Object tickLock = new Object(); // Lock for accessing TickNextTick list

    @Final
    private ForkJoinPool executorService = new ForkJoinPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
        null,
        true);

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinWorldTick) {
            // Update chunks asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    updateChunks((World) (Object) this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executorService);
        }
    }

    private void updateChunks(World world) throws Exception {
        ChunkProviderServer chunkProvider = (ChunkProviderServer) world.getChunkProvider();
        IChunkLoader chunkLoader = chunkProvider.currentChunkLoader;

        // Add all chunks that need to be updated to the queue
        for (Object chunk : chunkProvider.loadedChunks) {
            Chunk loadedChunk = (Chunk) chunk;
            ChunkCoordIntPair chunkCoord = loadedChunk.getChunkCoordIntPair();
            loadedChunks.put(chunkCoord, loadedChunk);
            chunksToUpdate.offer(loadedChunk);
        }

        // Process the chunks in batches
        while (!chunksToUpdate.isEmpty()) {
            List<Chunk> batch = new ArrayList<>();
            Set<ChunkCoordIntPair> adjacentChunks = new HashSet<>();
            for (int i = 0; i < updatechunkatonce && !chunksToUpdate.isEmpty(); i++) {
                Chunk chunk = chunksToUpdate.poll();
                if (chunk != null) {
                    batch.add(chunk);
                    ChunkCoordIntPair chunkCoord = chunk.getChunkCoordIntPair();
                    adjacentChunks.addAll(getAdjacentChunks(chunkCoord));
                }
            }

            // Load the adjacent chunks if they are not already loaded
            for (ChunkCoordIntPair chunkCoord : adjacentChunks) {
                if (!loadedChunks.containsKey(chunkCoord) && !loadingChunks.containsKey(chunkCoord)) {
                    loadingChunks.put(chunkCoord, CompletableFuture.supplyAsync(() -> {
                        try {
                            Chunk adjacentChunk = chunkLoader
                                .loadChunk(world, chunkCoord.chunkXPos, chunkCoord.chunkZPos);
                            loadedChunks.put(chunkCoord, adjacentChunk);
                            return adjacentChunk;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, executorService));
                }
            }

            // Wait for the adjacent chunks to finish loading
            for (CompletableFuture<Chunk> future : loadingChunks.values()) {
                Chunk adjacentChunk = future.join();
                if (adjacentChunk != null) {
                    loadedChunks.put(adjacentChunk.getChunkCoordIntPair(), adjacentChunk);
                }
            }
            loadingChunks.clear();

            // Process the batch of chunks
            synchronized (tickLock) { // Synchronize access to TickNextTick list
                processBatch(chunkProvider, batch);
            }
        }
    }


    private Set<ChunkCoordIntPair> getAdjacentChunks(ChunkCoordIntPair chunkCoord) {
        Set<ChunkCoordIntPair> adjacentChunks = new HashSet<>();
        int chunkX = chunkCoord.chunkXPos;
        int chunkZ = chunkCoord.chunkZPos;
        int radius = 1;

        for (int x = chunkX - radius; x <= chunkX + radius; x++) {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                if (x != chunkX || z != chunkZ) {
                    adjacentChunks.add(new ChunkCoordIntPair(x, z));
                }
            }
        }
        return adjacentChunks;
    }
    private void processBatch(ChunkProviderServer chunkProvider, List<Chunk> batch) {
        // Load the chunks from disk asynchronously and save them
        CompletableFuture.runAsync(() -> {
            batch.parallelStream()
                .forEach(chunk -> {
                    synchronized (lock) {
                        chunkProvider.saveChunks(true, null);
                    }
                });
            // Save all loaded chunks
            synchronized (lock) {
                chunkProvider.saveChunks(true, null);
            }
        }, executorService);
    }

}
