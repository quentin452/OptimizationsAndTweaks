package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.utils.apache.commons.math3.util.FastMath;

@Mixin(EntityMoveHelper.class)
public class MixinEntityMoveHelper {

    /** The EntityLiving that is being moved */
    @Shadow
    private EntityLiving entity;
    @Shadow
    private double posX;
    @Shadow
    private double posY;
    @Shadow
    private double posZ;
    /** The speed at which the entity should move */
    @Shadow
    private double speed;
    @Shadow
    private boolean update;

    /**
     * @author iamacatfr
     * @reason optimize tps
     */
    @Inject(at = @At("HEAD"), method = "onUpdateMoveHelper", cancellable = true)
    public void onUpdateMoveHelper(CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinEntityMoveHelper) {
            this.entity.setMoveForward(0.0F);

            if (this.update) {
                this.update = false;
                double posXDelta = this.posX - this.entity.posX;
                double posZDelta = this.posZ - this.entity.posZ;
                double posYDelta = this.posY - this.entity.boundingBox.minY;
                double squaredDistance = posXDelta * posXDelta + posYDelta * posYDelta + posZDelta * posZDelta;

                if (squaredDistance >= 2.500000277905201E-7D) {
                    float newYaw = (float) Math.toDegrees(FastMath.atan2(posZDelta, posXDelta)) - 90.0F;
                    this.entity.rotationYaw = this
                        .optimizationsAndTweaks$limitAngle(this.entity.rotationYaw, newYaw, 30.0F);

                    double movementSpeed = this.speed
                        * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                            .getAttributeValue();
                    this.entity.setAIMoveSpeed((float) movementSpeed);

                    if (posYDelta > 0.0 && posXDelta * posXDelta + posZDelta * posZDelta < 1.0) {
                        this.entity.getJumpHelper()
                            .setJumping();
                    }
                }
            }
            ci.cancel();
        }
    }

    /**
     * Limits the given angle to a upper and lower limit.
     */

    @Unique
    private float optimizationsAndTweaks$limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_) {

        float f3 = MathHelper.wrapAngleTo180_float(p_75639_2_ - p_75639_1_);

        if (f3 > p_75639_3_) {
            f3 = p_75639_3_;
        }

        if (f3 < -p_75639_3_) {
            f3 = -p_75639_3_;
        }

        return p_75639_1_ + f3;
    }
}
