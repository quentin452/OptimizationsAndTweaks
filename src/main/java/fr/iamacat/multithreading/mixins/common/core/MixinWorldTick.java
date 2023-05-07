package fr.iamacat.multithreading.mixins.common.core;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinWorldTick {

    private static final int UPDATE_CHUNK_AT_ONCE = 10;
    private final Map<ChunkCoordIntPair, Chunk> loadedChunks = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);
    private final List<Chunk> chunksToUpdate = new CopyOnWriteArrayList<>();
    private final Set<ChunkCoordIntPair> adjacentChunks = ConcurrentHashMap.newKeySet();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinWorldTick) {
            updateChunksAsync((World) (Object) this);
        }
    }

    private void updateChunksAsync(World world) {
        CompletableFuture.runAsync(() -> {
            try {
                updateChunks(world);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    private void updateChunks(World world) throws Exception {
        ChunkProviderServer chunkProvider = (ChunkProviderServer) world.getChunkProvider();
        IChunkLoader chunkLoader = chunkProvider.currentChunkLoader;

        Map<ChunkCoordIntPair, Chunk> loadedChunks = (Map<ChunkCoordIntPair, Chunk>) chunkProvider.loadedChunks;

        List<Chunk> chunksToUpdateCopy = new ArrayList<>(loadedChunks.values());
        chunksToUpdate.addAll(chunksToUpdateCopy);

        while (!chunksToUpdate.isEmpty()) {
            int numChunksToUpdate = Math.min(chunksToUpdate.size(), UPDATE_CHUNK_AT_ONCE);
            List<Chunk> batch = new ArrayList<>(numChunksToUpdate);
            for (int i = 0; i < numChunksToUpdate; i++) {
                batch.add(chunksToUpdate.remove(0));
            }

            adjacentChunks.clear();
            for (Chunk chunk : batch) {
                ChunkCoordIntPair chunkCoord = chunk.getChunkCoordIntPair();
                loadedChunks.put(chunkCoord, chunk);
                adjacentChunks.addAll(getAdjacentChunks(chunkCoord));
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (ChunkCoordIntPair chunkCoord : adjacentChunks) {
                if (!loadedChunks.containsKey(chunkCoord)) {
                    CompletableFuture<Chunk> future = CompletableFuture.supplyAsync(() -> {
                        try {
                            Chunk adjacentChunk;
                            synchronized (chunkLoader) {
                                adjacentChunk = chunkLoader
                                    .loadChunk(world, chunkCoord.chunkXPos, chunkCoord.chunkZPos);
                                chunkProvider.saveChunks(false, null);
                            }
                            return adjacentChunk;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, executorService);
                    futures.add(future.thenAcceptAsync(adjacentChunk -> {
                        if (adjacentChunk != null) {
                            loadedChunks.put(adjacentChunk.getChunkCoordIntPair(), adjacentChunk);
                            chunksToUpdate.add(adjacentChunk);
                        }
                    }));
                }
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .join();

            processBatch(chunkProvider, batch);
        }
    }

    private Set<ChunkCoordIntPair> getAdjacentChunks(ChunkCoordIntPair chunkCoord) {
        Set<ChunkCoordIntPair> adjacentChunks = ConcurrentHashMap.newKeySet();
        int chunkX = chunkCoord.chunkXPos;
        int chunkZ = chunkCoord.chunkZPos;
        int radius = 2;

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
        CompletableFuture.runAsync(() -> {
            synchronized (chunkProvider) {
                for (Chunk chunk : batch) {
                    chunkProvider.saveChunks(true, null);
                }
                for (Chunk chunk : loadedChunks.values()) {
                    chunkProvider.saveChunks(true, null);
                }
            }
        }, executorService);
    }
}
