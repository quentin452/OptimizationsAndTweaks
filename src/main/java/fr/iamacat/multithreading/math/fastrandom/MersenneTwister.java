/*
 * Copyright (C) 1997 - 2002, Makoto Matsumoto and Takuji Nishimura,
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The names of its contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package fr.iamacat.multithreading.math.fastrandom;

import java.io.Serializable;

/**
 * Implements MT19937, a generator with a period of 2<sup>19937</sup> - 1. For
 * more information, see "Mersenne Twister: A 623-dimensionally equidistributed
 * uniform pseudorandom generator" by Matsumoto and Nishimura.
 */
public class MersenneTwister extends AbstractFastRandom implements FastRandom, Serializable {

    private static final long serialVersionUID = -7746671748906043888L;

    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = 0x9908b0df;
    private static final int UPPER_MASK = 0x80000000;
    private static final int LOWER_MASK = 0x7fffffff;

    private final int[] mt;
    private int mti;

    /** Constructs a random number generator. */
    public MersenneTwister() {
        this(Utils.getSeed());
    }

    /** Constructs a random number generator seeded by an {@code int}. */
    public MersenneTwister(int seed) {
        mt = new int[N];
        setSeed(seed);
    }

    /** Constructs a random number generator seeded by a {@code long}. */
    public MersenneTwister(long seed) {
        mt = new int[N];
        setSeed(seed);
    }

    /**
     * Constructs a random number generator seeded by an {@code int} array.
     *
     * @throws NullPointerException     if seed is {@code null}
     * @throws IllegalArgumentException if seed is empty
     */
    public MersenneTwister(int[] seed) {
        mt = new int[N];
        setSeed(seed);
    }

    /** Seeds this generator with an {@code int}. */
    public void setSeed(int seed) {
        clearGaussian();
        mt[0] = seed;
        for (mti = 1; mti < N; ++mti) {
            mt[mti] = 1812433253 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti;
        }
    }

    @Override
    public void setSeed(long seed) {
        setSeed(new int[] { (int) (seed >> 32), (int) seed });
    }

    /**
     * Seeds this generator with an {@code int} array.
     *
     * @throws NullPointerException     if seed is {@code null}
     * @throws IllegalArgumentException if seed is empty
     */
    public void setSeed(int[] seed) {
        if (seed.length == 0) {
            throw new IllegalArgumentException("Empty seed array");
        }
        setSeed(19650218);
        int i = 1, j = 0;
        for (int k = Math.max(seed.length, N); k > 0; --k) {
            mt[i] = (mt[i] ^ (mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1664525) + seed[j] + j;
            ++i;
            ++j;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
            if (j >= seed.length) {
                j = 0;
            }
        }
        for (int k = N - 1; k > 0; --k) {
            mt[i] = (mt[i] ^ (mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1566083941) - i;
            ++i;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
        }
        mt[0] = 0x80000000;
    }

    @Override
    protected int next(int bits) {
        int y;
        if (mti >= N) {
            int[] mag01 = { 0, MATRIX_A };
            for (int i = 0; i < N - M; ++i) {
                y = (mt[i] & UPPER_MASK) | (mt[i + 1] & LOWER_MASK);
                mt[i] = mt[i + M] ^ (y >>> 1) ^ mag01[y & 1];
            }
            for (int i = N - M; i < N - 1; ++i) {
                y = (mt[i] & UPPER_MASK) | (mt[i + 1] & LOWER_MASK);
                mt[i] = mt[i + (M - N)] ^ (y >>> 1) ^ mag01[y & 1];
            }
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 1];
            mti = 0;
        }
        y = mt[mti++];
        y ^= y >>> 11;
        y ^= (y << 7) & 0x9d2c5680;
        y ^= (y << 15) & 0xefc60000;
        y ^= y >>> 18;
        return y >>> (32 - bits);
    }
}
