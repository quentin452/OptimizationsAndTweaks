package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

/**
 * Tries to fix ghost cascading worldgen caused by EntityMob from vanilla.
 */
@Mixin(EntityMob.class)
public abstract class MixinEntityMob extends EntityCreature implements IMob {

    public MixinEntityMob(World p_i1602_1_) {
        super(p_i1602_1_);
    }

    /**
     * @author
     * @reason : try to fix silent cascading worldgens caused by isValidLightLevel
     * @image : see https://mega.nz/file/P0FTEC4Z#TZoy-l2XaPIDY3PwxRgp_E1CfRtBo4w2E1nuoA0Dz7U
     */
    @Overwrite
    protected boolean isValidLightLevel() {
        if (!this.worldObj.getChunkProvider()
            .chunkExists(MathHelper.floor_double(this.posX) >> 4, MathHelper.floor_double(this.posZ) >> 4)) {
            return false;
        }
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > this.rand.nextInt(32)) {
            return false;
        } else {
            int l = this.worldObj.getBlockLightValue(i, j, k);
            if (this.worldObj.isThundering()) {
                int i1 = this.worldObj.skylightSubtracted;
                this.worldObj.skylightSubtracted = 10;
                l = this.worldObj.getBlockLightValue(i, j, k);
                this.worldObj.skylightSubtracted = i1;
            }
            return l <= this.rand.nextInt(8);
        }
    }

    /**
     * Prevent mobs from hitting through closed doors
     */
    @Overwrite
    public boolean attackEntityAsMob(Entity target) {
        if (isBlockedBySolidBlock(target)) {
            return false;
        }

        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
            .getAttributeValue();
        int knockback = 0;

        if (target instanceof EntityLivingBase) {
            damage += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase) target);
            knockback += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase) target);
        }

        boolean attacked = target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

        if (attacked) {
            if (knockback > 0) {
                target.addVelocity(
                    -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F,
                    0.1D,
                    MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F);
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int fireAspect = EnchantmentHelper.getFireAspectModifier(this);
            if (fireAspect > 0) {
                target.setFire(fireAspect * 4);
            }

            if (target instanceof EntityLivingBase) {
                EnchantmentHelper.func_151384_a((EntityLivingBase) target, this);
            }

            EnchantmentHelper.func_151385_b(this, target);
        }

        return attacked;
    }

    @Unique
    private boolean isBlockedBySolidBlock(Entity target) {
        double mobEyeY = this.posY + this.getEyeHeight();
        double targetEyeY = target.posY + target.getEyeHeight();

        Vec3 from = Vec3.createVectorHelper(this.posX, mobEyeY, this.posZ);
        Vec3 to = Vec3.createVectorHelper(target.posX, targetEyeY, target.posZ);

        MovingObjectPosition hit = this.worldObj.rayTraceBlocks(from, to);

        if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block block = this.worldObj.getBlock(hit.blockX, hit.blockY, hit.blockZ);
            return block.getMaterial()
                .isSolid();
        }
        return false;
    }
}
