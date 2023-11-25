package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityAIArrowAttack.class)
public class MixinEntityArrowAttack extends EntityAIBase {

    @Shadow
    private final EntityLiving entityHost;
    @Shadow
    private final IRangedAttackMob rangedAttackEntityHost;
    @Shadow
    private EntityLivingBase attackTarget;
    @Shadow
    private int rangedAttackTime;
    @Shadow
    private final double entityMoveSpeed;
    @Shadow
    private int field_75318_f;
    @Shadow
    private final int field_96561_g;

    @Shadow
    private final int maxRangedAttackTime;
    @Shadow
    private final float field_96562_i;
    @Shadow
    private final float field_82642_h;

    public MixinEntityArrowAttack(IRangedAttackMob p_i1650_1_, double p_i1650_2_, int p_i1650_4_, int p_i1650_5_,
        float p_i1650_6_) {
        this.rangedAttackTime = -1;

        if (!(p_i1650_1_ instanceof EntityLivingBase)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        } else {
            this.rangedAttackEntityHost = p_i1650_1_;
            this.entityHost = (EntityLiving) p_i1650_1_;
            this.entityMoveSpeed = p_i1650_2_;
            this.field_96561_g = p_i1650_4_;
            this.maxRangedAttackTime = p_i1650_5_;
            this.field_96562_i = p_i1650_6_;
            this.field_82642_h = p_i1650_6_ * p_i1650_6_;
            this.setMutexBits(3);
        }
    }

    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();

        if (entitylivingbase == null) {
            return false;
        } else {
            this.attackTarget = entitylivingbase;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return this.shouldExecute() || !this.entityHost.getNavigator()
            .noPath();
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        this.attackTarget = null;
        this.field_75318_f = 0;
        this.rangedAttackTime = -1;
    }

    /**
     * Updates the task
     */
    public void updateTask() {
        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);
        float maxRangeSq = this.field_82642_h * this.field_82642_h;

        if (flag) {
            ++this.field_75318_f;
        } else {
            this.field_75318_f = 0;
        }

        if (d0 <= maxRangeSq && this.field_75318_f >= 20) {
            this.entityHost.getNavigator().clearPathEntity();
            if (--this.rangedAttackTime == 0) {
                if (d0 <= maxRangeSq) {
                    float f = (float) Math.sqrt(d0) / this.field_96562_i;
                    float f1 = MathHelper.clamp_float(f, 0.1F, 1.0F);
                    this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, f1);
                    this.rangedAttackTime = MathHelper.floor_float(f * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
                } else {
                    this.rangedAttackTime = -1;
                }
            } else if (this.rangedAttackTime < 0) {
                float f = (float) Math.sqrt(d0) / this.field_96562_i;
                this.rangedAttackTime = MathHelper.floor_float(f * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
            }
        } else {
            double distance = this.entityHost.getDistanceToEntity(this.attackTarget);
            if (distance >= 10 && this.rangedAttackTime <= 0) {
                this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
            }
        }
        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
    }
}
