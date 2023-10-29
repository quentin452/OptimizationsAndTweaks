package fr.iamacat.optimizationsandtweaks.mixins.common.minefactoryreloaded;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import cofh.lib.util.helpers.BlockHelper;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

@Mixin(WorldGenRubberTree.class)
public class MixinFixRubberTreesCascadingWorldgenLag extends WorldGenerator {

    @Unique
    protected boolean optimizationsAndTweaks$doBlockNotify;

    /**
     * @author f
     * @reason f
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
        int var6 = BlockHelper.getSurfaceBlockY(var1, var3, var5);
        if (var6 <= 0) {
            return false;
        }

        return this.growTree(var1, var2, var3, var6 + 1, var5);
    }

    @Override
    public boolean generate(World world, Random rand, int x, int retries, int z) {
        if (!OptimizationsandTweaksConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix) {
            return true;
        }

        int y = optimizationsAndTweaks$getSurfaceBlockY(world, x, z);

        if (y <= 0) {
            return true;
        }

        return growTree(world, rand, x, y + 1, z);
    }

    @Unique
    private int optimizationsAndTweaks$getSurfaceBlockY(World world, int x, int z) {
        int y = world.getHeightValue(x, z);
        return y > 0 ? y - 1 : -1;
    }

    /**
     * @author f
     * @reason f
     */
    @Overwrite(remap = false)
    public boolean growTree(World world, Random rand, int x, int y, int z) {
        // Add a random chance for the tree not to grow (50% chance)
        if (rand.nextInt(2) == 0) {
            return false;
        }

        int treeHeight = rand.nextInt(3) + 5;
        int worldHeight = world.getHeight();

        if (y < 1 || y + treeHeight + 1 > worldHeight || !optimizationsAndTweaks$placeSapling(world, x, y, z)) {
            return false;
        }
        for (int yOffset = 0; yOffset < treeHeight; ++yOffset) {

            world.setBlock(x, y + yOffset, z, MFRThings.rubberWoodBlock);

            this.optimizationsAndTweaks$notifyBlockUpdate(world, x, y + yOffset, z, 0);
        }

        // Generate leaves without triggering updates
        for (int yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
            int var12 = yOffset - (y + treeHeight);
            int center = 1 - var12 / 2;

            for (int xOffset = x - center; xOffset <= x + center; ++xOffset) {
                for (int zOffset = z - center; zOffset <= z + center; ++zOffset) {
                    int xPos = xOffset - x;
                    int zPos = zOffset - z;

                    if ((xPos != center || zPos != center || rand.nextInt(2) != 0 && var12 != 0)) {

                        // Place leaves without block updates
                        // make cascading when enabled todo fixme
                        if (optimizationsAndTweaks$canPlaceLeaf(world, xOffset, yOffset, zOffset)) {
                            world.setBlock(xOffset, yOffset, zOffset, MFRThings.rubberLeavesBlock);
                        }

                        this.optimizationsAndTweaks$notifyBlockUpdate(world, xOffset, yOffset, zOffset, 0);
                    }
                }
            }
        }

        return true;
    }

    @Unique
    boolean optimizationsAndTweaks$canPlaceLeaf(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block != MFRThings.rubberWoodBlock;
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
    protected void optimizationsAndTweaks$notifyBlockUpdate(World world, int x, int y, int z, int meta) {

        world.markBlockForUpdate(x, y, z);

        if (!world.isRemote) {
            world.notifyBlockChange(x, y, z, Block.getBlockById(meta));
        }

    }

}
