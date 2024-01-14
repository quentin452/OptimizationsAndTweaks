package fr.iamacat.optimizationsandtweaks.mixins.common.animalsplus;

import clickme.animals.entity.water.EntitySwimming;
import fr.iamacat.optimizationsandtweaks.utils.apache.commons.math3.util.FastMath;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntitySwimming.class)
public abstract class MixinEntitySwimming extends EntityLiving implements IAnimals {
    @Shadow
    private double swimTargetX;
    @Shadow
    private double swimTargetY;
    @Shadow
    private double swimTargetZ;
    @Shadow
    private Entity targetEntity;
    @Shadow
    private boolean isAttacking;
    @Shadow
    protected float swimRadius = 4.0F;
    @Shadow
    protected float swimRadiusHeight = 4.0F;
    @Shadow
    protected boolean isAgressive = false;
    @Shadow
    protected int attackInterval = 50;
    @Shadow
    protected float attackSpeed = 1.2F;
    @Shadow
    protected float swimSpeed = 0.5F;
    @Shadow
    protected boolean jumpOnLand = true;

    public MixinEntitySwimming(World p_i1595_1_) {
        super(p_i1595_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected void func_70619_bc() {
        super.updateAITasks();
        if (this.isInWater()) {
            double dx = this.swimTargetX - this.posX;
            double dy = this.swimTargetY - this.posY;
            double dz = this.swimTargetZ - this.posZ;
            double dist = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
            if (dist < 1.0 || dist > 1000.0) {
                this.swimTargetX = this.posX + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * this.swimRadius);
                this.swimTargetY = this.posY + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * this.swimRadiusHeight);
                this.swimTargetZ = this.posZ + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * this.swimRadius);
                this.isAttacking = false;
            }

            if (this.worldObj.getBlock(MathHelper.floor_double(this.swimTargetX), MathHelper.floor_double(this.swimTargetY + (double)this.height), MathHelper.floor_double(this.swimTargetZ)).getMaterial() == Material.water) {
                this.motionX += dx / dist * 0.05 * this.swimSpeed;
                this.motionY += dy / dist * 0.1 * this.swimSpeed;
                this.motionZ += dz / dist * 0.05 * this.swimSpeed;
            } else {
                this.swimTargetX = this.posX;
                this.swimTargetY = this.posY + 0.1;
                this.swimTargetZ = this.posZ;
            }

            if (this.isAttacking) {
                this.motionX *= this.attackSpeed;
                this.motionY *= this.attackSpeed;
                this.motionZ *= this.attackSpeed;
            }

            if (this.isAgressive && this.rand.nextInt(this.attackInterval) == 0) {
                this.targetEntity = this.findPlayerToAttack();
                if (this.targetEntity != null && this.targetEntity.isInWater()) {
                    this.swimTargetX = this.targetEntity.posX;
                    this.swimTargetY = this.targetEntity.posY;
                    this.swimTargetZ = this.targetEntity.posZ;
                    this.isAttacking = true;
                }
            }

            this.renderYawOffset += (-((float)FastMath.atan2(this.motionY, this.motionZ)) * 180.0F / 3.1415927F - this.renderYawOffset) * 0.5F;
            this.rotationYaw = this.renderYawOffset;
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationPitch += ((float) FastMath.atan2(this.motionY, f) * 180.0F / 3.1415927F - this.rotationPitch) * 0.5F;
        } else {
            this.motionX = 0.0;
            this.motionY -= 0.08;
            this.motionY *= 0.9800000190734863;
            this.motionZ = 0.0;
            if (this.jumpOnLand && this.onGround && this.rand.nextInt(30) == 0) {
                this.motionY = 0.30000001192092896;
                this.motionX = (-0.4F + this.rand.nextFloat() * 0.8F);
                this.motionZ = (-0.4F + this.rand.nextFloat() * 0.8F);
            }
        }
    }
    @Shadow
    protected Entity findPlayerToAttack() {
        EntityPlayer player = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0);
        return player != null && this.canEntityBeSeen(player) ? player : null;
    }
}
