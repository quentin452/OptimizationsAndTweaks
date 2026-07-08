package fr.iamacat.optimizationsandtweaks.mixins.common.buildcraft.addon.oiltweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import buildcraft.BuildCraftEnergy;
import buildcraft.oiltweak.integration.simplyjetpacks.BuildCraftConfig;
import buildcraft.oiltweak.reference.Mods;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

/**
 * Optimizes the BuildCraftConfig class from BuildCraft.
 */
@Mixin(BuildCraftConfig.class)
public class MixinBuildCraftConfig {

    @Unique
    private boolean optimizationsAndTweaks$cachedIsOilDense = false;

    /**
     * @author iamacatfr
     * @reason calculate only one time to reduce tps lags caused by isLoaded
     */
    @Overwrite(remap = false)
    public boolean isOilDense() {
        if (optimizationsAndTweaks$cachedIsOilDense) {
            return true;
        }
        // Mods.isBCEnergyLoaded only exists in OilTweak >= 1.1.3 (the pack ships
        // 1.1.0) — NoSuchFieldError at runtime. Mods.BuildCraftEnergy is a String
        // constant, inlined at compile time, so FML Loader is version-proof here.
        optimizationsAndTweaks$cachedIsOilDense = Loader.isModLoaded(Mods.BuildCraftEnergy) && isOilDense_BC();
        return optimizationsAndTweaks$cachedIsOilDense;
    }

    @Shadow
    @Optional.Method(modid = Mods.BuildCraftEnergy)
    private boolean isOilDense_BC() {
        return BuildCraftEnergy.isOilDense;
    }
}
