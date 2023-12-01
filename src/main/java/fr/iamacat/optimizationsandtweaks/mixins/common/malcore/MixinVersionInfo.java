package fr.iamacat.optimizationsandtweaks.mixins.common.malcore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import mal.core.version.VersionInfo;

@Mixin(VersionInfo.class)
public class MixinVersionInfo {

    /**
     * @author iamacatfr
     * @reason remove version check from Mal Core mod
     */
    @Overwrite(remap = false)
    public void checkForNewVersion() {}
}
