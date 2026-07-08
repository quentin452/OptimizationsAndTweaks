package fr.iamacat.optimizationsandtweaks.mixins.common.automagy;

import org.spongepowered.asm.mixin.Mixin;

import tuhljin.automagy.lib.events.AutomagyEventHandler;

/**
 * {@code getNearbyPlayerWithItem} was a behavioral copy of the original Automagy method: same distance
 * check, same {@code TjUtil.playerHasItem} call, same result -- the only differences are an enhanced-for
 * loop instead of an indexed one and a redundant (always-true) {@code instanceof EntityPlayer} check on
 * {@code world.playerEntities}, neither of which changes behavior. No real delta found against decompiled
 * Automagy 222153; deleted as a dead dupe. This empties the mixin (no members left at all).
 *
 * @author OptimizationsAndTweaks
 * @reason dead whole-method @Overwrite dupe, no behavioral delta vs original Automagy AutomagyEventHandler
 */
@Mixin(AutomagyEventHandler.class)
public class MixinAutomagyEventHandler {

}
