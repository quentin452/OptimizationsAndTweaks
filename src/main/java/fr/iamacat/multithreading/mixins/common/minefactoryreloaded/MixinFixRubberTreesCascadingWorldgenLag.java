package fr.iamacat.multithreading.mixins.common.minefactoryreloaded;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

@Mixin(WorldGenRubberTree.class)
public class MixinFixRubberTreesCascadingWorldgenLag extends WorldGenerator {

    @Unique
    protected boolean doBlockNotify;

    @Unique
    private static final int MAX_GENERATION_ATTEMPTS = 5; // Increase the number of attempts

    // Reduce the search radius to 4 blocks
    @Unique
    private static final int SEARCH_RADIUS = 4;

    // Limit the range of movement after a failed attempt
    @Unique
    private static final int MAX_MOVE_RANGE = 8; // Adjust as needed

    /**
     * Generates a rubber tree with anti-cascading optimizations.
     */
    @Override
    public boolean generate(World world, Random rand, int x, int retries, int z) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix) {
            int generationAttempts = 0;
            while (generationAttempts < MAX_GENERATION_ATTEMPTS) {
                int y = this.getSurfaceBlockY(world, x, z);

                // Check if there's already a tree nearby
                if (!isTreeNearby(world, x, y, z)) {
                    if (y > 0 && !this.growTree(world, rand, x, y + 1, z)) {
                        generationAttempts++;
                    } else {
                        break; // Successfully generated tree
                    }
                } else {
                    // Move coordinates a small random amount and retry
                    x += rand.nextInt(MAX_MOVE_RANGE) - (MAX_MOVE_RANGE / 2);
                    z += rand.nextInt(MAX_MOVE_RANGE) - (MAX_MOVE_RANGE / 2);
                    generationAttempts++;
                }
            }
        }
        return true;
    }

    @Unique
    private boolean isTreeNearby(World world, int x, int y, int z) {
        // Move the nearby tree check radius inside the generation attempt loop
        for (int generationAttempt = 0; generationAttempt < MAX_GENERATION_ATTEMPTS; generationAttempt++) {
            for (int xOffset = -SEARCH_RADIUS; xOffset <= SEARCH_RADIUS; xOffset++) {
                for (int zOffset = -SEARCH_RADIUS; zOffset <= SEARCH_RADIUS; zOffset++) {
                    for (int yOffset = -SEARCH_RADIUS; yOffset <= SEARCH_RADIUS; yOffset++) {
                        if (Math.abs(xOffset) + Math.abs(yOffset) + Math.abs(zOffset) <= SEARCH_RADIUS) {
                            int checkX = x + xOffset;
                            int checkY = y + yOffset;
                            int checkZ = z + zOffset;

                            // Check if the destination chunk is generated
                            if (world.getChunkProvider()
                                .chunkExists(checkX >> 4, checkZ >> 4)) {
                                Block block = world.getBlock(checkX, checkY, checkZ);

                                // You can customize this condition to match the blocks that indicate trees
                                if (block.isWood(world, checkX, checkY, checkZ)) {
                                    return true; // Another tree is nearby
                                }
                            }
                        }
                    }
                }
            }
        }
        return false; // No trees found nearby
    }

    @Unique
    private int getSurfaceBlockY(World world, int x, int z) {
        int y = world.getHeightValue(x, z);
        if (y == 0) {
            return -1;
        } else {
            y--;
            return y;
        }
    }

    @Unique
    public boolean growTree(World world, Random rand, int x, int y, int z) {
        int treeHeight = rand.nextInt(3) + 5;
        int worldHeight = world.getHeight();
        Block block;

        if (y >= 1 && y + treeHeight + 1 <= worldHeight) {
            int xOffset;
            int yOffset;
            int zOffset;

            // Check if there is enough space for the tree
            if (isSpaceAvailable(world, x, y, z, treeHeight)) {
                for (yOffset = y; yOffset <= y + 1 + treeHeight; ++yOffset) {
                    byte radius;

                    if (yOffset >= y + 1 + treeHeight - 2) {
                        radius = 2;
                    } else {
                        radius = 0;
                    }

                    if (yOffset >= 0 & yOffset < worldHeight) {
                        if (radius == 0) {
                            block = world.getBlock(x, yOffset, z);
                            if (!(block.isLeaves(world, x, yOffset, z) || block.isAir(world, x, yOffset, z)
                                || block.isReplaceable(world, x, yOffset, z)
                                || block.canBeReplacedByLeaves(world, x, yOffset, z))) {
                                return false;
                            }

                            if (yOffset >= y + 1) {
                                radius = 1;
                                for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
                                    for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {
                                        block = world.getBlock(xOffset, yOffset, zOffset);

                                        if (block.getMaterial()
                                            .isLiquid()) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        } else {
                            for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
                                for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {
                                    block = world.getBlock(xOffset, yOffset, zOffset);

                                    if (!(block.isLeaves(world, xOffset, yOffset, zOffset)
                                        || block.isAir(world, xOffset, yOffset, zOffset)
                                        || block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset))) {
                                        return false;
                                    }
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                }

                block = world.getBlock(x, y - 1, z);
                if (!block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, MFRThings.rubberSaplingBlock)) {
                    return false; // another chunk generated and invalidated our location
                }
                if (!placeSapling(world, x, y - 1, z)) {
                    return false;
                }

                for (yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
                    int var12 = yOffset - (y + treeHeight);
                    int center = 1 - var12 / 2;

                    for (xOffset = x - center; xOffset <= x + center; ++xOffset) {
                        int xPos = xOffset - x;
                        int t = Integer.signum(xPos);
                        xPos = (xPos + t) ^ t;

                        for (zOffset = z - center; zOffset <= z + center; ++zOffset) {
                            int zPos = zOffset - z;
                            t = Integer.signum(zPos);
                            zPos = (zPos + t) ^ t;

                            block = world.getBlock(xOffset, yOffset, zOffset);

                            if (((xPos != center || zPos != center) || rand.nextInt(2) != 0 && var12 != 0)
                                && (block == null || block.isLeaves(world, xOffset, yOffset, zOffset)
                                    || block.isAir(world, xOffset, yOffset, zOffset)
                                    || block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset))) {

                                this.setBlockAndNotifyAdequately(
                                    world,
                                    xOffset,
                                    yOffset,
                                    zOffset,
                                    MFRThings.rubberLeavesBlock,
                                    0);
                            }
                        }
                    }
                }

                for (yOffset = 0; yOffset < treeHeight; ++yOffset) {
                    block = world.getBlock(x, y + yOffset, z);

                    if (block == null || block.isAir(world, x, y + yOffset, z)
                        || block.isLeaves(world, x, y + yOffset, z)
                        || block.isReplaceable(world, x, y + yOffset, z)) {
                        // replace snow
                        this.setBlockAndNotifyAdequately(world, x, y + yOffset, z, MFRThings.rubberWoodBlock, 1);
                    }
                }

                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean isSpaceAvailable(World world, int x, int y, int z, int height) {
        // Check for nearby blocks that might obstruct tree growth
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

    @Unique
    private boolean placeSapling(World world, int x, int y, int z) {
        // Attempt to place the sapling
        Block sapling = MFRThings.rubberSaplingBlock;
        if (sapling.canPlaceBlockAt(world, x, y, z)) {
            world.setBlock(x, y, z, sapling, 0, 2);
            return true;
        }
        return false;
    }

    @Override
    protected void setBlockAndNotifyAdequately(World world, int x, int y, int z, Block block, int meta) {
        final int flag;
        if (this.doBlockNotify) {
            flag = 3;
        } else {
            flag = 2;
        }
        if (world.setBlock(x, y, z, block, meta, flag)) {
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }
    }
}
