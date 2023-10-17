package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(BlockLeavesBase.class)
public abstract class MixinLeafDecay {

    @Unique
    private ThreadPoolExecutor multithreadingandtweaks$executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksConfig.numberofcpus,
        MultithreadingandtweaksConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>());

    @Unique
    private final int multithreadingandtweaks$batchSize = MultithreadingandtweaksConfig.batchsize;

    @Inject(method = "removeLeaves", at = @At("RETURN"))
    private void onRemoveLeaves(World world, int x, int y, int z, CallbackInfo ci) {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        if (MultithreadingandtweaksConfig.enableMixinLeafDecay) {
            int numLeaves = 0;
            for (int i = 0; i < 16; i++) {
                for (int k = 0; k < 16; k++) {
                    int j = 0;
                    while (j < 128) {
                        Block block = chunk.getBlock(i, j, k);
                        if (block != null && block.isLeaves(world, i, j, k)) {
                            numLeaves++;
                            if (numLeaves % multithreadingandtweaks$batchSize == 0) {
                                processLeavesBatch(world);
                            }
                            int finalI = i;
                            int finalJ = j;
                            int finalK = k;
                            multithreadingandtweaks$executorService.execute(
                                () -> multithreadingandtweaks$processLeafBlock(
                                    new BlockPos(chunk.xPosition * 16 + finalI, finalJ, chunk.zPosition * 16 + finalK),
                                    world,
                                    true));
                        }
                        j++;
                    }
                }
            }
            processLeavesBatch(world);
        }
    }

    private void processLeavesBatch(World world) {
        multithreadingandtweaks$executorService.shutdown();
        try {
            multithreadingandtweaks$executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        multithreadingandtweaks$executorService = new ThreadPoolExecutor(
            MultithreadingandtweaksConfig.numberofcpus,
            MultithreadingandtweaksConfig.numberofcpus,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());
    }

    @Unique
    private void multithreadingandtweaks$processLeafBlock(BlockPos pos, World world, boolean isInitial) {
        if (multithreadingandtweaks$shouldDecay(world, pos)) {
            world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
            if (isInitial) {
                multithreadingandtweaks$getNeighborPositions(pos, new ArrayList<>()).forEach(neighbor -> {
                    Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                    if (neighborBlock instanceof BlockLeaves) {
                        multithreadingandtweaks$executorService
                            .execute(() -> multithreadingandtweaks$processLeafBlock(neighbor, world, false));
                    }
                });
            } else {
                multithreadingandtweaks$getNeighborPositions(pos, new ArrayList<>()).forEach(neighbor -> {
                    Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                    if (neighborBlock instanceof BlockLeaves) {
                        multithreadingandtweaks$processLeafBlock(neighbor, world, false);
                    }
                });
            }
        }
    }

    @Unique
    public List<BlockPos> multithreadingandtweaks$getNeighborPositions(BlockPos pos, List<BlockPos> neighborPositions) {
        for (BlockPos neighbor : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (!neighbor.equals(pos)) {
                neighborPositions.add(new BlockPos(neighbor));
            }
        }
        return neighborPositions;
    }

    @Unique
    private boolean multithreadingandtweaks$shouldDecay(World world, BlockPos pos) {
        Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        if (block == null || !(block instanceof BlockLeaves)) {
            return false;
        }
        int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
        return (meta & 4) == 0;
    }
}
