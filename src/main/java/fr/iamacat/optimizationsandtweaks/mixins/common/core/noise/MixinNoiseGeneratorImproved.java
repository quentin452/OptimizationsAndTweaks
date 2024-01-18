package fr.iamacat.optimizationsandtweaks.mixins.common.core.noise;

import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorImproved;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NoiseGeneratorImproved.class)
public class MixinNoiseGeneratorImproved extends NoiseGenerator {

    @Shadow
    private int[] permutations;
    @Shadow
    public double xCoord;
    @Shadow
    public double yCoord;
    @Shadow
    public double zCoord;
    @Shadow
    private static final double[] field_152381_e = new double[] { 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D,
        0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D };
    @Shadow
    private static final double[] field_152382_f = new double[] { 1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D,
        1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D };
    @Shadow
    private static final double[] field_152383_g = new double[] { 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, -1.0D, -1.0D,
        1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 1.0D, 0.0D, -1.0D };
    @Shadow
    private static final double[] field_152384_h = new double[] { 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D,
        0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D };
    @Shadow
    private static final double[] field_152385_i = new double[] { 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, -1.0D, -1.0D,
        1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 1.0D, 0.0D, -1.0D };

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void populateNoiseArray(double[] p_76308_1_, double p_76308_2_, double p_76308_4_, double p_76308_6_,
        int p_76308_8_, int p_76308_9_, int p_76308_10_, double p_76308_11_, double p_76308_13_, double p_76308_15_,
        double p_76308_17_) {
        if (p_76308_9_ == 1) {
            optimizationsAndTweaks$populateNoiseArraysub1(
                p_76308_1_,
                p_76308_2_,
                p_76308_6_,
                p_76308_8_,
                p_76308_10_,
                p_76308_11_,
                p_76308_15_,
                p_76308_17_);
        } else {
            optimizationsAndTweaks$populateNoiseArraysub2(
                p_76308_1_,
                p_76308_2_,
                p_76308_4_,
                p_76308_6_,
                p_76308_8_,
                p_76308_9_,
                p_76308_10_,
                p_76308_11_,
                p_76308_13_,
                p_76308_15_,
                p_76308_17_);
        }
    }

    @Unique
    public void optimizationsAndTweaks$populateNoiseArraysub1(double[] noiseArray, double xOffset, double zOffset,
                                                              int xSize, int zSize, double xScale, double zScale,
                                                              double densityScale) {
        double densityScaleFactor = 1.0D / densityScale;

        for (int x = 0; x < xSize; ++x) {
            double xCoord = xOffset + x * xScale + this.xCoord;
            int intXCoord = (int) xCoord;
            intXCoord = (xCoord < intXCoord) ? intXCoord - 1 : intXCoord;
            int intX = intXCoord & 255;
            double dX = xCoord - intXCoord;
            double dX3 = dX * dX * dX;
            double dX6 = dX3 * dX3;
            double dX9 = dX6 * dX3;

            for (int z = 0; z < zSize; ++z) {
                double zCoord = zOffset + z * zScale + this.zCoord;
                int intZCoord = (int) zCoord;
                intZCoord = (zCoord < intZCoord) ? intZCoord - 1 : intZCoord;
                double dZ = zCoord - intZCoord;
                double dZ3 = dZ * dZ * dZ;
                double dZ6 = dZ3 * dZ3;

                double d8 = 0.0D, d9 = 0.0D, d10 = 0.0D, d11 = 0.0D;

                for (int l1 = 0; l1 < 4; ++l1) {
                    double d12 = (l1 & 2) - 1.0D;
                    double d13 = (l1 & 1) - 1.0D;

                    int index1 = (intX + (int) d12) & 255;
                    int index2 = (intX + 1 + (int) d12) & 255;

                    d8 += grad(this.permutations[index1], dX, dZ, dZ - d13);
                    d9 += grad(this.permutations[index2], dX - 1.0D, dZ, dZ - d13);
                    d10 += grad(this.permutations[index1], dX, dZ - 1.0D, dZ - d13);
                    d11 += grad(this.permutations[index2], dX - 1.0D, dZ - 1.0D, dZ - d13);
                }

                double d21 = d8 + dX9 * (d10 - d8) + dZ6 * (d11 - d8 + dX9 * (d9 - d8));
                noiseArray[x * zSize + z] += d21 * densityScaleFactor;
            }
        }
    }


    @Unique
    public void optimizationsAndTweaks$populateNoiseArraysub2(double[] noiseArray, double xOffset, double yOffset,
                                                              double zOffset, int xSize, int ySize, int zSize,
                                                              double xScale, double yScale, double zScale, double densityScale) {
        double densityScaleFactor = 1.0D / densityScale;
        int lastIntY = -1;

        for (int x = 0; x < xSize; ++x) {
            double xCoord = xOffset + x * xScale + this.xCoord;
            int intXCoord = (int) xCoord;
            intXCoord = (xCoord < intXCoord) ? intXCoord - 1 : intXCoord;
            int intX = intXCoord & 255;
            double dX = xCoord - intXCoord;
            double dX3 = dX * dX * dX;
            double dX6 = dX3 * dX3;
            double dX9 = dX6 * dX3;

            for (int z = 0; z < zSize; ++z) {
                double zCoord = zOffset + z * zScale + this.zCoord;
                int intZCoord = (int) zCoord;
                intZCoord = (zCoord < intZCoord) ? intZCoord - 1 : intZCoord;
                int intZ = intZCoord & 255;
                double dZ = zCoord - intZCoord;
                double dZ3 = dZ * dZ * dZ;
                double dZ6 = dZ3 * dZ3;

                for (int y = 0; y < ySize; ++y) {
                    double yCoord = yOffset + y * yScale + this.yCoord;
                    int intYCoord = (int) yCoord;
                    intYCoord = (yCoord < intYCoord) ? intYCoord - 1 : intYCoord;
                    int intY = intYCoord & 255;
                    double dY = yCoord - intYCoord;
                    double dY3 = dY * dY * dY;
                    double dY6 = dY3 * dY3;
                    double dY9 = dY6 * dY3;

                    if (y == 0 || intY != lastIntY) {
                        lastIntY = intY;
                        int baseX = permutations[intX] + intY;
                        int baseX1 = permutations[baseX] + intZ;
                        int baseX2 = permutations[baseX + 1] + intZ;
                        int baseX3 = permutations[intX + 1] + intY;
                        int baseX4 = permutations[baseX3] + intZ;
                        int baseX5 = permutations[baseX3 + 1] + intZ;

                        double d8 = lerp(dX9, grad(permutations[baseX1], dX, dY, dZ),
                            grad(permutations[baseX4], dX - 1.0D, dY, dZ));
                        double d9 = lerp(dX9, grad(permutations[baseX2], dX, dY - 1.0D, dZ),
                            grad(permutations[baseX5], dX - 1.0D, dY - 1.0D, dZ));
                        double d10 = lerp(dX9, grad(permutations[baseX1 + 1], dX, dY, dZ - 1.0D),
                            grad(permutations[baseX4 + 1], dX - 1.0D, dY, dZ - 1.0D));
                        double d11 = lerp(dX9, grad(permutations[baseX2 + 1], dX, dY - 1.0D, dZ - 1.0D),
                            grad(permutations[baseX5 + 1], dX - 1.0D, dY - 1.0D, dZ - 1.0D));

                        double d18 = lerp(dY9, d8, d9);
                        double d19 = lerp(dY9, d10, d11);
                        double d20 = lerp(dZ6, d18, d19);
                        noiseArray[x * zSize * ySize + z * ySize + y] += d20 * densityScaleFactor;
                    }
                }
            }
        }
    }

    @Shadow
    public final double func_76309_a(int p_76309_1_, double p_76309_2_, double p_76309_4_) {
        int j = p_76309_1_ & 15;
        return field_152384_h[j] * p_76309_2_ + field_152385_i[j] * p_76309_4_;
    }

    @Shadow
    public final double lerp(double p_76311_1_, double p_76311_3_, double p_76311_5_) {
        return p_76311_3_ + p_76311_1_ * (p_76311_5_ - p_76311_3_);
    }

    @Shadow
    public final double grad(int p_76310_1_, double p_76310_2_, double p_76310_4_, double p_76310_6_) {
        int j = p_76310_1_ & 15;
        return field_152381_e[j] * p_76310_2_ + field_152382_f[j] * p_76310_4_ + field_152383_g[j] * p_76310_6_;
    }
}
