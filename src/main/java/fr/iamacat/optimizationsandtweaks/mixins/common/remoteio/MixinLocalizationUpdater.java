package fr.iamacat.optimizationsandtweaks.mixins.common.remoteio;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import remoteio.common.core.handler.LocalizationUpdater;

@Mixin(LocalizationUpdater.class)
public class MixinLocalizationUpdater {

    @Overwrite(remap = false)
    private void loadLangFiles() {
        // disabling localizationUpdater
    }

}
