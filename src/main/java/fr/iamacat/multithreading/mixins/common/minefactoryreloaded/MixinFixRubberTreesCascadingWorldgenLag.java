package fr.iamacat.multithreading.mixins.common.minefactoryreloaded;

import java.util.Random;

import cofh.lib.util.helpers.BlockHelper;
import com.falsepattern.lib.compat.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

@Mixin(WorldGenRubberTree.class)
public class MixinFixRubberTreesCascadingWorldgenLag extends WorldGenerator {

    @Unique
    protected boolean multithreadingandtweaks$doBlockNotify;

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

        return this.multithreadingandtweaks$growTree(var1, var2, var3, var6 + 1, var5);
    }
    @Override
    public boolean generate(World world, Random rand, int x, int retries, int z) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix) {
            return true;
        }

        int y = multithreadingandtweaks$getSurfaceBlockY(world, x, z);

        if (y <= 0) {
            return true;
        }

        return multithreadingandtweaks$growTree(world, rand, x, y + 1, z);
    }

    @Unique
    private int multithreadingandtweaks$getSurfaceBlockY(World world, int x, int z) {
        int y = world.getHeightValue(x, z);
        return y > 0 ? y - 1 : -1;
    }

    @Unique
    public boolean multithreadingandtweaks$growTree(World world, Random rand, int x, int y, int z) {
        int treeHeight = rand.nextInt(3) + 5;
        int worldHeight = world.getHeight();

        if (y < 1 || y + treeHeight + 1 > worldHeight || !multithreadingandtweaks$placeSapling(world, x, y, z)) {
            return false;
        }

        for (int yOffset = 0; yOffset < treeHeight; ++yOffset) {

                world.setBlock(x, y + yOffset, z, MFRThings.rubberWoodBlock, 1, 2);

        }

        // Generate leaves without triggering updates
        for (int yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
            int var12 = yOffset - (y + treeHeight);
            int center = 1 - var12 / 2;

            for (int xOffset = x - center; xOffset <= x + center; ++xOffset) {
                for (int zOffset = z - center; zOffset <= z + center; ++zOffset) {
                    int xPos = xOffset - x;
                    int zPos = zOffset - z;

                    if ((xPos != center || zPos != center || rand.nextInt(2) != 0 && var12 != 0)
                       ) {

                        // Place leaves without block updates
                        // make cascading when enabled todo fixme
                        world.setBlock(xOffset, yOffset, zOffset, MFRThings.rubberLeavesBlock, 0, 2);
                    }
                }
            }
        }

        return true;
    }
    // make cascading when enabled todo fixme
    @Unique
    private boolean multithreadingandtweaks$isReplaceableOrLeaves(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block == null || block.isAir(world, x, y, z)
            || block.isLeaves(world, x, y, z)
            || block.isReplaceable(world, x, y, z);
    }

    @Unique
    private boolean multithreadingandtweaks$placeSapling(World world, int x, int y, int z) {
        Block sapling = MFRThings.rubberSaplingBlock;
        if (sapling.canPlaceBlockAt(world, x, y, z)) {
            world.setBlock(x, y, z, sapling, 0, 2);
            return true;
        }
        return false;
    }

    @Override
    protected void setBlockAndNotifyAdequately(World world, int x, int y, int z, Block block, int meta) {
        int flag = this.multithreadingandtweaks$doBlockNotify ? 3 : 2;
        if (world.setBlock(x, y, z, block, meta, flag)) {
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }
    }
}
