package fr.iamacat.optimizationsandtweaks.mixins.common.nei;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import codechicken.nei.WorldOverlayRenderer;

/**
 * Fixes null crash between F7 Key from NEi and Small Stairs block.
 */
@Mixin(WorldOverlayRenderer.class)
public class MixinWorldOverlayRenderer {

    @Shadow
    private static final Entity dummyEntity = new EntityPig(null);
    @Shadow
    private static final AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);

    /**
     * @author iamacatfr
     * @reason reclassify COMPLEX (not mechanically convertible): adds an early "sky light == 0" return-0 check
     *         (twice: once at the very top, and again -- now unreachable/dead, since sky light cannot change
     *         between the two checks -- right before the collision/liquid check). This is a real change to the
     *         NEI spawn-overlay categorization for fully-dark spots (vanilla could still return 2 there if the
     *         other conditions passed; this version forces 0), not just a call swap, and its exact intent isn't
     *         fully clear from the diff alone (class doc says this mixin's stated purpose is a null-crash fix
     *         with Small Stairs, not an overlay-color change) -- too risky to guess and convert. Left as
     *         @Overwrite; flagging the duplicate/dead second sky-light check for a follow-up look.
     */
    @Overwrite
    private static byte getSpawnMode(Chunk chunk, int x, int y, int z) {
        if (chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15) == 0) {
            return 0;
        }

        if (chunk.getSavedLightValue(EnumSkyBlock.Block, x & 15, y, z & 15) >= 8
            || !SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, chunk.worldObj, x, y, z)) {
            return 0;
        }

        aabb.minX = x + 0.2;
        aabb.maxX = x + 0.8;
        aabb.minY = y + 0.01;
        aabb.maxY = y + 1.8;
        aabb.minZ = z + 0.2;
        aabb.maxZ = z + 0.8;

        if (chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15) == 0) {
            return 0;
        }

        if (!chunk.worldObj.getCollidingBoundingBoxes(dummyEntity, aabb)
            .isEmpty() || chunk.worldObj.isAnyLiquid(aabb)) {
            return 0;
        }

        return (byte) (chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15) >= 8 ? 1 : 2);
    }

}
