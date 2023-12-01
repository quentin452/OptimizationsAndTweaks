package fr.iamacat.optimizationsandtweaks.mixins.common.buildcraft.addon.oiltweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import buildcraft.BuildCraftEnergy;
import buildcraft.oiltweak.integration.simplyjetpacks.BuildCraftConfig;
import buildcraft.oiltweak.reference.Mods;
import cpw.mods.fml.common.Optional;

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
        optimizationsAndTweaks$cachedIsOilDense = Mods.isLoaded(Mods.BuildCraftEnergy) && isOilDense_BC();
        return optimizationsAndTweaks$cachedIsOilDense;
    }

    @Shadow
    @Optional.Method(modid = Mods.BuildCraftEnergy)
    private boolean isOilDense_BC() {
        return BuildCraftEnergy.isOilDense;
    }
}
