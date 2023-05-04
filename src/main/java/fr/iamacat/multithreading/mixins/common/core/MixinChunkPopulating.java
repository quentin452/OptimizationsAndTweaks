package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import net.minecraft.crash.CrashReport;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
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

@Mixin(value = World.class, priority = 850)
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
            List<Chunk> chunksToProcess = new ArrayList<>(chunksInProgress.keySet());
            int numChunks = chunksToProcess.size();
            if (numChunks > 0) {
                try {
                    executorService.invokeAll(chunksToProcess.stream().map(chunk -> (Callable<Void>) () -> {
                        chunk.isTerrainPopulated = true;
                        chunk.populateChunk(chunkProvider, chunkProvider1, chunk.xPosition, chunk.zPosition);
                        return null;
                    }).collect(Collectors.toList()));
                } catch (InterruptedException e) {
                    // Handle exception appropriately
                }
            }
        }
    }
}
