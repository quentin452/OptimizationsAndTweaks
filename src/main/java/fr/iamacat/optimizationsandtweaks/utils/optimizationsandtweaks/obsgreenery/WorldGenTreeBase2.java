package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.obsgreenery;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.Classers;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
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

        Chunk chunk = world.getChunkFromBlockCoords(x, z);

        if (!chunk.isChunkLoaded) {
            return;
        }

        Block[][][] blocks = new Block[xzRadius * 2 + 1][yRadius * 2 + 1][xzRadius * 2 + 1];

        for (int xOff = -xzRadius; xOff <= xzRadius; ++xOff) {
            for (int zOff = -xzRadius; zOff <= xzRadius; ++zOff) {
                for (int yOff = yMin; yOff <= yMax; ++yOff) {
                    int posX = x + xOff;
                    int posY = y + yOff;
                    int posZ = z + zOff;

                    if (world.blockExists(posX, posY, posZ)) {
                        blocks[xOff + xzRadius][yOff + yRadius][zOff + xzRadius] = chunk.getBlock(posX & 15, posY, posZ & 15);
                    }
                }
            }
        }

        for (int xOff = -xzRadius; xOff <= xzRadius; ++xOff) {
            for (int zOff = -xzRadius; zOff <= xzRadius; ++zOff) {
                for (int yOff = yMin; yOff <= yMax; ++yOff) {
                    int xOffAbs = Math.abs(xOff);
                    int zOffAbs = Math.abs(zOff);
                    int yOffAbs = Math.abs(yOff);
                    if (xOffAbs + zOffAbs + yOffAbs <= bound) {
                        int posX = x + xOff;
                        int posY = y + yOff;
                        int posZ = z + zOff;

                        Block block = blocks[xOff + xzRadius][yOff + yRadius][zOff + xzRadius];

                        if (block != null && (xOff != 0 || yOff != 0 || zOff != 0) && block.isAir(world, posX, posY, posZ)) {
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
}
