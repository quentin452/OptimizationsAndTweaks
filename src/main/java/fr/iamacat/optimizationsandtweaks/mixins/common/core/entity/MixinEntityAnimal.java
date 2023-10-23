package fr.iamacat.optimizationsandtweaks.mixins.common.core.entity;

import java.util.Random;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.config.MultithreadingandtweaksConfig;

@Mixin(EntityAnimal.class)
public abstract class MixinEntityAnimal extends EntityAgeable implements IAnimals {

    @Shadow
    private int inLove;
    @Shadow
    private int breeding;

    @Unique
    private static Random multithreadingandtweaks$random = new Random();

    public MixinEntityAnimal(World p_i1578_1_) {
        super(p_i1578_1_);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Overwrite
    public void onLivingUpdate() {
        if (MultithreadingandtweaksConfig.enableMixinEntityAnimal) {
            super.onLivingUpdate();

            if (this.getGrowingAge() != 0) {
                this.inLove = 0;
            }

            if (this.inLove > 0) {
                --this.inLove;
                String s = "heart";

                if (this.inLove % 10 == 0) {
                    double d0 = multithreadingandtweaks$random.nextGaussian() * 0.02D;
                    double d1 = multithreadingandtweaks$random.nextGaussian() * 0.02D;
                    double d2 = multithreadingandtweaks$random.nextGaussian() * 0.02D;
                    this.worldObj.spawnParticle(
                        s,
                        this.posX + (double) (multithreadingandtweaks$random.nextFloat() * this.width * 2.0F)
                            - (double) this.width,
                        this.posY + 0.5D + (double) (multithreadingandtweaks$random.nextFloat() * this.height),
                        this.posZ + (double) (multithreadingandtweaks$random.nextFloat() * this.width * 2.0F)
                            - (double) this.width,
                        d0,
                        d1,
                        d2);
                }
            } else {
                this.breeding = 0;
            }
        }
    }
}
