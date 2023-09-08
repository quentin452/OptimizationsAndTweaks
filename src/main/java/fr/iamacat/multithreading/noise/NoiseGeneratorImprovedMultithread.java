package fr.iamacat.multithreading.noise;

import net.minecraft.world.gen.NoiseGenerator;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NoiseGeneratorImprovedMultithread extends NoiseGenerator {

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private int[] permutations;
    public double xCoord;
    public double yCoord;
    public double zCoord;
    private static final double[] field_152381_e = new double[] { 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D };
    private static final double[] field_152382_f = new double[] { 1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D };
    private static final double[] field_152383_g = new double[] { 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, -1.0D, -1.0D, 1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 1.0D, 0.0D, -1.0D };
    private static final double[] field_152384_h = new double[] { 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D };
    private static final double[] field_152385_i = new double[] { 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, -1.0D, -1.0D, 1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 1.0D, 0.0D, -1.0D };

    public NoiseGeneratorImprovedMultithread() {
        this(new Random());
    }

    public NoiseGeneratorImprovedMultithread(Random p_i45469_1_) {
        this.permutations = new int[512];
        this.xCoord = p_i45469_1_.nextDouble() * 256.0D;
        this.yCoord = p_i45469_1_.nextDouble() * 256.0D;
        this.zCoord = p_i45469_1_.nextDouble() * 256.0D;
        int i;

        for (i = 0; i < 256; this.permutations[i] = i++) {
            ;
        }

        for (i = 0; i < 256; ++i) {
            int j = p_i45469_1_.nextInt(256 - i) + i;
            int k = this.permutations[i];
            this.permutations[i] = this.permutations[j];
            this.permutations[j] = k;
            this.permutations[i + 256] = this.permutations[i];
        }
    }

    public void populateNoiseArray(final double[] p_76308_1_, final double p_76308_2_, final double p_76308_4_,
                                   final double p_76308_6_, final int p_76308_8_, final int p_76308_9_, final int p_76308_10_,
                                   final double p_76308_11_, final double p_76308_13_, final double p_76308_15_, final double p_76308_17_) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        final int chunkSize = p_76308_8_ / numThreads;
        Future<?>[] futures = new Future[numThreads];

        for (int thread = 0; thread < numThreads; thread++) {
            final int finalThread = thread;
            futures[thread] = executor.submit(() -> {
                int start = finalThread * chunkSize;
                int end = finalThread == numThreads - 1 ? p_76308_8_ : (finalThread + 1) * chunkSize;

                for (int x = start; x < end; x++) {
                    for (int z = 0; z < p_76308_10_; z++) {
                        for (int y = 0; y < p_76308_9_; y++) {
                            int index = x * p_76308_9_ * p_76308_10_ + z * p_76308_9_ + y;
                            double xOffset = p_76308_2_ + (double) x * p_76308_11_ + xCoord;
                            double yOffset = p_76308_4_ + (double) y * p_76308_13_ + yCoord;
                            double zOffset = p_76308_6_ + (double) z * p_76308_15_ + zCoord;

                            // Calculate the value at this position
                            double value = calculateValue(xOffset, yOffset, zOffset);
                            p_76308_1_[index] = value;
                        }
                    }
                }
            });
        }

        // Wait for all threads to finish
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private double calculateValue(double xOffset, double yOffset, double zOffset) {
        int ix = (int) Math.floor(xOffset);
        int iy = (int) Math.floor(yOffset);
        int iz = (int) Math.floor(zOffset);

        double dx = xOffset - ix;
        double dy = yOffset - iy;
        double dz = zOffset - iz;

        ix = ix & 255;
        iy = iy & 255;
        iz = iz & 255;

        int i = this.permutations[ix] + iy;
        int j = this.permutations[i] + iz;
        int k = this.permutations[i + 1] + iz;
        int l = this.permutations[ix + 1] + iy;

        double value = lerp(
            dx,
            func_76309_a(this.permutations[j], xOffset, yOffset),
            grad(this.permutations[k], xOffset - 1, 0, zOffset));

        value += lerp(
            dx,
            grad(this.permutations[j + 1], xOffset, 0, zOffset - 1),
            grad(this.permutations[k + 1], xOffset - 1, 0, zOffset - 1));

        value += lerp(
            dy,
            value,
            lerp(
                dx,
                grad(this.permutations[l], xOffset, yOffset - 1, zOffset),
                grad(this.permutations[l + 1], xOffset - 1, yOffset - 1, zOffset)));

        return value;
    }

    private double lerp(double p_76311_1_, double p_76311_3_, double p_76311_5_) {
        return p_76311_3_ + p_76311_1_ * (p_76311_5_ - p_76311_3_);
    }

    private double func_76309_a(int p_76309_1_, double p_76309_2_, double p_76309_4_) {
        int j = p_76309_1_ & 15;
        return field_152384_h[j] * p_76309_2_ + field_152385_i[j] * p_76309_4_;
    }

    private double grad(int p_76310_1_, double p_76310_2_, double p_76310_4_, double p_76310_6_) {
        int j = p_76310_1_ & 15;
        return field_152381_e[j] * p_76310_2_ + field_152382_f[j] * p_76310_4_ + field_152383_g[j] * p_76310_6_;
    }
}
