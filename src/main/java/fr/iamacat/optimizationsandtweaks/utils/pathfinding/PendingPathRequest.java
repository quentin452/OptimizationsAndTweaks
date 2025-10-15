package fr.iamacat.optimizationsandtweaks.utils.pathfinding;

public class PendingPathRequest {
    final long requestId;
    final double targetX, targetY, targetZ;
    final long timestamp;

    public PendingPathRequest(long requestId, double targetX, double targetY, double targetZ, long timestamp) {
        this.requestId = requestId;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.timestamp = timestamp;
    }

    /**
     * Check if pending request is still valid
     * Request expires after 0.4 seconds or if target moved significantly
     */
    public boolean isStillValid(double curTargetX, double curTargetY, double curTargetZ, long currentTime) {
        // Request expires after 0.4 seconds
        if (currentTime - timestamp > 400) {
            return false;
        }

        // Check if target moved significantly (more than 2.0 blocks)
        double dx = curTargetX - targetX;
        double dy = curTargetY - targetY;
        double dz = curTargetZ - targetZ;
        double distSq = dx * dx + dy * dy + dz * dz;
        if (distSq > 4.0) { // 2.0^2
            return false;
        }

        return true;
    }
}