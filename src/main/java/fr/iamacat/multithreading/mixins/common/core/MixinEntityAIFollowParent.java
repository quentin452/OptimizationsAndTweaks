package fr.iamacat.multithreading.mixins.common.core;

import java.util.List;

import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.passive.EntityAnimal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

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

    /**
     * Updates the task
     */
    @Overwrite
    public void updateTask() {
        if (MultithreadingandtweaksConfig.enableMixinEntityAIFollowParent) {
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

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean shouldExecute() {
        if (MultithreadingandtweaksConfig.enableMixinEntityAIFollowParent) {
            int childGrowingAge = this.childAnimal.getGrowingAge();

            if (childGrowingAge >= 0) {
                return false;
            }

            List<EntityAnimal> list = this.childAnimal.worldObj
                .getEntitiesWithinAABB(EntityAnimal.class, this.childAnimal.boundingBox.expand(8.0D, 4.0D, 8.0D));
            EntityAnimal entityanimal = null;
            double minDistanceSquared = 9.0D;

            for (EntityAnimal entityanimal1 : list) {
                int parentGrowingAge = entityanimal1.getGrowingAge();

                if (parentGrowingAge >= 0) {
                    double distanceSquared = this.childAnimal.getDistanceSqToEntity(entityanimal1);

                    if (distanceSquared < minDistanceSquared) {
                        minDistanceSquared = distanceSquared;
                        entityanimal = entityanimal1;
                    }
                }
            }

            if (entityanimal == null) {
                return false;
            }

            this.parentAnimal = entityanimal;
            return true;
        }
        return false;
    }
}
