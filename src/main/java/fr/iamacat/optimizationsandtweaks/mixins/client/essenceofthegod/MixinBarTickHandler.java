package fr.iamacat.optimizationsandtweaks.mixins.client.essenceofthegod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.essence.client.BarTickHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BarTickHandler.class)
public class MixinBarTickHandler {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {

    }
}
