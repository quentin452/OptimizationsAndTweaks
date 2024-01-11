package fr.iamacat.optimizationsandtweaks.mixins.common.lotr;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fr.iamacat.optimizationsandtweaks.utilsformods.lotr.LOTRConfigBiomeID;
import lotr.common.LOTRMod;

@Mixin(LOTRMod.class)
public class MixinLOTRMod {

    @Mod.EventHandler
    @Inject(method = "preload", at = @At("HEAD"), cancellable = false, remap = false)
    public void preload(FMLPreInitializationEvent event, CallbackInfo ci) {
        LOTRConfigBiomeID.setupAndLoad(event);
    }
}
