package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(BlockLeavesBase.class)
public abstract class MixinLeafDecay {

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>());

    @Inject(method = "removeLeaves", at = @At("RETURN"))
    private void onRemoveLeaves(World world, int x, int y, int z, CallbackInfo ci) {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        for (int i = 0; i < 16; i++) {
            for (int k = 0; k < 16; k++) {
                int j = 0;
                while (j < 128) {
                    Block block = chunk.getBlock(i, j, k);
                    if (block != null && block.isLeaves(world, i, j, k)) {
                        processLeafBlock(
                            new BlockPos(chunk.xPosition * 16 + i, j, chunk.zPosition * 16 + k),
                            world,
                            true);
                    }
                    j++;
                }
            }
        }
    }

    private void processLeafBlock(BlockPos pos, World world, boolean isInitial) {
        if (shouldDecay(world, pos)) {
            world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
            if (isInitial) {
                getNeighborPositions(pos, new ArrayList<>()).forEach(neighbor -> {
                    Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                    if (neighborBlock instanceof BlockLeaves) {
                        executorService.execute(() -> processLeafBlock(neighbor, world, false));
                    }
                });
            } else {
                getNeighborPositions(pos, new ArrayList<>()).forEach(neighbor -> {
                    Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                    if (neighborBlock instanceof BlockLeaves) {
                        processLeafBlock(neighbor, world, false);
                    }
                });
            }
        }
    }

    public List<BlockPos> getNeighborPositions(BlockPos pos, List<BlockPos> neighborPositions) {
        for (BlockPos neighbor : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (!neighbor.equals(pos)) {
                neighborPositions.add(new BlockPos(neighbor));
            }
        }
        return neighborPositions;
    }

    private boolean shouldDecay(World world, BlockPos pos) {
        Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        if (block == null || !(block instanceof BlockLeaves)) {
            return false;
        }
        int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
        return (meta & 4) == 0;
    }
}
