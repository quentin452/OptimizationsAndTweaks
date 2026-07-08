package fr.iamacat.optimizationsandtweaks.mixins.common.growthcraft;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import growthcraft.apples.GrowthCraftApples;
import growthcraft.apples.handler.AppleFuelHandler;

/**
 * Growthcraft's {@code AppleFuelHandler#getBurnTime} crashes with an NPE when
 * {@code GrowthCraftApples.blocks.appleSapling} (or its {@code getItem()}) isn't initialized yet -- the
 * original unconditionally calls {@code appleSapling.getItem().equals(item)} once {@code fuel != null}.
 * This injects the missing null guard at HEAD and cancels with the same fallback ({@code 0}) the original
 * falls through to; when the guard doesn't trip, the original body runs unchanged and safely.
 *
 * @author iamacatfr
 * @reason add null check
 */
@Mixin(AppleFuelHandler.class)
public class MixinAppleFuelHandler {

    @Inject(method = "getBurnTime", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$guardNullSapling(ItemStack fuel, CallbackInfoReturnable<Integer> cir) {
        if (fuel != null) {
            Item item = fuel.getItem();
            if (item == null || GrowthCraftApples.blocks.appleSapling == null
                || GrowthCraftApples.blocks.appleSapling.getItem() == null) {
                cir.setReturnValue(0);
            }
        }
    }
}
