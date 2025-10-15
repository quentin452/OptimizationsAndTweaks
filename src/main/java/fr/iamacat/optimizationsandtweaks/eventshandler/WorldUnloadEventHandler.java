package fr.iamacat.optimizationsandtweaks.eventshandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.StartupQueryState;
import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;
import fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfindingBridge;

public class WorldUnloadEventHandler {

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        // Reset any startup query state
        StartupQueryState.resetConfirmation();

        // Stop async pathfinding workers and clear global/native caches
        try { AsyncPathfindingExecutor.shutdown(); } catch (Throwable ignored) {}
        try { RustPathfindingBridge.clearGlobalBlockCache(); } catch (Throwable ignored) {}
        try { RustPathfindingBridge.clearPathResultCache(); } catch (Throwable ignored) {}

        // Clear any Java-side pending/cached paths and native pathfinder handles via reflection
        try { clearAsyncPathfindingCaches(); } catch (Throwable ignored) {}
    }

    private void clearAsyncPathfindingCaches() throws Exception {
        // Clear MixinPathFinder static maps (pending and cached paths)
        try {
            Class<?> mixinCls = Class.forName("fr.iamacat.optimizationsandtweaks.mixins.common.core.MixinPathFinder");
            java.lang.reflect.Field fPending = mixinCls.getDeclaredField("optimizationsAndTweaks$pendingPaths");
            java.lang.reflect.Field fCached = mixinCls.getDeclaredField("optimizationsAndTweaks$cachedPaths");
            fPending.setAccessible(true);
            fCached.setAccessible(true);
            Object pending = fPending.get(null);
            Object cached = fCached.get(null);
            if (pending instanceof java.util.Map) {
                ((java.util.Map<?, ?>) pending).clear();
            }
            if (cached instanceof java.util.Map) {
                ((java.util.Map<?, ?>) cached).clear();
            }
        } catch (Throwable ignored) {}

        // Clear RustPathfindingBridge PATHFINDER_CACHE (drop per-entity handles)
        try {
            Class<?> bridgeCls = Class.forName("fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfindingBridge");
            java.lang.reflect.Field fCache = bridgeCls.getDeclaredField("PATHFINDER_CACHE");
            fCache.setAccessible(true);
            Object cache = fCache.get(null);
            if (cache instanceof java.util.Map) {
                ((java.util.Map<?, ?>) cache).clear();
            }
        } catch (Throwable ignored) {}
    }
}
