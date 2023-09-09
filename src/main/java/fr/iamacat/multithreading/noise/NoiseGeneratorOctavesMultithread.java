package fr.iamacat.multithreading.noise;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.NoiseGenerator;

public class NoiseGeneratorOctavesMultithread extends NoiseGenerator {
    // ATTENTION IT BREAK VANILLA SEED PARITY

    /** Collection of noise generation functions. Output is combined to produce different octaves of noise. */
    private NoiseGeneratorImprovedMultithread[] generatorCollection;
    private int octaves;
    private ExecutorService executor;

    public NoiseGeneratorOctavesMultithread(Random p_i2111_1_, int p_i2111_2_) {
        this.octaves = p_i2111_2_;
        this.generatorCollection = new NoiseGeneratorImprovedMultithread[p_i2111_2_];

        for (int j = 0; j < p_i2111_2_; ++j) {
            this.generatorCollection[j] = new NoiseGeneratorImprovedMultithread(p_i2111_1_);
        }

        // Create an executor with a fixed number of threads (you can adjust the number as needed)
        this.executor = Executors.newFixedThreadPool(
            Runtime.getRuntime()
                .availableProcessors());
    }

    /**
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    public double[] generateNoiseOctaves(double[] p_76304_1_, int p_76304_2_, int p_76304_3_, int p_76304_4_,
        int p_76304_5_, int p_76304_6_, int p_76304_7_, double p_76304_8_, double p_76304_10_, double p_76304_12_) {
        if (p_76304_1_ == null) {
            p_76304_1_ = new double[p_76304_5_ * p_76304_6_ * p_76304_7_];
        } else {
            for (int k1 = 0; k1 < p_76304_1_.length; ++k1) {
                p_76304_1_[k1] = 0.0D;
            }
        }

        double d6 = 1.0D;

        CompletableFuture<Void>[] futures = new CompletableFuture[octaves];

        for (int l1 = 0; l1 < this.octaves; ++l1) {
            final int octave = l1;
            double d3 = (double) p_76304_2_ * d6 * p_76304_8_;
            double d4 = (double) p_76304_3_ * d6 * p_76304_10_;
            double d5 = (double) p_76304_4_ * d6 * p_76304_12_;
            long i2 = MathHelper.floor_double_long(d3);
            long j2 = MathHelper.floor_double_long(d5);
            d3 -= (double) i2;
            d5 -= (double) j2;
            i2 %= 16777216L;
            j2 %= 16777216L;
            d3 += (double) i2;
            d5 += (double) j2;

            double[] finalP_76304_1_ = p_76304_1_;
            double finalD = d3;
            double finalD1 = d5;
            double finalD2 = d6;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                this.generatorCollection[octave].populateNoiseArray(
                    finalP_76304_1_,
                    finalD,
                    d4,
                    finalD1,
                    p_76304_5_,
                    p_76304_6_,
                    p_76304_7_,
                    p_76304_8_ * finalD2,
                    p_76304_10_ * finalD2,
                    p_76304_12_ * finalD2,
                    finalD2);
            }, executor);

            futures[l1] = future;
            d6 /= 2.0D;
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);
        allOf.join();

        return p_76304_1_;
    }

    /**
     * Bouncer function to the main one with some default arguments.
     */
    public double[] generateNoiseOctaves(double[] p_76305_1_, int p_76305_2_, int p_76305_3_, int p_76305_4_,
        int p_76305_5_, double p_76305_6_, double p_76305_8_, double p_76305_10_) {
        return this.generateNoiseOctaves(
            p_76305_1_,
            p_76305_2_,
            10,
            p_76305_3_,
            p_76305_4_,
            1,
            p_76305_5_,
            p_76305_6_,
            1.0D,
            p_76305_8_);
    }
}
