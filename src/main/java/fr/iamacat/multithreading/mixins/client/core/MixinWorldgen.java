package fr.iamacat.multithreading.mixins.client.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.ChunkPos;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(WorldClient.class)
public abstract class MixinWorldgen {

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Worldgen-%d")
            .build());

    private World world;
    private final Map<Long, Chunk> loadedChunks = new ConcurrentHashMap<>();

    @Inject(method = "doPreChunk", at = @At("HEAD"))
    private void onDoPreChunk(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinWorldgen) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            int x = Math.floorDiv((int) player.posX, 16);
            int z = Math.floorDiv((int) player.posZ, 16);

            // Load chunk asynchronously
            CompletableFuture.supplyAsync(() -> loadChunk(x, z), executorService)
                .thenAccept(this::onChunkLoaded);
        } else {
            setActivePlayerChunksAndCheckLightPublic();
        }
    }

    private Chunk loadChunk(int x, int z) {
        long key = ChunkPos.asLong(x, z);
        Chunk cachedChunk = loadedChunks.get(key);
        if (cachedChunk != null) {
            return cachedChunk;
        }

        ChunkProviderClient chunkProvider = (ChunkProviderClient) world.getChunkProvider();
        Chunk chunk = chunkProvider.provideChunk(x, z);
        if (chunk == null) {
            // Load chunk synchronously
            chunkProvider.loadChunk(x, z);
            chunk = chunkProvider.provideChunk(x, z);
        }

        // Cache the loaded chunk
        if (chunk != null) {
            loadedChunks.put(key, chunk);
        }
        return chunk;
    }

    private void onChunkLoaded(Chunk chunk) {
        if (chunk != null) {
            setActivePlayerChunksAndCheckLightPublic();
        }
    }

    public void setActivePlayerChunksAndCheckLightPublic() {
        try {
            Method setActivePlayerChunksAndCheckLight = WorldClient.class
                .getDeclaredMethod("setActivePlayerChunksAndCheckLight");
            setActivePlayerChunksAndCheckLight.setAccessible(true);
            setActivePlayerChunksAndCheckLight.invoke(world);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
