package fr.iamacat.optimizationsandtweaks.mixins.common.lotrimprovements;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jediexe.lotrimprovements.Main;

/**
 * Disables the attack indicator from LOTR Improvements if the personal LOTR fork is installed.
 */
@Mixin(Main.class)
public class MixinMain {

    /**
     * @reason disable attackindicator from Lotr Improvements. HEAD-cancel instead of full replace so
     *         any other transform on this method still applies.
     */
    @Inject(method = "LOTROverride", at = @At("HEAD"), remap = false, cancellable = true)
    public static void LOTROverride(CallbackInfo ci) {
        ci.cancel();
    }
}
