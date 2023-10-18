package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityAgeable.class)
public abstract class MixinEntityAgeable extends EntityCreature {
    @Unique
    public float CHILD_SCALE = 0.5F;
    @Unique
    public int GROWING_AGE_DATA_WATCHER_ID = 12;
    @Unique
    public float ADULT_SCALE = 1.0F;

    @Shadow private float field_98056_d = -1.0F;
    @Shadow private float field_98057_e;

    public MixinEntityAgeable(World worldIn) {
        super(worldIn);
    }

    @Unique
    public int multithreadingandtweaks$getGrowingAge() {
        return this.dataWatcher.getWatchableObjectInt(GROWING_AGE_DATA_WATCHER_ID);
    }

    @Unique
    public void multithreadingandtweaks$setGrowingAge(int age) {
        this.dataWatcher.updateObject(GROWING_AGE_DATA_WATCHER_ID, age);
        this.multithreadingandtweaks$setScaleForAge(this.isChild());
    }

    @Unique
    public void multithreadingandtweaks$setScaleForAge(boolean isChild) {
        this.multithreadingandtweaks$setScale(isChild ? CHILD_SCALE : ADULT_SCALE);
    }

    @Unique
    protected final void multithreadingandtweaks$setScale(float scale) {
        super.setSize(this.field_98056_d * scale, this.field_98057_e * scale);
    }

    /**
     * @author iamacatfr
     * @reason r
     */
    @Overwrite
    public void addGrowth(int p_110195_1_){
        if (MultithreadingandtweaksConfig.enableMixinEntityAgeable) {
            int currentAge = multithreadingandtweaks$getGrowingAge();
            currentAge += p_110195_1_ * 20;

            if (currentAge > 0) {
                currentAge = 0;
            }

            multithreadingandtweaks$setGrowingAge(currentAge);
        }
    }

    /**
     * @author iamacatfr
     * @reason t
     */
    @Overwrite
    public void onLivingUpdate() {
        if (MultithreadingandtweaksConfig.enableMixinEntityAgeable) {
            super.onLivingUpdate();

            if (this.worldObj.isRemote) {
                multithreadingandtweaks$setScaleForAge(this.isChild());
            } else {
                int currentAge = multithreadingandtweaks$getGrowingAge();

                if (currentAge < 0) {
                    ++currentAge;
                    multithreadingandtweaks$setGrowingAge(currentAge);
                } else if (currentAge > 0) {
                    --currentAge;
                    multithreadingandtweaks$setGrowingAge(currentAge);
                }
            }
        }
    }

    public boolean isChild() {
        return multithreadingandtweaks$getGrowingAge() < 0;
    }
}
