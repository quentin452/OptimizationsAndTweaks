package fr.iamacat.optimizationsandtweaks.mixins.common.slimecarnage;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import supremopete.SlimeCarnage.worldgen.WorldGenMadLab;

/**
 * Fixes some cascading worldgen caused by WorldGenMadLab from Slime Carnage Mod.
 */
@Mixin(WorldGenMadLab.class)
public class MixinWorldGenMadLab {

    // func_76484_a: the ORIGINAL SlimeCarnage WorldGenMadLab#func_76484_a (verified against the
    // decompiled 1.0.5d jar) has the same 8x block checks but NO chunk-corner/bounds restriction. The
    // OaT delta is exactly one additional guard: require the call to land on the chunk's own origin
    // (i/k multiples of 16, in [0,16)) and j in [0,256), evaluated BEFORE the (unmodified) block checks
    // via short-circuit &&. No RNG is consumed by the guard itself, so converting it to a HEAD @Inject
    // that cancels with `false` reproduces the exact same short-circuit behavior: the original bytecode
    // (its own 8x block checks + inline generation, now unmodified) only runs when the guard passes.
    @Inject(method = "func_76484_a", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsAndTweaks$guardChunkOrigin(World world, Random rand, int i, int j, int k,
        CallbackInfoReturnable<Boolean> cir) {
        int chunkX = i >> 4;
        int chunkZ = k >> 4;
        boolean inBounds = i >= 0 && i < 16
            && k >= 0
            && k < 16
            && j >= 0
            && j < 256
            && chunkX * 16 == i
            && chunkZ * 16 == k;
        if (!inBounds) {
            cir.setReturnValue(false);
        }
    }

    // generate1-generate3 (previously @Unique helpers extracted from the func_76484_a @Overwrite body)
    // were removed: they are dead code now that the original (unmodified) bytecode - which inlines the
    // same generation directly - runs whenever the guard above passes.
}
