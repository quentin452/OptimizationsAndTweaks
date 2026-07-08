package fr.iamacat.optimizationsandtweaks.mixins.common.eternalfrost;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import eternalfrost.EFConfiguration;
import eternalfrost.EternalFrost;

/**
 * Widens Eternal Frost's dimension/biome ID config bounds from vanilla's 255 cap to 65536, so they don't
 * silently clamp/crash on modpacks with an extended ID space (dimension IDs and the 7 biome IDs).
 * <p>
 * The original {@code init(File)} calls {@code Configuration#getInt} 8 times (dimensionID + 7 biome IDs),
 * each with an upper-bound literal of {@code 255}; the fix is purely that one literal, so it's expressed
 * as a single {@link ModifyConstant} matching every {@code 255} in the method instead of copying the
 * whole method body.
 *
 * @author OptimizationsAndTweaks
 * @reason Widen the dimension/biome ID upper bound (255 -> 65536) to work around a mob-spawning crash and
 *         a ClassCastException on modpacks with an extended ID space.
 */
@Mixin(EFConfiguration.class)
public class MixinEFConfiguration {

    @Shadow
    public static boolean versionChecker;
    @Shadow
    public static String configVersion;

    @ModifyConstant(method = "init", constant = @Constant(intValue = 255), remap = false)
    private static int optimizationsandtweaks$widenIdRange(int original) {
        return 65536;
    }

    @Shadow
    public static void onRegistered() {
        if (versionChecker && !configVersion.equals("2.0b4")) {
            EternalFrost.logger.warn(
                "The config file of Eternal Frost is out of date and might cause problems, please remove it so it can be regenerated.");
        }

    }
}
