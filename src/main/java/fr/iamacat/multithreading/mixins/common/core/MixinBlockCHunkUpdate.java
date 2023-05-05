package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinBlockCHunkUpdate {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private static final ExecutorService THREAD_POOL = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    @Inject(method = "updateEntities", at = @At("HEAD"))
    private void onServerTick(CallbackInfo callbackInfo) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinBlockCHunkUpdate) {
            World world = (World) (Object) this;
            List<Chunk> chunksToTick = new ArrayList<>(
                world.getChunkProvider()
                    .getLoadedChunkCount());
            for (int i = 0; i < chunksToTick.size(); i += BATCH_SIZE) {
                final int start = i;
                final int end = Math.min(i + BATCH_SIZE, chunksToTick.size());
                THREAD_POOL.execute(() -> tickChunks(world, chunksToTick.subList(start, end)));
            }
        }
    }

    private void tickChunks(World world, List<Chunk> chunks) {
        for (Chunk chunk : chunks) {
            chunk.setChunkModified();
            ChunkCoordIntPair chunkCoords = chunk.getChunkCoordIntPair();
            world.getChunkProvider()
                .unloadQueuedChunks();
            world.theProfiler.startSection("chunkIOWrite");
            world.getChunkProvider()
                .saveChunks(true, null);
            world.theProfiler.endSection();
            world.theProfiler.startSection("chunkIORead");
            world.getChunkProvider()
                .loadChunk(chunkCoords.chunkXPos, chunkCoords.chunkZPos);
            world.theProfiler.endSection();
            for (int x = chunk.xPosition * 16; x < chunk.xPosition * 16 + 16; x++) {
                for (int z = chunk.zPosition * 16; z < chunk.zPosition * 16 + 16; z++) {
                    for (int y = 0; y < world.getHeight(); y++) {
                        Block block = world.getBlock(x, y, z);
                        if (block != null) {
                            block.updateTick(world, x, y, z, world.rand);
                        }
                    }
                }
            }
        }
    }

}
