package fr.iamacat.optimizationsandtweaks.utils.natives;

import cpw.mods.fml.common.FMLLog;

/**
 * Java wrapper for Rust pathfinding functions using JNI
 * Provides high-performance pathfinding implementation in Rust
 */
public class RustPathfinding {

    private static boolean available = false;

    // Native method declarations - implemented in Rust
    private static native long createPathFinder(boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed,
        boolean isPathingInWater, boolean canEntityDrown);

    private static native void destroyPathEntity(long handle);

    private static native int pathEntityGetCurrentIndex(long handle);

    private static native void pathEntitySetCurrentIndex(long handle, int index);

    private static native int pathEntityGetLength(long handle);

    private static native boolean pathEntityIsFinished(long handle);

    private static native int[] pathEntityGetPoint(long handle, int index);

    private static native int[] pathEntityGetAllPoints(long handle);

    // Direct pathfinding with no cache: calls into Java for world queries
    public static native long findPathDirect(long pathfinderHandle, Object worldAdapter, double entityX, double entityY,
        double entityZ, double targetX, double targetY, double targetZ, float entityWidth, float entityHeight,
        float maxDistance, boolean isInWater, int maxSafePointTries);

    // Alias for findPathDirect - uses direct world access with on-demand block encoding in Rust
    public static long findPathDirectWorld(long pathfinderHandle, Object worldAdapter, double entityX, double entityY,
        double entityZ, double targetX, double targetY, double targetZ, float entityWidth, float entityHeight,
        float maxDistance, boolean isInWater, int maxSafePointTries) {
        return findPathDirect(
            pathfinderHandle,
            worldAdapter,
            entityX,
            entityY,
            entityZ,
            targetX,
            targetY,
            targetZ,
            entityWidth,
            entityHeight,
            maxDistance,
            isInWater,
            maxSafePointTries);
    }

    /**
     * Batch pathfinding - processes multiple pathfinding requests in parallel
     * 
     * @param pathfinderHandles Array of pathfinder handles
     * @param offsetX           Array of X offsets for block caches
     * @param offsetY           Array of Y offsets for block caches
     * @param offsetZ           Array of Z offsets for block caches
     * @param widths            Array of cache widths
     * @param heights           Array of cache heights
     * @param depths            Array of cache depths
     * @param blockCodes        Array of block code arrays
     * @param entityX           Array of entity X positions (as long bits)
     * @param entityY           Array of entity Y positions (as long bits)
     * @param entityZ           Array of entity Z positions (as long bits)
     * @param targetX           Array of target X positions (as long bits)
     * @param targetY           Array of target Y positions (as long bits)
     * @param targetZ           Array of target Z positions (as long bits)
     * @param entityWidths      Array of entity widths (as int bits)
     * @param entityHeights     Array of entity heights (as int bits)
     * @param maxDistances      Array of max distances (as int bits)
     * @param isInWater         Array of water status flags
     * @param maxSafePointTries Array of max safe point tries
     * @return Array of path entity handles (0 = no path found)
     */
    public static native long[] findPathBatch(long[] pathfinderHandles, int[] offsetX, int[] offsetY, int[] offsetZ,
        int[] widths, int[] heights, int[] depths, byte[][] blockCodes, long[] entityX, long[] entityY, long[] entityZ,
        long[] targetX, long[] targetY, long[] targetZ, int[] entityWidths, int[] entityHeights, int[] maxDistances,
        int[] isInWater, int[] maxSafePointTries);

    /** Enable/disable Rust profiler */
    public static native void setProfilerEnabled(boolean enabled);

    /** Query if Rust profiler is enabled */
    public static native boolean isProfilerEnabled();

    /** Clear collected profiler stats */
    public static native void clearProfilerStats();

    // ========== Async Pathfinding Executor Methods ==========

    /**
     * Initialize the async pathfinding executor
     */
    public static native void initAsyncExecutor(int workerCount, int queueSize);

    /**
     * Submit an async pathfinding request to the Rust executor
     */
    public static native long submitAsyncPathfinding(long requestId, int priority, boolean isWoodenDoorAllowed,
        boolean isMovementBlockAllowed, boolean isPathingInWater, boolean canEntityDrown, int offsetX, int offsetY,
        int offsetZ, int width, int height, int depth, byte[] blockCache, double entityX, double entityY,
        double entityZ, double targetX, double targetY, double targetZ, float entityWidth, float entityHeight,
        float maxDistance, boolean isInWater, int maxSafePointTries);

    /**
     * Try to receive a completed async pathfinding result (non-blocking)
     */
    public static native long tryRecvAsyncResult(int[] outRequestId);

    /**
     * Get async executor statistics
     */
    public static native int[] getAsyncExecutorStats();

    /**
     * Shutdown the async executor gracefully
     */
    public static native void shutdownAsyncExecutor();

    /**
     * Checks if Rust pathfinding is available
     * 
     * @return true if the native library is loaded and ready
     */
    public static boolean isAvailable() {
        return available && RustFFI.isInitialized();
    }

    /**
     * Initializes the Rust pathfinding system
     * Should be called after RustFFI.initialize()
     */
    public static void initialize() {
        if (!RustFFI.isInitialized()) {
            FMLLog.info("[OptimizationsAndTweaks] Cannot initialize Rust pathfinding - RustFFI not initialized");
            return;
        }
        available = true;
    }

    /**
     * Wrapper class for a native PathFinder instance
     */
    public static class PathFinderHandle implements AutoCloseable {

        private long handle;
        private boolean closed = false;

        public PathFinderHandle(boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed, boolean isPathingInWater,
            boolean canEntityDrown) {
            if (!isAvailable()) {
                throw new IllegalStateException("Rust pathfinding is not available");
            }
            this.handle = createPathFinder(
                isWoodenDoorAllowed,
                isMovementBlockAllowed,
                isPathingInWater,
                canEntityDrown);
        }

        public long getHandle() {
            if (closed) {
                throw new IllegalStateException("PathFinder has been closed");
            }
            return handle;
        }

        @Override
        public void close() {}

        @Override
        protected void finalize() throws Throwable {
            close();
            super.finalize();
        }
    }

    /**
     * Wrapper class for a native PathEntity instance
     */
    public static class PathEntityHandle implements AutoCloseable {

        private long handle;
        private boolean closed = false;

        public PathEntityHandle(long handle) {
            this.handle = handle;
        }

        public long getHandle() {
            if (closed) {
                throw new IllegalStateException("PathEntity has been closed");
            }
            return handle;
        }

        public int getCurrentIndex() {
            return pathEntityGetCurrentIndex(getHandle());
        }

        public void setCurrentIndex(int index) {
            pathEntitySetCurrentIndex(getHandle(), index);
        }

        public int getLength() {
            return pathEntityGetLength(getHandle());
        }

        public boolean isFinished() {
            return pathEntityIsFinished(getHandle());
        }

        public int[] getPoint(int index) {
            return pathEntityGetPoint(getHandle(), index);
        }

        public int[] getAllPoints() {
            return pathEntityGetAllPoints(getHandle());
        }

        @Override
        public void close() {
            if (!closed) {
                destroyPathEntity(handle);
                closed = true;
            }
        }

        @Override
        protected void finalize() throws Throwable {
            close();
            super.finalize();
        }
    }
}
