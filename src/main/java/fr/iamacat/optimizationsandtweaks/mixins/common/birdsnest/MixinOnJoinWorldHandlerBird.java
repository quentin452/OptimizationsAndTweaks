package fr.iamacat.optimizationsandtweaks.mixins.common.birdsnest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import panda.birdsnests.OnJoinWorldHandler;

@Mixin(OnJoinWorldHandler.class)
public class MixinOnJoinWorldHandlerBird {

    @Overwrite(remap = false)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(TickEvent.PlayerTickEvent event) {}
}
