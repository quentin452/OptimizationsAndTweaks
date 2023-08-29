package fr.iamacat.multithreading.mixins.client.core;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.ChunkPos;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(WorldClient.class)
public abstract class MixinWorldgen {

    private final ExecutorService executorService = Executors.newFixedThreadPool(6);
    private final Map<Long, Chunk> loadedChunks = new ConcurrentHashMap<>();
    private final AtomicInteger chunksBeingLoaded = new AtomicInteger(0);

    @Inject(method = "doPreChunk", at = @At("HEAD"))
    private void onDoPreChunk(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinWorldgen) {
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            int x = Math.floorDiv((int) player.posX, 16);
            int z = Math.floorDiv((int) player.posZ, 16);

            long chunkPosKey = ChunkPos.asLong(x, z);
            loadChunkAsync(x, z);
        } else {
            setActivePlayerChunksAndCheckLightPublic();
        }
    }

    private void loadChunkAsync(int x, int z) {
        if (!loadedChunks.containsKey(ChunkPos.asLong(x, z))) {
            CompletableFuture.runAsync(() -> {
                Chunk chunk = loadChunk(x, z);
                if (chunk != null) {
                    loadedChunks.put(ChunkPos.asLong(x, z), chunk);
                    onChunkLoaded(chunk);
                }
            }, executorService);
        }
    }

    private Chunk loadChunk(int x, int z) {
        Chunk cachedChunk = loadedChunks.get(ChunkPos.asLong(x, z));
        if (cachedChunk != null) {
            return cachedChunk;
        }

        WorldClient world = Minecraft.getMinecraft().theWorld;
        ChunkProviderClient chunkProvider = (ChunkProviderClient) world.getChunkProvider();
        Chunk chunk = chunkProvider.provideChunk(x, z);

        if (chunk == null) {
            // Load chunk synchronously
            chunkProvider.loadChunk(x, z);
            chunk = chunkProvider.provideChunk(x, z);

            // Load adjacent chunks asynchronously if they're not already loaded
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = z - 1; j <= z + 1; j++) {
                    if (i == x && j == z) {
                        continue; // Skip the current chunk
                    }
                    long key = ChunkPos.asLong(i, j);
                    if (!loadedChunks.containsKey(key)) {
                        if (chunksBeingLoaded.incrementAndGet() <= 6) {
                            int finalI = i;
                            int finalJ = j;
                            CompletableFuture
                                .supplyAsync(
                                    () -> loadedChunks.computeIfAbsent(key, k -> loadChunk(finalI, finalJ)),
                                    executorService)
                                .thenAccept(this::onChunkLoaded)
                                .exceptionally(ex -> {
                                    ex.printStackTrace();
                                    return null;
                                });
                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                i--; // Retry current chunk in the next batch
                                chunksBeingLoaded.decrementAndGet();
                            }
                        }
                    }
                }
            }
        }

        // Cache the loaded chunk
        if (chunk != null) {
            loadedChunks.put(ChunkPos.asLong(x, z), chunk);
        }
        return chunk;
    }

    private void onChunkLoaded(Chunk chunk) {
        if (chunk != null) {
            chunksBeingLoaded.decrementAndGet();
            setActivePlayerChunksAndCheckLightPublic();
        }
    }

    public void setActivePlayerChunksAndCheckLightPublic() {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        try {
            Method method = World.class.getDeclaredMethod("setActivePlayerChunksAndCheckLight");
            method.setAccessible(true);
            method.invoke(world);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
