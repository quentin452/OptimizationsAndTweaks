package fr.iamacat.optimizationsandtweaks.mixins.common.core.entity;

import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.utils.apache.commons.math3.util.FastMath;

@Mixin(EntitySquid.class)
public class MixinEntitySquid extends EntityWaterMob {

    @Shadow
    public float squidPitch;
    @Shadow
    public float prevSquidPitch;
    @Shadow
    public float squidYaw;
    @Shadow
    public float prevSquidYaw;
    /** appears to be rotation in radians; we already have pitch & yaw, so this completes the triumvirate. */
    @Shadow
    public float squidRotation;
    /** previous squidRotation in radians */
    @Shadow
    public float prevSquidRotation;
    /** angle of the tentacles in radians */
    @Shadow
    public float tentacleAngle;
    /** the last calculated angle of the tentacles in radians */
    @Shadow
    public float lastTentacleAngle;
    @Shadow
    private float randomMotionSpeed;
    /** change in squidRotation in radians. */
    @Shadow
    private float rotationVelocity;
    @Shadow
    private float field_70871_bB;
    @Shadow
    private float randomMotionVecX;
    @Shadow
    private float randomMotionVecY;
    @Shadow
    private float randomMotionVecZ;

    public MixinEntitySquid(World p_i1695_1_) {
        super(p_i1695_1_);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Overwrite
    public void onLivingUpdate() {
        if (OptimizationsandTweaksConfig.enableMixinEntitySquid) {
            super.onLivingUpdate();
            this.prevSquidPitch = this.squidPitch;
            this.prevSquidYaw = this.squidYaw;
            this.prevSquidRotation = this.squidRotation;
            this.lastTentacleAngle = this.tentacleAngle;
            this.squidRotation += this.rotationVelocity;

            if (this.squidRotation > ((float) Math.PI * 2F)) {
                this.squidRotation -= ((float) Math.PI * 2F);

                if (this.rand.nextInt(10) == 0) {
                    this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
                }
            }

            if (this.isInWater()) {
                float f;

                if (this.squidRotation < (float) Math.PI) {
                    f = this.squidRotation / (float) Math.PI;
                    this.tentacleAngle = MathHelper.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;

                    if ((double) f > 0.75D) {
                        this.randomMotionSpeed = 1.0F;
                        this.field_70871_bB = 1.0F;
                    } else {
                        this.field_70871_bB *= 0.8F;
                    }
                } else {
                    this.tentacleAngle = 0.0F;
                    this.randomMotionSpeed *= 0.9F;
                    this.field_70871_bB *= 0.99F;
                }

                if (!this.worldObj.isRemote) {
                    this.motionX = this.randomMotionVecX * this.randomMotionSpeed;
                    this.motionY = this.randomMotionVecY * this.randomMotionSpeed;
                    this.motionZ = this.randomMotionVecZ * this.randomMotionSpeed;
                }

                f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                this.renderYawOffset += (-((float) FastMath.atan2(this.motionX, this.motionZ)) * 180.0F
                    / (float) Math.PI - this.renderYawOffset) * 0.1F;
                this.rotationYaw = this.renderYawOffset;
                this.squidYaw += (float) Math.PI * this.field_70871_bB * 1.5F;
                this.squidPitch += (-((float) FastMath.atan2(f, this.motionY)) * 180.0F / (float) Math.PI
                    - this.squidPitch) * 0.1F;
            } else {
                this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * (float) Math.PI * 0.25F;

                if (!this.worldObj.isRemote) {
                    this.motionX = 0.0D;
                    this.motionY -= 0.08D;
                    this.motionY *= 0.9800000190734863D;
                    this.motionZ = 0.0D;
                }

                this.squidPitch = (float) ((double) this.squidPitch + (double) (-90.0F - this.squidPitch) * 0.02D);
            }
        }
    }
}
