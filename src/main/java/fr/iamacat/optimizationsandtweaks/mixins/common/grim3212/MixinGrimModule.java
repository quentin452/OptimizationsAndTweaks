package fr.iamacat.optimizationsandtweaks.mixins.common.grim3212;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import grim3212.mc.core.GrimModule;
import grim3212.mc.core.manual.ManualRegistry;
import grim3212.mc.core.manual.ModSection;

@Mixin(GrimModule.class)
public class MixinGrimModule {

    @Shadow
    protected static ModSection newModSection;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected void registerMod(String modID, String modName, String modVersion) {
        this.registerSection(modName, modID);
        // this.registerVersionCheck(modID, modVersion);
    }

    @Shadow
    protected void registerSection(String modName, String modID) {
        ManualRegistry.registerMod(newModSection = new ModSection(modName, modID));
    }
}
