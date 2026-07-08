package fr.iamacat.optimizationsandtweaks.mixins.common.weathercarpet;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mc.Mitchellbrine.anchormanMod.util.CloudChecking;

/**
 * Disables the version check from the Weather Carpet mod.
 */
@Mixin(CloudChecking.class)
public class MixinCloudChecking {

    /**
     * @reason disable the Weather Carpet version check (feature disabled by this pack). HEAD-cancel with a
     *         computed return value instead of a full-method replace so any other transform on this method
     *         still applies.
     */
    @Inject(method = "userValidation", at = @At("HEAD"), remap = false, cancellable = true)
    private static void userValidation(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
