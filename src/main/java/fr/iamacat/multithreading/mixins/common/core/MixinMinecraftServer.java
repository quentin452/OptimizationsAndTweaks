package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
                world.tick();
            }

            try {
                // Adjust the sleep duration as needed
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void entityTickLoop() {
        while (running) {
            for (WorldServer world : worldServers) {
                world.updateEntities();
            }

            try {
                // Adjust the sleep duration as needed
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
