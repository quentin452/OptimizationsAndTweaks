package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(EntityAgeable.class)
public abstract class MixinEntityAgeable extends EntityCreature {

    @Unique
    public float CHILD_SCALE = 0.5F;
    @Unique
    public int GROWING_AGE_DATA_WATCHER_ID = 12;
    @Unique
    public float ADULT_SCALE = 1.0F;

    @Shadow
    private float field_98056_d = -1.0F;
    @Shadow
    private float field_98057_e;

    public MixinEntityAgeable(World worldIn) {
        super(worldIn);
    }

    @Unique
    public int optimizationsAndTweaks$getGrowingAge() {
        return this.dataWatcher.getWatchableObjectInt(GROWING_AGE_DATA_WATCHER_ID);
    }

    @Unique
    public void optimizationsAndTweaks$setGrowingAge(int age) {
        this.dataWatcher.updateObject(GROWING_AGE_DATA_WATCHER_ID, age);
        this.optimizationsAndTweaks$setScaleForAge(this.isChild());
    }

    @Unique
    public void optimizationsAndTweaks$setScaleForAge(boolean isChild) {
        this.optimizationsAndTweaks$setScale(isChild ? CHILD_SCALE : ADULT_SCALE);
    }

    @Unique
    protected final void optimizationsAndTweaks$setScale(float scale) {
        super.setSize(this.field_98056_d * scale, this.field_98057_e * scale);
    }

    /**
     * @author iamacatfr
     * @reason r
     */
    @Overwrite
    public void addGrowth(int p_110195_1_) {
        if (OptimizationsandTweaksConfig.enableMixinEntityAgeable) {
            int currentAge = optimizationsAndTweaks$getGrowingAge();
            currentAge += p_110195_1_ * 20;

            if (currentAge > 0) {
                currentAge = 0;
            }

            optimizationsAndTweaks$setGrowingAge(currentAge);
        }
    }

    /**
     * @author iamacatfr
     * @reason t
     */
    @Overwrite
    public void onLivingUpdate() {
        if (OptimizationsandTweaksConfig.enableMixinEntityAgeable) {
            super.onLivingUpdate();

            if (this.worldObj.isRemote) {
                optimizationsAndTweaks$setScaleForAge(this.isChild());
            } else {
                int currentAge = optimizationsAndTweaks$getGrowingAge();

                if (currentAge < 0) {
                    ++currentAge;
                    optimizationsAndTweaks$setGrowingAge(currentAge);
                } else if (currentAge > 0) {
                    --currentAge;
                    optimizationsAndTweaks$setGrowingAge(currentAge);
                }
            }
        }
    }

    public boolean isChild() {
        return optimizationsAndTweaks$getGrowingAge() < 0;
    }
}
