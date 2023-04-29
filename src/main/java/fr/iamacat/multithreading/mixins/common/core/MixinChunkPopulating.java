package fr.iamacat.multithreading.mixins.common.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.ISaveHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldServer.class, priority = 998)
public abstract class MixinChunkPopulating {

    private final ConcurrentLinkedQueue<Chunk> chunksToPopulate = new ConcurrentLinkedQueue<>();
    private final Set<Chunk> chunksInProgress = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final int MAX_CHUNKS_PER_TICK = 50;
    private ExecutorService executorService;
    private ExecutorCompletionService<Void> completionService;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(WorldServer world, ISaveHandler saveHandler, IChunkProvider chunkProvider, Profiler profiler,
        CallbackInfo ci) {
        executorService = Executors.newFixedThreadPool(8);
        completionService = new ExecutorCompletionService<>(executorService);
        for (Object chunk : ((ChunkProviderServer) chunkProvider).loadedChunks) {
            if (chunk instanceof Chunk && !((Chunk) chunk).isTerrainPopulated && ((Chunk) chunk).isChunkLoaded) {
                Chunk chunkToAdd = (Chunk) chunk;
                synchronized (chunksInProgress) {
                    if (!chunksInProgress.contains(chunkToAdd)) {
                        chunksToPopulate.add(chunkToAdd);
                        chunksInProgress.add(chunkToAdd);
                    }
                }
            }
        }
    }

    @Inject(method = "populate", at = @At("HEAD"))
    private void onPopulate(Chunk chunk, IChunkProvider chunkProvider, IChunkProvider chunkProvider1, CallbackInfo ci) {
        int count = 0;
        while (count < MAX_CHUNKS_PER_TICK) {
            Chunk chunkToPopulate = chunksToPopulate.poll();
            if (chunkToPopulate == null) {
                break;
            }
            completionService.submit(() -> {
                if (chunkToPopulate.isChunkLoaded && !chunkToPopulate.isTerrainPopulated) {
                    chunkToPopulate.isTerrainPopulated = true;
                    chunkToPopulate.populateChunk(
                        chunkProvider,
                        chunkProvider1,
                        chunkToPopulate.xPosition,
                        chunkToPopulate.zPosition);
                }
                synchronized (chunksInProgress) {
                    chunksInProgress.remove(chunkToPopulate);
                }
                return null;
            });
            count++;
        }
        try {
            for (int i = 0; i < count; i++) {
                completionService.take()
                    .get();
            }
        } catch (InterruptedException | ExecutionException e) {
            // Handle exceptions appropriately
        }
        executorService.shutdown();
    }
}
