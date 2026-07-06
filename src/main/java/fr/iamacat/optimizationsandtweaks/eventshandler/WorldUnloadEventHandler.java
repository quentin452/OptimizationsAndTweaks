package fr.iamacat.optimizationsandtweaks.eventshandler;

import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.StartupQueryState;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.AsyncPathCaches;

public class WorldUnloadEventHandler {

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        // Reset any startup query state
        StartupQueryState.resetConfirmation();

        // Stop the async pathfinding executor and drop any Java-side pending/cached paths.
        try {
            AsyncPathfindingExecutor.shutdown();
        } catch (Throwable ignored) {}
        try {
            AsyncPathCaches.cachedPaths.clear();
            AsyncPathCaches.pendingPaths.clear();
        } catch (Throwable ignored) {}
    }
}
