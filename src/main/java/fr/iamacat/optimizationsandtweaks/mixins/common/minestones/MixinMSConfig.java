package fr.iamacat.optimizationsandtweaks.mixins.common.minestones;

import net.minecraftforge.common.config.Configuration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.sinkillerj.minestones.MSConfig;

import fr.iamacat.optimizationsandtweaks.utilsformods.minestones.Patcher;

@Mixin(MSConfig.class)
public class MixinMSConfig {

    @Shadow
    public static boolean hostileDrop;

    /**
     * @author iamacatfr
     * @reason support decimal values for stoneDropRate config from Minestones
     */
    @Overwrite
    public static void init(Configuration config) {
        try {
            config.load();
            hostileDrop = config.getBoolean("hostileDrop", "loot", true, "Stones drop from hostile mobs.");

            // Get the stoneDropRate as a double
            Patcher.stoneDropRate = Double.parseDouble(
                config
                    .get(
                        "rates",
                        "stoneDropRate",
                        "6.0",
                        "Chance of a stone dropping from a hostile mob (as a decimal)")
                    .getString());
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
