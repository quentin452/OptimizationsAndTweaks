package fr.iamacat.optimizationsandtweaks.mixins.common.sgstreasure;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.someguyssoftware.plans.Plans;
import com.someguyssoftware.plans.PlansProcessor;

import fr.iamacat.optimizationsandtweaks.utilsformods.sgstreasure.PlansProcessor2;

/**
 * Fixes some cascading worldgen caused by PlansProcessor class from SGS Treasure mod.
 */
@Mixin(PlansProcessor.class)
public class MixinPlansProcessor {

    /**
     * @reason delegate to the optimized PlansProcessor2. HEAD-cancel instead of a full-method replace so
     *         any other transform on this method still applies.
     */
    @Inject(method = "construct", at = @At("HEAD"), remap = false, cancellable = true)
    private void construct(World world, int x, int y, int z, Plans plans, CallbackInfo ci) {
        PlansProcessor2 processor2 = new PlansProcessor2();
        processor2.construct(world, x, y, z, plans);
        ci.cancel();
    }
}
