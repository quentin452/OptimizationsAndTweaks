package fr.iamacat.optimizationsandtweaks.utilsformods.biomesoplenty;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-class cache of the reflective {@link Field} lookups performed by
 * {@code biomesoplenty.api.biome.BiomeFeatures#getFeature}.
 * <p>
 * Vanilla BoP re-runs {@code getClass().getField(name)} on every decoration-loop iteration; that call is a linear
 * scan over the declaring class' ~70 public fields. Since the (class, name) -> field mapping is immutable for a
 * given {@code BiomeFeatures} subclass, we resolve each field once and reuse it. {@link Field#setAccessible(boolean)}
 * is only a micro-optimisation here (the target fields are already public), but it removes the per-access language
 * check. The result is identical to what {@code getClass().getField(name)} would return, so behaviour is preserved.
 * <p>
 * Missing fields are intentionally not cached: {@link Class#getField(String)} throws {@link NoSuchFieldException},
 * which propagates exactly as before (BoP wraps it into {@code NoSuchFeatureException}).
 */
public final class BiomeFeaturesFieldCache {

    private BiomeFeaturesFieldCache() {}

    private static final Map<Class<?>, Map<String, Field>> CACHE = new ConcurrentHashMap<>();

    public static Field get(Class<?> owner, String name) throws NoSuchFieldException {
        Map<String, Field> byName = CACHE.computeIfAbsent(owner, k -> new ConcurrentHashMap<>());
        Field field = byName.get(name);
        if (field == null) {
            field = owner.getField(name);
            field.setAccessible(true);
            byName.put(name, field);
        }
        return field;
    }
}
