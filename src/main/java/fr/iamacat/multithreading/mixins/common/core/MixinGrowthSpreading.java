package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockReed;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinGrowthSpreading {

    @Unique
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("Growth-Spreading-%d")
            .build());
    @Unique
    private World world;
    @Unique
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    @Unique
    private final Queue<BlockPos> growthQueue = new ConcurrentLinkedQueue<>();

    @Unique
    public abstract Block getBlock(int x, int y, int z);

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        this.world = world; // assign world parameter to instance variable
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinGrowthSpreading) {
            growthQueue.clear();
        } else {
            addBlocksToGrowthQueue();
            processBlocksInGrowthQueue();
        }
    }

    @Unique
    private void addBlocksToGrowthQueue() {
        for (int x = -64; x <= 64; x++) {
            for (int z = -64; z <= 64; z++) {
                for (int y = 0; y <= 255; y++) {
                    Block block = getBlock(x, y, z);
                    if (block instanceof BlockCrops || block instanceof BlockReed) {
                        growthQueue.add(new BlockPos(x, y, z).toImmutable());
                    }
                }
            }
        }
    }

    @Unique
    private void processBlocksInGrowthQueue() {
        List<BlockPos> batch = new ArrayList<>(BATCH_SIZE);
        while (!growthQueue.isEmpty()) {
            int batchSize = Math.min(growthQueue.size(), BATCH_SIZE);
            for (int i = 0; i < batchSize; i++) {
                batch.add(growthQueue.poll());
            }
            if (!batch.isEmpty()) {
                EXECUTOR.submit(() -> {
                    batch.forEach(pos -> { Block block = getBlock(pos.getX(), pos.getY(), pos.getZ()); });
                    batch.clear();
                });
            }
        }
    }

}
