package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.obsgreenery;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.Classers;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public abstract class WorldGenTreeBase2 extends WorldGenerator {
    public void optimizationsAndTweaks$recursiveBranch45(World world, int x, int y, int z, int len, Classers.Quadrant quadrant, Block a_log, int a_logMeta, Block a_leaves, int a_leavesMeta) {
        if (len > 0) {
            int posX = x;
            int posY = y;
            int posZ = z;
            int branchAt = -1;
            if (world.rand.nextInt(5) >= 1) {
                branchAt = y + (int)(len * 0.6F);
            }

            for(int i = 0; i < len; ++i) {
                posX = this.optimizationsAndTweaks$incX(posX, quadrant);
                ++posY;
                posZ = this.optimizationsAndTweaks$incX(posZ, quadrant);
                this.placeBlock(world, posX, posY, posZ, a_log, a_logMeta + 12);
                if (i >= len - 4) {
                    this.leafClump(world, posX, posY, posZ, a_leaves, a_leavesMeta, i != len - 1 && i != len - 4, i == len - 1, i == len - 4);
                }

                if (branchAt > 0 && posY == branchAt && len > 3) {
                    int bLen = len / 2;
                    this.optimizationsAndTweaks$recursiveBranch45(world, posX, posY, posZ, bLen, quadrant.next(), a_log, a_logMeta, a_leaves, a_leavesMeta);
                    this.optimizationsAndTweaks$recursiveBranch45(world, posX, posY, posZ, bLen, quadrant.previous(), a_log, a_logMeta, a_leaves, a_leavesMeta);
                }
            }
        }
    }
    protected int optimizationsAndTweaks$incX(int coord, Classers.Quadrant quadrant) {
        switch (quadrant) {
            case NEGX_Z:
            case NEGX_NEGZ:
                return coord - 1;
            case X_Z:
            case X_NEGZ:
            default:
                return coord + 1;
        }
    }
    protected void leafClump(World world, int x, int y, int z, Block a_leaves, int a_leavesMeta) {
        this.leafClump(world, x, y, z, a_leaves, a_leavesMeta, false, false, false);
    }
    protected void leafClump(World world, int x, int y, int z, Block a_leaves, int a_leavesMeta, boolean xzPlaneOnly, boolean topOnly, boolean bottomOnly) {
        int xzRadius = 2;
        int yRadius = 2;
        int yMin = -yRadius;
        int yMax = yRadius;
        int bound = 3;
        if (xzPlaneOnly) {
            yMax = 0;
            yMin = 0;
        } else if (topOnly) {
            yMin = 0;
        } else if (bottomOnly) {
            yMax = 0;
        }

        for(int xOff = -xzRadius; xOff <= xzRadius; ++xOff) {
            int xOffAbs = this.absolute(xOff);

            for(int zOff = -xzRadius; zOff <= xzRadius; ++zOff) {
                int zOffAbs = this.absolute(zOff);

                for(int yOff = yMin; yOff <= yMax; ++yOff) {
                    int yOffAbs = this.absolute(yOff);
                    if (xOffAbs + zOffAbs + yOffAbs <= bound) {
                        int posX = x + xOff;
                        int posY = y + yOff;
                        int posZ = z + zOff;
                        if ((xOff != 0 || yOff != 0 || zOff != 0) && this.canPlaceLeaves(world, posX, posY, posZ, a_leaves, a_leavesMeta)) {
                            this.placeBlock(world, posX, posY, posZ, a_leaves, a_leavesMeta);
                        }
                    }
                }
            }
        }
    }
    protected void placeBlock(World world, int x, int y, int z, Block block, int meta) {
        this.setBlockAndNotifyAdequately(world, x, y, z, block, meta);
    }
    protected int absolute(int num) {
        return num < 0 ? -num : num;
    }
    protected boolean canPlaceLeaves(World world, int x, int y, int z, Block leaves, int meta) {
        Block block = this.getBlockAt(world, x, y, z);
        return block != leaves && this.isBlockReplacable(block, world, x, y, z);
    }
    protected Block getBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    protected boolean isBlockReplacable(Block block, World world, int x, int y, int z) {
        if (block == null) {
            return true;
        } else if (block.isAir(world, x, y, z)) {
            return true;
        } else if (block.isLeaves(world, x, y, z)) {
            return false;
        } else if (block.isReplaceable(world, x, y, z)) {
            return true;
        } else {
            return block.canBeReplacedByLeaves(world, x, y, z);
        }
    }
}
