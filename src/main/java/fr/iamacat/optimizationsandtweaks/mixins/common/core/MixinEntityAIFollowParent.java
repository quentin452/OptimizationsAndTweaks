package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.passive.EntityAnimal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(EntityAIFollowParent.class)
public class MixinEntityAIFollowParent {

    @Shadow
    EntityAnimal childAnimal;
    @Shadow
    double field_75347_c;
    @Shadow
    EntityAnimal parentAnimal;
    @Shadow
    private int field_75345_d;

    @Unique
    private List<EntityAnimal> multithreadingandtweaks$cachedEntitiesWithinAABB = null;

    @Unique
    private List<EntityAnimal> multithreadingandtweaks$getCachedEntitiesWithinAABB() {
        if (multithreadingandtweaks$cachedEntitiesWithinAABB == null) {
            multithreadingandtweaks$cachedEntitiesWithinAABB = this.childAnimal.worldObj.getEntitiesWithinAABB(
                this.childAnimal.getClass(),
                this.childAnimal.boundingBox.expand(8.0D, 4.0D, 8.0D));
        }
        return multithreadingandtweaks$cachedEntitiesWithinAABB;
    }

    /**
     * Updates the task
     */
    @Overwrite
    public boolean continueExecuting() {
        return this.parentAnimal != null && this.parentAnimal.isEntityAlive()
            && this.childAnimal.getDistanceSqToEntity(this.parentAnimal) >= 9.0D;
    }

    /**
     * Updates the task
     */
    @Overwrite
    public void updateTask() {
        if (OptimizationsandTweaksConfig.enableMixinEntityAIFollowParent) {
            if (--this.field_75345_d <= 0) {
                this.field_75345_d = 10;

                if (this.childAnimal != null && this.parentAnimal != null) {
                    double distance = this.childAnimal.getDistanceSqToEntity(this.parentAnimal);

                    if (distance > 2.0) {
                        this.childAnimal.getNavigator()
                            .tryMoveToEntityLiving(this.parentAnimal, this.field_75347_c);
                    }
                }
            }
        }
    }

    @Unique
    private Map<EntityAnimal, Integer> multithreadingandtweaks$growingAgeCache = new HashMap<>();

    /**
     * @author iamacatfr
     * @reason fix a large bottleneck/freeze tps when there are alot of babies in the game caused by GrowingAge by
     *         adding a cache
     */
    @Overwrite
    public boolean shouldExecute() {
        List<EntityAnimal> nearbyEntities = multithreadingandtweaks$getCachedEntitiesWithinAABB();
        double minDistanceSq = Double.MAX_VALUE;
        boolean hasValidParent = false;

        for (EntityAnimal entity : nearbyEntities) {
            Integer growingAge = multithreadingandtweaks$growingAgeCache.get(entity);

            if (growingAge == null) {
                growingAge = entity.getGrowingAge();
                multithreadingandtweaks$growingAgeCache.put(entity, growingAge);
            }

            if (growingAge <= 0) {
                double distanceSq = this.childAnimal.getDistanceSqToEntity(entity);

                if (distanceSq <= this.field_75347_c && distanceSq < minDistanceSq) {
                    minDistanceSq = distanceSq;
                    this.parentAnimal = entity;
                    hasValidParent = true;
                }
            }
        }

        if (hasValidParent && this.childAnimal.getGrowingAge() <= 0) {
            while (continueExecuting()) {
                this.childAnimal.getNavigator()
                    .tryMoveToEntityLiving(this.parentAnimal, this.field_75347_c);
            }
        }

        return hasValidParent && this.childAnimal.getDistanceToEntity(this.parentAnimal) > 5.0;
    }
}
