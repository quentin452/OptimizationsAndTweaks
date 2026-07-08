package fr.iamacat.optimizationsandtweaks.mixins.common.potionshards;

import net.minecraftforge.event.world.BlockEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.posh.EventHandler;

/**
 * Original {@code onHarvest} calls {@code event.harvester.getHeldItem()} unconditionally per ore check --
 * NPEs when the block is harvested by something other than a player-with-a-held-item-check path (e.g. an
 * explosion or non-player harvester leaves {@code event.harvester} null). The fix adds a single
 * {@code event.harvester != null} guard before all 9 ore checks; injecting it at HEAD preserves the
 * original per-ore drop logic untouched (when {@code event.harvester} isn't null, the original's own
 * {@code getHeldItem() != null} check already guards the rest).
 *
 * @author OptimizationsAndTweaks
 * @reason Fixes null crashes caused by PotionShards' EventHandler#onHarvest when harvested by a non-player
 *         source.
 */
@Mixin(EventHandler.class)
public class MixinEventHandlerPotionShards {

    @Inject(method = "onHarvest", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$guardNullHarvester(BlockEvent.HarvestDropsEvent event, CallbackInfo ci) {
        if (event.harvester == null) {
            ci.cancel();
        }
    }
}
