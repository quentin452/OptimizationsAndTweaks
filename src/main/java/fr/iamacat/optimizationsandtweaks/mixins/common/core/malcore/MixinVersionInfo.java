package fr.iamacat.optimizationsandtweaks.mixins.common.core.malcore;

import mal.core.version.VersionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(VersionInfo.class)
public class MixinVersionInfo {

    /**
     * @author iamacatfr
     * @reason remove version check from Mal Core mod
     */
    @Overwrite
    public void checkForNewVersion() {
    }
}
