package fr.iamacat.optimizationsandtweaks.mixins.common.core.pathfinding;

import java.util.Map;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;

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
    @Inject(method = "func_82565_a", at = @At("HEAD"), cancellable = true)
    private static int func_82565_a(Entity entity, int x, int y, int z, PathPoint pathPoint, boolean checkWater,
        boolean avoidWater, boolean checkDoors, CallbackInfoReturnable cir) {
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
        cir.setReturnValue(false);
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
    private PathPoint getSafePoint(Entity p_75858_1_, int p_75858_2_, int p_75858_3_, int p_75858_4_,
        PathPoint p_75858_5_, int p_75858_6_, CallbackInfoReturnable<PathPoint> cir) {
        if (OptimizationsandTweaksConfig.enableMixinPathFinding) {
            PathPoint pathpoint1 = null;
            int i1 = this.multithreadingandtweaks$getVerticalOffset(
                p_75858_1_,
                p_75858_2_,
                p_75858_3_,
                p_75858_4_,
                p_75858_5_,
                cir);

            if (i1 == 2) {
                return this.openPoint(p_75858_2_, p_75858_3_, p_75858_4_, cir);
            } else {
                if (i1 == 1) {
                    pathpoint1 = this.openPoint(p_75858_2_, p_75858_3_, p_75858_4_, cir);
                }

                if (pathpoint1 == null && p_75858_6_ > 0
                    && i1 != -3
                    && i1 != -4
                    && this.multithreadingandtweaks$getVerticalOffset(
                        p_75858_1_,
                        p_75858_2_,
                        p_75858_3_ + p_75858_6_,
                        p_75858_4_,
                        p_75858_5_,
                        cir) == 1) {
                    pathpoint1 = this.openPoint(p_75858_2_, p_75858_3_ + p_75858_6_, p_75858_4_, cir);
                    p_75858_3_ += p_75858_6_;
                }

                if (pathpoint1 != null) {
                    int j1 = 0;
                    int k1 = 0;

                    while (p_75858_3_ > 0) {
                        k1 = this.multithreadingandtweaks$getVerticalOffset(
                            p_75858_1_,
                            p_75858_2_,
                            p_75858_3_ - 1,
                            p_75858_4_,
                            p_75858_5_,
                            cir);

                        if (this.isPathingInWater && k1 == -1) {
                            return null;
                        }

                        if (k1 != 1) {
                            break;
                        }

                        if (j1++ >= p_75858_1_.getMaxSafePointTries()) {
                            return null;
                        }

                        --p_75858_3_;

                        if (p_75858_3_ > 0) {
                            pathpoint1 = this.openPoint(p_75858_2_, p_75858_3_, p_75858_4_, cir);
                        }
                    }

                    if (k1 == -2) {
                        return null;
                    }
                }

                return pathpoint1;
            }
        }
        cir.setReturnValue(p_75858_5_);
        return p_75858_5_;

    }

    @Redirect(
        method = "findPathOptions",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/pathfinding/PathFinder;getVerticalOffset(Lnet/minecraft/entity/Entity;IIIILnet/minecraft/pathfinding/PathPoint;)I"))
    private int redirectGetVerticalOffset(PathFinder pathFinder, Entity entity, int x, int y, int z,
        PathPoint pathPoint, float p_75860_5, CallbackInfoReturnable cir) {
        return multithreadingandtweaks$getVerticalOffset(entity, x, y, z, pathPoint, cir);
    }

    @Unique
    public int multithreadingandtweaks$getVerticalOffset(Entity p_75855_1_, int p_75855_2_, int p_75855_3_,
        int p_75855_4_, PathPoint p_75855_5_, CallbackInfoReturnable cir) {
        return func_82565_a(
            p_75855_1_,
            p_75855_2_,
            p_75855_3_,
            p_75855_4_,
            p_75855_5_,
            this.isPathingInWater,
            this.isMovementBlockAllowed,
            this.isWoddenDoorAllowed,
            cir);
    }
}
