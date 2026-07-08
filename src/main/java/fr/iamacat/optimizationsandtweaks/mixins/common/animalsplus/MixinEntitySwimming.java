package fr.iamacat.optimizationsandtweaks.mixins.common.animalsplus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import clickme.animals.entity.water.EntitySwimming;
import fr.iamacat.optimizationsandtweaks.utils.apache.commons.math3.util.FastMath;

/**
 * Optimizes EntitySwimming class from Animal Plus.
 * <p>
 * {@code func_70619_bc} was a byte-for-byte behavioral copy of the mod's original (same swim-target
 * pathing, attack, and land-hop logic) except the two {@code Math.atan2} calls (yaw, pitch) -- redirecting
 * those to {@link FastMath#atan2} lets the original (untouched) body run instead of a full copy.
 */
@Mixin(EntitySwimming.class)
public abstract class MixinEntitySwimming extends EntityLiving implements IAnimals {

    public MixinEntitySwimming(World p_i1595_1_) {
        super(p_i1595_1_);
    }

    @Redirect(
        method = "func_70619_bc",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;atan2(DD)D", ordinal = 0),
        remap = false)
    private double optimizationsAndTweaks$fastAtan2Yaw(double y, double x) {
        return FastMath.atan2(y, x);
    }

    @Redirect(
        method = "func_70619_bc",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;atan2(DD)D", ordinal = 1),
        remap = false)
    private double optimizationsAndTweaks$fastAtan2Pitch(double y, double x) {
        return FastMath.atan2(y, x);
    }

    @Shadow
    protected Entity findPlayerToAttack() {
        EntityPlayer player = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0);
        return player != null && this.canEntityBeSeen(player) ? player : null;
    }
}
