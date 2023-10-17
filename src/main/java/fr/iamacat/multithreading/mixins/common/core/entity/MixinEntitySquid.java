package fr.iamacat.multithreading.mixins.common.core.entity;

import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntitySquid.class, priority = 999)
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
        super.onLivingUpdate();

        final float twoPI = (float) Math.PI * 2F;
        final float quarterPI = (float) Math.PI * 0.25F;

        float motionX = this.randomMotionVecX * this.randomMotionSpeed;
        float motionY = this.randomMotionVecY * this.randomMotionSpeed;
        float motionZ = this.randomMotionVecZ * this.randomMotionSpeed;

        float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

        float newRenderYawOffset = (-((float) Math.atan2(motionX, motionZ)) * 180.0F / (float) Math.PI - this.renderYawOffset) * 0.1F;
        this.renderYawOffset += newRenderYawOffset;
        this.rotationYaw = this.renderYawOffset;

        this.squidYaw += (float) Math.PI * this.field_70871_bB * 1.5F;

        float newSquidPitch = (-((float) Math.atan2(f, motionY) * 180.0F / (float) Math.PI - this.squidPitch) * 0.1F);
        this.squidPitch += newSquidPitch;

        if (this.isInWater()) {
            float squidRotation = this.squidRotation + this.rotationVelocity;

            if (squidRotation > twoPI) {
                squidRotation -= twoPI;

                if (this.rand.nextInt(10) == 0) {
                    this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
                }
            }

            float f2 = squidRotation / (float) Math.PI;
            this.tentacleAngle = MathHelper.sin(f2 * f2 * (float) Math.PI) * quarterPI;

            if (f2 > 0.75D) {
                this.randomMotionSpeed = 1.0F;
                this.field_70871_bB = 1.0F;
            } else {
                this.field_70871_bB *= 0.8F;
            }

            if (!this.worldObj.isRemote) {
                this.motionX = motionX;
                this.motionY = motionY;
                this.motionZ = motionZ;
            }
        } else {
            this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * quarterPI;

            if (!this.worldObj.isRemote) {
                this.motionX = 0.0D;
                this.motionY -= 0.08D;
                this.motionY *= 0.9800000190734863D;
                this.motionZ = 0.0D;
            }

            this.squidPitch = (float)((double)this.squidPitch + (double)(-90.0F - this.squidPitch) * 0.02D);
        }
    }
}
