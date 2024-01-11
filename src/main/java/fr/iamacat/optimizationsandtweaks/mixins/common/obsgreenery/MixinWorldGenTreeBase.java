package fr.iamacat.optimizationsandtweaks.mixins.common.obsgreenery;

import com.jim.obsgreenery.world.WorldGenTreeBase;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.mixins.Classers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fluids.BlockFluidBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(WorldGenTreeBase.class)
public abstract class MixinWorldGenTreeBase extends WorldGenerator {
    @Unique
    protected final boolean notifyBlocks;
    public MixinWorldGenTreeBase(boolean notifyBlocks) {
        super(notifyBlocks);
        this.notifyBlocks = notifyBlocks;
    }
    @Shadow
    public boolean func_76484_a(World world, Random rand, int x, int y, int z) {
        return false;
    }
    @Shadow
    protected void placeBlock(World world, int x, int y, int z, Block block, int meta) {
        this.setBlockAndNotifyAdequately(world, x, y, z, block, meta);
    }
    @Shadow
    protected Block getBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    @Shadow
    protected int getGroundYPosition(World world, int x, int z) {
        int posY = world.getChunkFromBlockCoords(x, z).getTopFilledSegment() + 16;

        Block block;
        do {
            --posY;
            if (posY <= 0) {
                break;
            }

            block = this.getBlockAt(world, x, posY, z);
        } while(this.isBlockReplacable(block, world, x, posY, z));

        return posY;
    }
    @Shadow
    protected boolean canPlaceLeaves(World world, int x, int y, int z, Block leaves, int meta) {
        Block block = this.getBlockAt(world, x, y, z);
        return block == leaves ? false : this.isBlockReplacable(block, world, x, y, z);
    }
    @Shadow
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
    @Shadow
    protected boolean isBaseBlockValid(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block != Blocks.dirt && block != Blocks.grass) {
            return false;
        } else {
            for(int i = 0; i < 5; ++i) {
                if (y + i < world.getActualHeight()) {
                    block = world.getBlock(x, y + i, z);
                    if (block == Blocks.water || block == Blocks.lava || block instanceof BlockFluidBase || block instanceof BlockLiquid) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
    @Shadow
    protected int absolute(int num) {
        return num < 0 ? -num : num;
    }
    @Shadow
    protected void drawBranch(Random rand, int yAngle, int yAngleRange, int xzAngle, int xzAngleRange, int minLen, int maxLen, Block log, int logMeta) {
        int len = minLen + rand.nextInt(maxLen - minLen);
    }
    @Shadow
    protected void thinTrunk(World world, int x, int y, int z, int height, Block a_log, int a_logMeta) {
        for(int i = 0; i < height; ++i) {
            this.placeBlock(world, x, y + i, z, a_log, a_logMeta);
        }

    }

    @Shadow
    protected void branch22_3(World world, int x, int y, int z, int len, int incX, int incZ, Block a_log, int a_logMeta, Block a_leaves, int a_leavesMeta) {
        int yPos;
        int xOff;
        if (incX != 0) {
            if (incZ == 0) {
                if (incX > 0) {
                    yPos = y;

                    for(xOff = 0; xOff < len; ++yPos) {
                        this.sliceNS(world, x + xOff, yPos - 1, yPos + 1, z - 1, z + 1, a_log, a_logMeta);
                        ++xOff;
                        this.sliceNS(world, x + xOff, yPos, yPos + 1, z - 1, z + 1, a_log, a_logMeta);
                        ++xOff;
                        this.sliceNS(world, x + xOff, yPos, yPos + 1, z - 1, z + 1, a_log, a_logMeta);
                        ++xOff;
                    }
                } else {
                    yPos = y;

                    for(xOff = 0; xOff < len; ++yPos) {
                        this.sliceNS(world, x - xOff, yPos - 1, yPos + 1, z - 1, z + 1, a_log, a_logMeta);
                        ++xOff;
                        this.sliceNS(world, x - xOff, yPos, yPos + 1, z - 1, z + 1, a_log, a_logMeta);
                        ++xOff;
                        this.sliceNS(world, x - xOff, yPos, yPos + 1, z - 1, z + 1, a_log, a_logMeta);
                        ++xOff;
                    }
                }
            }
        } else if (incZ > 0) {
            yPos = y;

            for(xOff = 0; xOff < len; ++yPos) {
                this.sliceEW(world, x - 1, x + 1, yPos - 1, yPos + 1, z + xOff, a_log, a_logMeta);
                ++xOff;
                this.sliceEW(world, x - 1, x + 1, yPos, yPos + 1, z + xOff, a_log, a_logMeta);
                ++xOff;
                this.sliceEW(world, x - 1, x + 1, yPos, yPos + 1, z + xOff, a_log, a_logMeta);
                ++xOff;
            }
        } else {
            yPos = y;

            for(xOff = 0; xOff < len; ++yPos) {
                this.sliceEW(world, x - 1, x + 1, yPos - 1, yPos + 1, z - xOff, a_log, a_logMeta);
                ++xOff;
                this.sliceEW(world, x - 1, x + 1, yPos, yPos + 1, z - xOff, a_log, a_logMeta);
                ++xOff;
                this.sliceEW(world, x - 1, x + 1, yPos, yPos + 1, z - xOff, a_log, a_logMeta);
                ++xOff;
            }
        }

    }
    @Shadow
    protected void sliceNS(World world, int x, int minY, int maxY, int minZ, int maxZ, Block log, int logMeta) {
        for(int posZ = minZ; posZ <= maxZ; ++posZ) {
            for(int posY = minY; posY <= maxY; ++posY) {
                this.placeBlock(world, x, posY, posZ, log, logMeta + 12);
            }
        }

    }
    @Shadow
    protected void sliceEW(World world, int minX, int maxX, int minY, int maxY, int z, Block log, int logMeta) {
        for(int posX = minX; posX <= maxX; ++posX) {
            for(int posY = minY; posY <= maxY; ++posY) {
                this.placeBlock(world, posX, posY, z, log, logMeta + 12);
            }
        }

    }
    @Shadow
    protected void leafClump(World world, int x, int y, int z, Block a_leaves, int a_leavesMeta) {
        this.leafClump(world, x, y, z, a_leaves, a_leavesMeta, false, false, false);
    }
    @Shadow
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
    @Shadow
    protected int getLogOffsetMeta(int meta, int dir) {
        switch (dir) {
            case 0:
            case 1:
            default:
                return meta;
            case 2:
            case 3:
                return meta + 8;
            case 4:
            case 5:
                return meta + 4;
        }
    }
    @Shadow
    protected void branchT(World world, int x, int y, int z, int dir, int len, int tOffset, int tLen, Block block, int meta, Block leafBlock, int leafMeta) {
        Classers.XZCoord mainXZ = new Classers.XZCoord(x, z);
        this.branchLine(world, x, y, z, dir, len, block, meta);
        Classers.XZCoord xzT = mainXZ.offset(dir, tOffset);
        byte directionT;
        switch (dir) {
            case 2:
            case 3:
            default:
                xzT = xzT.offset(5, tLen);
                directionT = 4;
                break;
            case 4:
            case 5:
                xzT = xzT.offset(2, tLen);
                directionT = 3;
        }

        this.branchLine(world, xzT.x, y, xzT.z, directionT, tLen, block, meta);
        xzT = xzT.offset(directionT, tLen + 1);
        this.branchLine(world, xzT.x, y, xzT.z, directionT, tLen, block, meta);
        Classers.XZCoord leafXZ = mainXZ.offset(dir, len - 2);
        this.leafClump(world, leafXZ.x, y, leafXZ.z, leafBlock, leafMeta);
        xzT = mainXZ.offset(dir, tOffset - 1);
        leafXZ = xzT.offset(directionT, tLen - 1);
        this.leafClump(world, leafXZ.x, y, leafXZ.z, leafBlock, leafMeta);
        leafXZ = xzT.offset(directionT, -tLen + 1);
        this.leafClump(world, leafXZ.x, y, leafXZ.z, leafBlock, leafMeta);
    }
    @Shadow
    protected void branchLine(World world, int x, int y, int z, int dir, int len, Block block, int meta) {
        int offsetMeta = this.getLogOffsetMeta(meta, dir);
        Classers.XZCoord xzPos = new Classers.XZCoord(x, z);

        for(int offset = 0; offset < len; ++offset) {
            Classers.XZCoord xz = xzPos.offset(dir, offset);
            this.placeBlock(world, xz.x, y, xz.z, block, offsetMeta);
        }

    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected void leafRing(World world, int x, int y, int z, Block leaves, int leavesMeta, int radius) {
        int minX = x - radius;
        int maxX = x + radius;
        int minZ = z - radius;
        int maxZ = z + radius;

        Chunk chunk = world.getChunkFromBlockCoords(x, z);

        if (!chunk.isChunkLoaded) {
            return;
        }

        for (int xPos = minX; xPos <= maxX; ++xPos) {
            for (int zPos = minZ; zPos <= maxZ; ++zPos) {
                if ((xPos != minX && xPos != maxX || zPos != minZ && zPos != maxZ) && (zPos != z || xPos != x) && (world.blockExists(xPos, y, zPos) && this.canPlaceLeaves(world, xPos, y, zPos, leaves, leavesMeta))) {
                        this.placeBlock(world, xPos, y, zPos, leaves, leavesMeta);
                }
            }
        }
    }
    @Shadow
    protected void trunkRing4(World world, int x, int y, int z, Block log, int logMeta) {
        this.placeBlock(world, x - 4, y, z - 2, log, logMeta);
        this.placeBlock(world, x - 4, y, z - 1, log, logMeta);
        this.placeBlock(world, x - 4, y, z, log, logMeta);
        this.placeBlock(world, x - 4, y, z + 1, log, logMeta);
        this.placeBlock(world, x - 4, y, z + 2, log, logMeta);
        this.placeBlock(world, x + 4, y, z - 2, log, logMeta);
        this.placeBlock(world, x + 4, y, z - 1, log, logMeta);
        this.placeBlock(world, x + 4, y, z, log, logMeta);
        this.placeBlock(world, x + 4, y, z + 1, log, logMeta);
        this.placeBlock(world, x + 4, y, z + 2, log, logMeta);
        this.placeBlock(world, x - 2, y, z + 4, log, logMeta);
        this.placeBlock(world, x - 1, y, z + 4, log, logMeta);
        this.placeBlock(world, x, y, z + 4, log, logMeta);
        this.placeBlock(world, x + 1, y, z + 4, log, logMeta);
        this.placeBlock(world, x + 2, y, z + 4, log, logMeta);
        this.placeBlock(world, x - 2, y, z - 4, log, logMeta);
        this.placeBlock(world, x - 1, y, z - 4, log, logMeta);
        this.placeBlock(world, x, y, z - 4, log, logMeta);
        this.placeBlock(world, x + 1, y, z - 4, log, logMeta);
        this.placeBlock(world, x + 2, y, z - 4, log, logMeta);
        this.placeBlock(world, x - 3, y, z - 3, log, logMeta);
        this.placeBlock(world, x - 3, y, z + 3, log, logMeta);
        this.placeBlock(world, x + 3, y, z - 3, log, logMeta);
        this.placeBlock(world, x + 3, y, z + 3, log, logMeta);
    }
    @Shadow
    protected void trunkRing3(World world, int x, int y, int z, Block log, int logMeta) {
        this.placeBlock(world, x - 3, y, z - 1, log, logMeta);
        this.placeBlock(world, x - 3, y, z, log, logMeta);
        this.placeBlock(world, x - 3, y, z + 1, log, logMeta);
        this.placeBlock(world, x + 3, y, z - 1, log, logMeta);
        this.placeBlock(world, x + 3, y, z, log, logMeta);
        this.placeBlock(world, x + 3, y, z + 1, log, logMeta);
        this.placeBlock(world, x - 1, y, z + 3, log, logMeta);
        this.placeBlock(world, x, y, z + 3, log, logMeta);
        this.placeBlock(world, x + 1, y, z + 3, log, logMeta);
        this.placeBlock(world, x - 1, y, z - 3, log, logMeta);
        this.placeBlock(world, x, y, z - 3, log, logMeta);
        this.placeBlock(world, x + 1, y, z - 3, log, logMeta);
        this.placeBlock(world, x - 2, y, z - 2, log, logMeta);
        this.placeBlock(world, x - 2, y, z + 2, log, logMeta);
        this.placeBlock(world, x + 2, y, z - 2, log, logMeta);
        this.placeBlock(world, x + 2, y, z + 2, log, logMeta);
    }
    @Shadow
    protected void trunkRing2(World world, int x, int y, int z, Block log, int logMeta) {
        this.placeBlock(world, x - 2, y, z - 1, log, logMeta);
        this.placeBlock(world, x - 2, y, z, log, logMeta);
        this.placeBlock(world, x - 2, y, z + 1, log, logMeta);
        this.placeBlock(world, x + 2, y, z - 1, log, logMeta);
        this.placeBlock(world, x + 2, y, z, log, logMeta);
        this.placeBlock(world, x + 2, y, z + 1, log, logMeta);
        this.placeBlock(world, x - 1, y, z + 2, log, logMeta);
        this.placeBlock(world, x, y, z + 2, log, logMeta);
        this.placeBlock(world, x + 1, y, z + 2, log, logMeta);
        this.placeBlock(world, x - 1, y, z - 2, log, logMeta);
        this.placeBlock(world, x, y, z - 2, log, logMeta);
        this.placeBlock(world, x + 1, y, z - 2, log, logMeta);
    }
    @Shadow
    protected void knot3x3FacingNS(World world, int x, int y, int z, Block log, int logMeta) {
        for(int posZ = z - 1; posZ <= z + 1; ++posZ) {
            for(int posY = y - 1; posY <= y + 1; ++posY) {
                if (posZ == z && posY == y) {
                    this.placeBlock(world, x, posY, posZ, log, logMeta + 4);
                } else {
                    this.placeBlock(world, x, posY, posZ, log, logMeta + 4);
                }
            }
        }
    }
    @Shadow
    protected void knot3x3FacingEW(World world, int x, int y, int z, Block log, int logMeta) {
        for(int posX = x - 1; posX <= x + 1; ++posX) {
            for(int posY = y - 1; posY <= y + 1; ++posY) {
                if (posX == z && posY == y) {
                    this.placeBlock(world, posX, posY, z, log, logMeta + 8);
                } else {
                    this.placeBlock(world, posX, posY, z, log, logMeta + 8);
                }
            }
        }

    }
    @Shadow
    protected void trunk4(World world, Random rand, int x, int y, int z, Block log, int logMeta, int height, double knotChance) {
        for(int yOffset = 0; yOffset < height; ++yOffset) {
            this.trunkRing4(world, x, y + yOffset, z, log, logMeta);
            if (knotChance > 0.0 && rand.nextDouble() <= knotChance) {
                int dir = rand.nextInt(4);
                switch (dir) {
                    case 0:
                        this.knot3x3FacingNS(world, x - 5, y + yOffset, z, log, logMeta);
                        break;
                    case 1:
                        this.knot3x3FacingNS(world, x + 5, y + yOffset, z, log, logMeta);
                        break;
                    case 2:
                        this.knot3x3FacingEW(world, x, y + yOffset, z - 5, log, logMeta);
                        break;
                    default:
                        this.knot3x3FacingEW(world, x, y + yOffset, z + 5, log, logMeta);
                }
            }
        }

    }
    @Shadow
    protected void trunk3(World world, int x, int y, int z, Block log, int logMeta, int height) {
        for(int yOffset = 0; yOffset < height; ++yOffset) {
            this.trunkRing3(world, x, y + yOffset, z, log, logMeta);
        }

    }
    @Shadow
    protected void trunk2(World world, int x, int y, int z, Block log, int logMeta, int height) {
        for(int yOffset = 0; yOffset < height; ++yOffset) {
            this.trunkRing2(world, x, y + yOffset, z, log, logMeta);
        }

    }
    @Shadow
    public boolean growPine(World world, Random rand, int x, int y, int z, Block log, int logMeta, Block leaves, int leavesMeta, int minTrunkHeight, int maxTrunkHeight) {
        int trunkHeight = minTrunkHeight + rand.nextInt(maxTrunkHeight - minTrunkHeight);
        int worldHeight = world.getHeight();
        if (y >= 1 && y + trunkHeight + 1 <= worldHeight && this.isBaseBlockValid(world, x, y - 1, z)) {
            this.thinTrunk(world, x, y, z, trunkHeight, log, logMeta);
            int count = 0;

            for(int yOffset = trunkHeight - 1; yOffset > 3; yOffset -= 2) {
                int radius = 3;
                if (count == 0) {
                    radius = 1;
                } else if (count == 1) {
                    radius = 2;
                }

                this.leafRing(world, x, y + yOffset, z, leaves, leavesMeta, radius);
                ++count;
            }

            return true;
        } else {
            return false;
        }
    }

}
