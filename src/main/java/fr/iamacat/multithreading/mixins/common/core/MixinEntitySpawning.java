package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.network.internal.EntitySpawnHandler;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(EntitySpawnHandler.class)
public abstract class MixinEntitySpawning {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final List<Entity> spawnQueue = new ArrayList<>();

    private final AtomicInteger batchSize = new AtomicInteger(BATCH_SIZE);
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        r -> new Thread(r, "Entity-Spawning-%d" + MixinEntitySpawning.this.hashCode()));

    public void close() {
        executorService.shutdown();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(World world, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitySpawning && !spawnQueue.isEmpty()) {
            executorService.submit(() -> spawnEntitiesInQueue(world));
        }
    }

    private void spawnEntitiesInQueue(World world) {
        List<Entity> entities = new ArrayList<>();
        int numEntities = batchSize.getAndSet(0);
        synchronized (spawnQueue) {
            for (Iterator<Entity> iterator = spawnQueue.iterator(); iterator.hasNext() && numEntities > 0;) {
                entities.add(iterator.next());
                iterator.remove();
                numEntities--;
            }
        }
        int numThreads = Math.min(MultithreadingandtweaksMultithreadingConfig.numberofcpus, entities.size());
        if (numThreads > 1) {
            List<List<Entity>> partitions = Lists.partition(entities, entities.size() / numThreads + 1);
            List<Future<?>> futures = new ArrayList<>(numThreads);
            ForkJoinPool pool = new ForkJoinPool(
                numThreads,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
                true);

            for (List<Entity> partition : partitions) {
                futures.add(pool.submit(() -> {
                    for (Entity entity : partition) {
                        world.spawnEntityInWorld(entity);
                    }
                }));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            for (Entity entity : entities) {
                world.spawnEntityInWorld(entity);
            }
        }
    }

    @Redirect(
        method = "renderEntities",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/Render;doRender(Lnet/minecraft/entity/Entity;DDDFF)V"))
    private void redirectDoRenderEntities(Render render, Entity entity, double x, double y, double z, float yaw,
        float partialTicks) {
        render.doRender(entity, x, y, z, yaw, partialTicks);
    }
}
