package fr.iamacat.optimizationsandtweaks.mixins.common.core.entity;

import net.minecraft.entity.passive.EntitySquid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fr.iamacat.optimizationsandtweaks.utils.apache.commons.math3.util.FastMath;

/**
 * Optimizes the EntitySquid class.
 * <p>
 * {@code onLivingUpdate} was a byte-for-byte behavioral copy of vanilla (same rotation/tentacle/motion
 * math, same {@code rand}/{@code getRNG()} accessor) except the two {@code Math.atan2} calls (yaw, pitch)
 * -- redirecting those to {@link FastMath#atan2} lets the original (untouched) vanilla body run instead
 * of a full copy.
 */
@Mixin(EntitySquid.class)
public class MixinEntitySquid {

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Ljava/lang/Math;atan2(DD)D", ordinal = 0))
    private double optimizationsAndTweaks$fastAtan2Yaw(double y, double x) {
        return FastMath.atan2(y, x);
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Ljava/lang/Math;atan2(DD)D", ordinal = 1))
    private double optimizationsAndTweaks$fastAtan2Pitch(double y, double x) {
        return FastMath.atan2(y, x);
    }
}
