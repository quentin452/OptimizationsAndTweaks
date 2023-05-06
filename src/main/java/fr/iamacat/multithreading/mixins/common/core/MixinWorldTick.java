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
    private static final int UPDATE_CHUNK_AT_ONCE = 10;
    private final ConcurrentLinkedQueue<Chunk> chunksToUpdate = new ConcurrentLinkedQueue<>();
    private final Map<ChunkCoordIntPair, Chunk> loadedChunks = new ConcurrentHashMap<>();
    private final Map<ChunkCoordIntPair, CompletableFuture<Chunk>> loadingChunks = new ConcurrentHashMap<>();

    private final Object tickLock = new Object(); // Lock for accessing TickNextTick list

    @Final
    private volatile ForkJoinPool executorService = new ForkJoinPool(
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

        List<Chunk> chunksToUpdateCopy;
        synchronized (chunkProvider.loadedChunks) {
            chunksToUpdateCopy = new ArrayList<>(chunkProvider.loadedChunks);
        }

        List<CompletableFuture<Chunk>> futures = new ArrayList<>();
        Set<ChunkCoordIntPair> adjacentChunks = new HashSet<>();
        Iterator<Chunk> iter = chunksToUpdateCopy.iterator();

        ConcurrentHashMap<ChunkCoordIntPair, Chunk> loadedChunks = new ConcurrentHashMap<>((Map) chunkProvider.loadedChunks);
        ConcurrentLinkedQueue<Chunk> chunksToUpdate = new ConcurrentLinkedQueue<>(chunksToUpdateCopy);
        ConcurrentHashMap<ChunkCoordIntPair, CompletableFuture<Chunk>> loadingChunks = new ConcurrentHashMap<>();

        while (iter.hasNext()) {
            Chunk chunk = iter.next();
            ChunkCoordIntPair chunkCoord = chunk.getChunkCoordIntPair();
            loadedChunks.put(chunkCoord, chunk);
            chunksToUpdate.offer(chunk);
            adjacentChunks.addAll(getAdjacentChunks(chunkCoord));
            iter.remove(); // remove chunk from the list while iterating
        }

        while (!chunksToUpdate.isEmpty()) {
            List<Chunk> batch = new ArrayList<>();
            for (int i = 0; i < UPDATE_CHUNK_AT_ONCE && !chunksToUpdate.isEmpty(); i++) {
                batch.add(chunksToUpdate.poll());
            }

            for (ChunkCoordIntPair chunkCoord : adjacentChunks) {
                if (!loadedChunks.containsKey(chunkCoord) && !loadingChunks.containsKey(chunkCoord)) {
                    CompletableFuture<Chunk> future = CompletableFuture.supplyAsync(() -> {
                        try {
                            Chunk adjacentChunk;
                            synchronized (lock) {
                                adjacentChunk = chunkLoader.loadChunk(world, chunkCoord.chunkXPos, chunkCoord.chunkZPos);
                                chunkProvider.saveChunks(false, null);
                            }
                            loadedChunks.put(chunkCoord, adjacentChunk);
                            return adjacentChunk;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, executorService);
                    futures.add(future);
                    loadingChunks.put(chunkCoord, future);
                }
            }

            for (CompletableFuture<Chunk> future : futures) {
                Chunk adjacentChunk = future.join();
                if (adjacentChunk != null) {
                    loadedChunks.put(adjacentChunk.getChunkCoordIntPair(), adjacentChunk);
                }
            }
            futures.clear();

            synchronized (tickLock) {
                processBatch(chunkProvider, new CopyOnWriteArrayList<>(batch));
            }
        }
    }




    private Set<ChunkCoordIntPair> getAdjacentChunks(ChunkCoordIntPair chunkCoord) {
        Set<ChunkCoordIntPair> adjacentChunks = ConcurrentHashMap.newKeySet();
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
            synchronized (lock) {
                for (Chunk chunk : batch) {
                    chunkProvider.saveChunks(true, null);
                }
                // Save all loaded chunks
                for (Chunk chunk : loadedChunks.values()) {
                    chunkProvider.saveChunks(true, null);
                }
            }
        }, executorService);
    }
}
