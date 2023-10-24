package fr.iamacat.optimizationsandtweaks.mixins.common.minestones;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sinkillerj.minestones.ItemMinestone;

@Mixin(ItemMinestone.class)
public class MixinItemMinestone extends Item {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void setMaxStackSize(CallbackInfo info) {
        ((ItemMinestone) (Object) this).setMaxStackSize(64);
    }
}
