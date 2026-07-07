package fr.iamacat.optimizationsandtweaks.mixins.client.manametalmod;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Prevents a client crash on entering survival caused by ManaMetalMod's HUD handler.
 * <p>
 * {@code project.studio.manametalmod.event.EventGUI#onGame} unconditionally calls
 * {@code setCanceled(true)} on the {@link RenderGameOverlayEvent} for the HEALTH and BOSSHEALTH
 * element types. When the uncancelable {@code .Post} variant fires — e.g. via Tinkers' Construct's
 * health-bar renderer while the survival HUD draws — {@code setCanceled} throws
 * {@code IllegalArgumentException} and hard-crashes the client.
 * <p>
 * The ideal fix (a no-op {@code Event#setCanceled} for uncancelable events) is impossible here:
 * {@code cpw.mods.fml.common.eventhandler.Event} is loaded during FML bootstrap, before any mixin
 * or coremod transformer can touch it. So each offending handler is guarded individually. This
 * redirect keeps ManaMetal's HUD working (it still cancels the cancelable {@code .Pre} events) and
 * only skips the illegal cancel on the uncancelable ones.
 */
@Mixin(targets = "project.studio.manametalmod.event.EventGUI", remap = false)
public class MixinEventGUI {

    @Redirect(
        method = "onGame",
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/RenderGameOverlayEvent;setCanceled(Z)V"),
        remap = false)
    private void optimizationsandtweaks$guardUncancelable(RenderGameOverlayEvent event, boolean cancel) {
        if (event.isCancelable()) {
            event.setCanceled(cancel);
        }
    }
}
