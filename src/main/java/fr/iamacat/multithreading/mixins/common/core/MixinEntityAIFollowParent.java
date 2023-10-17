package fr.iamacat.multithreading.mixins.common.core;

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

}
