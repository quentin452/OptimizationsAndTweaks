package fr.iamacat.optimizationsandtweaks.mixins.common.malcore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mal.core.version.VersionInfo;

/**
 * Removes the version check from the Mal Core mod.
 */
@Mixin(VersionInfo.class)
public class MixinVersionInfo {

    /**
     * @reason remove version check from Mal Core mod. HEAD-cancel instead of full replace so any
     *         other transform on this method still applies.
     */
    @Inject(method = "checkForNewVersion", at = @At("HEAD"), remap = false, cancellable = true)
    public void checkForNewVersion(CallbackInfo ci) {
        ci.cancel();
    }
}
