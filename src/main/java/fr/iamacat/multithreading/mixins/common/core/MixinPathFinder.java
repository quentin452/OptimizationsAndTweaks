package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PathFinder.class)
public class MixinPathFinder {
    /** should the PathFinder go through wodden door blocks */
    @Shadow
    private boolean isWoddenDoorAllowed;
    /** should the PathFinder disregard BlockMovement type materials in its path */
    @Shadow
    private boolean isMovementBlockAllowed;
    @Shadow
    private boolean isPathingInWater;
    @Shadow
    private PathPoint[] pathOptions = new PathPoint[32];
    @Shadow

    private IntHashMap pointMap = new IntHashMap();
    /**
     * populates pathOptions with available points and returns the number of options found (args: unused1, currentPoint,
     * unused2, targetPoint, maxDistance)
     */
    @Overwrite
    private int findPathOptions(Entity p_75860_1_, PathPoint p_75860_2_, PathPoint p_75860_3_, PathPoint p_75860_4_, float p_75860_5_)
    {
        if (MultithreadingandtweaksConfig.enableMixinPathFinder){
        int i = 0;
        byte b0 = 0;

        if (this.multithreadingandtweaks$getVerticalOffset(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord + 1, p_75860_2_.zCoord, p_75860_3_) == 1)
        {
            b0 = 1;
        }

        PathPoint pathpoint3 = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord, p_75860_2_.zCoord + 1, p_75860_3_, b0);
        PathPoint pathpoint4 = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord - 1, p_75860_2_.yCoord, p_75860_2_.zCoord, p_75860_3_, b0);
        PathPoint pathpoint5 = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord + 1, p_75860_2_.yCoord, p_75860_2_.zCoord, p_75860_3_, b0);
        PathPoint pathpoint6 = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord, p_75860_2_.zCoord - 1, p_75860_3_, b0);

        if (pathpoint3 != null && !pathpoint3.isFirst && pathpoint3.distanceTo(p_75860_4_) < p_75860_5_)
        {
            this.pathOptions[i++] = pathpoint3;
        }

        if (pathpoint4 != null && !pathpoint4.isFirst && pathpoint4.distanceTo(p_75860_4_) < p_75860_5_)
        {
            this.pathOptions[i++] = pathpoint4;
        }

        if (pathpoint5 != null && !pathpoint5.isFirst && pathpoint5.distanceTo(p_75860_4_) < p_75860_5_)
        {
            this.pathOptions[i++] = pathpoint5;
        }

        if (pathpoint6 != null && !pathpoint6.isFirst && pathpoint6.distanceTo(p_75860_4_) < p_75860_5_)
        {
            this.pathOptions[i++] = pathpoint6;
        }

        return i;
        }
        return 0;
    }
    /**
     * Checks if an entity collides with blocks at a position. Returns 1 if clear, 0 for colliding with any solid block,
     * -1 for water(if avoiding water) but otherwise clear, -2 for lava, -3 for fence, -4 for closed trapdoor, 2 if
     * otherwise clear except for open trapdoor or water(if not avoiding)
     */
    @Unique
    public int multithreadingandtweaks$getVerticalOffset(Entity p_75855_1_, int p_75855_2_, int p_75855_3_, int p_75855_4_, PathPoint p_75855_5_)
    {
        return func_82565_a(p_75855_1_, p_75855_2_, p_75855_3_, p_75855_4_, p_75855_5_, this.isPathingInWater, this.isMovementBlockAllowed, this.isWoddenDoorAllowed);
    }
    @Unique
    private static int func_82565_a(Entity p_82565_0_, int p_82565_1_, int p_82565_2_, int p_82565_3_, PathPoint p_82565_4_, boolean p_82565_5_, boolean p_82565_6_, boolean p_82565_7_)
    {
        boolean flag3 = false;

        for (int l = p_82565_1_; l < p_82565_1_ + p_82565_4_.xCoord; ++l)
        {
            for (int i1 = p_82565_2_; i1 < p_82565_2_ + p_82565_4_.yCoord; ++i1)
            {
                for (int j1 = p_82565_3_; j1 < p_82565_3_ + p_82565_4_.zCoord; ++j1)
                {
                    Block block = p_82565_0_.worldObj.getBlock(l, i1, j1);

                    if (block.getMaterial() != Material.air)
                    {
                        if (block == Blocks.trapdoor)
                        {
                            flag3 = true;
                        }
                        else if (block != Blocks.flowing_water && block != Blocks.water)
                        {
                            if (!p_82565_7_ && block == Blocks.wooden_door)
                            {
                                return 0;
                            }
                        }
                        else
                        {
                            if (p_82565_5_)
                            {
                                return -1;
                            }

                            flag3 = true;
                        }

                        int k1 = block.getRenderType();

                        if (p_82565_0_.worldObj.getBlock(l, i1, j1).getRenderType() == 9)
                        {
                            int j2 = MathHelper.floor_double(p_82565_0_.posX);
                            int l1 = MathHelper.floor_double(p_82565_0_.posY);
                            int i2 = MathHelper.floor_double(p_82565_0_.posZ);

                            if (p_82565_0_.worldObj.getBlock(j2, l1, i2).getRenderType() != 9 && p_82565_0_.worldObj.getBlock(j2, l1 - 1, i2).getRenderType() != 9)
                            {
                                return -3;
                            }
                        }
                        else if (!block.getBlocksMovement(p_82565_0_.worldObj, l, i1, j1) && (!p_82565_6_ || block != Blocks.wooden_door))
                        {
                            if (k1 == 11 || block == Blocks.fence_gate || k1 == 32)
                            {
                                return -3;
                            }

                            if (block == Blocks.trapdoor)
                            {
                                return -4;
                            }

                            Material material = block.getMaterial();

                            if (material != Material.lava)
                            {
                                return 0;
                            }

                            if (!p_82565_0_.handleLavaMovement())
                            {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return flag3 ? 2 : 1;
    }

    /**
     * Returns a point that the entity can safely move to
     */
    @Overwrite
    private PathPoint getSafePoint(Entity p_75858_1_, int p_75858_2_, int p_75858_3_, int p_75858_4_, PathPoint p_75858_5_, int p_75858_6_) {
        if (MultithreadingandtweaksConfig.enableMixinPathFinder) {
            int i1 = this.multithreadingandtweaks$getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_, p_75858_4_, p_75858_5_);

            if (i1 == 2) {
                return this.multithreadingandtweaks$openPoint(p_75858_2_, p_75858_3_, p_75858_4_);
            } else {
                PathPoint pathpoint1 = null;

                if (i1 == 1) {
                    pathpoint1 = this.multithreadingandtweaks$openPoint(p_75858_2_, p_75858_3_, p_75858_4_);
                }

                if (pathpoint1 == null && p_75858_6_ > 0 && i1 != -3 && i1 != -4) {
                    int yOffset = 0;
                    int k1 = this.multithreadingandtweaks$getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_ + p_75858_6_, p_75858_4_, p_75858_5_);

                    if (k1 == 1) {
                        pathpoint1 = this.multithreadingandtweaks$openPoint(p_75858_2_, p_75858_3_ + p_75858_6_, p_75858_4_);
                        p_75858_3_ += p_75858_6_;
                    }

                    if (pathpoint1 != null) {
                        int j1 = 0;

                        while (p_75858_3_ > 0) {
                            k1 = this.multithreadingandtweaks$getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_ - 1, p_75858_4_, p_75858_5_);

                            if (this.isPathingInWater && k1 == -1) {
                                return null;
                            }

                            if (k1 != 1) {
                                break;
                            }

                            if (j1++ >= p_75858_1_.getMaxSafePointTries()) {
                                return null;
                            }

                            yOffset -= 1;
                            p_75858_3_--;

                            if (p_75858_3_ > 0) {
                                pathpoint1 = this.multithreadingandtweaks$openPoint(p_75858_2_, p_75858_3_, p_75858_4_);
                            }
                        }

                        if (k1 == -2) {
                            return null;
                        }
                    }
                }

                return pathpoint1;
            }
        }

        return p_75858_5_;
    }


    /**
     * Returns a mapped point or creates and adds one
     */
    @Unique
    private PathPoint multithreadingandtweaks$openPoint(int p_75854_1_, int p_75854_2_, int p_75854_3_)
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
}
