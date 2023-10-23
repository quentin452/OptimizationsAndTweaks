package fr.iamacat.multithreading.mixins.common.core.pathfinding;

import fr.iamacat.multithreading.asm.TargetedMod;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.pathfinding.PathPoint2;
import fr.iamacat.multithreading.utils.trove.map.hash.THashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(PathFinder.class)
public class MixinPathFinder {
    @Shadow
    private boolean isWoddenDoorAllowed;
    /** should the PathFinder disregard BlockMovement type materials in its path */
    @Shadow
    private boolean isMovementBlockAllowed;
    @Shadow
    private boolean isPathingInWater;
    @Unique
    private TreeMap<Integer, PathPoint> multithreadingandtweaks$pointMap = new TreeMap<>();

    /**
     * @author iamacatfr
     * @reason optimize func_82565_a
     */
    @Overwrite
    public static int func_82565_a(Entity entity, int x, int y, int z, PathPoint pathPoint, boolean checkWater, boolean avoidWater, boolean checkDoors) {
        if (!MultithreadingandtweaksConfig.enableMixinPathFinding) {
        }
        boolean isTrapdoorPresent = false;

        int posX = (int) entity.posX;
        int posY = (int) entity.posY;
        int posZ = (int) entity.posZ;
        Block block;

        for (int i = x; i < x + pathPoint.xCoord; ++i) {
            for (int j = y; j < y + pathPoint.yCoord; ++j) {
                for (int k = z; k < z + pathPoint.zCoord; ++k) {
                    block = entity.worldObj.getBlock(i, j, k);
                    Material material = block.getMaterial();

                    if (material != Material.air) {
                        if (block == Blocks.trapdoor) {
                            isTrapdoorPresent = true;
                        } else if (block != Blocks.flowing_water && block != Blocks.water) {
                            if (!checkDoors && block == Blocks.wooden_door) {
                                return 0;
                            }
                        } else {
                            if (checkWater) {
                                return -1;
                            }
                            isTrapdoorPresent = true;
                        }

                        int renderType = block.getRenderType();

                        if (renderType == 9) {
                            if (entity.worldObj.getBlock(posX, posY, posZ).getRenderType() != 9 && entity.worldObj.getBlock(posX, posY - 1, posZ).getRenderType() != 9) {
                                return -3;
                            }
                        } else if (!block.getBlocksMovement(entity.worldObj, i, j, k) && (!avoidWater || block != Blocks.wooden_door)) {
                            if (renderType == 11 || block == Blocks.fence_gate || renderType == 32) {
                                return -3;
                            }

                            if (block == Blocks.trapdoor) {
                                return -4;
                            }

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

        return isTrapdoorPresent ? 2 : 1;
    }
    /**
     * @author iamacatfr
     * @reason optimize openPoint
     */
    @Overwrite
    private final PathPoint openPoint(int x, int y, int z) {
        int hash = PathPoint.makeHash(x, y, z);
        PathPoint pathPoint = this.multithreadingandtweaks$pointMap.get(hash);

        if (pathPoint == null) {
            pathPoint = new PathPoint(x, y, z);
            this.multithreadingandtweaks$pointMap.put(hash, pathPoint);
        }

        return pathPoint;
    }
    @Redirect(
        method = "addToPath",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/pathfinding/Path;pointMap:Ljava/util/Map;"
        )
    )
    private Map<?, ?> redirectPointMap(PathEntity pathEntity) {
        return multithreadingandtweaks$pointMap;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private PathPoint getSafePoint(Entity p_75858_1_, int p_75858_2_, int p_75858_3_, int p_75858_4_, PathPoint p_75858_5_, int p_75858_6_)
    {
      if (MultithreadingandtweaksConfig.enableMixinPathFinding){
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
        return p_75858_5_;
    }


    @Shadow
    public int getVerticalOffset(Entity p_75855_1_, int p_75855_2_, int p_75855_3_, int p_75855_4_, PathPoint p_75855_5_)
    {
        return func_82565_a(p_75855_1_, p_75855_2_, p_75855_3_, p_75855_4_, p_75855_5_, this.isPathingInWater, this.isMovementBlockAllowed, this.isWoddenDoorAllowed);
    }
}
