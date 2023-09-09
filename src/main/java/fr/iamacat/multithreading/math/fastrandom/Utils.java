package fr.iamacat.multithreading.math.fastrandom;

import java.util.concurrent.atomic.AtomicLong;

public final class Utils {

    private static final AtomicLong seed = new AtomicLong();

    private Utils() {}

    /** Returns (ax + b) mod p. */
    public static long lcg(long x) {
        return x * 6364136223846793005L + 1442695040888963407L;
    }

    /** Returns a pseudorandomly generated seed. */
    public static long getSeed() {
        long current, next;
        do {
            current = seed.get();
            next = lcg(current);
        } while (!seed.compareAndSet(current, next));
        return next ^ System.nanoTime();
    }
}
