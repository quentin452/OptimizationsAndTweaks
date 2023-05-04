package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(WorldServer.class)
public abstract class MixinEntitiesTick {
    public abstract List<Entity> getEntities();
    private final ExecutorService executorService = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("Entity-Tick-%d").build()
    );
    private final ConcurrentLinkedQueue<Entity> entityQueue = new ConcurrentLinkedQueue<>();

    @Inject(method = "updateEntities", at = @At("HEAD"))
    public void onPreUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesTick) {
            // Add entities directly to the entity queue
            entityQueue.addAll(getEntities());
        }
    }

    @Inject(method = "updateEntities", at = @At("RETURN"))
    public void onPostUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesTick) {
            // Submit entity ticking tasks to the executor service
            int numThreads = Math.min(entityQueue.size(), MultithreadingandtweaksMultithreadingConfig.numberofcpus);
            List<Thread> threads = new ArrayList<>(numThreads);
            for (int i = 0; i < numThreads; i++) {
                Thread thread = new Thread(() -> {
                    while (true) {
                        Entity entity = entityQueue.poll();
                        if (entity == null) {
                            break;
                        }
                        try {
                            if (entity.isEntityAlive()) {
                                entity.onUpdate();
                            }
                        } catch (Throwable t) {
                            // Catch and print any exceptions thrown during entity ticking
                            t.printStackTrace();
                        }
                    }
                });
                thread.start();
                threads.add(thread);
            }
            // Wait for all threads to finish
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    // Handle exception appropriately
                }
            }
        }
    }
}

