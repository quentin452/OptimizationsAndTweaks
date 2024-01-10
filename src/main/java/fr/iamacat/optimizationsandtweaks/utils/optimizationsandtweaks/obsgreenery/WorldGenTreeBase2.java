package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.obsgreenery;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.Classers;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class WorldGenTreeBase2 extends WorldGenerator {
    public void optimizationsAndTweaks$recursiveBranch45(World world, int x, int y, int z, int len, Classers.Quadrant quadrant, Block a_log, int a_logMeta, Block a_leaves, int a_leavesMeta) {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);

        if(chunk.isChunkLoaded) {
            return;
        }
        if (len <= 0) {
            return;
        }

        Deque<BranchInfo> stack = new ArrayDeque<>();
        stack.push(new BranchInfo(x, y, z, len, quadrant));

        while (!stack.isEmpty()) {
            BranchInfo branch = stack.pop();

            int posX = branch.getX();
            int posY = branch.getY();
            int posZ = branch.getZ();
            int branchLen = branch.getLen();
            Classers.Quadrant branchQuadrant = branch.getQuadrant();

            int branchAt = -1;

            if (world.rand.nextInt(5) >= 1) {
                branchAt = posY + (int) (branchLen * 0.6F);
            }

            for (int i = 0; i < branchLen; ++i) {
                posX = this.optimizationsAndTweaks$incX(posX, branchQuadrant);
                ++posY;
                posZ = this.optimizationsAndTweaks$incX(posZ, branchQuadrant);

                boolean isLeaf1 = i != branchLen - 1 && i != branchLen - 4;
                boolean isLeaf2 = i == branchLen - 1;
                boolean isLeaf3 = i == branchLen - 4;

                this.placeBlock(world, posX, posY, posZ, a_log, a_logMeta + 12);

                if (i >= branchLen - 4) {
                    this.leafClump(world, posX, posY, posZ, a_leaves, a_leavesMeta, isLeaf1, isLeaf2, isLeaf3);
                }

                if (branchAt > 0 && posY == branchAt && branchLen > 3) {
                    int bLen = branchLen / 2;
                    stack.push(new BranchInfo(posX, posY, posZ, bLen, branchQuadrant.next()));
                    stack.push(new BranchInfo(posX, posY, posZ, bLen, branchQuadrant.previous()));
                    break;
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
