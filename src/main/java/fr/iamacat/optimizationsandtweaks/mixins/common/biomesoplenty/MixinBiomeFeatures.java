package fr.iamacat.optimizationsandtweaks.mixins.common.biomesoplenty;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import biomesoplenty.api.biome.BiomeFeatures;
import fr.iamacat.optimizationsandtweaks.utilsformods.biomesoplenty.BiomeFeaturesFieldCache;

/**
 * Caches the reflective field lookup in {@code BiomeFeatures#getFeature}.
 * <p>
 * BoP resolves each biome feature with {@code getClass().getField(name).get(this)} and re-runs it every decoration
 * iteration. {@link Class#getField(String)} is an O(public fields) linear search (~70 fields on the overworld
 * feature class), repeated for every feature of every decorated chunk. This {@link Redirect} swaps the
 * {@code getField} call for a per-class cached lookup (see {@link BiomeFeaturesFieldCache}); the returned field is
 * the same one vanilla would find, so {@code get(this)} yields identical values and worldgen is unchanged.
 * <p>
 * BoP is a compile-only dependency (the pack provides it at runtime); the redirected member is a JDK method, so the
 * injector uses {@code remap = false}. On a genuinely missing field the cache re-throws {@link NoSuchFieldException},
 * which BoP's surrounding {@code catch (Exception)} turns into a {@code NoSuchFeatureException} exactly as before.
 *
 * @author OptimizationsAndTweaks
 * @reason Behaviour-preserving worldgen speedup: replace BoP's per-iteration reflective {@code Class#getField}
 *         linear search with a per-class {@code Map<String, Field>} cache.
 */
@Mixin(BiomeFeatures.class)
public class MixinBiomeFeatures {

    @Redirect(
        method = "getFeature",
        at = @At(value = "INVOKE", target = "Ljava/lang/Class;getField(Ljava/lang/String;)Ljava/lang/reflect/Field;"),
        remap = false)
    private Field optimizationsandtweaks$cachedGetField(Class<?> owner, String name) throws NoSuchFieldException {
        return BiomeFeaturesFieldCache.get(owner, name);
    }
}
