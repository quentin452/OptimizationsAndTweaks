package fr.iamacat.multithreading.noise;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.world.gen.NoiseGenerator;

public class NoiseGeneratorPerlinMultithread extends NoiseGenerator {
    // ATTENTION IT BREAK VANILLA SEED PARITY

    private NoiseGeneratorSimplexMultithread[] field_151603_a;
    private int field_151602_b;
    private ExecutorService executor;

    public NoiseGeneratorPerlinMultithread(Random p_i45470_1_, int p_i45470_2_) {
        this.field_151602_b = p_i45470_2_;
        this.field_151603_a = new NoiseGeneratorSimplexMultithread[p_i45470_2_];

        for (int j = 0; j < p_i45470_2_; ++j) {
            this.field_151603_a[j] = new NoiseGeneratorSimplexMultithread(p_i45470_1_);
        }

        // Create an executor with a fixed number of threads (you can adjust the number as needed)
        this.executor = Executors.newFixedThreadPool(
            Runtime.getRuntime()
                .availableProcessors());
    }

    public double func_151601_a(double p_151601_1_, double p_151601_3_) {
        double d2 = 0.0D;
        double d3 = 1.0D;

        CompletableFuture<Double>[] futures = new CompletableFuture[field_151602_b];

        for (int i = 0; i < this.field_151602_b; ++i) {
            final int octave = i;
            double finalD3 = d3;
            CompletableFuture<Double> future = CompletableFuture
                .supplyAsync(
                    () -> {
                        return field_151603_a[octave].func_151605_a(p_151601_1_ * finalD3, p_151601_3_ * finalD3)
                            / finalD3;
                    },
                    executor);

            futures[i] = future;
            d3 /= 2.0D;
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);

        try {
            allOf.join();
            for (CompletableFuture<Double> future : futures) {
                d2 += future.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return d2;
    }

    public double[] func_151599_a(double[] p_151599_1_, double p_151599_2_, double p_151599_4_, int p_151599_6_,
        int p_151599_7_, double p_151599_8_, double p_151599_10_, double p_151599_12_) {
        return this.func_151600_a(
            p_151599_1_,
            p_151599_2_,
            p_151599_4_,
            p_151599_6_,
            p_151599_7_,
            p_151599_8_,
            p_151599_10_,
            p_151599_12_,
            0.5D);
    }

    public double[] func_151600_a(double[] p_151600_1_, double p_151600_2_, double p_151600_4_, int p_151600_6_,
        int p_151600_7_, double p_151600_8_, double p_151600_10_, double p_151600_12_, double p_151600_14_) {
        if (p_151600_1_ != null && p_151600_1_.length >= p_151600_6_ * p_151600_7_) {
            for (int k = 0; k < p_151600_1_.length; ++k) {
                p_151600_1_[k] = 0.0D;
            }
        } else {
            p_151600_1_ = new double[p_151600_6_ * p_151600_7_];
        }

        double d7 = 1.0D;
        double d6 = 1.0D;

        CompletableFuture<Void>[] futures = new CompletableFuture[field_151602_b];

        for (int l = 0; l < this.field_151602_b; ++l) {
            final int octave = l;
            double finalD6 = d6;
            double finalD7 = d7;
            double[] finalP_151600_1_ = p_151600_1_;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                field_151603_a[octave].func_151606_a(
                    finalP_151600_1_,
                    p_151600_2_,
                    p_151600_4_,
                    p_151600_6_,
                    p_151600_7_,
                    p_151600_8_ * finalD6 * finalD7,
                    p_151600_10_ * finalD6 * finalD7,
                    0.55D / finalD7);
            }, executor);

            futures[l] = future;
            d6 *= p_151600_12_;
            d7 *= p_151600_14_;
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);

        try {
            allOf.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return p_151600_1_;
    }
}
