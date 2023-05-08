package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = BlockLeavesBase.class, priority = 900)
public abstract class MixinLeafDecay {

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>());

    // private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    @Inject(method = "updateLeaves", at = @At("RETURN"))
    private void onUpdateLeaves(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinLeafDecay) {
            return;
        }

        // Add leaf blocks that need to decay to the queue
        List<Chunk> loadedChunks = ((ChunkProviderServer) world.getChunkProvider()).loadedChunks;
        for (Chunk chunk : loadedChunks) {
            List<BlockPos> chunkBlocks = new ArrayList<>();
            for (int i = chunk.xPosition * 16; i < chunk.xPosition * 16 + 16; i++) {
                for (int j = 0; j < 256; j++) {
                    for (int k = chunk.zPosition * 16; k < chunk.zPosition * 16 + 16; k++) {
                        Block block = world.getBlock(i, j, k);
                        if (block != null && block.isLeaves(world, i, j, k)) {
                            chunkBlocks.add(new BlockPos(i, j, k));
                        }
                    }
                }
            }
            processLeafBlocks(chunkBlocks, world);
        }
    }

    public void processLeafBlocks(List<BlockPos> batch, World world) {
        List<BlockPos> neighborPositions = new ArrayList<>();
        for (BlockPos pos : batch) {
            try {
                if (shouldDecay(pos, world)) {
                    world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                    getNeighborPositions(pos, neighborPositions);
                    for (BlockPos neighbor : neighborPositions) {
                        Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                        if (neighborBlock instanceof BlockLeaves) {
                            executorService.execute(() -> processLeafBlock(neighbor, world));
                        }
                    }
                }
            } catch (Exception e) {
                LogManager.getLogger()
                    .error("Failed to process leaf block at " + pos, e);
            }
            neighborPositions.clear();
        }
    }

    public void processLeafBlock(BlockPos pos, World world) {
        List<BlockPos> neighborPositions = new ArrayList<>();
        try {
            if (shouldDecay(pos, world)) {
                world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                getNeighborPositions(pos, neighborPositions);
                for (BlockPos neighbor : neighborPositions) {
                    Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                    if (neighborBlock instanceof BlockLeaves) {
                        executorService.execute(() -> processLeafBlock(neighbor, world));
                    }
                }
            }
        } catch (Exception e) {
            LogManager.getLogger()
                .error("Failed to process leaf block at " + pos, e);
        }
    }

    public List<BlockPos> getNeighborPositions(BlockPos pos, List<BlockPos> neighborPositions) {
        List<BlockPos> neighbors = new ArrayList<>();
        for (BlockPos neighbor : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (!neighbor.equals(pos)) {
                neighbors.add(new BlockPos(neighbor));
            }
        }
        return neighbors;
    }

    private boolean shouldDecay(BlockPos pos, World world) {
        Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        if (block == null || !(block instanceof BlockLeaves)) {
            return false;
        }
        int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
        return (meta & 4) == 0;
    }
}
