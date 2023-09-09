package fr.iamacat.multithreading.math.fastrandom;

/** Like {@link java.util.Random} but faster! */
public interface FastRandom {
    /** Seeds this random number generator. */
    void setSeed(long seed);

    /** Fills an array with random bytes. */
    void nextBytes(byte[] bytes);

    /** Returns uniformly distributed {@code int} values. */
    int nextInt();

    /** Returns uniformly distributed {@code int} values in the range [0, n). */
    int nextInt(int n);

    /** Returns uniformly distributed {@code long} values. */
    long nextLong();

    /** Returns uniformly distributed {@code boolean} values. */
    boolean nextBoolean();

    /** Returns uniformly distributed {@code float} values in the range [0, 1). */
    float nextFloat();

    /** Returns uniformly distributed {@code double} values in the range [0, 1). */
    double nextDouble();

    /** Returns {@code double} values from a standard normal distribution. */
    double nextGaussian();
}
