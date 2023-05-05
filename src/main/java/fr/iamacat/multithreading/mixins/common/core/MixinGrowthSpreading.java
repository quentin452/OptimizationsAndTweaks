package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockReed;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = World.class, priority = 998)
public abstract class MixinGrowthSpreading {

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Growth-Spreading-%d")
            .build());

    private int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;

    private PriorityQueue<BlockPos> growthQueue = new PriorityQueue<>(1000, Comparator.comparingInt(BlockPos::getY));

    // Define a getter method for the world object
    public World getWorld() {
        return (World) (Object) this;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinGrowthSpreading
            && getWorld().getTotalWorldTime() % 10 == 0) {
                growthQueue.clear();
            } else {
                addBlocksToGrowthQueue();
                processBlocksInGrowthQueue();
            }
        }

    private void addBlocksToGrowthQueue() {
        for (int x = -64; x <= 64; x++) {
            for (int z = -64; z <= 64; z++) {
                for (int y = 0; y <= 255; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = getWorld().getBlock(pos.getX(), pos.getY(), pos.getZ());
                    if (block instanceof BlockCrops || block instanceof BlockReed) {
                        growthQueue.add(pos);
                    }
                }
            }
        }
    }

    // Process blocks in growth queue using executor service
    private void processBlocksInGrowthQueue() {
        ExecutorService executor = Executors
            .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);
        while (!growthQueue.isEmpty()) {
            List<BlockPos> batch = new ArrayList<>();
            int batchSize = Math.min(growthQueue.size(), this.batchSize);
            for (int i = 0; i < batchSize; i++) {
                batch.add(growthQueue.poll());
            }
            if (!batch.isEmpty()) {
                executor.submit(() -> {
                    batch.forEach(pos -> {
                        Block block = getWorld().getBlock(pos.getX(), pos.getY(), pos.getZ());
                        getWorld().markBlockForUpdate(pos.getX(), pos.getY(), pos.getZ());
                        getWorld().notifyBlockOfNeighborChange(pos.getX(), pos.getY(), pos.getZ(), block);

                    });
                });
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // Handle interruption
        }
    }
}
