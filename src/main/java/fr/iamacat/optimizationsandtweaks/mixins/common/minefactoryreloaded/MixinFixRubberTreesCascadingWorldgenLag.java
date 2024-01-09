package fr.iamacat.optimizationsandtweaks.mixins.common.minefactoryreloaded;

import net.minecraft.block.Block;
import net.minecraft.world.World;
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
            int surfaceY = optimizationsAndTweaks$getSurfaceBlockY(world, x, z);

            if (surfaceY > 0 && growTree(world, rand, x, surfaceY + 1, z)) {
                return true;
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
        optimizationsAndTweaks$placeRubberWoodBlocks(world, x, y, z, treeHeight);
        optimizationsAndTweaks$generateLeaves(world, rand, x, y, z, treeHeight);
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
        for (int yOffset = 0; yOffset < treeHeight; ++yOffset) {
            world.setBlock(x, y + yOffset, z, MFRThings.rubberWoodBlock);
            optimizationsAndTweaks$notifyBlockUpdate(world, x, y + yOffset, z, 0);
        }
    }
    @Unique
    private void optimizationsAndTweaks$generateLeaves(World world, Random rand, int x, int y, int z, int treeHeight) {
        for (int yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
            int var12 = yOffset - (y + treeHeight);
            int center = 1 - var12 / 2;
            for (int xOffset = x - center; xOffset <= x + center; ++xOffset) {
                for (int zOffset = z - center; zOffset <= z + center; ++zOffset) {
                    int xPos = xOffset - x;
                    int zPos = zOffset - z;
                    if ((xPos != center || zPos != center || rand.nextInt(2) != 0 && var12 != 0)) {
                        if (optimizationsAndTweaks$canPlaceLeaf(rand)) {
                            world.setBlock(xOffset, yOffset, zOffset, MFRThings.rubberLeavesBlock);
                        }
                        optimizationsAndTweaks$notifyBlockUpdate(world, xOffset, yOffset, zOffset, 0);
                    }
                }
            }
        }
    }
    @Unique
    private boolean optimizationsAndTweaks$canPlaceLeaf(Random rand) {
        return rand.nextFloat() < 0.9f;
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
    @Unique
    private int optimizationsAndTweaks$getSurfaceBlockY(World world, int x, int z) {
        for (int y = world.getHeight() - 1; y > 0; y--) {
            if (world.getBlock(x, y, z).getMaterial().isSolid()) {
                return y;
            }
        }
        return -1;
    }
}
