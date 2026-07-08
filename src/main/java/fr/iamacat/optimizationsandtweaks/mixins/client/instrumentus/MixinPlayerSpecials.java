package fr.iamacat.optimizationsandtweaks.mixins.client.instrumentus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import info.beanbot.morepaxels.client.player.PlayerSpecials;

/**
 * Original {@code nameIsGood} calls {@code getTextWithoutFormattingCodes} + {@code equals} four times (one
 * per allow-listed name). This computes the same formatted name once and does a single HashSet lookup,
 * returning byte-for-byte the same result for every input -- a pure perf micro-opt with no observable
 * behavior change. Expressed as a HEAD-cancel + recompute since there's no single call to redirect (4
 * separate equals() calls collapse into 1 contains()).
 *
 * @author OptimizationsAndTweaks
 * @reason Optimizes PlayerSpecials class from Instrumentus (O(4) string comparisons -> O(1) set lookup).
 */
@Mixin(PlayerSpecials.class)
public class MixinPlayerSpecials {

    @Unique
    private static final Set<String> ALLOWED_NAMES = new HashSet<>(
        Arrays.asList("Beanxxbot", "TheDiscoCreeper", "Rajecent", "Hermyone"));

    @Inject(method = "nameIsGood", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsAndTweaks$fastNameCheck(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        String name = EnumChatFormatting.getTextWithoutFormattingCodes(entity.getCommandSenderName());
        cir.setReturnValue(ALLOWED_NAMES.contains(name));
    }
}
