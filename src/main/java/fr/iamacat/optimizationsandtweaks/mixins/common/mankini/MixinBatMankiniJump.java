package fr.iamacat.optimizationsandtweaks.mixins.common.mankini;

import net.minecraftforge.event.entity.living.LivingFallEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import matgm50.mankini.util.BatMankiniJump;

@Mixin(BatMankiniJump.class)
public class MixinBatMankiniJump {

    /**
     * @reason disabling anti fall damage. HEAD-cancel instead of full replace so any other
     *         transform on this method still applies.
     */
    @Inject(method = "PlayerFall", at = @At("HEAD"), remap = false, cancellable = true)
    public void PlayerFall(LivingFallEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
