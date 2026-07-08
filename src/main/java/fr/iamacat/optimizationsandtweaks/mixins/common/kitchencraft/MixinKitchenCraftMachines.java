package fr.iamacat.optimizationsandtweaks.mixins.common.kitchencraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.wyldmods.kitchencraft.machines.KitchenCraftMachines;

/**
 * KitchenCraft's {@code loadRF()} prints "Initialized RF value to ..." once (on the first call, guarded
 * by {@code rfCheckLoaded}) and then ALSO prints "Checking RF: ..." on every single call, unconditionally
 * -- spam on every check. This drops only the second, unconditional println; the first (one-time) println
 * and the RF-detection logic itself are untouched original bytecode.
 *
 * @author OptimizationsAndTweaks
 * @reason Removes the unconditional per-call "Checking RF" println spam from KitchenCraft - Machines.
 */
@Mixin(KitchenCraftMachines.class)
public class MixinKitchenCraftMachines {

    @Redirect(
        method = "loadRF",
        at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", ordinal = 1),
        remap = false)
    private static void optimizationsandtweaks$skipCheckingRfLog(java.io.PrintStream out, String message) {
        // no-op: drop the noisy unconditional "Checking RF: ..." println (see class javadoc)
    }
}
