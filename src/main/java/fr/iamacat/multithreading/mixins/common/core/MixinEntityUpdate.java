package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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
        private int numberOfCPUs = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
        private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
        private final AtomicReference<World> world = new AtomicReference<>((World) (Object) this);
        private final ArrayList<Entity> entitiesToUpdate = new ArrayList<>();

        protected MixinEntityUpdate() {
            int corePoolSize = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
            int maxPoolSize = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
            long keepAliveTime = 60L;
            TimeUnit unit = TimeUnit.SECONDS;
            BlockingQueue<Runnable> workQueue = new SynchronousQueue<>();
            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Entity-Update-%d").build();
            executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory);
            numberOfCPUs = corePoolSize;
        }

        @Inject(method = "<init>", at = @At("RETURN"))
        private void onInit(CallbackInfo ci) {
            World World = (World) (Object) this;
            world.set(World);
            addEntitiesToUpdateQueue(World.loadedEntityList);
        }

        private synchronized void addEntitiesToUpdateQueue(Collection<Entity> entities) {
            entitiesToUpdate.addAll(entities);
        }

        @Inject(method = "tick", at = @At("HEAD"))
        private void onTick(CallbackInfo ci) {
            if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
                int numBatches = (entitiesToUpdate.size() + MAX_ENTITIES_PER_TICK - 1) / MAX_ENTITIES_PER_TICK; // round up
                // division
                List<List<Entity>> batches = new ArrayList<>(numBatches);
                for (int i = 0; i < numBatches; i++) {
                    List<Entity> batch = new ArrayList<>(MAX_ENTITIES_PER_TICK);
                    for (int j = 0; j < MAX_ENTITIES_PER_TICK && i * MAX_ENTITIES_PER_TICK + j < entitiesToUpdate.size(); j++) {
                        batch.add(entitiesToUpdate.get(i * MAX_ENTITIES_PER_TICK + j));
                    }
                    batches.add(batch);
                }

                for (List<Entity> batch : batches) {
                    executorService.execute(() -> {
                        for (Entity entity : batch) {
                            try {
                                if (entity != null) {
                                    entity.onEntityUpdate();
                                }
                            } catch (Exception e) {
                                // Handle the exception in a specific way, such as logging the error and continuing with the
                                // program.
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }

        @Inject(method = "close", at = @At("HEAD"))
        private void onClose(CallbackInfo ci) {
            executorService.shutdown();
        }
    }
