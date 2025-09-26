package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.tidychunkbackport;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.item.EntityItem;

public class PlayerDroppedItemTracker {
    
    private static final Set<EntityItem> playerDroppedItems = ConcurrentHashMap.newKeySet();
    
    public static void markAsPlayerDropped(EntityItem item) {
        if (item != null) {
            playerDroppedItems.add(item);
        }
    }

    public static boolean isPlayerDropped(EntityItem item) {
        return item != null && playerDroppedItems.contains(item);
    }
 
    public static void untrackItem(EntityItem item) {
        if (item != null) {
            playerDroppedItems.remove(item);
        }
    }
    
    public static void clearAll() {
        playerDroppedItems.clear();
    }
    
    public static int getTrackedItemCount() {
        return playerDroppedItems.size();
    }
}