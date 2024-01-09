package fr.iamacat.optimizationsandtweaks.mixins.common.minefactoryreloaded;

import cofh.lib.util.helpers.BlockHelper;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minefactoryreloaded.WorldGenRubberTree2;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

import java.util.Random;
@Mixin(WorldGenRubberTree.class)
public abstract class MixinFixRubberTreesCascadingWorldgenLag extends WorldGenerator {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random rand, int x, int y, int z) {
        int numAttempts = 4;

        for (int attempt = 0; attempt < numAttempts; ++attempt) {
            int surfaceY = BlockHelper.getSurfaceBlockY(world, x, z);

            if (surfaceY > 0) {
                if (attempt == 0) {
                    if (growTree(world, rand, x, surfaceY + 1, z)) {
                        return true;
                    }
                } else {
                    int xOffset = x + rand.nextInt(16) - 8;
                    int zOffset = z + rand.nextInt(16) - 8;

                    if (growTree(world, rand, xOffset, surfaceY + 1, zOffset)) {
                        return true;
                    }
                }
            }
            x += rand.nextInt(16) - 8;
            z += rand.nextInt(16) - 8;
        }

        return false;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean growTree(World world, Random rand, int x, int y, int z) {
        if (optimizationsAndTweaks$shouldNotGrowTree(rand)) {
            return false;
        }
        int treeHeight = rand.nextInt(3) + 5;
        int worldHeight = world.getHeight();
        if (optimizationsAndTweaks$isInvalidTreePosition(world, x, y, z, treeHeight, worldHeight)) {
            return false;
        }
        optimizationsAndTweaks$placeRubberWoodBlocks(world,x, y, z, treeHeight);
        WorldGenRubberTree2 worldGenRubberTree2 = new WorldGenRubberTree2();
        worldGenRubberTree2.generateLeaves(world, rand, x, y, z, treeHeight);
        return true;
    }
    @Unique
    private boolean optimizationsAndTweaks$shouldNotGrowTree(Random rand) {
        return rand.nextInt(2) == 0;
    }
    @Unique
    private boolean optimizationsAndTweaks$isInvalidTreePosition(World world, int x, int y, int z, int treeHeight, int worldHeight) {
        return y < 1 || y + treeHeight + 1 > worldHeight || !optimizationsAndTweaks$placeSapling(world, x, y, z);
    }
    @Unique
    private void optimizationsAndTweaks$placeRubberWoodBlocks(World world, int x, int y, int z, int treeHeight) {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        for (int yOffset = 0; yOffset < treeHeight; ++yOffset) {
            int blockY = y + yOffset;
            if (!chunk.func_150807_a(x & 15, blockY, z & 15, MFRThings.rubberWoodBlock, 0)) {
                chunk.func_150807_a(x & 15, blockY, z & 15, MFRThings.rubberWoodBlock, 0);
                optimizationsAndTweaks$notifyBlockUpdate(world, x, blockY, z, 0);
            }
        }
    }
    @Unique
    private boolean optimizationsAndTweaks$placeSapling(World world, int x, int y, int z) {
        Block sapling = MFRThings.rubberSaplingBlock;
        if (sapling.canPlaceBlockAt(world, x, y, z)) {
            world.setBlock(x, y, z, sapling, 0, 2);
            return true;
        }
        return false;
    }
    @Unique
    private void optimizationsAndTweaks$notifyBlockUpdate(World world, int x, int y, int z, int meta) {
        world.markBlockForUpdate(x, y, z);
        if (!world.isRemote) {
            world.notifyBlockChange(x, y, z, Block.getBlockById(meta));
        }
    }
}
