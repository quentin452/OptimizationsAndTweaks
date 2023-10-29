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

import java.util.concurrent.CompletableFuture;

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

        for (int currentX = p_82565_1_, currentY = p_82565_2_, currentZ = p_82565_3_; currentX < endX && currentY < endY && currentZ < endZ; ++currentX, ++currentY, ++currentZ) {
            int chunkX = currentX >> 4;
            int chunkZ = currentZ >> 4;
            Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

            int localX = currentX & 15;
            int localZ = currentZ & 15;

            Block block = chunk.getBlock(localX, currentY, localZ);

            int result = processBlock(p_82565_0_, block, world, entityX, entityY, entityZ, p_82565_6_, p_82565_7_);
            if (result == 0) {
                return 0;
            } else if (result == -1) {
                if (p_82565_5_) {
                    return -1;
                }
                isTrapdoorPresent = true;
            } else if (result == -2) {
                return -2;
            } else if (result == -3) {
                return -3;
            } else if (result == -4) {
                return -4;
            }
        }

        return isTrapdoorPresent ? 2 : 1;
    }

    @Unique
    private static int processBlock(Entity entity, Block block, World world, int entityX, int entityY, int entityZ, boolean p_82565_6, boolean p_82565_7) {
        Material material = block.getMaterial();
        int renderType = block.getRenderType();

        if (material == Material.air) {
            return 1;
        }

        if (block == Blocks.trapdoor) {
            return -4;
        }

        if (block == Blocks.flowing_water || block == Blocks.water) {
            if (p_82565_6) {
                return -1;
            }
            return 1;
        }

        if (!p_82565_6 && block == Blocks.wooden_door) {
            return 0;
        }

        if (renderType == 9 && !isRenderTypeValid(world, entityX, entityY, entityZ)) {
            return -3;
        }

        if (!block.getBlocksMovement(world, entityX, entityY, entityZ) && (!p_82565_7 || block != Blocks.wooden_door)) {
            return checkInvalidBlock(renderType, block, material, entity);
        }

        return 1;
    }

    @Unique
    private static int checkInvalidBlock(int renderType, Block block, Material material, Entity entity) {
        if (isInvalidMovementBlock(renderType, block)) {
            return -3;
        }

        if (material != Material.lava) {
            return 0;
        }

        if (!entity.handleLavaMovement()) {
            return -2;
        }

        return 1;
    }
    @Unique
    private static boolean isRenderTypeValid(World world, int x, int y, int z) {
        Block currentBlock = world.getBlock(x, y, z);
        Block blockBelow = world.getBlock(x, y - 1, z);
        int currentBlockRenderType = currentBlock.getRenderType();
        int blockBelowRenderType = blockBelow.getRenderType();
        return currentBlockRenderType == 9 || blockBelowRenderType == 9;
    }

    @Unique
    private static boolean isInvalidMovementBlock(int renderType, Block block) {
        return renderType == 11 || block == Blocks.fence_gate || renderType == 32;
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

    /**
     * @author
     * @reason
     */
    @Overwrite
    private PathPoint getSafePoint(Entity p_75858_1_, int p_75858_2_, int p_75858_3_, int p_75858_4_, PathPoint p_75858_5_, int p_75858_6_)
    {
        PathPoint pathpoint1 = null;
        int i1 = this.getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_, p_75858_4_, p_75858_5_);

        if (i1 == 2)
        {
            return this.openPoint(p_75858_2_, p_75858_3_, p_75858_4_);
        }
        else
        {
            if (i1 == 1)
            {
                pathpoint1 = this.openPoint(p_75858_2_, p_75858_3_, p_75858_4_);
            }

            if (pathpoint1 == null && p_75858_6_ > 0 && i1 != -3 && i1 != -4 && this.getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_ + p_75858_6_, p_75858_4_, p_75858_5_) == 1)
            {
                pathpoint1 = this.openPoint(p_75858_2_, p_75858_3_ + p_75858_6_, p_75858_4_);
                p_75858_3_ += p_75858_6_;
            }

            if (pathpoint1 != null)
            {
                int j1 = 0;
                int k1 = 0;

                while (p_75858_3_ > 0)
                {
                    k1 = this.getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_ - 1, p_75858_4_, p_75858_5_);

                    if (this.isPathingInWater && k1 == -1)
                    {
                        return null;
                    }

                    if (k1 != 1)
                    {
                        break;
                    }

                    if (j1++ >= p_75858_1_.getMaxSafePointTries())
                    {
                        return null;
                    }

                    --p_75858_3_;

                    if (p_75858_3_ > 0)
                    {
                        pathpoint1 = this.openPoint(p_75858_2_, p_75858_3_, p_75858_4_);
                    }
                }

                if (k1 == -2)
                {
                    return null;
                }
            }

            return pathpoint1;
        }
    }
    /**
     * @author
     * @reason
     */
    @Inject(method = "findPathOptions", at = @At("HEAD"), cancellable = true)
    private int findPathOptions(Entity p_75860_1_, PathPoint p_75860_2_, PathPoint p_75860_3_, PathPoint p_75860_4_,
                                float p_75860_5_, CallbackInfoReturnable<Integer> cir) {

        cir.cancel();
        int i = 0;
        byte b0 = 0;

        int verticalOffsetResult = this.getVerticalOffset(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord + 1, p_75860_2_.zCoord, p_75860_3_);
        if (verticalOffsetResult == 1) {
            b0 = 1;
        }

        PathPoint[] pathpoints = new PathPoint[4];
        pathpoints[0] = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord, p_75860_2_.zCoord + 1, p_75860_3_, b0);
        pathpoints[1] = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord - 1, p_75860_2_.yCoord, p_75860_2_.zCoord, p_75860_3_, b0);
        pathpoints[2] = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord + 1, p_75860_2_.yCoord, p_75860_2_.zCoord, p_75860_3_, b0);
        pathpoints[3] = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord, p_75860_2_.zCoord - 1, p_75860_3_, b0);

        for (PathPoint pathpoint : pathpoints) {
            if (pathpoint != null && !pathpoint.isFirst) {
                double distanceSquared = pathpoint.distanceToSquared(p_75860_4_);
                if (distanceSquared < p_75860_5_ * p_75860_5_) {
                    this.pathOptions[i++] = pathpoint;
                }
            }
        }

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
