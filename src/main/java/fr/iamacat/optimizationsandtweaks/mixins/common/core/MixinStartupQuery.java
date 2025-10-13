package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.StartupQuery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.StartupQueryState;

@Mixin(StartupQuery.class)
public class MixinStartupQuery {

    @Inject(method = "confirm", at = @At("HEAD"), cancellable = true, remap = false)
    private static void optimizationsAndTweaks$handleAutoConfirm(String text, CallbackInfoReturnable<Boolean> cir) {
        if (StartupQueryState.hasConfirmedOnce() && StartupQueryState.isWorldRepairQuery(text)) {
            FMLLog.info("[Optimizations] Auto-confirming world repair query due to previous confirmation.");
            cir.setReturnValue(true);
            return;
        }
        
        StartupQueryState.updateLastQueryText(text);
    }

    @Inject(method = "confirm", at = @At("RETURN"), remap = false)
    private static void optimizationsAndTweaks$trackConfirmation(String text, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() != null && cir.getReturnValue() && StartupQueryState.isWorldRepairQuery(text)) {
            StartupQueryState.markConfirmedOnce();
        }
    }
}