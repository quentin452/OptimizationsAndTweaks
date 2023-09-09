package fr.iamacat.multithreading.math.fastrandom;

import java.io.Serializable;

/**
 * A combined linear feedback shift register with a period of ~2<sup>88</sup>.
 * Adapted from code found in "Maximally Equidistributed Combined Tausworthe
 * Generators" by L'Ecuyer.
 */
public class Taus88 extends AbstractFastRandom implements FastRandom, Serializable {

    private static final long serialVersionUID = 3042624850227558320L;

    private int s0, s1, s2;

    /** Constructs a random number generator. */
    public Taus88() {
        this(Utils.getSeed());
    }

    /** Constructs a random number generator with the specified seed. */
    public Taus88(long seed) {
        setSeed(seed);
    }

    @Override
    public void setSeed(long seed) {
        clearGaussian();
        /* Upper 31 bits must not be all zero. */
        s0 = (int) seed;
        if ((s0 & 0xffffffffL) < 2) {
            s0 += 2;
        }
        /* Upper 29 bits must not be all zero. */
        s1 = (int) (seed >> 32);
        if ((s1 & 0xffffffffL) < 8) {
            s1 += 8;
        }
        /* Upper 28 bits must not be all zero. */
        s2 = (int) (Utils.lcg(seed) >> 32);
        if ((s2 & 0xffffffffL) < 16) {
            s2 += 16;
        }
    }

    @Override
    protected int next(int bits) {
        s0 = ((s0 & -2) << 12) ^ (((s0 << 13) ^ s0) >>> 19);
        s1 = ((s1 & -8) << 4) ^ (((s1 << 2) ^ s1) >>> 25);
        s2 = ((s2 & -16) << 17) ^ (((s2 << 3) ^ s2) >>> 11);
        return (s0 ^ s1 ^ s2) >>> (32 - bits);
    }
}
