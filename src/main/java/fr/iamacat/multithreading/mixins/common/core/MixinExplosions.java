package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.tasking.ExplosionTask;

@Mixin(World.class)
public abstract class MixinExplosions {

    @Unique
    private final Object explosionsLock = new Object();
    @Unique
    private final ConcurrentLinkedDeque<ExplosionTask> multithreadingandtweaks$explosionsToProcess = new ConcurrentLinkedDeque<>();
    @Unique
    private final ThreadPoolExecutor multithreadingandtweaks$executorService;

    public MixinExplosions() {
        int numThreads = MultithreadingandtweaksConfig.numberofcpus;
        multithreadingandtweaks$executorService = new ThreadPoolExecutor(
            numThreads,
            numThreads,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("Explosion-Thread-%d")
                .build());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        // Traitez les explosions de manière asynchrone pendant le tick
        synchronized (explosionsLock) {
            while (!multithreadingandtweaks$explosionsToProcess.isEmpty()) {
                List<ExplosionTask> batch = new ArrayList<>();
                ExplosionTask task = multithreadingandtweaks$explosionsToProcess.poll();
                while (task != null) {
                    batch.add(task);
                    task = multithreadingandtweaks$explosionsToProcess.poll();
                }
                List<CompletableFuture<Void>> futures = batch.stream()
                    .map(t -> CompletableFuture.runAsync(t::tickExplosion, multithreadingandtweaks$executorService))
                    .collect(Collectors.toList());
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .join();
            }

        }
    }
}
