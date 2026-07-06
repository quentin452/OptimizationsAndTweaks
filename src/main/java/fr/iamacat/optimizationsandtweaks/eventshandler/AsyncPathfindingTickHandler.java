package fr.iamacat.optimizationsandtweaks.eventshandler;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.AsyncPathCaches;

/**
 * Drains completed async pathfinding results on the <b>server tick only</b>.
 *
 * <p>
 * Polling exclusively on the server thread is a safety requirement: results carry callbacks
 * that mutate server-side entities ({@code navigator.setPath}). Polling on the client tick too
 * (as a previous version did) could dispatch a server entity's callback on the client thread of
 * an integrated server — an off-thread entity mutation race. Pathfinding is server-side in
 * 1.7.10, so there is nothing to poll on the client anyway.
 */
public class AsyncPathfindingTickHandler {

    private int tickCounter = 0;
    private static final int STATS_LOG_INTERVAL = 1200; // 60 s (20 ticks/s)

    private int sweepCounter = 0;
    private static final int SWEEP_INTERVAL = 200; // 10 s
    private static final long CACHE_MAX_AGE_MS = 30_000L;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!AsyncPathfindingExecutor.isInitialized()) {
            return;
        }

        AsyncPathfindingExecutor.pollResults();

        // Prune stale cached paths for entities that vanished without a death event.
        if (++sweepCounter >= SWEEP_INTERVAL) {
            sweepCounter = 0;
            AsyncPathCaches.sweep(System.currentTimeMillis(), CACHE_MAX_AGE_MS);
        }

        if (++tickCounter >= STATS_LOG_INTERVAL) {
            tickCounter = 0;
            logStatistics();
        }
    }

    private void logStatistics() {
        int[] stats = AsyncPathfindingExecutor.getStatistics();

        if (stats[0] > 0) { // total_submitted > 0
            FMLLog.info("[AsyncPathfinding] %s", AsyncPathfindingExecutor.getStatisticsString());

            int queueSize = stats[4];
            int workerCount = stats[6];
            int maxQueueSize = workerCount * 4;

            if (maxQueueSize > 0 && queueSize > maxQueueSize * 0.8) {
                FMLLog.warning(
                    "[AsyncPathfinding] Queue nearly full: %d/%d (%.1f%%) - Consider increasing worker count",
                    queueSize,
                    maxQueueSize,
                    (queueSize * 100.0 / maxQueueSize));
            }

            int totalSubmitted = stats[0];
            int totalFailed = stats[2];
            if (totalSubmitted > 100 && totalFailed > totalSubmitted * 0.1) {
                FMLLog.warning(
                    "[AsyncPathfinding] High failure rate: %d/%d (%.1f%%)",
                    totalFailed,
                    totalSubmitted,
                    (totalFailed * 100.0 / totalSubmitted));
            }
        }
    }
}
