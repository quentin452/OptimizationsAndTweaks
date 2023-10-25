package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityAIAttackOnCollide.class)
public class MixinEntityAIAttackOnCollide extends EntityAIBase {
    @Shadow
    World worldObj;
    @Shadow
    EntityCreature attacker;

    @Shadow
    int attackTick;
    @Shadow
    double speedTowardsTarget;
    @Shadow
    boolean longMemory;
    @Shadow
    PathEntity entityPathEntity;
    @Shadow
    Class classTarget;
    @Shadow
    private int field_75445_i;
    @Shadow
    private double field_151497_i;
    @Shadow
    private double field_151495_j;
    @Shadow
    private double field_151496_k;
    @Shadow
    private int failedPathFindingPenalty;

    public MixinEntityAIAttackOnCollide(EntityCreature attacker, double speedTowardsTarget, boolean longMemory) {
        this.attacker = attacker;
        this.worldObj = attacker.worldObj;
        this.speedTowardsTarget = speedTowardsTarget;
        this.longMemory = longMemory;
        this.setMutexBits(3);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean shouldExecute() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive() || (this.classTarget != null && !this.classTarget.isAssignableFrom(target.getClass()))) {
            return false;
        }
        if (--this.field_75445_i <= 0) {
            this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(target);
            this.field_75445_i = 4 + this.attacker.getRNG().nextInt(7);
            return this.entityPathEntity != null;
        } else {
            return true;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean continueExecuting() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        return target != null && target.isEntityAlive() && (!this.longMemory || !this.attacker.getNavigator().noPath())
            && this.attacker.isWithinHomeDistance(MathHelper.floor_double(target.posX), MathHelper.floor_double(target.posY), MathHelper.floor_double(target.posZ));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void startExecuting() {
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
        this.field_75445_i = 0;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void resetTask() {
        this.attacker.getNavigator().clearPathEntity();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateTask() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        double distanceSquared = this.attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
        double attackRange = this.attacker.width * 2.0F * this.attacker.width * 2.0F + target.width;
        --this.field_75445_i;

        if ((this.longMemory || this.attacker.getEntitySenses().canSee(target))
            && this.field_75445_i <= 0
            && (this.field_151497_i == 0.0D && this.field_151495_j == 0.0D && this.field_151496_k == 0.0D
            || target.getDistanceSq(this.field_151497_i, this.field_151495_j, this.field_151496_k) >= 1.0D
            || this.attacker.getRNG().nextFloat() < 0.05F)) {
            this.field_151497_i = target.posX;
            this.field_151495_j = target.boundingBox.minY;
            this.field_151496_k = target.posZ;
            this.field_75445_i = this.failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

            PathEntity attackerPath = this.attacker.getNavigator().getPath();
            if (attackerPath != null) {
                PathPoint finalPathPoint = attackerPath.getFinalPathPoint();
                if (finalPathPoint != null && target.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1) {
                    this.failedPathFindingPenalty = 0;
                } else {
                    this.failedPathFindingPenalty += 10;
                }
            } else {
                this.failedPathFindingPenalty += 10;
            }

            if (distanceSquared > 1024.0D) {
                this.field_75445_i += 10;
            } else if (distanceSquared > 256.0D) {
                this.field_75445_i += 5;
            }

            if (!this.attacker.getNavigator().tryMoveToEntityLiving(target, this.speedTowardsTarget)) {
                this.field_75445_i += 15;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);

        if (distanceSquared <= attackRange && this.attackTick <= 20) {
            this.attackTick = 20;
            if (this.attacker.getHeldItem() != null) {
                this.attacker.swingItem();
            }
            this.attacker.attackEntityAsMob(target);
        }
    }
}
