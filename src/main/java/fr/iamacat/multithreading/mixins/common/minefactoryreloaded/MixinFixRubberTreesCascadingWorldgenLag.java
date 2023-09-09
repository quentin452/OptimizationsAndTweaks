package fr.iamacat.multithreading.mixins.common.minefactoryreloaded;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

@Mixin(WorldGenRubberTree.class)
public class MixinFixRubberTreesCascadingWorldgenLag extends WorldGenerator {

    @Unique
    protected boolean doBlockNotify;

    /**
     * Generates a rubber tree with anti-cascading optimizations.
     */
    @Override
    public boolean generate(World world, Random rand, int x, int retries, int z) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix) {
            return true;
        }

        int y = getSurfaceBlockY(world, x, z);

        if (y <= 0) {
            return true;
        }

        return growTree(world, rand, x, y + 1, z);
    }

    @Unique
    private int getSurfaceBlockY(World world, int x, int z) {
        int y = world.getHeightValue(x, z);
        return y > 0 ? y - 1 : -1;
    }

    @Unique
    public boolean growTree(World world, Random rand, int x, int y, int z) {
        int treeHeight = rand.nextInt(3) + 5;
        int worldHeight = world.getHeight();

        if (y < 1 || y + treeHeight + 1 > worldHeight || !placeSapling(world, x, y, z)) {
            return false;
        }

        generateLeaves(world, rand, x, y, z, treeHeight);
        generateTrunk(world, x, y, z, treeHeight);
        return true;
    }

    @Unique
    private void generateLeaves(World world, Random rand, int x, int y, int z, int treeHeight) {
        for (int yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
            int var12 = yOffset - (y + treeHeight);
            int center = 1 - var12 / 2;

            for (int xOffset = x - center; xOffset <= x + center; ++xOffset) {
                for (int zOffset = z - center; zOffset <= z + center; ++zOffset) {
                    int xPos = xOffset - x;
                    int zPos = zOffset - z;

                    if ((xPos != center || zPos != center || rand.nextInt(2) != 0 && var12 != 0)
                        && isReplaceableOrLeaves(world, xOffset, yOffset, zOffset)) {

                        setBlockAndNotifyAdequately(world, xOffset, yOffset, zOffset, MFRThings.rubberLeavesBlock, 0);
                    }
                }
            }
        }
    }

    @Unique
    private void generateTrunk(World world, int x, int y, int z, int treeHeight) {
        for (int yOffset = 0; yOffset < treeHeight; ++yOffset) {
            if (isReplaceableOrLeaves(world, x, y + yOffset, z)) {
                setBlockAndNotifyAdequately(world, x, y + yOffset, z, MFRThings.rubberWoodBlock, 1);
            }
        }
    }

    @Unique
    private boolean isReplaceableOrLeaves(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block == null || block.isAir(world, x, y, z)
            || block.isLeaves(world, x, y, z)
            || block.isReplaceable(world, x, y, z);
    }

    @Unique
    private boolean placeSapling(World world, int x, int y, int z) {
        Block sapling = MFRThings.rubberSaplingBlock;
        if (sapling.canPlaceBlockAt(world, x, y, z)) {
            world.setBlock(x, y, z, sapling, 0, 2);
            return true;
        }
        return false;
    }

    @Unique
    private boolean isSpaceAvailable(World world, int x, int y, int z, int height) {
        for (int yOffset = 0; yOffset < height + 3; ++yOffset) {
            for (int xOffset = -1; xOffset <= 1; ++xOffset) {
                for (int zOffset = -1; zOffset <= 1; ++zOffset) {
                    Block block = world.getBlock(x + xOffset, y + yOffset, z + zOffset);

                    if (!(block instanceof IPlantable || block.isLeaves(world, x + xOffset, y + yOffset, z + zOffset)
                        || block.isAir(world, x + xOffset, y + yOffset, z + zOffset)
                        || block.isReplaceable(world, x + xOffset, y + yOffset, z + zOffset))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void setBlockAndNotifyAdequately(World world, int x, int y, int z, Block block, int meta) {
        int flag = this.doBlockNotify ? 3 : 2;
        if (world.setBlock(x, y, z, block, meta, flag)) {
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }
    }
}
