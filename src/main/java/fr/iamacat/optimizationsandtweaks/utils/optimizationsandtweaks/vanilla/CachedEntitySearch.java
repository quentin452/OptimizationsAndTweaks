package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla;

import java.util.List;

import net.minecraft.entity.Entity;

public class CachedEntitySearch {

    public final List<Entity> entities;
    public final long timestamp;

    public CachedEntitySearch(List<Entity> entities, long timestamp) {
        this.entities = entities;
        this.timestamp = timestamp;
    }
}
