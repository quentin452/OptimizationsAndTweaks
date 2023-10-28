package fr.iamacat.optimizationsandtweaks.mixins.common.core.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Int2ObjectHashMap;

@Mixin(PathFinder.class)
public class MixinPathFinder {

    @Shadow
    private boolean canEntityDrown;

    @Shadow
    private PathPoint[] pathOptions = new PathPoint[32];
    @Shadow
    private boolean isWoddenDoorAllowed;
    /** should the PathFinder disregard BlockMovement type materials in its path */
    @Shadow
    private boolean isMovementBlockAllowed;
    @Shadow
    private boolean isPathingInWater;
    @Shadow
    private IBlockAccess worldMap;
    /** The path being generated */
    @Shadow
    private Path path = new Path();
    @Unique
    float totalPathDistance;
    /** The linear distance to the next point */
    @Unique
    float distanceToNext;
    /** The distance to the target */
    @Unique
    float distanceToTarget;
    @Unique
    PathPoint previous;

    /**
     * @author iamacatfr
     * @reason optimize func_82565_a
     */
    @Overwrite
    public static int func_82565_a(Entity p_82565_0_, int p_82565_1_, int p_82565_2_, int p_82565_3_,
                                   PathPoint p_82565_4_, boolean p_82565_5_, boolean p_82565_6_, boolean p_82565_7_) {
        World world = p_82565_0_.worldObj;
        int entityX = (int) p_82565_0_.posX;
        int entityY = (int) p_82565_0_.posY;
        int entityZ = (int) p_82565_0_.posZ;

        int endX = p_82565_1_ + p_82565_4_.xCoord;
        int endY = p_82565_2_ + p_82565_4_.yCoord;
        int endZ = p_82565_3_ + p_82565_4_.zCoord;

        boolean isTrapdoorPresent = false;

        for (int x = p_82565_1_, y = p_82565_2_, z = p_82565_3_; x < endX && y < endY && z < endZ; ++x, ++y, ++z) {
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

            int localX = x & 15;
            int localZ = z & 15;

            Block block = chunk.getBlock(localX, y, localZ);
            Material material = block.getMaterial();
            int renderType = block.getRenderType();

            if (material != Material.air) {
                if (block == Blocks.trapdoor) {
                    isTrapdoorPresent = true;
                } else if (block != Blocks.flowing_water && block != Blocks.water) {
                    if (!p_82565_6_ && block == Blocks.wooden_door) {
                        return 0;
                    }
                } else {
                    if (p_82565_5_) {
                        return -1;
                    }
                    isTrapdoorPresent = true;
                }

                if (renderType == 9) {
                    Block currentBlock = world.getBlock(entityX, entityY, entityZ);
                    Block blockBelow = world.getBlock(entityX, entityY - 1, entityZ);
                    int currentBlockRenderType = currentBlock.getRenderType();
                    int blockBelowRenderType = blockBelow.getRenderType();
                    if (currentBlockRenderType != 9 && blockBelowRenderType != 9) {
                        return -3;
                    }
                } else if (!block.getBlocksMovement(world, x, y, z)
                    && (!p_82565_7_ || block != Blocks.wooden_door)) {
                    if (renderType == 11 || block == Blocks.fence_gate || renderType == 32) {
                        return -3;
                    }
                    if (block == Blocks.trapdoor) {
                        return -4;
                    }
                    if (material != Material.lava) {
                        return 0;
                    }
                    if (!p_82565_0_.handleLavaMovement()) {
                        return -2;
                    }
                }
            }
        }

        return isTrapdoorPresent ? 2 : 1;
    }

    /**
     * @author iamacatfr
     * @reason optimize openPoint
     */
    @Unique
    private final Int2ObjectHashMap<PathPoint> multithreadingandtweaks$pointCache = new Int2ObjectHashMap<>();
    /**
     * @author
     * @reason
     */
    @Overwrite
    private final PathPoint openPoint(int p_75854_1_, int p_75854_2_, int p_75854_3_) {
        int l = PathPoint.makeHash(p_75854_1_, p_75854_2_, p_75854_3_);

        PathPoint pathpoint = multithreadingandtweaks$pointCache.get(l);

        if (pathpoint == null) {
            pathpoint = new PathPoint(p_75854_1_, p_75854_2_, p_75854_3_);

            multithreadingandtweaks$pointCache.put(l, pathpoint);
        }

        return pathpoint;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private PathPoint getSafePoint(Entity p_75858_1_, int p_75858_2_, int p_75858_3_, int p_75858_4_, PathPoint p_75858_5_, int p_75858_6_) {
        int verticalOffset = getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_, p_75858_4_, p_75858_5_);
        int newOffset = getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_ - 1,p_75858_4_, p_75858_5_);

        if (isVerticalOffsetValid(verticalOffset)) {
            return openPoint(p_75858_2_, p_75858_3_, p_75858_4_);
        }

        return multithreadingandtweaks$calculatePathPoint(p_75858_1_, p_75858_2_, p_75858_3_, p_75858_4_, p_75858_6_, verticalOffset, newOffset, p_75858_5_);
    }

    @Unique
    private PathPoint multithreadingandtweaks$calculatePathPoint(Entity entity, int x, int y, int z, int yOffset, int verticalOffset, int newOffset, PathPoint originalPoint) {
        PathPoint pathPoint = null;

        if (shouldCalculatePathPoint(yOffset, verticalOffset, newOffset)) {
            pathPoint = calculatePathPointInternal(entity, x, y, z, yOffset, newOffset, originalPoint);
        }

        return pathPoint;
    }

    @Unique
    private boolean shouldCalculatePathPoint(int yOffset, int verticalOffset, int newOffset) {
        return yOffset > 0 && isVerticalOffsetValid(verticalOffset) && newOffset == -2;
    }

    @Unique
    private PathPoint calculatePathPointInternal(Entity entity, int x, int y, int z, int yOffset, int newOffset, PathPoint originalPoint) {
        int newY = y + yOffset;
        int newVerticalOffset = getVerticalOffset(entity, x, newY, z, originalPoint);

        if (newVerticalOffset == 1) {
            return calculatePathPointFromNewOffset(entity, x, newY, z, yOffset, newOffset, originalPoint);
        }

        return null;
    }

    @Unique
    private PathPoint calculatePathPointFromNewOffset(Entity entity, int x, int y, int z, int yOffset, int newOffset, PathPoint originalPoint) {
        PathPoint pathPoint = openPoint(x, y, z);

        while (y > 0) {
            if (isPathingInWater && newOffset == -1) {
                return null;
            }

            if (newOffset != 1 || y - 1 <= 0) {
                return null;
            }

            y--;
            pathPoint = openPoint(x, y, z);
        }

        return pathPoint;
    }

    @Unique
    private boolean isVerticalOffsetValid(int offset) {
        return offset == 2 || offset == 1;
    }
    /**
     * @author
     * @reason
     */
    @Inject(method = "findPathOptions", at = @At("HEAD"), cancellable = true)
    private int findPathOptions(Entity p_75860_1_, PathPoint p_75860_2_, PathPoint p_75860_3_, PathPoint p_75860_4_,
        float p_75860_5_, CallbackInfoReturnable<Integer> cir) {
        int i = 0;
        byte b0 = 0;

        if (this.getVerticalOffset(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord + 1, p_75860_2_.zCoord, p_75860_3_)
            == 1) {
            b0 = 1;
        }

        PathPoint[] pathpoints = {
            this.getSafePoint(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord, p_75860_2_.zCoord + 1, p_75860_3_, b0),
            this.getSafePoint(p_75860_1_, p_75860_2_.xCoord - 1, p_75860_2_.yCoord, p_75860_2_.zCoord, p_75860_3_, b0),
            this.getSafePoint(p_75860_1_, p_75860_2_.xCoord + 1, p_75860_2_.yCoord, p_75860_2_.zCoord, p_75860_3_, b0),
            this.getSafePoint(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord, p_75860_2_.zCoord - 1, p_75860_3_, b0) };

        for (PathPoint pathpoint : pathpoints) {
            if (pathpoint != null && !pathpoint.isFirst && pathpoint.distanceTo(p_75860_4_) < p_75860_5_) {
                this.pathOptions[i++] = pathpoint;
            }
        }

        cir.cancel();
        cir.setReturnValue(i);
        return i;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getVerticalOffset(Entity p_75855_1_, int p_75855_2_, int p_75855_3_, int p_75855_4_,
        PathPoint p_75855_5_) {
        return func_82565_a(
            p_75855_1_,
            p_75855_2_,
            p_75855_3_,
            p_75855_4_,
            p_75855_5_,
            this.isPathingInWater,
            this.isMovementBlockAllowed,
            this.isWoddenDoorAllowed);
    }
}
