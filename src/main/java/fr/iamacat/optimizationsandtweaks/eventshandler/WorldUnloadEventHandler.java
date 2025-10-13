package fr.iamacat.optimizationsandtweaks.eventshandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.StartupQueryState;

public class WorldUnloadEventHandler {

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        StartupQueryState.resetConfirmation();
    }
}
