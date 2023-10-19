package fr.iamacat.multithreading.mixins.common.core.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(EntityZombie.class)
public class MixinEntityZombie extends EntityMob {

    public MixinEntityZombie(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    public void onLivingUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntityZombie) {
            super.onLivingUpdate();
            // Add a check for the conditions where the entity can catch fire
            if (multithreadingandtweaks$shouldCatchFire()) {
                multithreadingandtweaks$catchFire();
            }

            // Handle riding logic
            multithreadingandtweaks$handleRiding();
            ci.cancel();
        }
    }

    @Unique
    private boolean multithreadingandtweaks$shouldCatchFire() {
        return worldObj.isDaytime() && !worldObj.isRemote
            && !multithreadingandtweaks$isChild()
            && multithreadingandtweaks$shouldCatchFireByBrightness();
    }

    @Unique
    private boolean multithreadingandtweaks$shouldCatchFireByBrightness() {
        float brightness = getBrightness(1.0F);
        return brightness > 0.5F && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
            && worldObj.canBlockSeeTheSky(
                MathHelper.floor_double(posX),
                MathHelper.floor_double(posY),
                MathHelper.floor_double(posZ));
    }

    @Unique
    private void multithreadingandtweaks$catchFire() {
        ItemStack helmet = getEquipmentInSlot(4);
        if (helmet != null) {
            if (multithreadingandtweaks$handleDamageableItem(helmet)) {
                return;
            }
        }

        setFire(8);
    }

    @Unique
    private boolean multithreadingandtweaks$handleDamageableItem(ItemStack itemStack) {
        if (itemStack.isItemStackDamageable()) {
            itemStack.setItemDamage(itemStack.getItemDamageForDisplay() + rand.nextInt(2));
            if (itemStack.getItemDamageForDisplay() >= itemStack.getMaxDamage()) {
                renderBrokenItemStack(itemStack);
                setCurrentItemOrArmor(4, null);
            }
            return true;
        }
        return false;
    }

    @Unique
    private void multithreadingandtweaks$handleRiding() {
        if (isRiding() && getAttackTarget() != null && ridingEntity instanceof EntityChicken) {
            ((EntityLiving) ridingEntity).getNavigator()
                .setPath(getNavigator().getPath(), 1.5D);
        }
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    @Unique
    public boolean multithreadingandtweaks$isChild() {
        return this.getDataWatcher()
            .getWatchableObjectByte(12) == 1;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound() {
        return "mob.zombie.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound() {
        return "mob.zombie.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound() {
        return "mob.zombie.death";
    }
}
