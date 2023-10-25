package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityAIAttackOnCollide.class)
public class MixinEntityAIAttackOnCollide {

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

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean shouldExecute() {
        EntityLivingBase targetEntity = this.attacker.getAttackTarget();

        if (targetEntity == null || !targetEntity.isEntityAlive() || (this.classTarget != null && !this.classTarget.isAssignableFrom(targetEntity.getClass()))) {
            return false;
        }

        if (--this.field_75445_i <= 0 || this.entityPathEntity == null) {
            this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(targetEntity);
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
        EntityLivingBase targetEntity = this.attacker.getAttackTarget();

        if (targetEntity == null || !targetEntity.isEntityAlive()) {
            return false;
        }

        if (!this.longMemory) {
            return !this.attacker.getNavigator().noPath();
        } else {
            int posX = MathHelper.floor_double(targetEntity.posX);
            int posY = MathHelper.floor_double(targetEntity.posY);
            int posZ = MathHelper.floor_double(targetEntity.posZ);
            return this.attacker.isWithinHomeDistance(posX, posY, posZ);
        }
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
    public void updateTask() {
        EntityLivingBase targetEntity = this.attacker.getAttackTarget();

        if (targetEntity == null) {
            return;
        }

        this.attacker.getLookHelper().setLookPositionWithEntity(targetEntity, 30.0F, 30.0F);

        double distanceSq = this.attacker.getDistanceSq(targetEntity.posX, targetEntity.boundingBox.minY, targetEntity.posZ);
        double maxAttackDistance = this.attacker.width * 2.0F * this.attacker.width * 2.0F + targetEntity.width;

        if (distanceSq <= maxAttackDistance && this.attackTick <= 20) {
            this.attackTick = 20;
            this.attacker.swingItem();

            if (this.attacker.getHeldItem() != null) {
                this.attacker.attackEntityAsMob(targetEntity);
            }
        } else if (this.longMemory || this.attacker.getEntitySenses().canSee(targetEntity)) {
            if (this.field_75445_i <= 0) {
                this.multithreadingandtweaks$updatePathToTarget(targetEntity);
            } else {
                this.field_75445_i--;
            }
        }
    }

    @Unique
    private void multithreadingandtweaks$updatePathToTarget(EntityLivingBase targetEntity) {
        this.field_151497_i = targetEntity.posX;
        this.field_151495_j = targetEntity.boundingBox.minY;
        this.field_151496_k = targetEntity.posZ;

        if (this.attacker.getNavigator().getPath() == null || this.field_75445_i <= 0) {
            this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(targetEntity);
            this.field_75445_i = failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

            if (this.attacker.getNavigator().getPath() != null) {
                PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();

                if (finalPathPoint != null && targetEntity.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1) {
                    failedPathFindingPenalty = 0;
                } else {
                    failedPathFindingPenalty += 10;
                }
            } else {
                failedPathFindingPenalty += 10;
            }

            double distanceSq = this.attacker.getDistanceSq(targetEntity.posX, targetEntity.boundingBox.minY, targetEntity.posZ);

            if (distanceSq > 1024.0D) {
                this.field_75445_i += 10;
            } else if (distanceSq > 256.0D) {
                this.field_75445_i += 5;
            }

            if (!this.attacker.getNavigator().tryMoveToEntityLiving(targetEntity, this.speedTowardsTarget)) {
                this.field_75445_i += 15;
            }
        }
    }
}
