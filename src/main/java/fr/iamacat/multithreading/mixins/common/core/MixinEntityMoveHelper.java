package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

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
    @Overwrite
    public void onUpdateMoveHelper() {
        if(MultithreadingandtweaksConfig.enableMixinEntityMoveHelper){

        this.entity.setMoveForward(0.0F);

        if (this.update) {
            this.update = false;
            int i = MathHelper.floor_double(this.entity.boundingBox.minY + 0.5D);
            double dX = this.posX - this.entity.posX;
            double dZ = this.posZ - this.entity.posZ;
            double dY = this.posY - (double) i;
            double squaredDistance = dX * dX + dY * dY + dZ * dZ;

            if (squaredDistance >= 2.500000277905201E-7D) {
                float newRotationYaw = (float) (Math.toDegrees(Math.atan2(dZ, dX)) - 90.0);
                this.entity.rotationYaw = this.multithreadingandtweaks$limitAngle(this.entity.rotationYaw, newRotationYaw, 30.0F);

                double movementSpeed = this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
                this.entity.setAIMoveSpeed((float) movementSpeed);

                if (dY > 0.0 && squaredDistance < 1.0) {
                    this.entity.getJumpHelper().setJumping();
                }
            }
        }
        }
    }

    /**
     * Limits the given angle to a upper and lower limit.
     */

    @Unique
    private float multithreadingandtweaks$limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_)
    {

        float f3 = MathHelper.wrapAngleTo180_float(p_75639_2_ - p_75639_1_);

        if (f3 > p_75639_3_)
        {
            f3 = p_75639_3_;
        }

        if (f3 < -p_75639_3_)
        {
            f3 = -p_75639_3_;
        }

        return p_75639_1_ + f3;
    }
}
