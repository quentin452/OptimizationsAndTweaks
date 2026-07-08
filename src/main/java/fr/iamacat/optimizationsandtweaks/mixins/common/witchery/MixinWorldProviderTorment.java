package fr.iamacat.optimizationsandtweaks.mixins.common.witchery;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.emoniph.witchery.dimension.WorldProviderTorment;

import fr.iamacat.optimizationsandtweaks.utilsformods.witchery.WorldChunkManagerTorment2;

@Mixin(WorldProviderTorment.class)
public abstract class MixinWorldProviderTorment extends WorldProvider {

    /**
     * @reason redirect to the optimized WorldChunkManagerTorment2. HEAD-cancel with a computed return
     *         value instead of a full-method replace so any other transform on this method still applies.
     */
    @Inject(method = "func_76555_c", at = @At("HEAD"), remap = false, cancellable = true)
    private void func_76555_c(CallbackInfoReturnable<IChunkProvider> cir) {
        cir.setReturnValue(new WorldChunkManagerTorment2(this.worldObj));
    }
}
