package fr.iamacat.multithreading.mixins.common.core.entity;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    @Inject(method = "onLivingUpdate", at = @At("HEAD"), remap = false, cancellable = true)
    public void modifiedonLivingUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntitySquid) {
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
        }

        if (this.isInWater()) {
            float f = 0;

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
                double atan2MotionX = Math.atan2(this.motionX, this.motionZ);
                double atan2MotionY = Math.atan2(f, this.motionY);
                this.motionX = this.randomMotionVecX * this.randomMotionSpeed;
                this.motionY = this.randomMotionVecY * this.randomMotionSpeed;
                this.motionZ = this.randomMotionVecZ * this.randomMotionSpeed;
                this.renderYawOffset += (-((float) atan2MotionX) * 180.0F / (float) Math.PI - this.renderYawOffset) * 0.1F;
                this.rotationYaw = this.renderYawOffset;
                this.squidYaw += (float) Math.PI * this.field_70871_bB * 1.5F;
                this.squidPitch += (-((float) atan2MotionY) * 180.0F / (float) Math.PI - this.squidPitch) * 0.1F;
            }
        }
        ci.cancel();
    }
}
