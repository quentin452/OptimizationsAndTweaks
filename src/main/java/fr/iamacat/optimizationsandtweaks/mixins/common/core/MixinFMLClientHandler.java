package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.GuiConfirmation;
import cpw.mods.fml.client.GuiNotification;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.StartupQuery;
import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FMLClientHandler.class)
public abstract class MixinFMLClientHandler  implements IFMLSidedHandler {
    // todo avoid the usage of Tread.sleep + Thread.interrupted
    private Minecraft client;

    @Override
    public void queryUser(StartupQuery query) throws InterruptedException
    {
        if (query.getResult() == null)
        {
            client.displayGuiScreen(new GuiNotification(query));
        }
        else
        {
            client.displayGuiScreen(new GuiConfirmation(query));
        }

        if (query.isSynchronous())
        {
            while (client.currentScreen instanceof GuiNotification)
            {
                if (Thread.interrupted()) throw new InterruptedException();

                client.loadingScreen.resetProgresAndWorkingMessage("");

                Thread.sleep(50);
            }

            client.loadingScreen.resetProgresAndWorkingMessage(""); // make sure the blank screen is being drawn at the end
        }
    }

}
