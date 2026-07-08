package fr.iamacat.optimizationsandtweaks.mixins.common.hardcorewither;

import net.minecraftforge.event.entity.living.LivingEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import thor12022.hardcorewither.EventHandler;
import thor12022.hardcorewither.powerUps.PowerUpManager;

/**
 * Original {@code onLivingUpdate} dereferences {@code event.entity.worldObj} and
 * {@code this.powerUpManager} unconditionally (besides the Wither-class check). The fix adds
 * {@code event.entity != null} and {@code powerUpManager != null} guards; injecting them at HEAD preserves
 * the rest (the Wither-class check and the actual {@code powerUpManager.update(...)} call) as original
 * bytecode.
 *
 * @author iamacatfr
 * @reason fix null crash caused by onLivingUpdate from Hardcore Wither mod
 */
@Mixin(EventHandler.class)
public class MixinEventHandler {

    @Shadow
    private PowerUpManager powerUpManager;

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$guardNullCrash(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (event.entity == null || this.powerUpManager == null) {
            ci.cancel();
        }
    }
}
