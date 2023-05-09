package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinBlockCHunkUpdate {

    private static int batchsize = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private static final ExecutorService THREAD_POOL = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    @Inject(method = "updateBlocks", at = @At("HEAD"))
    private void onServerTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinBlockCHunkUpdate) {
            World world = (World) (Object) this;
            List<Chunk> chunksToTick = new ArrayList<>(
                world.getChunkProvider()
                    .getLoadedChunkCount());
            for (int i = 0; i < chunksToTick.size(); i += batchsize) {
                final int start = i;
                final int end = Math.min(i + batchsize, chunksToTick.size());
                THREAD_POOL.execute(() -> tickChunks(world, chunksToTick.subList(start, end)));
            }
        }
    }

    private void tickChunks(World world, List<Chunk> chunks) {
        int numBatches = (int) Math.ceil(chunks.size() / (double) batchsize);
        List<List<Chunk>> batches = new ArrayList<>(numBatches);

        for (int i = 0; i < numBatches; i++) {
            int start = i * batchsize;
            int end = Math.min(start + batchsize, chunks.size());
            batches.add(chunks.subList(start, end));
        }

        for (List<Chunk> batch : batches) {
            THREAD_POOL.execute(() -> tickChunkBatch(world, batch));
        }

        // Unload queued chunks after all the batches have been ticked
        world.getChunkProvider()
            .unloadQueuedChunks();
    }

    private void tickChunkBatch(World world, List<Chunk> chunks) {
        for (Chunk chunk : chunks) {
            ChunkCoordIntPair chunkPos = chunk.getChunkCoordIntPair();
            chunk.setChunkModified();

            // Iterate over every block in the chunk and update it
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block != null) {
                            block.updateTick(
                                world,
                                x + chunkPos.chunkXPos * 16,
                                y,
                                z + chunkPos.chunkZPos * 16,
                                world.rand);
                        }
                    }
                }
            }

            // Save changes to the chunk
            chunk.onChunkUnload();
            ((IChunkProvider) world.getChunkProvider()).saveChunks(true, null);
        }
    }
}
