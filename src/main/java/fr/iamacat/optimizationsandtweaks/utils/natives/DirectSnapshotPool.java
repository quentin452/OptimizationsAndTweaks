package fr.iamacat.optimizationsandtweaks.utils.natives;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Pool of reused off-heap snapshot buffers for the zero-copy pathfinding submit path (PoC).
 *
 * <p>
 * Instead of allocating a fresh heap {@code byte[]} per request and letting JNI copy it
 * ({@code GetByteArrayRegion} on the Rust side), the region snapshot is encoded directly into a
 * pooled {@link ByteBuffer#allocateDirect direct} buffer whose native address Rust reads in place
 * ({@code GetDirectBufferAddress} + {@code slice::from_raw_parts}) — no copy, no pin.
 *
 * <p>
 * <b>Ownership / thread-handoff protocol</b> (the snapshot must stay immutable while Rust reads):
 * <ol>
 * <li>{@link #acquire()} on the <b>server thread</b> — slot leaves the free list, nobody else can
 * touch it.</li>
 * <li>{@link Slot#beginWrite()} bumps the slot generation and writes it into the 8-byte header;
 * the server thread then encodes the block region after the header.</li>
 * <li>{@code RustPathfinding.submitAsyncPathfindingDirect} hands the buffer + expected generation
 * to Rust. If accepted, the slot is registered in-flight ({@link #markInFlight}); from that point
 * the Java side <b>must not write the buffer</b> until the request's result is drained.</li>
 * <li>{@code AsyncPathfindingExecutor.pollResults()} (server tick) receives the result — Rust has
 * finished reading — and calls {@link #releaseByRequestId}, returning the slot to the free
 * list.</li>
 * </ol>
 * Acquire and release both happen on the server thread, so the free list needs no cross-thread
 * synchronization for correctness; methods are {@code synchronized} anyway as cheap, uncontended
 * insurance. The generation header lets the Rust worker detect a protocol violation (slot rewritten
 * mid-read): it re-checks the header generation after the A* run and discards the result on
 * mismatch. This is the single-buffer analogue of a seqlock — with correct pooling the generation
 * can never change mid-flight, so a mismatch always means a Java-side bug, never a benign race.
 *
 * <p>
 * A/B toggle: {@code -Doptimizationsandtweaks.pathfinding.directSnapshot=true} enables this path;
 * default is the legacy heap {@code byte[]} path, so both can be benched on the same build.
 * Pool exhaustion or a native library without the {@code submitAsyncPathfindingDirect} symbol
 * falls back to the legacy path transparently.
 */
public final class DirectSnapshotPool {

    /** Bytes reserved at offset 0 for the little/native-endian i64 generation header. */
    public static final int HEADER_BYTES = 8;

    /**
     * Maximum snapshot payload, matching {@code AsyncPathRequestDispatcher.MAX_VOLUME} (region
     * volume cap in blocks = bytes, one encoded byte per block).
     */
    public static final int MAX_SNAPSHOT_BYTES = 220_000;

    private static final int SLOT_CAPACITY = HEADER_BYTES + MAX_SNAPSHOT_BYTES;

    /** A/B switch: off by default so the legacy heap path is untouched unless explicitly benched. */
    private static final boolean ENABLED = Boolean.getBoolean("optimizationsandtweaks.pathfinding.directSnapshot");

    /**
     * Upper bound on pooled slots (~220 KB each; 64 slots = ~14 MB off-heap worst case). Slots are
     * allocated lazily, so an idle server pays nothing. Should comfortably exceed the native queue
     * size + worker count so exhaustion (→ legacy fallback) stays rare.
     */
    private static final int MAX_SLOTS = Integer.getInteger("optimizationsandtweaks.pathfinding.directSlots", 64);

    private static final ArrayDeque<Slot> freeSlots = new ArrayDeque<>();
    private static int allocatedSlots = 0;

    /** Slots currently owned by Rust, keyed by request id; released when the result is drained. */
    private static final ConcurrentHashMap<Long, Slot> inFlight = new ConcurrentHashMap<>();

    // Bench counters (read via statsString / logs during A/B runs).
    private static final AtomicLong directSubmits = new AtomicLong();
    private static final AtomicLong poolExhaustedCount = new AtomicLong();

    private DirectSnapshotPool() {}

    /** Whether the off-heap direct-snapshot path is enabled (sysprop, read once). */
    public static boolean isEnabled() {
        return ENABLED;
    }

    /**
     * Take a free slot, allocating lazily up to the cap. Returns {@code null} when the pool is
     * exhausted — the caller must fall back to the legacy heap path. Server thread only.
     */
    public static synchronized Slot acquire() {
        Slot slot = freeSlots.pollFirst();
        if (slot != null) {
            return slot;
        }
        if (allocatedSlots >= Math.max(1, MAX_SLOTS)) {
            poolExhaustedCount.incrementAndGet();
            return null;
        }
        allocatedSlots++;
        return new Slot(
            ByteBuffer.allocateDirect(SLOT_CAPACITY)
                .order(ByteOrder.nativeOrder()));
    }

    /** Register a slot as owned by Rust for {@code requestId}. Called after an accepted submit. */
    static void markInFlight(long requestId, Slot slot) {
        inFlight.put(requestId, slot);
        directSubmits.incrementAndGet();
    }

    /**
     * Return the slot tied to {@code requestId} (if any) to the free list. Called from the server
     * tick result drain — at that point Rust has finished reading the buffer. No-op for requests
     * that used the legacy heap path.
     */
    static void releaseByRequestId(long requestId) {
        Slot slot = inFlight.remove(requestId);
        if (slot != null) {
            release(slot);
        }
    }

    /** Return a slot that never made it in-flight (rejected submit, encode failure). */
    static synchronized void release(Slot slot) {
        freeSlots.addFirst(slot);
    }

    /**
     * Drop all slots (executor shutdown). Any in-flight buffers are abandoned to GC — the native
     * executor is shutting down with them, so nothing reads them anymore.
     */
    static synchronized void reset() {
        inFlight.clear();
        freeSlots.clear();
        allocatedSlots = 0;
    }

    /** Bench counters, logged at shutdown for A/B accounting. */
    public static String statsString() {
        return String.format(
            "DirectSnapshotPool - enabled: %s, directSubmits: %d, poolExhausted: %d, allocatedSlots: %d, inFlight: %d",
            ENABLED,
            directSubmits.get(),
            poolExhaustedCount.get(),
            allocatedSlots,
            inFlight.size());
    }

    /**
     * One reusable off-heap snapshot buffer. Layout (native byte order):
     *
     * <pre>
     * offset 0  : i64 generation (bumped by every beginWrite; Rust validates it)
     * offset 8  : block codes, layout [y][z][x] (x fastest) — same encoding as the legacy
     *             byte[] path, see RustPathfindingBridge.encodeBlockCache
     * </pre>
     */
    public static final class Slot {

        private final ByteBuffer buffer;
        private long generation = 0;

        Slot(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        /**
         * Start a new snapshot: bump + write the generation header and position the buffer at the
         * payload start. The caller encodes block codes with relative puts. Server thread only,
         * and only while the slot is NOT in-flight.
         */
        public ByteBuffer beginWrite() {
            generation++;
            buffer.clear();
            buffer.putLong(0, generation);
            buffer.position(HEADER_BYTES);
            return buffer;
        }

        /** Generation written by the last {@link #beginWrite()}; passed to Rust for validation. */
        public long generation() {
            return generation;
        }

        /** The pooled direct buffer (whole slot, header included). */
        public ByteBuffer buffer() {
            return buffer;
        }
    }
}
