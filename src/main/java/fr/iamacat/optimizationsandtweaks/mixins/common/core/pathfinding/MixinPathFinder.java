package fr.iamacat.optimizationsandtweaks.mixins.common.core.pathfinding;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
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
        int entityX = MathHelper.floor_double(p_82565_0_.posX);
        int entityY = MathHelper.floor_double(p_82565_0_.posY);
        int entityZ = MathHelper.floor_double(p_82565_0_.posZ);

        int endX = p_82565_1_ + p_82565_4_.xCoord;
        int endY = p_82565_2_ + p_82565_4_.yCoord;
        int endZ = p_82565_3_ + p_82565_4_.zCoord;

        boolean isTrapdoorPresent = false;

        for (int x = p_82565_1_; x < endX; ++x) {
            for (int y = p_82565_2_; y < endY; ++y) {
                for (int z = p_82565_3_; z < endZ; ++z) {
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

    private final PathPoint openPoint(int p_75854_1_, int p_75854_2_, int p_75854_3_) {
        int l = PathPoint.makeHash(p_75854_1_, p_75854_2_, p_75854_3_);

        PathPoint pathpoint = multithreadingandtweaks$pointCache.get(l);

        if (pathpoint == null) {
            pathpoint = new PathPoint(p_75854_1_, p_75854_2_, p_75854_3_);

            multithreadingandtweaks$pointCache.put(l, pathpoint);
        }

        return pathpoint;
    }

    @Unique
    private PathEntity multithreadingandtweaks$createEntityPath(PathPoint p_75853_1_, PathPoint p_75853_2_) {
        int i = 1;
        PathPoint pathpoint2;

        for (pathpoint2 = p_75853_2_; previous != null; pathpoint2 = previous) {
            ++i;
        }

        PathPoint[] apathpoint = new PathPoint[i];
        pathpoint2 = p_75853_2_;
        --i;

        for (apathpoint[i] = p_75853_2_; previous != null; apathpoint[i] = pathpoint2) {
            pathpoint2 = previous;
            --i;
        }
        return new PathEntity(apathpoint);
        /*
         * int i = 1;
         * PathPoint pathpoint2;
         * for (pathpoint2 = p_75853_2_; pathpoint2.previous != null; pathpoint2 = pathpoint2.previous)
         * {
         * ++i;
         * }
         * PathPoint[] apathpoint = new PathPoint[i];
         * pathpoint2 = p_75853_2_;
         * --i;
         * for (apathpoint[i] = p_75853_2_; pathpoint2.previous != null; apathpoint[i] = pathpoint2)
         * {
         * pathpoint2 = pathpoint2.previous;
         * --i;
         * }
         * return new PathEntity2(apathpoint);
         */
    }

    /**
     * @author
     * @reason
     */
    @Unique
    private PathEntity addToPath(Entity p_75861_1_, PathPoint p_75861_2_, PathPoint p_75861_3_, PathPoint p_75861_4_,
        float p_75861_5_, CallbackInfoReturnable cir) {
        totalPathDistance = 0.0F;
        distanceToNext = p_75861_2_.distanceToSquared(p_75861_3_);
        distanceToTarget = distanceToNext;
        this.path.clearPath();
        this.path.addPoint(p_75861_2_);
        PathPoint pathpoint3 = p_75861_2_;

        while (!this.path.isPathEmpty()) {
            PathPoint pathpoint4 = this.path.dequeue();

            if (pathpoint4.equals(p_75861_3_)) {
                return this.multithreadingandtweaks$createEntityPath(p_75861_2_, p_75861_3_);
            }

            if (pathpoint4.distanceToSquared(p_75861_3_) < pathpoint3.distanceToSquared(p_75861_3_)) {
                pathpoint3 = pathpoint4;
            }

            pathpoint4.isFirst = true;
            int i = this.findPathOptions(p_75861_1_, pathpoint4, p_75861_4_, p_75861_3_, p_75861_5_, cir);

            for (int j = 0; j < i; ++j) {
                PathPoint pathpoint5 = this.pathOptions[j];
                float f1 = totalPathDistance + pathpoint4.distanceToSquared(pathpoint5);

                if (!pathpoint5.isAssigned() || f1 < totalPathDistance) {
                    previous = pathpoint4;
                    totalPathDistance = f1;
                    distanceToNext = pathpoint5.distanceToSquared(p_75861_3_);

                    if (pathpoint5.isAssigned()) {
                        this.path.changeDistance(pathpoint5, totalPathDistance + distanceToNext);
                    } else {
                        distanceToTarget = totalPathDistance + distanceToNext;
                        this.path.addPoint(pathpoint5);
                    }
                }
            }
        }

        if (pathpoint3 == p_75861_2_) {
            return null;
        } else {
            return this.multithreadingandtweaks$createEntityPath(p_75861_2_, pathpoint3);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private PathPoint getSafePoint(Entity entity, int x, int y, int z, PathPoint currentPoint, int yOffset) {
        int verticalOffset = getVerticalOffset(entity, x, y, z, currentPoint);
        int newOffset = getVerticalOffset(entity, x, y - 1, z, currentPoint);

        if (verticalOffset == 2 || verticalOffset == 1) {
            return openPoint(x, y, z);
        }

        PathPoint pathPoint = null;

        if (yOffset > 0 && verticalOffset != -3 && verticalOffset != -4) {
            int newY = y + yOffset;
            int newVerticalOffset = getVerticalOffset(entity, x, newY, z, currentPoint);

            if (newVerticalOffset == 1) {
                pathPoint = openPoint(x, newY, z);
                y = newY;

                while (y > 0) {
                    if (isPathingInWater && newOffset == -1) {
                        return null;
                    }

                    if (newOffset != 1) {
                        break;
                    }

                    if (y - 1 <= 0) {
                        return null;
                    }

                    y--;
                    pathPoint = openPoint(x, y, z);
                }

                if (newOffset == -2) {
                    return null;
                }
            }
        }

        return pathPoint;
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
            this.getSafePoint(
                p_75860_1_,
                p_75860_2_.xCoord,
                p_75860_2_.yCoord,
                p_75860_2_.zCoord - 1,
                p_75860_3_,
                b0) };

        for (PathPoint pathpoint : pathpoints) {
            if (pathpoint != null && !pathpoint.isFirst && pathpoint.distanceTo(p_75860_4_) < p_75860_5_) {
                this.pathOptions[i++] = pathpoint;
            }
        }

        cir.cancel();
        cir.setReturnValue(i);
        return i;
    }

    @Unique
    private final Map<Integer, Integer> multithreadingandtweaks$blockCache = new HashMap<>();

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
