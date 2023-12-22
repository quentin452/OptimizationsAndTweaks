package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.gen.layer.IntCache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IntCache.class)
public class MixinIntCache {

    @Shadow
    private static int intCacheSize = 256;
    /** A list of pre-allocated int[256] arrays that are currently unused and can be returned by getIntCache() */
    @Shadow
    private static List freeSmallArrays = new ArrayList();
    /**
     * A list of pre-allocated int[256] arrays that were previously returned by getIntCache() and which will not be re-
     * used again until resetIntCache() is called.
     */
    @Shadow
    private static List inUseSmallArrays = new ArrayList();
    /** A list of pre-allocated int[cacheSize] arrays that are currently unused and can be returned by getIntCache() */
    @Shadow
    private static List freeLargeArrays = new ArrayList();
    /**
     * A list of pre-allocated int[cacheSize] arrays that were previously returned by getIntCache() and which will not
     * be re-used again until resetIntCache() is called.
     */
    @Shadow
    private static List inUseLargeArrays = new ArrayList();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void resetIntCache() {
        freeLargeArrays.addAll(inUseLargeArrays);
        inUseLargeArrays.clear();

        freeSmallArrays.addAll(inUseSmallArrays);
        inUseSmallArrays.clear();

    }

}
