package fr.iamacat.multithreading.mixins.common.core;


import com.falsepattern.lib.compat.MathHelper;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.libraries.com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(World.class)
public abstract class MixinChunkPopulating {

    private final LinkedBlockingQueue<Chunk> chunksToPopulate = new LinkedBlockingQueue<>();

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Chunk-Populator-%d")
            .build());

    @Inject(method = "populate", at = @At("TAIL"))
    private void onPopulate(IChunkProvider chunkProvider, IChunkProvider chunkProvider1, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinChunkPopulating) {
            int maxChunksPerTick = MultithreadingandtweaksMultithreadingConfig.batchsize; // Change this value to limit
            // the number of chunks to be
            // processed per tick
            int numChunksProcessed = 0;
            Chunk chunk = null;
            while (numChunksProcessed < maxChunksPerTick && (chunk = chunksToPopulate.poll()) != null) {
                final Chunk finalChunk = chunk;
                executorService.execute(() -> {
                    finalChunk.isTerrainPopulated = true;
                    finalChunk.populateChunk(chunkProvider, chunkProvider1, finalChunk.xPosition, finalChunk.zPosition);
                });

                numChunksProcessed++;
            }
        }
    }

    @Inject(
        method = "Lnet/minecraft/world/WorldServer;<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/ExecutorService;Lnet/minecraft/world/storage/ISaveHandler;Lnet/minecraft/world/storage/WorldInfo;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;Lnet/minecraft/crash/CrashReport;Lnet/minecraft/util/ReportedException;)V",
        at = @At("RETURN"))
    private void onInitialize(MinecraftServer server, ExecutorService executorService, ISaveHandler saveHandler,
                              WorldInfo info, WorldProvider provider, Profiler profiler, CrashReport report, CallbackInfo ci) {
        ServerConfigurationManager playerList = server.getConfigurationManager();
        EntityPlayerMP player = (EntityPlayerMP) playerList.playerEntityList.get(0);
        int viewDistance = playerList.getViewDistance() * 16; // Distance in blocks
        int playerX = MathHelper.floor_double(player.posX) >> 4;
        int playerZ = MathHelper.floor_double(player.posZ) >> 4;
        for (int x = playerX - viewDistance; x <= playerX + viewDistance; x++)
            for (int z = playerZ - viewDistance; z <= playerZ + viewDistance; z++) {
                Chunk chunk = provider.worldObj.getChunkFromChunkCoords(x, z);
                if (!chunk.isTerrainPopulated && chunk.isChunkLoaded) {
                    chunksToPopulate.offer(chunk);
                }
            }
        }
    }
