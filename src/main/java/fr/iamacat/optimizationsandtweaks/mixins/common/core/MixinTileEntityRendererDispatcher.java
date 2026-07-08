package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Collections;
import java.util.Map;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

/**
 * Fixes concurrent modification exception from TileEntityRendererDispatcher.
 * <p>
 * The original {@code func_147543_a} iterates {@code mapSpecialRenderers} unsynchronized while other
 * threads (mod tile entity renderer registration) can mutate it concurrently -- wrapping the whole
 * original call in a {@code synchronized (mapSpecialRenderers)} block (paired with the constructor
 * swapping the map for a {@link Collections#synchronizedMap}) is the standard fix for manual iteration
 * over a synchronized collection, per its javadoc. The original body (field assignment + iteration) is
 * untouched, just wrapped.
 */
@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {

    @Shadow
    public Map mapSpecialRenderers;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSynchronizedMap(CallbackInfo ci) {
        this.mapSpecialRenderers = Collections.synchronizedMap(this.mapSpecialRenderers);
    }

    @WrapMethod(method = "func_147543_a", remap = false)
    private void optimizationsAndTweaks$synchronizeRenderers(World world, Operation<Void> original) {
        synchronized (mapSpecialRenderers) {
            original.call(world);
        }
    }
}
