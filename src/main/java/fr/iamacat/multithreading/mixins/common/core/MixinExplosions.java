package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import fr.iamacat.multithreading.tasking.ExplosionTask;

@Mixin(World.class)
public abstract class MixinExplosions {

    @Unique
    private final ConcurrentLinkedDeque<ExplosionTask> explosionsToProcess = new ConcurrentLinkedDeque<>();
    @Unique
    private final ThreadPoolExecutor executorService;

    public MixinExplosions() {
        int numThreads = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
        executorService = new ThreadPoolExecutor(
            numThreads,
            numThreads,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("Explosion-Rendering-%d")
                .build());
    }

    @Inject(method = "newExplosion", at = @At("RETURN"))
    private void onNewExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming,
        boolean isSmoking, CallbackInfoReturnable<Explosion> ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinExplosions) {
            ExplosionTask task = new ExplosionTask(ci.getReturnValue(), entityIn.worldObj);
            explosionsToProcess.add(task);
            if (explosionsToProcess.size() == 1) {
                executorService.submit(this::processExplosions);
            }
        }
    }

    @Unique
    private void processExplosions() {
        while (!explosionsToProcess.isEmpty()) {
            List<ExplosionTask> batch = new ArrayList<>();
            ExplosionTask task = explosionsToProcess.poll();
            while (task != null) {
                batch.add(task);
                task = explosionsToProcess.poll();
            }
            List<CompletableFuture<Void>> futures = batch.stream()
                .map(t -> CompletableFuture.runAsync(t::renderExplosion, executorService))
                .collect(Collectors.toList());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .join();
        }
    }

}
