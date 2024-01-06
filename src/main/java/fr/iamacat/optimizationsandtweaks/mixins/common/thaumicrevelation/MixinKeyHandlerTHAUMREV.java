package fr.iamacat.optimizationsandtweaks.mixins.common.thaumicrevelation;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumrev.item.baubles.ItemWardenAmulet;
import thaumrev.lib.network.entities.CreatePacketClientSide;
import thaumrev.lib.utils.KeyHandler;

@Mixin(KeyHandler.class)
public class MixinKeyHandlerTHAUMREV {

    @Shadow
    private long lastPressKeyAmulet = 0L;
    @Shadow
    public KeyBinding keyAmulet = new KeyBinding("key.amulet.desc", 19, "key.thaumrev.category");

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT && FMLClientHandler.instance()
            .getClient().inGameHasFocus
            && !FMLClientHandler.instance()
                .isGUIOpen(GuiChat.class)) {
            ItemStack amulet = ItemWardenAmulet.getAmulet(event.player);
            if (amulet != null && this.keyAmulet.getIsKeyPressed() && amulet.getItemDamage() == 0) {
                this.lastPressKeyAmulet = System.currentTimeMillis();
                CreatePacketClientSide.sendAmuletUsePacket(amulet.getItemDamage());
            } else if (this.lastPressKeyAmulet + 10000L >= System.currentTimeMillis()) {
                ItemWardenAmulet.amuletParticles(event.player);
            }
        }
    }
}
