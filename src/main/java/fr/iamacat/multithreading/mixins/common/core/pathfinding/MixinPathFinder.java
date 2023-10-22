package fr.iamacat.multithreading.mixins.common.core.pathfinding;

import fr.iamacat.multithreading.asm.TargetedMod;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(PathFinder.class)
public class MixinPathFinder {
    @Unique
    private THashMap<Integer, PathPoint> multithreadingandtweaks$pointMap = new THashMap<>();

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
}
