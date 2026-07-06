package fr.iamacat.optimizationsandtweaks.eventshandler;

import net.minecraftforge.event.entity.living.LivingDeathEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.AsyncPathCaches;

/**
 * Promptly drops async pathfinding state for entities that die. Despawns and chunk unloads
 * (which fire no death event) are covered by the periodic sweep in
 * {@link AsyncPathfindingTickHandler}.
 */
public class AsyncPathCleanupHandler {

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entityLiving != null) {
            AsyncPathCaches.purge(event.entityLiving.getEntityId());
        }
    }
}
