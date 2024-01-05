package fr.iamacat.optimizationsandtweaks.mixins.common.thaumicrevelation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import thaumrev.api.wardenic.WardenicChargeEvents;
import thaumrev.item.ItemWardenWeapon;
import thaumrev.item.armor.ItemWardenArmor;

@Mixin(WardenicChargeEvents.class)
public class MixinWardenicChargeEvents {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entity;
        for (int i = 0; i < 4; ++i) {
            ItemStack armor = player.getCurrentArmor(i);
            if (armor != null && armor.getItem() instanceof ItemWardenArmor) {
                armor.setItemDamage(0);
            }
        }

        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof ItemWardenWeapon) {
            heldItem.setItemDamage(0);
        }
    }
}
