package fr.iamacat.optimizationsandtweaks.mixins.common.farlanders;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabiulu.farlanders.common.entity.EntityEnderminion;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(EntityEnderminion.class)
public abstract class MixinEntityEnderminion extends EntityTameable {

    public MixinEntityEnderminion(World p_i1604_1_) {
        super(p_i1604_1_);
    }

    @Inject(method = "func_70639_aQ", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70639_aQ(CallbackInfoReturnable<String> cir) {
        if (OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod) {

            if (this.rand.nextInt(2) == 0) {
                this.worldObj.playSoundAtEntity(this, "farlanders:enderminionidle", 0.5F, 1.4F);
            }
        }
        cir.setReturnValue(null);
    }

    @Inject(method = "func_70621_aR", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70621_aR(CallbackInfoReturnable<String> cir) {
        if (OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod) {
            this.worldObj.playSoundAtEntity(this, "farlanders:enderminionhit", 0.5F, 1.6F);
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "func_70673_aS", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70673_aS(CallbackInfoReturnable<String> cir) {
        if (OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod) {
            this.worldObj.playSoundAtEntity(this, "farlanders:enderminiondeath", 0.5F, 1.4F);
            cir.setReturnValue(null);
        }

    }
}
