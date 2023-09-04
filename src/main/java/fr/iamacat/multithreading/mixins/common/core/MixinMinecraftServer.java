package fr.iamacat.multithreading.mixins.common.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.MultithreadingLogger;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Shadow
    private WorldServer[] worldServers;

    private final ExecutorService worldTickExecutor = Executors.newFixedThreadPool(worldServers.length);
    private final ExecutorService entityTickExecutor = Executors.newFixedThreadPool(worldServers.length);

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinMinecraftServer) {
            // Start the world tick threads after worldServers is fully initialized
            for (WorldServer world : worldServers) {
                worldTickExecutor.submit(() -> worldTickLoop(world));
                entityTickExecutor.submit(() -> entityTickLoop(world));
            }
        }
    }

    private void worldTickLoop(WorldServer world) {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (worldTickExecutor) {
                try {
                    MultithreadingLogger.LOGGER.info("World tick loop - {}", world.provider.getDimensionName());
                    world.tick();
                    worldTickExecutor.wait(50); // Adjust the wait duration as needed
                } catch (InterruptedException e) {
                    MultithreadingLogger.LOGGER.error("World tick loop interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void entityTickLoop(WorldServer world) {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (entityTickExecutor) {
                try {
                    MultithreadingLogger.LOGGER.info("Entity tick loop - {}", world.provider.getDimensionName());
                    world.updateEntities();
                    entityTickExecutor.wait(50); // Adjust the wait duration as needed
                } catch (InterruptedException e) {
                    MultithreadingLogger.LOGGER.error("Entity tick loop interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
