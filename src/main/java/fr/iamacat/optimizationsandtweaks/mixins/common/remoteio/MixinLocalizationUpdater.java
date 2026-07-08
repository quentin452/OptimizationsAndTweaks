package fr.iamacat.optimizationsandtweaks.mixins.common.remoteio;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import remoteio.common.core.handler.LocalizationUpdater;

/**
 * Disables LocalizationUpdater from RemoteIO.
 */
@Mixin(LocalizationUpdater.class)
public class MixinLocalizationUpdater {

    // disabling localizationUpdater; HEAD-cancel instead of full replace so any other transform on
    // this method still applies.
    @Inject(method = "loadLangFiles", at = @At("HEAD"), remap = false, cancellable = true)
    private void loadLangFiles(CallbackInfo ci) {
        ci.cancel();
    }

}
