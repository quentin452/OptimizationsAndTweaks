package fr.iamacat.multithreading.mixins.common.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {

    // Fixme todo
    @Shadow
    public Chunk loadChunk(int x, int z) {
        return null;
    }

    @Unique
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksConfig.numberofcpus,
        MultithreadingandtweaksConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Chunk-Provider-Server-%d")
            .build());

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinChunkProviderServer) {
            try {
                int batchSize = MultithreadingandtweaksConfig.batchsize;

                List<Chunk> chunksToProcess = new ArrayList<>();
                WorldServer world = MinecraftServer.getServer().worldServers[0];
                ChunkProviderServer chunkProvider = (ChunkProviderServer) world.getChunkProvider();

                synchronized (chunkProvider) {
                    Field currentChunkSetField = ChunkProviderServer.class.getDeclaredField("currentChunkSet");
                    currentChunkSetField.setAccessible(true);
                    ChunkCoordIntPair currentChunkSet = (ChunkCoordIntPair) currentChunkSetField.get(chunkProvider);

                    for (int chunkX = currentChunkSet.chunkXPos - batchSize; chunkX
                        <= currentChunkSet.chunkXPos + batchSize; chunkX++) {
                        for (int chunkZ = currentChunkSet.chunkZPos - batchSize; chunkZ
                            <= currentChunkSet.chunkZPos + batchSize; chunkZ++) {
                            Chunk chunk = chunkProvider.loadChunk(chunkX, chunkZ);
                            if (chunk != null) {
                                chunksToProcess.add(chunk);
                            }
                        }
                    }
                }

                for (Chunk chunk : chunksToProcess) {
                    executorService.execute(() -> {
                        synchronized (chunk) {
                            if (chunk.isModified) {
                                chunk.isModified = false;
                                chunkProvider.saveChunks(true, null);
                            }
                        }
                    });
                }

                executorService.shutdown();

                ci.cancel();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
