package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = World.class, priority = 902)
public abstract class MixinEntityUpdate {

    private final ThreadPoolExecutor executorService;
    private final int numberOfCPUs;
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final AtomicReference<World> world = new AtomicReference<>((World) (Object) this);
    private final LinkedBlockingQueue<Entity> entitiesToUpdate = new LinkedBlockingQueue<>();

    protected MixinEntityUpdate() {
        int corePoolSize = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
        int maxPoolSize = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
        long keepAliveTime = 60L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new SynchronousQueue<>();
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Entity-Update-%d")
            .build();
        executorService = new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            threadFactory);
        numberOfCPUs = corePoolSize;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        World World = (World) (Object) this;
        world.set(World);
        addEntitiesToUpdateQueue(World.loadedEntityList);
    }

    private void addEntitiesToUpdateQueue(Collection<Entity> entities) {
        entitiesToUpdate.addAll(entities);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            AtomicInteger indexCounter = new AtomicInteger(0);
            entitiesToUpdate.parallelStream()
                .collect(Collectors.groupingByConcurrent(
                    entity -> (int) Math.floor(indexCounter.getAndIncrement() / (double) MAX_ENTITIES_PER_TICK)))
                .values()
                .parallelStream()
                .forEach(batch -> {
                    batch.parallelStream()
                        .forEach(entity -> {
                            try {
                                if (entity != null) {
                                    executorService.submit(entity::onEntityUpdate);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                });
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        executorService.shutdown();
    }
}
