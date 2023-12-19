package fr.iamacat.optimizationsandtweaks.mixins.common.core.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PathFinder.class)
public class MixinPathFinder {
    @Shadow
    private IBlockAccess worldMap;
    /** The path being generated */
    @Shadow
    private Path path = new Path();
    /** The points in the path */
    @Shadow
    private IntHashMap pointMap = new IntHashMap();
    /** Selection of path points to add to the path */
    @Shadow
    private PathPoint[] pathOptions = new PathPoint[32];
    /** should the PathFinder go through wodden door blocks */
    @Shadow
    private boolean isWoddenDoorAllowed;
    /** should the PathFinder disregard BlockMovement type materials in its path */
    @Shadow
    private boolean isMovementBlockAllowed;
    @Shadow
    private boolean isPathingInWater;

    /** tells the FathFinder to not stop pathing underwater */
    @Shadow
    private boolean canEntityDrown;


    /**
     * @author
     * @reason
     */
    @Overwrite
    public static int func_82565_a(Entity entity, int x, int y, int z, PathPoint pathPoint, boolean canPassFluids,
        boolean canOpenDoors, boolean canEnterDoors) {
        boolean hasTrapdoor = false;

        World world = entity.worldObj;

        int pathX = pathPoint.xCoord;
        int pathY = pathPoint.yCoord;
        int pathZ = pathPoint.zCoord;

        for (int l = x; l < x + pathX; ++l) {
            for (int i1 = y; i1 < y + pathY; ++i1) {
                for (int j1 = z; j1 < z + pathZ; ++j1) {
                    Block block = world.getBlock(l, i1, j1);

                    if (block.getMaterial() != Material.air) {
                        if (block == Blocks.trapdoor) {
                            hasTrapdoor = true;
                        } else if (block != Blocks.flowing_water && block != Blocks.water) {
                            if (!canEnterDoors && block == Blocks.wooden_door) {
                                return 0;
                            }
                        } else {
                            if (canPassFluids) {
                                return -1;
                            }
                            hasTrapdoor = true;
                        }

                        int renderType = block.getRenderType();

                        if (renderType == 9) {
                            int posX = MathHelper.floor_double(entity.posX);
                            int posY = MathHelper.floor_double(entity.posY);
                            int posZ = MathHelper.floor_double(entity.posZ);

                            if (world.getBlock(posX, posY, posZ)
                                .getRenderType() != 9
                                && world.getBlock(posX, posY - 1, posZ)
                                    .getRenderType() != 9) {
                                return -3;
                            }
                        } else if (!block.getBlocksMovement(world, l, i1, j1)
                            && (!canOpenDoors || block != Blocks.wooden_door)) {
                                if (renderType == 11 || block == Blocks.fence_gate || renderType == 32) {
                                    return -3;
                                }

                                if (block == Blocks.trapdoor) {
                                    return -4;
                                }

                                Material material = block.getMaterial();

                                if (material != Material.lava) {
                                    return 0;
                                }

                                if (!entity.handleLavaMovement()) {
                                    return -2;
                                }
                            }
                    }
                }
            }
        }
        return hasTrapdoor ? 2 : 1;
    }
    @Shadow
    public int getVerticalOffset(Entity p_75855_1_, int p_75855_2_, int p_75855_3_, int p_75855_4_, PathPoint p_75855_5_)
    {
        return func_82565_a(p_75855_1_, p_75855_2_, p_75855_3_, p_75855_4_, p_75855_5_, this.isPathingInWater, this.isMovementBlockAllowed, this.isWoddenDoorAllowed);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public PathPoint getSafePoint(Entity p_75858_1_, int p_75858_2_, int p_75858_3_, int p_75858_4_, PathPoint p_75858_5_, int p_75858_6_)
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
    @Overwrite
    public final PathPoint openPoint(int p_75854_1_, int p_75854_2_, int p_75854_3_)
    {
        int l = PathPoint.makeHash(p_75854_1_, p_75854_2_, p_75854_3_);
        PathPoint pathpoint = (PathPoint)this.pointMap.lookup(l);

        if (pathpoint == null)
        {
            pathpoint = new PathPoint(p_75854_1_, p_75854_2_, p_75854_3_);
            this.pointMap.addKey(l, pathpoint);
        }

        return pathpoint;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private int findPathOptions(Entity entity, PathPoint start, PathPoint up, PathPoint down, float maxDistance) {
        int i = 0;
        byte b0 = (byte)(this.getVerticalOffset(entity, start.xCoord, start.yCoord + 1, start.zCoord, up) == 1 ? 1 : 0);

        PathPoint[] surroundingPoints = {
            this.getSafePoint(entity, start.xCoord, start.yCoord, start.zCoord + 1, up, b0),
            this.getSafePoint(entity, start.xCoord - 1, start.yCoord, start.zCoord, up, b0),
            this.getSafePoint(entity, start.xCoord + 1, start.yCoord, start.zCoord, up, b0),
            this.getSafePoint(entity, start.xCoord, start.yCoord, start.zCoord - 1, up, b0)
        };

        for (PathPoint point : surroundingPoints) {
            if (point != null && !point.isFirst && point.distanceTo(down) < maxDistance) {
                this.pathOptions[i++] = point;
            }
        }

        return i;
    }
}
