package fr.iamacat.optimizationsandtweaks.mixins.client.essenceofthegod;

import net.essence.client.PlayerStats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mixin(PlayerStats.class)
public class MixinPlayerStats {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void renderEvent(RenderGameOverlayEvent event) {}

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void tickEvent(TickEvent.RenderTickEvent event) {}
}
