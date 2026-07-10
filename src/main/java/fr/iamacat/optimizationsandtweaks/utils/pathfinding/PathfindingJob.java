package fr.iamacat.optimizationsandtweaks.utils.pathfinding;

import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;

import fr.iamacat.exec.Job;

/**
 * The async pathfinding request expressed as a matoulib {@link Job} (hub doc 28): {@code compute} is the
 * A* over the immutable {@link PathSnapshot}, {@code apply} installs the resulting path on the server tick,
 * and {@code isValid} drops the result if the entity died in flight.
 *
 * <p>
 * <b>Native-only compute.</b> For pathfinding the {@code compute} runs in Rust (an off-thread worker pool
 * owned by the native executor), not in the JVM, so this job is routed exclusively through
 * {@link NativePathBackend}. It MUST NOT be submitted to {@code SerialBackend}/{@code WorkerPoolBackend}:
 * a serial backend would call {@link #compute} inline on the server tick and block it on the A*,
 * re-introducing the tick stall the async design exists to avoid — hence {@link #compute} throws. The
 * {@code serial == parallel} determinism guarantee therefore does not cover this native path (documented
 * limitation in hub doc 28).
 */
public final class PathfindingJob implements Job<PathSnapshot, PathEntity> {

    private final Entity entity;
    private final Consumer<PathEntity> onComplete;
    private final Consumer<String> onFailure;

    public PathfindingJob(Entity entity, Consumer<PathEntity> onComplete, Consumer<String> onFailure) {
        this.entity = entity;
        this.onComplete = onComplete;
        this.onFailure = onFailure;
    }

    public Entity entity() {
        return entity;
    }

    /**
     * Never called on the JVM — the A* is computed natively by the Rust worker pool behind
     * {@link NativePathBackend}. Throwing makes a mis-route to a JVM backend fail loudly instead of
     * silently blocking the server tick.
     */
    @Override
    public PathEntity compute(PathSnapshot snapshot) {
        throw new UnsupportedOperationException(
            "PathfindingJob is computed natively; submit it only through NativePathBackend, never a JVM ExecutionBackend");
    }

    /**
     * On the server tick: a non-null path is the found route (run the completion callback, which calls
     * {@code navigator.setPath}); a null result is the "no path" outcome (run the failure callback),
     * matching the native poll's success/failure split.
     */
    @Override
    public void apply(PathEntity result) {
        if (result != null) {
            if (onComplete != null) {
                onComplete.accept(result);
            }
        } else if (onFailure != null) {
            onFailure.accept("No path found");
        }
    }

    /** Skip applying a result whose entity vanished between submit and drain. */
    @Override
    public boolean isValid() {
        return entity != null && !entity.isDead;
    }
}
