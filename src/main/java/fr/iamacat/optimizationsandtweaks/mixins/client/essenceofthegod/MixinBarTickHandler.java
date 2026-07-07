package fr.iamacat.optimizationsandtweaks.mixins.client.essenceofthegod;

import net.essence.client.BarTickHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mixin(BarTickHandler.class)
public class MixinBarTickHandler {

    /**
     * @reason disable Essence of the Gods bar rendering (feature disabled by this pack). HEAD-cancel
     *         instead of a full-method replace so any other transform on this method still applies.
     */
    @Inject(method = "onRender", at = @At("HEAD"), remap = false, cancellable = true)
    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
