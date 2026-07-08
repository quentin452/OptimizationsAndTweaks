package fr.iamacat.optimizationsandtweaks.mixins.common.betterburning;

import net.darkhax.betterburning.BetterBurning;
import net.minecraftforge.event.entity.living.LivingEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Original {@code onLivingTick} dereferences {@code event.entityLiving.worldObj} unconditionally; the fix
 * only adds null guards for {@code event}/{@code event.entityLiving}/{@code worldObj} before that. This
 * injects the same combined guard at HEAD instead of copying the whole method; when it doesn't trip, the
 * original body (fire-resistance extinguish check) runs unchanged.
 *
 * @author iamacatfr
 * @reason fix null crash caused by onLivingTick from Better Burning mod
 */
@Mixin(BetterBurning.class)
public class MixinBetterBurning {

    @Inject(method = "onLivingTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$guardNullCrash(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (event == null || event.entityLiving == null || event.entityLiving.worldObj == null) {
            ci.cancel();
        }
    }
}
