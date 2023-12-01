package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.command.ServerCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.StartupQuery;
import cpw.mods.fml.common.functions.GenericIterableFactory;
import cpw.mods.fml.server.FMLServerHandler;

@Mixin(FMLServerHandler.class)
public abstract class MixinFMLServerHandler implements IFMLSidedHandler {

    @Shadow
    private MinecraftServer server;

    @Override
    public void queryUser(StartupQuery query) throws InterruptedException {
        if (query.getResult() == null) {
            FMLLog.warning("%s", query.getText());
            query.finish();
        } else {
            String text = query.getText() + "\n\nRun the command /fml confirm or /fml cancel to proceed."
                + "\nAlternatively start the server with -Dfml.queryResult=confirm or -Dfml.queryResult=cancel to preselect the answer.";
            FMLLog.warning("%s", text);

            if (!query.isSynchronous()) return;

            AtomicBoolean done = new AtomicBoolean(false);

            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                while (!done.get() && server.isServerRunning()) {
                    if (Thread.interrupted()) {
                        query.finish();
                        return;
                    }

                    DedicatedServer dedServer = (DedicatedServer) server;

                    synchronized (dedServer.pendingCommandList) {
                        for (Iterator<ServerCommand> it = GenericIterableFactory
                            .newCastingIterable(dedServer.pendingCommandList, ServerCommand.class)
                            .iterator(); it.hasNext();) {
                            String cmd = it.next().command.trim()
                                .toLowerCase();

                            if (cmd.equals("/fml confirm")) {
                                FMLLog.info("confirmed");
                                query.setResult(true);
                                done.set(true);
                                it.remove();
                            } else if (cmd.equals("/fml cancel")) {
                                FMLLog.info("cancelled");
                                query.setResult(false);
                                done.set(true);
                                it.remove();
                            } else if (cmd.equals("/stop")) {
                                StartupQuery.abort();
                            }
                        }
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        query.finish();
                    }
                }
            });

            try {
                completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            query.finish();
        }
    }
}
