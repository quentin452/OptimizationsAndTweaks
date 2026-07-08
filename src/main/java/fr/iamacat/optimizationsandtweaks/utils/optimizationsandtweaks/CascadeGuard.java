package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

/**
 * Thread-local "am I inside chunk populate" flag for the experimental worldgen cascade net
 * ({@link OptimizationsandTweaksConfig#enableWorldgenCascadeNet}).
 * <p>
 * {@code MixinChunkProviderServerCascadeNet} arms it around {@code ChunkProviderServer.populate}
 * (which wraps both vanilla decoration and mod {@code IWorldGenerator} via
 * {@code GameRegistry.generateWorld}); {@code MixinWorld} reads it to skip neighbour notifications
 * that would drag a not-yet-generated chunk in. Populate is main-thread and synchronous in 1.7.10,
 * but a thread-local keeps the flag correct even for mods that touch other threads.
 */
public final class CascadeGuard {

    private CascadeGuard() {}

    private static final ThreadLocal<Boolean> POPULATING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /** Enter a populate scope (no-op unless the net is enabled). */
    public static void beginPopulate() {
        if (OptimizationsandTweaksConfig.enableWorldgenCascadeNet) {
            POPULATING.set(Boolean.TRUE);
        }
    }

    /** Leave the populate scope. */
    public static void endPopulate() {
        POPULATING.set(Boolean.FALSE);
    }

    /** True only while the net is enabled AND we are inside a populate scope on this thread. */
    public static boolean isPopulating() {
        return OptimizationsandTweaksConfig.enableWorldgenCascadeNet && POPULATING.get();
    }
}
