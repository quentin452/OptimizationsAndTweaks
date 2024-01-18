package fr.iamacat.optimizationsandtweaks.mixins.common.core.noise;

import fr.iamacat.optimizationsandtweaks.noise.NoiseGeneratorImprovedMultithread;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(NoiseGeneratorOctaves.class)
public class MixinNoiseGeneratorOctaves extends NoiseGenerator
{
    @Unique
    private NoiseGeneratorImprovedMultithread[] optimizationsAndTweaks$generatorCollection;
    @Shadow
    private int octaves;
    public MixinNoiseGeneratorOctaves(Random p_i2111_1_, int p_i2111_2_)
    {
        this.octaves = p_i2111_2_;
        this.optimizationsAndTweaks$generatorCollection = new NoiseGeneratorImprovedMultithread[p_i2111_2_];

        for (int j = 0; j < p_i2111_2_; ++j)
        {
            this.optimizationsAndTweaks$generatorCollection[j] = new NoiseGeneratorImprovedMultithread(p_i2111_1_);
        }
    }

    /**
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    @Overwrite
    public double[] generateNoiseOctaves(double[] p_76304_1_, int p_76304_2_, int p_76304_3_, int p_76304_4_, int p_76304_5_, int p_76304_6_, int p_76304_7_, double p_76304_8_, double p_76304_10_, double p_76304_12_)
    {
        if (p_76304_1_ == null)
        {
            p_76304_1_ = new double[p_76304_5_ * p_76304_6_ * p_76304_7_];
        }
        else
        {
            for (int k1 = 0; k1 < p_76304_1_.length; ++k1)
            {
                p_76304_1_[k1] = 0.0D;
            }
        }

        double d6 = 1.0D;

        for (int l1 = 0; l1 < this.octaves; ++l1)
        {
            double d3 = (double)p_76304_2_ * d6 * p_76304_8_;
            double d4 = (double)p_76304_3_ * d6 * p_76304_10_;
            double d5 = (double)p_76304_4_ * d6 * p_76304_12_;
            long i2 = MathHelper.floor_double_long(d3);
            long j2 = MathHelper.floor_double_long(d5);
            d3 -= (double)i2;
            d5 -= (double)j2;
            i2 %= 16777216L;
            j2 %= 16777216L;
            d3 += (double)i2;
            d5 += (double)j2;
            this.optimizationsAndTweaks$generatorCollection[l1].populateNoiseArray(p_76304_1_, d3, d4, d5, p_76304_5_, p_76304_6_, p_76304_7_, p_76304_8_ * d6, p_76304_10_ * d6, p_76304_12_ * d6, d6);
            d6 /= 2.0D;
        }

        return p_76304_1_;
    }

    /**
     * Bouncer function to the main one with some default arguments.
     */
    @Overwrite
    public double[] generateNoiseOctaves(double[] p_76305_1_, int p_76305_2_, int p_76305_3_, int p_76305_4_, int p_76305_5_, double p_76305_6_, double p_76305_8_, double p_76305_10_)
    {
        return this.generateNoiseOctaves(p_76305_1_, p_76305_2_, 10, p_76305_3_, p_76305_4_, 1, p_76305_5_, p_76305_6_, 1.0D, p_76305_8_);
    }
}
