package fr.iamacat.multithreading.mixins.common.core;

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

    private Thread worldTickThread;
    private Thread entityTickThread;
    private boolean running;

    @Inject(method = "run", at = @At("HEAD"))
    private void startThreads(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinMinecraftServer) {
            running = true;

            // Start the world tick thread
            worldTickThread = new Thread(this::worldTickLoop, "WorldTickThread");
            worldTickThread.start();

            // Start the entity tick thread
            entityTickThread = new Thread(this::entityTickLoop, "EntityTickThread");
            entityTickThread.start();
        }
    }

    private void worldTickLoop() {
        while (running) {
            for (WorldServer world : worldServers) {
                MultithreadingLogger.LOGGER.debug("World tick loop - {}", world.provider.getDimensionName());
                world.tick();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                MultithreadingLogger.LOGGER.error("World tick loop interrupted", e);
                Thread.currentThread()
                    .interrupt();
            }
        }
    }

    private void entityTickLoop() {
        while (running) {
            for (WorldServer world : worldServers) {
                MultithreadingLogger.LOGGER.debug("Entity tick loop - {}", world.provider.getDimensionName());
                world.updateEntities();
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                MultithreadingLogger.LOGGER.error("Entity tick loop interrupted", e);
                Thread.currentThread()
                    .interrupt();
            }
        }
    }
}
