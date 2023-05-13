package fr.iamacat.multithreading.mixins.common.core;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Arrays;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    private CopyOnWriteArrayList<WorldServer> worldServers;

    private Thread worldTickThread;
    private Thread entityTickThread;
    private boolean running;

    @Inject(method = "func_71190_q", at = @At("RETURN"))
    private void captureWorldServers(CallbackInfo ci) {
        try {
            Field field = MinecraftServer.class.getDeclaredFields()[0];
            field.setAccessible(true);

            worldServers = new CopyOnWriteArrayList<>((List<WorldServer>) field.get(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            if (worldServers != null) {
                for (WorldServer world : worldServers) {
                    world.tick();
                }
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
            if (worldServers != null) {
                for (WorldServer world : worldServers) {
                    world.updateEntities();
                }
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
