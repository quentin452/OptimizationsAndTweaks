package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.GuiConfirmation;
import cpw.mods.fml.client.GuiNotification;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.StartupQuery;

@Mixin(FMLClientHandler.class)
public abstract class MixinFMLClientHandler implements IFMLSidedHandler {

    @Shadow
    private Minecraft client;
    @Unique
    private final Object guiLock = new Object();

    @Override
    public void queryUser(StartupQuery query) throws InterruptedException {
        if (query.getResult() == null) {
            client.displayGuiScreen(new GuiNotification(query));
        } else {
            client.displayGuiScreen(new GuiConfirmation(query));
        }

        if (query.isSynchronous()) {
            CompletableFuture<Void> waitForGui = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                synchronized (guiLock) {
                    while (client.currentScreen instanceof GuiNotification) {
                        client.loadingScreen.resetProgresAndWorkingMessage("");
                        try {
                            guiLock.wait(50);
                        } catch (InterruptedException e) {
                            waitForGui.completeExceptionally(e);
                            return;
                        }
                    }
                }
                waitForGui.complete(null);
            });

            try {
                waitForGui.join();
            } catch (CompletionException e) {
                if (e.getCause() instanceof InterruptedException) {
                    throw (InterruptedException) e.getCause();
                }
            }

            client.loadingScreen.resetProgresAndWorkingMessage("");
        }
    }
}
