package fr.iamacat.optimizationsandtweaks.utils.pathfinding;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-entity async pathfinding state, kept outside the PathFinder mixin so it can be
 * purged on entity death/despawn (the mixin merges into vanilla PathFinder and its
 * private statics are not reachable from event handlers).
 *
 * All access happens on the server thread (submit from AI tick, result apply from the
 * server tick poll), so these maps are only concurrent as a defensive measure.
 */
public final class AsyncPathCaches {

    /** entityId -> last computed path (short-lived, see {@link CachedPath#isValid}). */
    public static final Map<Integer, CachedPath> cachedPaths = new ConcurrentHashMap<>();

    /** entityId -> in-flight async request. */
    public static final Map<Integer, PendingPathRequest> pendingPaths = new ConcurrentHashMap<>();

    /**
     * entityId -> last "no path found" verdict (unreachable-target backoff, issue #49). When a request comes back with
     * no path, re-pathing to (roughly) the same target every AI interval is wasted work: it floods the async queue and
     * churns the AI while the mob keeps failing to reach an unreachable goal (e.g. a player standing on a ledge the mob
     * cannot climb). We remember the verdict and skip re-submitting until it expires or the target moves.
     */
    public static final Map<Integer, NoPathVerdict> noPathBackoff = new ConcurrentHashMap<>();

    /** How long a "no path" verdict suppresses re-pathing to the same target. */
    private static final long NO_PATH_COOLDOWN_MS = 2000L;
    /** Squared distance the target must move to invalidate the verdict (the goal may have become reachable). */
    private static final double NO_PATH_TARGET_MOVE_SQ = 4.0; // 2.0^2

    /**
     * entityId -> speed the navigator was last asked to move at (panic 2.0, follow/flee > 1, wander
     * 1.0). Captured at {@code PathNavigate.setPath} so the async apply can restore the caller's
     * intended speed instead of a hardcoded 1.0 — otherwise every async-pathed mob walks at normal
     * speed and panic/flee never visibly sprints.
     */
    public static final Map<Integer, Double> requestedSpeed = new ConcurrentHashMap<>();

    private AsyncPathCaches() {}

    /** Drop all state for an entity (called on death). */
    public static void purge(int entityId) {
        cachedPaths.remove(entityId);
        pendingPaths.remove(entityId);
        requestedSpeed.remove(entityId);
        noPathBackoff.remove(entityId);
    }

    /**
     * Whether re-pathing for {@code entityId} toward this target should be skipped because a recent request already
     * returned no path (issue #49). True while the verdict is fresh AND the target has not moved significantly — a
     * moved target (or an expired verdict) re-opens pathing so a goal that became reachable is retried.
     */
    public static boolean isNoPathCooling(int entityId, double targetX, double targetY, double targetZ, long now) {
        NoPathVerdict v = noPathBackoff.get(entityId);
        if (v == null) {
            return false;
        }
        if (now - v.timestamp > NO_PATH_COOLDOWN_MS) {
            noPathBackoff.remove(entityId);
            return false;
        }
        double dx = targetX - v.targetX, dy = targetY - v.targetY, dz = targetZ - v.targetZ;
        if (dx * dx + dy * dy + dz * dz > NO_PATH_TARGET_MOVE_SQ) {
            noPathBackoff.remove(entityId);
            return false;
        }
        return true;
    }

    /** Record that pathing for {@code entityId} to this target found no path (starts the backoff window). */
    public static void recordNoPath(int entityId, double targetX, double targetY, double targetZ, long now) {
        noPathBackoff.put(entityId, new NoPathVerdict(targetX, targetY, targetZ, now));
    }

    /** Clear the backoff for {@code entityId} (a path was found — the target is reachable again). */
    public static void clearNoPath(int entityId) {
        noPathBackoff.remove(entityId);
    }

    /** A remembered "no path found" outcome for a target position. */
    public static final class NoPathVerdict {

        final double targetX, targetY, targetZ;
        final long timestamp;

        NoPathVerdict(double targetX, double targetY, double targetZ, long timestamp) {
            this.targetX = targetX;
            this.targetY = targetY;
            this.targetZ = targetZ;
            this.timestamp = timestamp;
        }
    }

    /**
     * Drop cached paths older than {@code maxAgeMs}. Catches entities that vanished
     * without a death event (despawn, chunk unload) so the maps stay bounded.
     */
    public static void sweep(long now, long maxAgeMs) {
        Iterator<Map.Entry<Integer, CachedPath>> it = cachedPaths.entrySet()
            .iterator();
        while (it.hasNext()) {
            if (now - it.next()
                .getValue().timestamp > maxAgeMs) {
                it.remove();
            }
        }
    }
}
