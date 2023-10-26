package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityAITarget.class)
public abstract class MixinEntityAITarget extends EntityAIBase {

    /** The entity that this task belongs to */
    @Shadow
    protected EntityCreature taskOwner;
    /** If true, EntityAI targets must be able to be seen (cannot be blocked by walls) to be suitable targets. */
    @Shadow
    protected boolean shouldCheckSight;
    /** When true, only entities that can be reached with minimal effort will be targetted. */
    @Shadow
    private boolean nearbyOnly;
    /** When nearbyOnly is true: 0 -> No target, but OK to search; 1 -> Nearby target found; 2 -> Target too far. */
    @Shadow
    private int targetSearchStatus;
    /** When nearbyOnly is true, this throttles target searching to avoid excessive pathfinding. */
    @Shadow
    private int targetSearchDelay;
    @Shadow
    private int field_75298_g;

    public MixinEntityAITarget(EntityCreature p_i1669_1_, boolean p_i1669_2_) {
        this(p_i1669_1_, p_i1669_2_, false);
    }

    public MixinEntityAITarget(EntityCreature p_i1670_1_, boolean p_i1670_2_, boolean p_i1670_3_) {
        this.taskOwner = p_i1670_1_;
        this.shouldCheckSight = p_i1670_2_;
        this.nearbyOnly = p_i1670_3_;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Overwrite
    public boolean continueExecuting() {
        EntityLivingBase targetEntity = this.taskOwner.getAttackTarget();

        if (targetEntity == null || !targetEntity.isEntityAlive()) {
            return false;
        }

        double targetDistanceSq = this.getTargetDistance() * this.getTargetDistance();

        if (this.taskOwner.getDistanceSqToEntity(targetEntity) > targetDistanceSq) {
            return false;
        }

        if (this.shouldCheckSight) {
            if (this.taskOwner.getEntitySenses()
                .canSee(targetEntity)) {
                this.field_75298_g = 0;
            } else if (++this.field_75298_g > 60) {
                return false;
            }
        }

        return !(targetEntity instanceof EntityPlayerMP)
            || !((EntityPlayerMP) targetEntity).theItemInWorldManager.isCreative();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected double getTargetDistance() {
        IAttributeInstance iattributeinstance = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
        return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Overwrite
    public void startExecuting() {
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.field_75298_g = 0;
    }

    /**
     * Resets the task
     */
    @Overwrite
    public void resetTask() {
        this.taskOwner.setAttackTarget(null);
    }

    /**
     * A method used to see if an entity is a suitable target through a number of checks.
     */
    @Overwrite
    protected boolean isSuitableTarget(EntityLivingBase targetEntity, boolean checkSight) {
        if (targetEntity == null || targetEntity == this.taskOwner
            || !targetEntity.isEntityAlive()
            || !this.taskOwner.canAttackClass(targetEntity.getClass())) {
            return false;
        }

        if (this.taskOwner instanceof IEntityOwnable) {
            IEntityOwnable ownable = (IEntityOwnable) this.taskOwner;
            if (StringUtils.isNotEmpty(ownable.func_152113_b())) {
                if (targetEntity instanceof IEntityOwnable) {
                    if (ownable.func_152113_b()
                        .equals(((IEntityOwnable) targetEntity).func_152113_b())) {
                        return false;
                    }
                }
                if (targetEntity == ownable.getOwner()) {
                    return false;
                }
            }
        } else if (targetEntity instanceof EntityPlayer && !checkSight
            && ((EntityPlayer) targetEntity).capabilities.disableDamage) {
                return false;
            }

        if (!this.taskOwner.isWithinHomeDistance(
            MathHelper.floor_double(targetEntity.posX),
            MathHelper.floor_double(targetEntity.posY),
            MathHelper.floor_double(targetEntity.posZ))) {
            return false;
        }

        if (this.shouldCheckSight && !this.taskOwner.getEntitySenses()
            .canSee(targetEntity)) {
            return false;
        }

        if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
                this.targetSearchStatus = 0;
            }
            if (this.targetSearchStatus == 0) {
                this.targetSearchStatus = this.canEasilyReach(targetEntity) ? 1 : 2;
            }
            return this.targetSearchStatus != 2;
        }

        return true;
    }

    /**
     * Checks to see if this entity can find a short path to the given target.
     */
    @Overwrite
    public boolean canEasilyReach(EntityLivingBase targetEntity) {
        this.targetSearchDelay = 10 + this.taskOwner.getRNG()
            .nextInt(5);
        PathEntity pathEntity = this.taskOwner.getNavigator()
            .getPathToEntityLiving(targetEntity);

        if (pathEntity == null || pathEntity.getFinalPathPoint() == null) {
            return false;
        }

        PathPoint finalPathPoint = pathEntity.getFinalPathPoint();
        int dx = finalPathPoint.xCoord - MathHelper.floor_double(targetEntity.posX);
        int dz = finalPathPoint.zCoord - MathHelper.floor_double(targetEntity.posZ);
        return (dx * dx + dz * dz) <= 2.25D;
    }
}
