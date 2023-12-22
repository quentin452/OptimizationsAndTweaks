package fr.iamacat.optimizationsandtweaks.mixins.client.davincivessels;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.client.control.ShipKeyHandler;
import darkevilmac.archimedes.common.ArchimedesConfig;
import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.network.ClientOpenGuiMessage;
import darkevilmac.movingworld.MovingWorld;
import darkevilmac.movingworld.common.network.MovingWorldClientActionMessage;

@SideOnly(Side.CLIENT)
@Mixin(ShipKeyHandler.class)
public class MixinShipKeyHandler {

    @Shadow
    private ArchimedesConfig config;
    @Shadow
    private boolean kbShipGuiPrevState;
    @Shadow
    private boolean kbDisassemblePrevState;

    public MixinShipKeyHandler(ArchimedesConfig cfg) {
        this.config = cfg;
        this.kbShipGuiPrevState = this.kbDisassemblePrevState = false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void updateControl(TickEvent.PlayerTickEvent e) {
        if (!optimizationsAndTweaks$isValidUpdateEvent(e)) {
            return;
        }

        optimizationsAndTweaks$handleShipGuiKeyPress();
        optimizationsAndTweaks$handleDisassembleKeyPress(e);

        EntityShip ship = (EntityShip) e.player.ridingEntity;
        int heightControl = optimizationsAndTweaks$calculateHeightControl();
        optimizationsAndTweaks$updateShipControl(ship, e.player, heightControl);
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidUpdateEvent(TickEvent.PlayerTickEvent e) {
        return e.phase == TickEvent.Phase.START && e.side == Side.CLIENT
            && e.player == FMLClientHandler.instance()
                .getClientPlayerEntity()
            && e.player.ridingEntity instanceof EntityShip;
    }

    @Unique
    private void optimizationsAndTweaks$handleShipGuiKeyPress() {
        boolean shipGuiKeyPressed = config.kbShipInv.getIsKeyPressed();
        if (shipGuiKeyPressed && !kbShipGuiPrevState) {
            ClientOpenGuiMessage msg = new ClientOpenGuiMessage(2);
            ArchimedesShipMod.instance.network.sendToServer(msg);
        }
        kbShipGuiPrevState = shipGuiKeyPressed;
    }

    @Unique
    private void optimizationsAndTweaks$handleDisassembleKeyPress(TickEvent.PlayerTickEvent e) {
        if (!(e.player instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = e.player;
        boolean disassembleKeyPressed = config.kbDisassemble.getIsKeyPressed();

        if (disassembleKeyPressed && !kbDisassemblePrevState && (player.ridingEntity instanceof EntityShip)) {
            EntityShip ship = (EntityShip) player.ridingEntity;
            MovingWorldClientActionMessage msg = new MovingWorldClientActionMessage(
                ship,
                MovingWorldClientActionMessage.Action.DISASSEMBLE);
            MovingWorld.instance.network.sendToServer(msg);

        }
        kbDisassemblePrevState = disassembleKeyPressed;
    }

    @Unique
    private int optimizationsAndTweaks$calculateHeightControl() {
        if (config.kbAlign.getIsKeyPressed()) {
            return 4;
        } else if (config.kbBrake.getIsKeyPressed()) {
            return 3;
        } else {
            int vert = 0;
            vert += config.kbUp.getIsKeyPressed() ? 1 : 0;
            vert += config.kbDown.getIsKeyPressed() ? -1 : 0;
            return (vert < 0) ? 1 : ((vert > 0) ? 2 : 0);
        }
    }

    @Unique
    private void optimizationsAndTweaks$updateShipControl(EntityShip ship, EntityPlayer player, int heightControl) {
        if (heightControl != ship.getController()
            .getShipControl()) {
            ship.getController()
                .updateControl(ship, player, heightControl);
        }
    }
}
