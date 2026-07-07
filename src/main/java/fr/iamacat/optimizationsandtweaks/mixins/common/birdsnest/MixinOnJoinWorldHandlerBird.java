package fr.iamacat.optimizationsandtweaks.mixins.common.birdsnest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import panda.birdsnests.OnJoinWorldHandler;

@Mixin(OnJoinWorldHandler.class)
public class MixinOnJoinWorldHandlerBird {

    /**
     * @reason disable Bird Nests' per-player-tick world-join scan (feature disabled by this pack).
     *         HEAD-cancel instead of a full-method replace so any other transform on this method still
     *         applies.
     */
    @Inject(method = "onEvent", at = @At("HEAD"), remap = false, cancellable = true)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
