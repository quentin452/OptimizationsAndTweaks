package fr.iamacat.optimizationsandtweaks.mixins.common.cofhcore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cofh.mod.updater.UpdateCheckThread;

@Mixin(UpdateCheckThread.class)
public class MixinUpdateCheckThreadCOFH {

    /**
     * @reason disabling update checks to reduce CPU time from COFH mods. HEAD-cancel instead of a
     *         full-method replace so any other transform on this method still applies.
     */
    @Inject(method = "run", at = @At("HEAD"), remap = false, cancellable = true)
    public void run(CallbackInfo ci) {
        ci.cancel();
    }
}
