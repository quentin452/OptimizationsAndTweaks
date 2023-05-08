package fr.iamacat.multithreading.mixins.common.core;

import java.util.concurrent.*;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(Entity.class)
public abstract class MixinEntitiesTick {

    private final ExecutorService executorService = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("Entity-Tick-%d")
            .build());

    private CopyOnWriteArrayList<Entity> entityList = new CopyOnWriteArrayList<>();

    @Inject(method = "updateEntities", at = @At("HEAD"))
    public void onPreUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesTick) {
            // Save a copy of the entity list to avoid concurrent modification
        }
    }

    @Inject(method = "updateEntities", at = @At("RETURN"))
    public void onPostUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesTick) {
            // Submit entity ticking tasks to the executor service
            for (Entity entity : entityList) {
                executorService.submit(() -> {
                    try {
                        if (entity.isEntityAlive()) {
                            entity.onUpdate();
                        }
                    } catch (Throwable t) {
                        // Catch and print any exceptions thrown during entity ticking
                        t.printStackTrace();
                    }
                });
            }
        }
    }
}
