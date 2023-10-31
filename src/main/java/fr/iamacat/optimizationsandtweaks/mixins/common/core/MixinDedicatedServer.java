package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.network.rcon.IServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;
import java.net.Proxy;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer  extends MinecraftServer implements IServer {
    // todo avoid the usage of Tread.sleep
    /**
     * @author
     * @reason
     */

    protected MixinDedicatedServer(File workDir)
    {
        super(workDir, Proxy.NO_PROXY);
        Thread thread = new Thread("Server Infinisleeper")
        {
            {
                this.setDaemon(true);
                this.start();
            }
            public void run()
            {
                while (true)
                {
                    try
                    {
                        while (true)
                        {
                            Thread.sleep(2147483647L);
                        }
                    }
                    catch (InterruptedException interruptedexception)
                    {
                        ;
                    }
                }
            }
        };
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void func_152369_aG()
    {
        try
        {
            Thread.sleep(5000L);
        }
        catch (InterruptedException interruptedexception)
        {
            ;
        }
    }
}
