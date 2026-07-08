package fr.iamacat.optimizationsandtweaks.mixins.common.shincolle;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.lulan.shincolle.handler.EVENT_BUS_EventHandler;

/**
 * Original {@code onEntityConstructing} checks {@code player.getExtendedProperties("TeitokuExtProps") ==
 * null} once and then unconditionally registers -- re-entrant construction (the ShinColle/Custom Mob
 * Spawner interaction) can re-enter this handler for the SAME player before the first registration
 * completes, double-registering and stack-overflowing. The fix re-checks the same condition immediately
 * before the {@code registerExtendedProperties} call for the player branch (the ship branch is untouched).
 * Redirecting that one call preserves the rest of the method (including the ship branch and both debug
 * logs) as original bytecode.
 *
 * @author iamacatfr
 * @reason fix stackoverflow between Custom Mob Spawner and ShinColle
 */
@Mixin(EVENT_BUS_EventHandler.class)
public class MixinEVENT_BUS_EventHandler {

    @Redirect(
        method = "onEntityConstructing",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;registerExtendedProperties(Ljava/lang/String;Lnet/minecraftforge/common/IExtendedEntityProperties;)V",
            ordinal = 1),
        remap = false)
    private void optimizationsandtweaks$guardDoubleRegister(EntityPlayer player, String identifier,
        IExtendedEntityProperties props) {
        if (player.getExtendedProperties(identifier) == null) {
            player.registerExtendedProperties(identifier, props);
        }
    }
}
