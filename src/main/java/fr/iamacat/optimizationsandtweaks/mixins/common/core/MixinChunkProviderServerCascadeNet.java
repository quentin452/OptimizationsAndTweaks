package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.CascadeGuard;

/**
 * Arms the {@link CascadeGuard} populate flag around {@code ChunkProviderServer.populate}, so
 * {@code MixinWorld} can skip cross-chunk neighbour notifications during worldgen (the
 * experimental cascade net). {@code populate} wraps both vanilla decoration and every mod
 * {@code IWorldGenerator} (via {@code GameRegistry.generateWorld}), so this one scope covers all
 * populate-time cascades. The injects no-op when the net is disabled in config.
 */
@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServerCascadeNet {

    @Inject(method = "populate", at = @At("HEAD"))
    private void optimizationsAndTweaks$beginPopulate(net.minecraft.world.chunk.IChunkProvider provider, int x, int z,
        CallbackInfo ci) {
        CascadeGuard.beginPopulate();
    }

    @Inject(method = "populate", at = @At("RETURN"))
    private void optimizationsAndTweaks$endPopulate(net.minecraft.world.chunk.IChunkProvider provider, int x, int z,
        CallbackInfo ci) {
        CascadeGuard.endPopulate();
    }
}
