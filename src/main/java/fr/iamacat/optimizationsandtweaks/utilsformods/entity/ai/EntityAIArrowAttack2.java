package fr.iamacat.optimizationsandtweaks.utilsformods.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;

public class EntityAIArrowAttack2 extends EntityAIBase {

    /** The entity the AI instance has been applied to */
    private final EntityLiving entityHost;
    /** The entity (as a RangedAttackMob) the AI instance has been applied to. */
    private final IRangedAttackMob rangedAttackEntityHost;
    private EntityLivingBase attackTarget;
    /**
     * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
     * maxRangedAttackTime.
     */
    private int rangedAttackTime;
    private final double entityMoveSpeed;
    private int field_75318_f;
    private final int field_96561_g;
    /** The maximum time the AI has to wait before peforming another ranged attack. */
    private final int maxRangedAttackTime;
    private final float field_96562_i;
    private final float field_82642_h;

    public EntityAIArrowAttack2(IRangedAttackMob p_i1649_1_, double p_i1649_2_, int p_i1649_4_, float p_i1649_5_) {
        this(p_i1649_1_, p_i1649_2_, p_i1649_4_, p_i1649_4_, p_i1649_5_);
    }

    public EntityAIArrowAttack2(IRangedAttackMob p_i1650_1_, double p_i1650_2_, int p_i1650_4_, int p_i1650_5_,
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

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
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
        double d0 = this.entityHost
            .getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        boolean flag = this.entityHost.getEntitySenses()
            .canSee(this.attackTarget);
        float maxRangeSq = this.field_82642_h * this.field_82642_h;

        if (flag) {
            ++this.field_75318_f;
        } else {
            this.field_75318_f = 0;
        }

        if (d0 <= maxRangeSq && this.field_75318_f >= 20) {
            this.entityHost.getNavigator()
                .clearPathEntity();
        } else {
            this.entityHost.getNavigator()
                .tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }

        this.entityHost.getLookHelper()
            .setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);

        if (--this.rangedAttackTime == 0) {
            if (d0 > maxRangeSq || !flag) {
                return;
            }

            float f = (float) Math.sqrt(d0) / this.field_96562_i;
            float f1 = MathHelper.clamp_float(f, 0.1F, 1.0F);
            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, f1);
            this.rangedAttackTime = MathHelper
                .floor_float(f * (float) (this.maxRangedAttackTime - this.field_96561_g) + (float) this.field_96561_g);
        } else if (this.rangedAttackTime < 0) {
            float f = (float) Math.sqrt(d0) / this.field_96562_i;
            this.rangedAttackTime = MathHelper
                .floor_float(f * (float) (this.maxRangedAttackTime - this.field_96561_g) + (float) this.field_96561_g);
        }
    }

}
