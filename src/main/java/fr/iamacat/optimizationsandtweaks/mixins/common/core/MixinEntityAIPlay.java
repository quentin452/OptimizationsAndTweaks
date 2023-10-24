package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityAIPlay.class)
public class MixinEntityAIPlay extends EntityAIBase {

    @Shadow
    private EntityVillager villagerObj;
    @Shadow
    private EntityLivingBase targetVillager;
    @Shadow
    private double field_75261_c;
    @Shadow
    private int playTime;

    public MixinEntityAIPlay(EntityVillager p_i1646_1_, double p_i1646_2_) {
        this.villagerObj = p_i1646_1_;
        this.field_75261_c = p_i1646_2_;
        this.setMutexBits(1);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean shouldExecute() {
        if (this.villagerObj.getGrowingAge() >= 0) {
            return false;
        }

        if (this.playTime > 0) {
            return false;
        }

        if (this.targetVillager != null) {
            if (!this.targetVillager.isEntityAlive()) {
                this.targetVillager = null;
                return false;
            }

            double distanceSq = this.villagerObj.getDistanceSqToEntity(this.targetVillager);
            if (distanceSq > 4.0D) {
                this.villagerObj.getNavigator()
                    .tryMoveToEntityLiving(this.targetVillager, this.field_75261_c);
            }
            return true;
        }

        List<EntityVillager> nearbyVillagers = this.villagerObj.worldObj
            .getEntitiesWithinAABB(EntityVillager.class, this.villagerObj.boundingBox.expand(6.0D, 3.0D, 6.0D));

        double closestDistanceSq = Double.MAX_VALUE;

        for (EntityVillager entityvillager : nearbyVillagers) {
            if (entityvillager != this.villagerObj && !entityvillager.isPlaying()
                && entityvillager.getGrowingAge() < 0) {
                double distanceSq = entityvillager.getDistanceSqToEntity(this.villagerObj);
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    this.targetVillager = entityvillager;
                }
            }
        }

        if (this.targetVillager == null) {
            Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);
            if (vec3 != null) {
                this.villagerObj.getNavigator()
                    .tryMoveToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.field_75261_c);
                return true;
            }
        }
        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void startExecuting() {
        if (this.targetVillager != null) {
            this.villagerObj.setPlaying(true);
        }
        this.playTime = 1000;
    }

    public void resetTask() {
        this.villagerObj.setPlaying(false);
        this.targetVillager = null;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateTask() {
        --this.playTime;
    }
}
