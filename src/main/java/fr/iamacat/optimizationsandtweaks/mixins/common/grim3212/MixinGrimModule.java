package fr.iamacat.optimizationsandtweaks.mixins.common.grim3212;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import grim3212.mc.core.GrimModule;
import grim3212.mc.core.manual.ManualRegistry;
import grim3212.mc.core.manual.ModSection;

/**
 * Original {@code registerMod} calls {@code this.registerSection(...)} followed by
 * {@code this.registerVersionCheck(modID, modVersion)}. The only change here is dropping that second
 * call; this redirects it to a no-op instead of copying the whole method, so {@code registerSection}
 * (and any future upstream changes to it) stay live.
 *
 * @author OptimizationsAndTweaks
 * @reason Removes Grim3212 Version Checker to prevent
 *         https://github.com/quentin452/privates-minecraft-modpack/issues/903.
 */
@Mixin(GrimModule.class)
public class MixinGrimModule {

    @Shadow
    protected static ModSection newModSection;

    @Redirect(
        method = "registerMod",
        at = @At(
            value = "INVOKE",
            target = "Lgrim3212/mc/core/GrimModule;registerVersionCheck(Ljava/lang/String;Ljava/lang/String;)V"),
        remap = false)
    private void optimizationsandtweaks$skipVersionCheck(GrimModule instance, String modID, String modVersion) {
        // no-op: disable Grim3212's built-in version checker (see class javadoc)
    }

    @Shadow
    protected void registerSection(String modName, String modID) {
        ManualRegistry.registerMod(newModSection = new ModSection(modName, modID));
    }
}
