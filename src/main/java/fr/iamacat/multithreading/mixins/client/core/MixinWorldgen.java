package fr.iamacat.multithreading.mixins.client.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "doPreChunk", at = @At("HEAD"))
    private void onDoPreChunk(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinWorldgen) {
            // Load chunk asynchronously
            CompletableFuture.supplyAsync(this::loadChunk, executorService)
                .thenAccept(this::onChunkLoaded);
        } else {
            setActivePlayerChunksAndCheckLightPublic();
        }
    }

    private Chunk loadChunk() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        int x = MathHelper.floor_double(player.posX) >> 4;
        int z = MathHelper.floor_double(player.posZ) >> 4;
        boolean loadChunk = true;

        ChunkProviderClient chunkProvider = (ChunkProviderClient) world.getChunkProvider();
        Chunk chunk = chunkProvider.provideChunk(x, z);
        if (loadChunk && chunk == null) {
            // Load chunk synchronously
            chunkProvider.loadChunk(x, z);
            chunk = chunkProvider.provideChunk(x, z);
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
