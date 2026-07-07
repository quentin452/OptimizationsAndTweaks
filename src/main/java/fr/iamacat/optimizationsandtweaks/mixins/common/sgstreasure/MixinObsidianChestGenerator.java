package fr.iamacat.optimizationsandtweaks.mixins.common.sgstreasure;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.someguyssoftware.mod.Coords;
import com.someguyssoftware.treasure.worldgen.chest.ObsidianChestGenerator;

import fr.iamacat.optimizationsandtweaks.utilsformods.sgstreasure.ObsidianChestGenerator2;

@Mixin(ObsidianChestGenerator.class)
public class MixinObsidianChestGenerator {

    /**
     * @reason redirect to the optimized ObsidianChestGenerator2 lookup. HEAD-cancel with a computed
     *         return value instead of full replace so any other transform on this method still
     *         applies.
     */
    @Inject(method = "getNearestChestCoords", at = @At("HEAD"), remap = false, cancellable = true)
    private void getNearestChestCoords(World world, int x, int y, int z, CallbackInfoReturnable<Coords> cir) {
        cir.setReturnValue(ObsidianChestGenerator2.getNearestChestCoords(world, x, y, z));
    }
}
