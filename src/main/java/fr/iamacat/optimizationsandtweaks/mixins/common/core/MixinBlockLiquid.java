package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Reduces TPS lags caused by BlockLiquid.
 */
@Mixin(BlockLiquid.class)
public class MixinBlockLiquid extends Block {

    @Unique
    private static final Set<Integer> NULL_BIOME_DIMENSION_CACHE = new HashSet<>();

    protected MixinBlockLiquid(Material materialIn) {
        super(materialIn);
    }

    /**
     * Avoid returning an error if biome is null at getWaterColorMultiplier
     */
    @Redirect(
        method = "colorMultiplier",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/BiomeGenBase;getWaterColorMultiplier()I"))
    private int safeGetWaterColorMultiplier(BiomeGenBase biome, IBlockAccess world, int x, int y, int z) {
        if (biome == null) {
            if (world instanceof World) {
                World actualWorld = (World) world;
                int dimId = actualWorld.provider.dimensionId;
                if (!NULL_BIOME_DIMENSION_CACHE.contains(dimId)) {
                    System.err.println(
                        "[NullBiomeFix] BiomeGenBase is NULL in Dimension " + dimId
                            + " (at x="
                            + x
                            + ", z="
                            + z
                            + ").");
                    NULL_BIOME_DIMENSION_CACHE.add(dimId);
                }
            }
            return 0x4080FF; // blue color
        }

        return biome.getWaterColorMultiplier();
    }
}
