package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.tidychunkbackport.PlayerDroppedItemTracker;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {

    @Inject(method = "dropPlayerItemWithRandomChoice", at = @At("RETURN"))
    private void onDropPlayerItem(ItemStack itemStack, boolean unused, CallbackInfoReturnable<EntityItem> cir) {
        EntityItem droppedItem = cir.getReturnValue();
        if (droppedItem != null) {
            PlayerDroppedItemTracker.markAsPlayerDropped(droppedItem);
        }
    }
    
    @Inject(method = "func_146097_a", at = @At("RETURN"))
    private void onDropItem(ItemStack itemStack, boolean dropAround, boolean traceItem, CallbackInfoReturnable<EntityItem> cir) {
        EntityItem droppedItem = cir.getReturnValue();
        if (droppedItem != null) {
            PlayerDroppedItemTracker.markAsPlayerDropped(droppedItem);
        }
    }
}