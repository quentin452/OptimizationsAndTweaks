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

    private AsyncPathCaches() {}

    /** Drop all state for an entity (called on death). */
    public static void purge(int entityId) {
        cachedPaths.remove(entityId);
        pendingPaths.remove(entityId);
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
