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

@Mixin(World.class)
public abstract class MixinEntityUpdate {
    private final ExecutorService executorService;
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final AtomicReference<World> world = new AtomicReference<>((World) (Object) this);
    private final List<Entity> entitiesToUpdate = Collections.synchronizedList(new ArrayList<>());

    public MixinEntityUpdate() {
        int poolSize = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
        executorService = Executors.newFixedThreadPool(poolSize, new ThreadFactoryBuilder().setNameFormat("Entity-Update-%d").build());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        World world = (World) (Object) this;
        this.world.set(world);
        addEntitiesToUpdateQueue(world.loadedEntityList);
    }

    private synchronized void addEntitiesToUpdateQueue(Collection<Entity> entities) {
        entitiesToUpdate.addAll(entities);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            List<Entity> entityList = new ArrayList<>(entitiesToUpdate);
            entitiesToUpdate.clear();
            int numBatches = (int) Math.ceil((double) entityList.size() / MAX_ENTITIES_PER_TICK);
            for (int i = 0; i < numBatches; i++) {
                int start = i * MAX_ENTITIES_PER_TICK;
                int end = Math.min(start + MAX_ENTITIES_PER_TICK, entityList.size());
                List<Entity> batch = entityList.subList(start, end);
                CompletableFuture.allOf(batch.stream().map(entity ->
                            CompletableFuture.runAsync(entity::onEntityUpdate, executorService))
                        .toArray(CompletableFuture[]::new))
                    .join();
            }
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        executorService.shutdown();
    }
}

