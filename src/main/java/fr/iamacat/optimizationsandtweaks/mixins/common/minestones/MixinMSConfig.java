package fr.iamacat.optimizationsandtweaks.mixins.common.minestones;

import net.minecraftforge.common.config.Configuration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.sinkillerj.minestones.MSConfig;

import fr.iamacat.optimizationsandtweaks.utilsformods.minestones.Patcher;

/**
 * Minestones reads {@code stoneDropRate} as a plain integer "1 in X" config value. OaT wants a decimal
 * probability instead (see {@link Patcher#stoneDropRate}, consumed by
 * {@link fr.iamacat.optimizationsandtweaks.mixins.common.minestones.MixinMSEvents}), so the single
 * {@code Configuration#getInt} call for that key is redirected to read the SAME key/category as a
 * decimal string and store it on {@link Patcher} instead of the shadowed (and now unused) int field.
 *
 * @author iamacatfr
 * @reason support decimal values for stoneDropRate config from Minestones
 */
@Mixin(MSConfig.class)
public class MixinMSConfig {

    @Shadow
    public static boolean hostileDrop;

    @Redirect(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/config/Configuration;getInt(Ljava/lang/String;Ljava/lang/String;IIILjava/lang/String;)I"),
        remap = false)
    private static int optimizationsandtweaks$readDecimalStoneDropRate(Configuration config, String name,
        String category, int defaultValue, int minValue, int maxValue, String comment) {
        Patcher.stoneDropRate = Double.parseDouble(
            config.get("rates", "stoneDropRate", "6.0", "Chance of a stone dropping from a hostile mob (as a decimal)")
                .getString());
        return defaultValue;
    }
}
