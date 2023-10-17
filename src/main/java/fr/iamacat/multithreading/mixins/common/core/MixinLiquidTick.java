package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.block.BlockLiquid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(BlockLiquid.class)
public abstract class MixinLiquidTick {

    // todo // todo
    @Unique
    private final ThreadPoolExecutor multithreadingandtweaks$executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksConfig.numberofcpus,
        MultithreadingandtweaksConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Liquid-Tick-%d")
            .build());
}
