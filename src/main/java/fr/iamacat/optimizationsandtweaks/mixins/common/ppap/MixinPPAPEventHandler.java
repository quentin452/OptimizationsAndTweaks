package fr.iamacat.optimizationsandtweaks.mixins.common.ppap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.laureegrd.ppapmod.item.ItemPPAP;
import com.laureegrd.ppapmod.util.Config;
import com.laureegrd.ppapmod.util.PPAPEventHandler;

/**
 * Original {@code onMobDrops} does {@code event.source.getEntity() instanceof EntityPlayer} (NPEs if
 * {@code event.source} is null, e.g. non-entity damage sources) and
 * {@code player.getHeldItem().getItem() instanceof ItemPPAP} (NPEs if unarmed). The fix adds null guards
 * for both before the rest of the method runs; this injects the combined guard at HEAD instead of copying
 * the whole method -- the drop-chance roll and record-drop logic are untouched original bytecode.
 *
 * @author OptimizationsAndTweaks
 * @reason Fixes null crashes caused by PPAPEventHandler from PPAP mod.
 */
@Mixin(PPAPEventHandler.class)
public class MixinPPAPEventHandler {

    @Inject(method = "onMobDrops", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$guardNullCrashes(LivingDropsEvent event, CallbackInfo ci) {
        if (Config.disableDrops || event.source == null || !(event.source.getEntity() instanceof EntityPlayer)) {
            ci.cancel();
            return;
        }
        EntityPlayer player = (EntityPlayer) event.source.getEntity();
        if (player.getHeldItem() == null || !(player.getHeldItem()
            .getItem() instanceof ItemPPAP)) {
            ci.cancel();
        }
    }
}
