package fr.iamacat.multithreading.mixins.common.core.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PathFinder.class)
public class MixinPathFinder {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static int func_82565_a(Entity entity, int x, int y, int z, PathPoint pathPoint, boolean checkWater, boolean avoidWater, boolean checkDoors) {
        boolean isTrapdoorPresent = false;

        for (int i = x; i < x + pathPoint.xCoord; ++i) {
            for (int j = y; j < y + pathPoint.yCoord; ++j) {
                for (int k = z; k < z + pathPoint.zCoord; ++k) {
                    Block block = entity.worldObj.getBlock(i, j, k);
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
                            int posX = (int) entity.posX;
                            int posY = (int) entity.posY;
                            int posZ = (int) entity.posZ;

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
}
