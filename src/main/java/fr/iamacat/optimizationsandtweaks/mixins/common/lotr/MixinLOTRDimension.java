package fr.iamacat.optimizationsandtweaks.mixins.common.lotr;

import lotr.common.LOTRDimension;
import lotr.common.world.biome.LOTRBiome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

@Mixin(LOTRDimension.class)
public abstract class MixinLOTRDimension {

    @Mutable
    @Shadow
    public LOTRBiome[] biomeList = new LOTRBiome[256];
    /*
    Require Endlessids
    Fix crash on startup if you use a too much higher value about more than 256 to a biome id from LOTR(BUT NEED TO FIX CRASH WHEN TELEPORTING TO THE DIMENSION)
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBiomeList(String s, int i, String c, int flag, Class spawns, boolean regions, int r, EnumSet par8, CallbackInfo ci) {
        this.biomeList = new LOTRBiome[65535];
    }
}
