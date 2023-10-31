package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.StartupQuery;
import cpw.mods.fml.common.functions.GenericIterableFactory;
import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.command.ServerCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

@Mixin(FMLServerHandler.class)
public abstract class MixinFMLServerHandler implements IFMLSidedHandler {
    // todo avoid the usage of Tread.sleep + Thread.interrupted

    @Shadow
    private MinecraftServer server;
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void queryUser(StartupQuery query) throws InterruptedException
    {
        if (query.getResult() == null)
        {
            FMLLog.warning("%s", query.getText());
            query.finish();
        }
        else
        {
            String text = query.getText() +
                "\n\nRun the command /fml confirm or or /fml cancel to proceed." +
                "\nAlternatively start the server with -Dfml.queryResult=confirm or -Dfml.queryResult=cancel to preselect the answer.";
            FMLLog.warning("%s", text);

            if (!query.isSynchronous()) return; // no-op until mc does commands in another thread (if ever)

            boolean done = false;

            while (!done && server.isServerRunning())
            {
                if (Thread.interrupted()) throw new InterruptedException();

                DedicatedServer dedServer = (DedicatedServer) server;

                // rudimentary command processing, check for fml confirm/cancel and stop commands
                synchronized (dedServer.pendingCommandList)
                {
                    for (Iterator<ServerCommand> it = GenericIterableFactory.newCastingIterable(dedServer.pendingCommandList, ServerCommand.class).iterator(); it.hasNext(); )
                    {
                        String cmd = it.next().command.trim().toLowerCase();

                        if (cmd.equals("/fml confirm"))
                        {
                            FMLLog.info("confirmed");
                            query.setResult(true);
                            done = true;
                            it.remove();
                        }
                        else if (cmd.equals("/fml cancel"))
                        {
                            FMLLog.info("cancelled");
                            query.setResult(false);
                            done = true;
                            it.remove();
                        }
                        else if (cmd.equals("/stop"))
                        {
                            StartupQuery.abort();
                        }
                    }
                }

                Thread.sleep(10L);
            }

            query.finish();
        }
    }
}
