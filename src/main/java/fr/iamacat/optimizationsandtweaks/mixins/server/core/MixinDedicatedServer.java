package fr.iamacat.optimizationsandtweaks.mixins.server.core;

import java.io.File;
import java.net.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import net.minecraft.network.rcon.IServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer extends MinecraftServer implements IServer {

    private long lastTime = System.nanoTime();

    protected MixinDedicatedServer(File workDir) {
        super(workDir, Proxy.NO_PROXY);

        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    CompletableFuture<Void> sleepFuture = new CompletableFuture<>();
                    sleepFuture.get();
                } catch (InterruptedException | ExecutionException e) {}
            }
        });
    }

    @Overwrite
    private void func_152369_aG() {
        long startTime = System.nanoTime();
        long delay = 5L * 1_000_000_000L;

        while (System.nanoTime() - startTime < delay) {}
    }
}
