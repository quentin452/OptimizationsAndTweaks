package fr.iamacat.multithreading.mixins.common.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import net.minecraft.world.*;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinWorldTick {

    private static final int BATCH_SIZE = 8;
    private final ConcurrentLinkedQueue<Chunk> chunksToUpdate = new ConcurrentLinkedQueue<>();

    @Final
    private ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("World-Tick-%d").build());

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
            chunksToUpdate.offer((Chunk) chunk);
        }

        // Process the chunks in batches
        while (!chunksToUpdate.isEmpty()) {
            List<Chunk> batch = new ArrayList<>();
            for (int i = 0; i < BATCH_SIZE && !chunksToUpdate.isEmpty(); i++) {
                Chunk chunk = chunksToUpdate.poll();
                if (chunk != null) {
                    batch.add(chunk);
                }
            }
            processBatch(chunkProvider, chunkLoader, batch);
        }
    }

    private void processBatch(ChunkProviderServer chunkProvider, IChunkLoader chunkLoader, List<Chunk> batch) {
        // Load the chunks from disk and save them
        batch.parallelStream().forEach(chunk -> {
            synchronized (chunk) {
                synchronized (chunkLoader) {
                    WorldProvider worldProvider = chunkProvider.worldObj.provider;
                    chunkLoader.saveExtraChunkData(worldProvider.worldObj, chunk);
                }
            }
        });
    }
}
