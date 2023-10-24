package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityAIAttackOnCollide.class)
public class MixinEntityAIAttackOnCollide {

    @Shadow
    World worldObj;
    @Shadow
    EntityCreature attacker;
    /** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
    @Shadow
    int attackTick;
    /** The speed with which the mob will approach the target */
    @Shadow
    double speedTowardsTarget;
    /** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
    @Shadow
    boolean longMemory;
    /** The PathEntity of our entity. */
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

    @Inject(method = "updateTask", at = @At("HEAD"), cancellable = true)
    public void updateTask(CallbackInfo ci) {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null) {
            return;
        }

        this.attacker.getLookHelper()
            .setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);

        double d0 = this.attacker
            .getDistanceSq(entitylivingbase.posX, entitylivingbase.boundingBox.minY, entitylivingbase.posZ);
        double d1 = (this.attacker.width * 2.0F * this.attacker.width * 2.0F + entitylivingbase.width);

        if (d0 <= d1 && this.attackTick <= 20) {
            this.attackTick = 20;

            if (this.attacker.getHeldItem() != null) {
                this.attacker.swingItem();
            }

            this.attacker.attackEntityAsMob(entitylivingbase);
            return;
        }

        if (this.longMemory || this.attacker.getEntitySenses()
            .canSee(entitylivingbase)) {
            --this.field_75445_i;

            if (this.field_75445_i <= 0) {
                this.field_151497_i = entitylivingbase.posX;
                this.field_151495_j = entitylivingbase.boundingBox.minY;
                this.field_151496_k = entitylivingbase.posZ;
                this.field_75445_i = failedPathFindingPenalty + 4
                    + this.attacker.getRNG()
                        .nextInt(7);

                if (this.attacker.getNavigator()
                    .getPath() != null) {
                    PathPoint finalPathPoint = this.attacker.getNavigator()
                        .getPath()
                        .getFinalPathPoint();

                    if (finalPathPoint != null && entitylivingbase
                        .getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1) {
                        failedPathFindingPenalty = 0;
                    } else {
                        failedPathFindingPenalty += 10;
                    }
                } else {
                    failedPathFindingPenalty += 10;
                }

                if (d0 > 1024.0D) {
                    this.field_75445_i += 10;
                } else if (d0 > 256.0D) {
                    this.field_75445_i += 5;
                }

                if (!this.attacker.getNavigator()
                    .tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget)) {
                    this.field_75445_i += 15;
                }
            }
        }
        ci.cancel();
    }
}
