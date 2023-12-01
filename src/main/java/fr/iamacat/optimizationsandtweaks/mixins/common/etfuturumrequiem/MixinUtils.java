package fr.iamacat.optimizationsandtweaks.mixins.common.etfuturumrequiem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import ganymedes01.etfuturum.core.utils.Utils;

@Mixin(Utils.class)
public class MixinUtils {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static double atan2(double p_181159_0_, double p_181159_2_) {
        double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
        if (Double.isNaN(d0)) {
            return Double.NaN;
        } else {
            boolean flag = p_181159_0_ < 0.0;
            if (flag) {
                p_181159_0_ = -p_181159_0_;
            }

            boolean flag1 = p_181159_2_ < 0.0;
            if (flag1) {
                p_181159_2_ = -p_181159_2_;
            }

            boolean flag2 = p_181159_0_ > p_181159_2_;
            double d9;
            if (flag2) {
                d9 = p_181159_2_;
                p_181159_2_ = p_181159_0_;
                p_181159_0_ = d9;
            }

            d9 = fastInvSqrt(d0);
            p_181159_2_ *= d9;
            p_181159_0_ *= d9;

            double d2 = p_181159_0_ + 0.5;
            double d3 = Math.asin(d2);
            double d4 = Math.cos(d3);
            double d5 = p_181159_0_ * d4 - p_181159_2_;

            double d6 = (6.0 + d5 * d5) * d5 * 0.16666666666666666;
            double d7 = d3 + d6;
            if (flag2) {
                d7 = 1.5707963267948966 - d7;
            }

            if (flag1) {
                d7 = Math.PI - d7;
            }

            if (flag) {
                d7 = -d7;
            }

            return d7;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static double fastInvSqrt(double p_181161_0_) {
        double d0 = 0.5 * p_181161_0_;
        long i = Double.doubleToRawLongBits(p_181161_0_);
        i = 6910469410427058090L - (i >> 1);
        p_181161_0_ = Double.longBitsToDouble(i);
        p_181161_0_ *= 1.5 - d0 * p_181161_0_ * p_181161_0_;
        return p_181161_0_;
    }
}
