package fr.iamacat.multithreading.math.fastrandom;

import java.io.Serializable;

/**
 * Well Equidistributed Long-period Linear (WELL) generator with a state size of
 * 512 bits and a period of 2<sup>512</sup> - 1. See "Improved Long-Period
 * Generators Based on Linear Recurrences Modulo 2" by L'Ecuyer, Matsumoto, and
 * Panneton.
 */
public class Well512a extends AbstractFastRandom implements FastRandom, Serializable {
    private static final long serialVersionUID = -178962713019714243L;

    private static final int R  = 16;
    private static final int M1 = 13;
    private static final int M2 = 9;

    private final int[] s;
    private int si;

    /** Constructs a random number generator. */
    public Well512a() {
        this(Utils.getSeed());
    }

    /** Constructs a random number generator with the specified seed. */
    public Well512a(long seed) {
        s = new int[R];
        setSeed(seed);
    }

    @Override
    public void setSeed(long seed) {
        clearGaussian();
        si = 0;
        /* At least one element in the state array must be nonzero. */
        s[0] = (int) (seed >> 32);
        s[1] = (int) seed;
        long next = seed;
        for (int i = 2; i < R; ++i) {
            next = Utils.lcg(next);
            s[i] = (int) (next >> 32);
        }
    }

    @Override
    protected int next(int bits) {
        int z1 = s[si];
        int z2 = s[(si + M1) & 0xf];
        z1 = (z1 ^ (z1 << 16)) ^ (z2 ^ (z2 << 15));
        z2 = s[(si + M2) & 0xf];
        z2 ^= z2 >>> 11;
        int v = s[si] = z1 ^ z2;
        si = (si + 15) & 0xf;
        int z0 = s[si];
        v = s[si] = (z0 ^ (z0 << 2)) ^ (z1 ^ (z1 << 18)) ^ (z2 << 28) ^ (v ^ ((v << 5) & 0xda442d24));
        return v >>> (32 - bits);
    }
}
