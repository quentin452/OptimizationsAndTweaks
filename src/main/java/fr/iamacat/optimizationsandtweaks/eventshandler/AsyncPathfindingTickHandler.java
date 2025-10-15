package fr.iamacat.optimizationsandtweaks.eventshandler;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;

/**
 * Event handler to poll async pathfinding results every tick
 * This ensures callbacks are executed on the main game thread
 */
public class AsyncPathfindingTickHandler {
    
    private int tickCounter = 0;
    private static final int STATS_LOG_INTERVAL = 1200; // Log stats every 60 seconds (20 ticks/sec * 60)
    
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Poll for completed pathfinding results
            if (AsyncPathfindingExecutor.isInitialized()) {
                AsyncPathfindingExecutor.pollResults();
                
                // Periodically log statistics
                tickCounter++;
                if (tickCounter >= STATS_LOG_INTERVAL) {
                    tickCounter = 0;
                    logStatistics();
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Poll for completed pathfinding results on client side too
            if (AsyncPathfindingExecutor.isInitialized()) {
                AsyncPathfindingExecutor.pollResults();
            }
        }
    }
    
    private void logStatistics() {
        if (AsyncPathfindingExecutor.isInitialized()) {
            int[] stats = AsyncPathfindingExecutor.getStatistics();
            
            // Only log if there's been activity
            if (stats[0] > 0) { // total_submitted > 0
                FMLLog.info("[AsyncPathfinding] %s", AsyncPathfindingExecutor.getStatisticsString());
                
                // Warn if queue is getting full
                int queueSize = stats[4];
                int workerCount = stats[6];
                int maxQueueSize = workerCount * 4;
                
                if (queueSize > maxQueueSize * 0.8) {
                    FMLLog.warning(
                        "[AsyncPathfinding] Queue nearly full: %d/%d (%.1f%%) - Consider increasing worker count",
                        queueSize, maxQueueSize, (queueSize * 100.0 / maxQueueSize)
                    );
                }
                
                // Warn if many failures
                int totalSubmitted = stats[0];
                int totalFailed = stats[2];
                if (totalSubmitted > 100 && totalFailed > totalSubmitted * 0.1) {
                    FMLLog.warning(
                        "[AsyncPathfinding] High failure rate: %d/%d (%.1f%%)",
                        totalFailed, totalSubmitted, (totalFailed * 100.0 / totalSubmitted)
                    );
                }
            }
        }
    }
}
