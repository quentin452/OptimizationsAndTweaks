package fr.iamacat.optimizationsandtweaks.devtools;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-entity ring buffer of AI/pathfinding events, for observing decisions at the mixin level
 * instead of inferring them from position/speed (which conflates knockback, wander and panic).
 *
 * <p>
 * Recording is a no-op unless an entity id has been {@link #watch(int) watched} (via the matoulib
 * RPC {@code /aitrace}), so there is zero cost in production. The trace mixins that feed this are
 * only applied when {@code -Doat.aitrace=true} is set, so the class is dormant otherwise.
 *
 * <p>
 * Read back by reflection (no compile dependency) from matoulib's devtools RPC.
 */
public final class AiEventTrace {

    private AiEventTrace() {}

    private static final int CAP = 400;
    private static final Map<Integer, Deque<String>> LOG = new ConcurrentHashMap<>();

    /** Start recording events for this entity id (idempotent). */
    public static void watch(int entityId) {
        LOG.computeIfAbsent(entityId, k -> new ArrayDeque<>());
    }

    public static boolean watched(int entityId) {
        return LOG.containsKey(entityId);
    }

    /** Append an event line; no-op if the id is not watched. */
    public static void record(int entityId, String event) {
        Deque<String> q = LOG.get(entityId);
        if (q == null) {
            return;
        }
        synchronized (q) {
            q.addLast(System.nanoTime() + " " + event);
            while (q.size() > CAP) {
                q.removeFirst();
            }
        }
    }

    /** Snapshot the recorded events for an id (empty if not watched). */
    public static List<String> dump(int entityId) {
        Deque<String> q = LOG.get(entityId);
        if (q == null) {
            return Collections.emptyList();
        }
        synchronized (q) {
            return new ArrayList<>(q);
        }
    }

    public static void clear(int entityId) {
        Deque<String> q = LOG.get(entityId);
        if (q != null) {
            synchronized (q) {
                q.clear();
            }
        }
    }
}
