package fr.iamacat.optimizationsandtweaks.mixins.common.core.pathfinding;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;

import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(PathFinder.class)
public class MixinPathFinder {
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
    private ConcurrentSkipListMap<Integer, PathPoint> multithreadingandtweaks$pointMap = new ConcurrentSkipListMap<>();

    /**
     * @author iamacatfr
     * @reason optimize func_82565_a
     */
    @Inject(method = "func_82565_a", at = @At("HEAD"), cancellable = true)
    private static int func_82565_a(Entity entity, int x, int y, int z, PathPoint pathPoint, boolean checkWater,
        boolean avoidWater, boolean checkDoors, CallbackInfoReturnable<PathPoint> cir) {
        if (OptimizationsandTweaksConfig.enableMixinPathFinding) {
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
                                if (entity.worldObj.getBlock(posX, posY, posZ)
                                    .getRenderType() != 9
                                    && entity.worldObj.getBlock(posX, posY - 1, posZ)
                                        .getRenderType() != 9) {
                                    return -3;
                                }
                            } else if (!block.getBlocksMovement(entity.worldObj, i, j, k)
                                && (!avoidWater || block != Blocks.wooden_door)) {
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
        cir.setReturnValue(null);
        cir.cancel();
        return x;
    }

    /**
     * @author iamacatfr
     * @reason optimize openPoint
     */
    @Inject(method = "openPoint", at = @At("HEAD"), cancellable = true)
    private final PathPoint openPoint(int x, int y, int z, CallbackInfoReturnable<PathPoint> cir) {
        if (OptimizationsandTweaksConfig.enableMixinPathFinding) {
            int hash = PathPoint.makeHash(x, y, z);
            PathPoint pathPoint = this.multithreadingandtweaks$pointMap.get(hash);

            if (pathPoint == null) {
                pathPoint = new PathPoint(x, y, z);
                this.multithreadingandtweaks$pointMap.put(hash, pathPoint);
            }
            cir.setReturnValue(pathPoint);
            return pathPoint;
        }
        cir.cancel();
        return null;
    }

    @Redirect(
        method = "addToPath",
        at = @At(value = "FIELD", target = "Lnet/minecraft/pathfinding/Path;pointMap:Ljava/util/Map;"))
    private Map<?, ?> redirectPointMap(PathEntity pathEntity) {
        return multithreadingandtweaks$pointMap;
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "getSafePoint", at = @At("HEAD"), cancellable = true)
    private PathPoint getSafePoint(Entity p_75858_1_, int p_75858_2_, int p_75858_3_, int p_75858_4_, PathPoint p_75858_5_, int p_75858_6_, CallbackInfoReturnable<PathPoint> cir) {
        int verticalOffset = this.multithreadingandtweaks$getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_, p_75858_4_, p_75858_5_, cir);

        if (verticalOffset == 2) {
            cir.cancel();
            cir.setReturnValue(openPoint(p_75858_2_, p_75858_3_,p_75858_4_, cir));
            return null;
        }

        PathPoint resultPoint = null;

        if (verticalOffset == 1) {
            resultPoint = openPoint(p_75858_2_, p_75858_3_, p_75858_4_, cir);
        }

        while (resultPoint == null && p_75858_6_ > 0 && verticalOffset != -3 && verticalOffset != -4) {
            int yOffset = this.multithreadingandtweaks$getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_ + p_75858_6_, p_75858_4_, p_75858_5_, cir);

            if (yOffset == 1) {
                resultPoint = openPoint(p_75858_2_, p_75858_3_ + p_75858_6_, p_75858_4_, cir);
                p_75858_3_ += p_75858_6_;
            } else {
                break;
            }

            p_75858_6_--;
        }

        if (resultPoint != null) {
            int tries = 0;
            int yOffset = 0;

            while (p_75858_3_ > 0) {
                yOffset = this.multithreadingandtweaks$getVerticalOffset(p_75858_1_, p_75858_2_, p_75858_3_ - 1, p_75858_4_, p_75858_5_, cir);

                if (isPathingInWater && yOffset == -1) {
                    cir.cancel();
                    cir.setReturnValue(null);
                    return null;
                }

                if (yOffset != 1) {
                    break;
                }

                if (tries++ >= p_75858_1_.getMaxSafePointTries()) {
                    cir.cancel();
                    cir.setReturnValue(null);
                    return null;
                }

                p_75858_3_--;

                if (p_75858_3_ > 0) {
                    resultPoint = openPoint(p_75858_2_, p_75858_3_, p_75858_4_, cir);
                }
            }

            if (yOffset == -2) {
                cir.cancel();
                cir.setReturnValue(null);
                return null;
            }
        }

        cir.setReturnValue(resultPoint);
        return resultPoint;
    }

    @Inject(method = "findPathOptions", at = @At("HEAD"), cancellable = true)
    private int findPathOptions(Entity p_75860_1_, PathPoint p_75860_2_, PathPoint p_75860_3_, PathPoint p_75860_4_, float p_75860_5_, CallbackInfoReturnable cir)
    {
        int i = 0;
        byte b0 = 0;

        if (this.multithreadingandtweaks$getVerticalOffset(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord + 1, p_75860_2_.zCoord, p_75860_3_,cir) == 1)
        {
            b0 = 1;
        }

        PathPoint pathpoint3 = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord, p_75860_2_.zCoord + 1, p_75860_3_, b0,cir);
        PathPoint pathpoint4 = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord - 1, p_75860_2_.yCoord, p_75860_2_.zCoord, p_75860_3_, b0,cir);
        PathPoint pathpoint5 = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord + 1, p_75860_2_.yCoord, p_75860_2_.zCoord, p_75860_3_, b0,cir);
        PathPoint pathpoint6 = this.getSafePoint(p_75860_1_, p_75860_2_.xCoord, p_75860_2_.yCoord, p_75860_2_.zCoord - 1, p_75860_3_, b0,cir);

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
        cir.setReturnValue(i);
        return i;
    }

    @Unique
    private final Map<Integer, Integer> multithreadingandtweaks$blockCache = new ConcurrentHashMap<>();

    @Unique
    public int multithreadingandtweaks$getVerticalOffset(Entity entity, int x, int y, int z, PathPoint pathPoint,
        CallbackInfoReturnable<PathPoint> cir) {
        int key = ((x & 0xFFF) << 20) | ((z & 0xFFF) << 8) | (y & 0xFF);

        Integer cachedResult = multithreadingandtweaks$blockCache.get(key);
        if (cachedResult != null) {
            return cachedResult;
        }

        int result = func_82565_a(
            entity,
            x,
            y,
            z,
            pathPoint,
            this.isPathingInWater,
            this.isMovementBlockAllowed,
            this.isWoddenDoorAllowed,
            cir);

        multithreadingandtweaks$blockCache.put(key, result);

        return result;
    }
}
