package fr.iamacat.optimizationsandtweaks.utils.pathfinding;

import net.minecraft.pathfinding.PathEntity;

public class CachedPath {
    final PathEntity path;
    final double entityX, entityY, entityZ;
    final double targetX, targetY, targetZ;
    final long timestamp;

    public CachedPath(PathEntity path, double entityX, double entityY, double entityZ,
                double targetX, double targetY, double targetZ, long timestamp) {
        this.path = path;
        this.entityX = entityX;
        this.entityY = entityY;
        this.entityZ = entityZ;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.timestamp = timestamp;
    }

    public PathEntity getPath() {
        return path;
    }

    /**
     * Check if cached path is still valid
     * Cache expires after 0.6 second or if entity/target moved significantly
     */
    public boolean isValid(double curEntityX, double curEntityY, double curEntityZ,
                    double curTargetX, double curTargetY, double curTargetZ,
                    long currentTime) {
        // Cache expires after 0.6 second (13 ticks)
        if (currentTime - timestamp > 600) {
            return false;
        }

        // Check if entity moved significantly (more than 0.25 blocks)
        double entityDist = distanceSquared(entityX, entityY, entityZ, curEntityX, curEntityY, curEntityZ);
        if (entityDist > 0.36) { // ~0.6^2
            return false;
        }

        // Check if target moved significantly (more than 1.0 blocks)
        double targetDist = distanceSquared(targetX, targetY, targetZ, curTargetX, curTargetY, curTargetZ);
        if (targetDist > 1.44) { // ~1.2^2
            return false;
        }

        return true;
    }

    private static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return dx * dx + dy * dy + dz * dz;
    }
}
