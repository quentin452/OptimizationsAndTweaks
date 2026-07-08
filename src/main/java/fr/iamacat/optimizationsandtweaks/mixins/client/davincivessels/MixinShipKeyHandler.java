package fr.iamacat.optimizationsandtweaks.mixins.client.davincivessels;

import org.spongepowered.asm.mixin.Mixin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import darkevilmac.archimedes.client.control.ShipKeyHandler;

/**
 * {@code updateControl} was a behavioral copy of the original Davinci Vessels method, just decomposed
 * into named helper methods: same START/CLIENT/riding-ship guard, same ship-gui/disassemble key-press
 * handling, same height-control calculation. The mixin's extra checks
 * ({@code e.player instanceof EntityPlayer}, re-checking {@code ridingEntity instanceof EntityShip}) are
 * redundant tautologies given {@code TickEvent.PlayerTickEvent#player} is already typed
 * {@code EntityPlayer} and the outer guard already confirmed the riding-ship type. The mixin constructor
 * likewise just re-assigned the same values the original constructor already sets. No real delta found
 * against decompiled Davinci Vessels 228029; deleted as a dead dupe. This empties the mixin (no members
 * left at all).
 *
 * @author OptimizationsAndTweaks
 * @reason dead whole-method @Overwrite dupe, no behavioral delta vs original Davinci Vessels ShipKeyHandler
 */
@SideOnly(Side.CLIENT)
@Mixin(ShipKeyHandler.class)
public class MixinShipKeyHandler {

}
