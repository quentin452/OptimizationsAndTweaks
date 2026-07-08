package fr.iamacat.optimizationsandtweaks.mixins.client.essenceofthegod;

import net.essence.client.PlayerStats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.gameevent.TickEvent;

@Mixin(PlayerStats.class)
public class MixinPlayerStats {

    /**
     * @reason disable Essence of the Gods player-stats HUD rendering (feature disabled by this pack).
     *         HEAD-cancel instead of a full-method replace so any other transform on this method still
     *         applies.
     */
    @Inject(method = "renderEvent", at = @At("HEAD"), remap = false, cancellable = true)
    public void renderEvent(RenderGameOverlayEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * @reason disable Essence of the Gods player-stats tick handling (feature disabled by this pack).
     *         HEAD-cancel instead of a full-method replace so any other transform on this method still
     *         applies.
     */
    @Inject(method = "tickEvent", at = @At("HEAD"), remap = false, cancellable = true)
    public void tickEvent(TickEvent.RenderTickEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
