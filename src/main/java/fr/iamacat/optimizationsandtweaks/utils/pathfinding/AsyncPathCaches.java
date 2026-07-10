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

    /**
     * entityId -> progress toward an unreachable target across successive partial paths (issue #49). The plain
     * null-result backoff only fires once a mob is <i>already sitting</i> on the closest reachable node (the native
     * A* then returns a single-node path -> null -> {@code onFailure}). In a dense pile-up a stuck mob is jostled
     * every tick, so it never sits still: each request yields a fresh multi-node <i>partial</i> path that reaches
     * nowhere near the goal, the completion path clears the verdict, and the flood resumes. We track whether the
     * mob is actually closing distance on the target; when it stops making progress on a non-reaching path we start
     * the backoff even though no clean null ever arrived. A far but <i>reachable</i> target keeps closing the
     * distance (vanilla staged approach via maxDistance), so it is never flagged.
     */
    public static final Map<Integer, Approach> approachTracker = new ConcurrentHashMap<>();

    /** How long a "no path" verdict suppresses re-pathing to the same target. */
    private static final long NO_PATH_COOLDOWN_MS = 2000L;
    /** Squared distance the target must move to invalidate the verdict (the goal may have become reachable). */
    private static final double NO_PATH_TARGET_MOVE_SQ = 4.0; // 2.0^2
    /** How much closer (squared blocks) the mob must get to the target to count as progress, not jitter. */
    private static final double PROGRESS_EPS_SQ = 1.0; // ~1.0 block
    /** Consecutive no-progress partial paths before a mob is judged stuck and backed off. */
    private static final int STUCK_STRIKES = 2;

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
        approachTracker.remove(entityId);
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

    /**
     * Feed the outcome of a completed <b>partial</b> path (one that did not reach the target) and report whether the
     * mob should now back off (issue #49). Returns {@code true} when the mob has failed to close the distance to
     * (roughly) this target on {@link #STUCK_STRIKES} successive attempts — it is stuck against an unreachable goal.
     * A moved target resets the tracker; any attempt that closes the distance by more than {@link #PROGRESS_EPS_SQ}
     * resets the strike count (a far but reachable target is walked toward in stages and never trips this).
     *
     * @param entityToTargetSq squared distance from the mob's CURRENT position to the target
     */
    public static boolean noteUnreachedAndCheckStuck(int entityId, double targetX, double targetY, double targetZ,
        double entityToTargetSq, long now) {
        Approach a = approachTracker.get(entityId);
        if (a == null || movedTooFar(a.targetX - targetX, a.targetY - targetY, a.targetZ - targetZ)) {
            approachTracker.put(entityId, new Approach(targetX, targetY, targetZ, entityToTargetSq, now));
            return false;
        }
        if (entityToTargetSq < a.bestDistSq - PROGRESS_EPS_SQ) {
            // Closed the distance since the last attempt: still approaching a reachable goal, keep pathing.
            a.bestDistSq = entityToTargetSq;
            a.strikes = 0;
            a.timestamp = now;
            return false;
        }
        a.strikes++;
        a.timestamp = now;
        return a.strikes >= STUCK_STRIKES;
    }

    /** Drop the approach tracker for {@code entityId} (target reached, or target changed). */
    public static void clearApproach(int entityId) {
        approachTracker.remove(entityId);
    }

    private static boolean movedTooFar(double dx, double dy, double dz) {
        return dx * dx + dy * dy + dz * dz > NO_PATH_TARGET_MOVE_SQ;
    }

    /**
     * Per-entity record of the best distance reached toward a stubborn target, and the failed-approach strike count.
     */
    public static final class Approach {

        final double targetX, targetY, targetZ;
        double bestDistSq;
        int strikes;
        long timestamp;

        Approach(double targetX, double targetY, double targetZ, double bestDistSq, long timestamp) {
            this.targetX = targetX;
            this.targetY = targetY;
            this.targetZ = targetZ;
            this.bestDistSq = bestDistSq;
            this.strikes = 0;
            this.timestamp = timestamp;
        }
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
        // approachTracker has no expiry-on-read (unlike noPathBackoff), so age it out here to stay bounded
        // for mobs that vanished without a death event (despawn, chunk unload).
        Iterator<Map.Entry<Integer, Approach>> at = approachTracker.entrySet()
            .iterator();
        while (at.hasNext()) {
            if (now - at.next()
                .getValue().timestamp > maxAgeMs) {
                at.remove();
            }
        }
    }
}
