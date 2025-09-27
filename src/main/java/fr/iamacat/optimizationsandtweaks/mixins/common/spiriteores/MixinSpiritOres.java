package fr.iamacat.optimizationsandtweaks.mixins.common.spiriteores;

import howl01.spiritores.SpiritOres;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cyano.mineralogy.blocks.Ore;
import fr.iamacat.optimizationsandtweaks.utilsformods.spiritores.SpiritOreConfig;
import net.minecraftforge.common.config.Configuration;

@Mixin(SpiritOres.class)
public class MixinSpiritOres {

    @Inject(method = "preInit", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    public void addCustomOreConfig(FMLPreInitializationEvent event, CallbackInfo ci, Configuration config) {
        for (SpiritOreConfig ore : SpiritOreConfig.values()) {
            boolean enabled = config.get(
                "general", 
                "Enable " + ore.name() + " Ore", 
                ore.isEnabled() 
            ).getBoolean(ore.isEnabled());

            ore.setEnabled(enabled); 
        }
        config.save();
    }
}
