package fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.ai;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAIFollowParent2 extends EntityAIBase {

    private final EntityAnimal childAnimal;
    private final double followDistanceSq;
    private EntityAnimal parentAnimal;

    public EntityAIFollowParent2(EntityAnimal childAnimal, double followDistance) {
        this.childAnimal = childAnimal;
        this.followDistanceSq = followDistance * followDistance;
    }

    private List<EntityAnimal> cachedEntitiesWithinAABB = null;

    private List<EntityAnimal> getCachedEntitiesWithinAABB() {
        if (cachedEntitiesWithinAABB == null) {
            cachedEntitiesWithinAABB = this.childAnimal.worldObj.getEntitiesWithinAABB(
                this.childAnimal.getClass(),
                this.childAnimal.boundingBox.expand(8.0D, 4.0D, 8.0D));
        }
        return cachedEntitiesWithinAABB;
    }

    public boolean continueExecuting() {
        return this.parentAnimal != null && this.parentAnimal.isEntityAlive()
            && this.childAnimal.getDistanceSqToEntity(this.parentAnimal) >= 9.0D;
    }

    public boolean shouldExecute() {
        List<EntityAnimal> nearbyEntities = getCachedEntitiesWithinAABB();
        double minDistanceSq = Double.MAX_VALUE;
        boolean hasValidParent = false;

        for (EntityAnimal entity : nearbyEntities) {
            int growingAge = entity.getGrowingAge();
            if (growingAge <= 0) {
                double distanceSq = this.childAnimal.getDistanceSqToEntity(entity);

                if (distanceSq <= this.followDistanceSq && distanceSq < minDistanceSq) {
                    minDistanceSq = distanceSq;
                    this.parentAnimal = entity;
                    hasValidParent = true;
                }
            }
        }

        if (hasValidParent && this.childAnimal.getGrowingAge() <= 0) {
            while (continueExecuting()) {
                this.childAnimal.getNavigator()
                    .tryMoveToEntityLiving(this.parentAnimal, this.followDistanceSq);
            }
        }

        return hasValidParent && this.childAnimal.getDistanceToEntity(this.parentAnimal) > 5.0;
    }
}
