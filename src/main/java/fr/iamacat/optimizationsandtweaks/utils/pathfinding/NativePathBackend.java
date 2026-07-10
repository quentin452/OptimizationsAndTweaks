package fr.iamacat.optimizationsandtweaks.utils.pathfinding;

import net.minecraft.pathfinding.PathEntity;

import fr.iamacat.exec.ExecutionBackend;
import fr.iamacat.exec.Handle;
import fr.iamacat.exec.Job;
import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;

/**
 * The matoulib {@link ExecutionBackend} (hub doc 28) for async pathfinding: the FFI Rust executor behind
 * the seam interface. It does NOT run {@link Job#compute} on the JVM — the A* is computed by the native
 * worker pool ({@link AsyncPathfindingExecutor}), which owns the queue, priority scheduling and result
 * channel. This backend adapts that native runtime to the seam lifecycle: {@link #submit} enqueues the
 * request and returns a {@link Handle}; {@link #drainAndApply} runs on the server tick and applies the
 * completed results through {@link Job#apply}/{@link Job#isValid}.
 *
 * <p>
 * Only {@link PathfindingJob} + {@link PathSnapshot} may be submitted — this is a pathfinding-specialised
 * backend, not a general-purpose one. Because the compute is native, the {@code serial == parallel}
 * determinism guarantee that covers JVM backends does not apply here (documented in hub doc 28).
 */
public final class NativePathBackend implements ExecutionBackend {

    private static final NativePathBackend INSTANCE = new NativePathBackend();

    private NativePathBackend() {}

    public static NativePathBackend get() {
        return INSTANCE;
    }

    /**
     * Enqueue the request against its immutable {@link PathSnapshot} on the native executor.
     *
     * @return a {@link PathHandle} on acceptance, or {@code null} if the request was declined (native
     *         queue full / executor unavailable) — the caller falls back to a synchronous vanilla path.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S, R> Handle<R> submit(Job<S, R> job, S snapshot) {
        PathfindingJob pathJob = (PathfindingJob) job;
        PathSnapshot snap = (PathSnapshot) snapshot;

        // Bridge the native poll's success/failure split onto Job.apply: a found path applies the result,
        // a "no path" applies null. Both run on the server tick inside pollResults().
        final long requestId;
        if (snap.slot != null) {
            requestId = AsyncPathfindingExecutor.submitSnapshotPathfindingDirect(
                pathJob.entity(),
                snap.priority,
                snap.isWoodenDoorAllowed,
                snap.isMovementBlockAllowed,
                snap.isPathingInWater,
                snap.canEntityDrown,
                snap.offsetX,
                snap.offsetY,
                snap.offsetZ,
                snap.width,
                snap.height,
                snap.depth,
                snap.slot,
                snap.targetX,
                snap.targetY,
                snap.targetZ,
                snap.maxDistance,
                pathJob::apply,
                reason -> pathJob.apply(null));
        } else {
            requestId = AsyncPathfindingExecutor.submitSnapshotPathfinding(
                pathJob.entity(),
                snap.priority,
                snap.isWoodenDoorAllowed,
                snap.isMovementBlockAllowed,
                snap.isPathingInWater,
                snap.canEntityDrown,
                snap.offsetX,
                snap.offsetY,
                snap.offsetZ,
                snap.width,
                snap.height,
                snap.depth,
                snap.heapCache,
                snap.targetX,
                snap.targetY,
                snap.targetZ,
                snap.maxDistance,
                pathJob::apply,
                reason -> pathJob.apply(null));
        }

        if (requestId == 0) {
            return null;
        }
        return (Handle<R>) new PathHandle(pathJob, requestId);
    }

    /**
     * Drain the native result channel on the server tick and apply completed paths through
     * {@link Job#apply}. The {@code budgetNanos} bound is not enforced here: the native executor already
     * caps its queue, so a poll drains a bounded batch (the historical behaviour is unbounded per tick).
     */
    @Override
    public void drainAndApply(long budgetNanos) {
        AsyncPathfindingExecutor.pollResults();
    }

    @Override
    public void shutdown() {
        AsyncPathfindingExecutor.shutdown();
    }

    /** Lifecycle handle over a native request id. */
    public static final class PathHandle implements Handle<PathEntity> {

        private final PathfindingJob job;
        private final long requestId;
        private volatile boolean cancelled;

        PathHandle(PathfindingJob job, long requestId) {
            this.job = job;
            this.requestId = requestId;
        }

        /** The native executor's request id (opaque token; non-zero on an accepted request). */
        public long requestId() {
            return requestId;
        }

        @Override
        public boolean isDone() {
            // The native executor owns completion; treat a cancelled handle as done, otherwise unknown.
            return cancelled;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean cancel() {
            if (cancelled) {
                return false;
            }
            cancelled = true;
            AsyncPathfindingExecutor.cancelRequest(requestId);
            return true;
        }

        @Override
        public boolean isValid() {
            return job.isValid();
        }
    }
}
