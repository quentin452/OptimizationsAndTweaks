package fr.iamacat.optimizationsandtweaks.mixins.common.sgstreasure;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.someguyssoftware.mod.util.WorldUtil;

import fr.iamacat.optimizationsandtweaks.utilsformods.sgstreasure.WorldUtil2SGSTREASURE;

/**
 * Fixes some cascading worldgen caused by WorldUtilSGSTREASURE class from SGS Treasure mod.
 */
@Mixin(WorldUtil.class)
public class MixinWorldUtilSGSTREASURE {

    /**
     * @reason redirect to the optimized WorldUtil2SGSTREASURE lookup. HEAD-cancel with a computed
     *         return value instead of full replace so any other transform on this method still
     *         applies.
     */
    @Inject(method = "isSolidBase", at = @At("HEAD"), remap = false, cancellable = true)
    private static void isSolidBase(World world, int x, int y, int z, int width, int depth, int percentRequired,
        CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(WorldUtil2SGSTREASURE.isSolidBase(world, x, y, z, width, depth, percentRequired));
    }
}
