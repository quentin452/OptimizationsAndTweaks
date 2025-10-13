package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla;

import net.minecraft.entity.Entity;
import java.util.List;

public class CachedEntitySearch {
    public final List<Entity> entities;
    public final long timestamp;

    public CachedEntitySearch(List<Entity> entities, long timestamp) {
        this.entities = entities;
        this.timestamp = timestamp;
    }
}
