package fr.iamacat.optimizationsandtweaks.mixins.common.stealth;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Cuts ~2/3 of StealthMod's worldgen cost — the single biggest IWorldGenerator on the pack.
 * <p>
 * {@code WorldRegister.mainRegistry()} registers {@code new WorldGenStealth()} THREE times (three
 * byte-identical lines), so its whole 26-vein ore routine runs 3x per chunk (measured 44 ms/chunk,
 * 82% of all IWorldGenerator time). Because FML re-seeds the per-chunk {@code Random} to the same
 * chunkSeed before EACH registered generator, passes 2-3 reproduce the exact same vein geometry over
 * blocks pass 1 already converted — pure redundant work with zero effect on the generated world.
 * <p>
 * This injects at HEAD and performs the registration ONCE (same weight -1), then cancels the
 * original. Output-identical by construction: the removed passes could not change any block. The mod
 * is not a compile dependency, so the target is named ({@code remap = false}) and the generator is
 * instantiated reflectively; on any reflection failure we fall through to the original method
 * (fail-open: triple registration is slow but harmless).
 */
@Mixin(targets = "com.stealth.mod.init.objects.world.WorldRegister", remap = false)
public class MixinWorldRegister {

    @Inject(method = "mainRegistry", at = @At("HEAD"), cancellable = true, remap = false)
    private static void optimizationsandtweaks$registerOnce(CallbackInfo ci) {
        try {
            Object generator = Class.forName("com.stealth.mod.init.objects.world.WorldGenStealth")
                .getDeclaredConstructor()
                .newInstance();
            GameRegistry.registerWorldGenerator((IWorldGenerator) generator, -1);
            ci.cancel();
        } catch (Throwable t) {
            // Fall through: the original triple registration runs (slow but correct).
        }
    }
}
