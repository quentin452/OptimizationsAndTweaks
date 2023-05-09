package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer {
    @Shadow
    public Chunk loadChunk(int x, int z) {
        return null;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinChunkProviderServer) {
            try {
                // Number of chunks to process per tick
                int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;
                // Number of threads to use
                int numThreads = MultithreadingandtweaksMultithreadingConfig.numberofcpus;

                // Create a list of chunks to process
                List<Chunk> chunksToProcess = new ArrayList<>();
                WorldServer world = MinecraftServer.getServer().worldServers[0];
                ChunkProviderServer chunkProvider = (ChunkProviderServer) world.getChunkProvider();

                // Use reflection to make the currentChunkSet field accessible
                Field currentChunkSetField = ChunkProviderServer.class.getDeclaredField("currentChunkSet");
                currentChunkSetField.setAccessible(true);
                ChunkCoordIntPair currentChunkSet = (ChunkCoordIntPair) currentChunkSetField.get(chunkProvider);

                for (int chunkX = currentChunkSet.chunkXPos - batchSize; chunkX <= currentChunkSet.chunkXPos + batchSize; chunkX++) {
                    for (int chunkZ = currentChunkSet.chunkZPos - batchSize; chunkZ <= currentChunkSet.chunkZPos + batchSize; chunkZ++) {
                        Chunk chunk = chunkProvider.loadChunk(chunkX, chunkZ);
                        if (chunk != null) {
                            chunksToProcess.add(chunk);
                        }
                    }
                }

                // Process the chunks using a thread pool
                ExecutorService executor = Executors.newFixedThreadPool(numThreads);
                for (Chunk chunk : chunksToProcess) {
                    executor.execute(() -> {
                        ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);
                        if (chunk.isModified) {
                            chunk.isModified = false;
                            chunkProvider.saveChunks(true, null);
                        }
                    });
                }
                executor.shutdown();

                ci.cancel();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

