package fr.iamacat.optimizationsandtweaks.mixins.common.core.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PathFinder.class)
public class MixinPathFinder {

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
                    int result = optimizationsAndTweaks$checkBlock(world, entity, l, i1, j1, canPassFluids, canOpenDoors, canEnterDoors, hasTrapdoor);
                    if (result != 0) {
                        return result;
                    }
                }
            }
        }
        return 0;
    }
    @Unique
    private static int optimizationsAndTweaks$checkBlock(World world, Entity entity, int x, int y, int z, boolean canPassFluids,
                                                         boolean canOpenDoors, boolean canEnterDoors, boolean hasTrapdoor) {
        Block block = world.getBlock(x, y, z);

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
                return optimizationsAndTweaks$checkRenderTypeNine(world, entity);
            } else {
                return optimizationsAndTweaks$checkOtherBlockConditions(world, entity, x, y, z, canOpenDoors, block, renderType, hasTrapdoor);
            }
        }
        return 0;
    }
    @Unique
    private static int optimizationsAndTweaks$checkRenderTypeNine(World world, Entity entity) {
        int posX = MathHelper.floor_double(entity.posX);
        int posY = MathHelper.floor_double(entity.posY);
        int posZ = MathHelper.floor_double(entity.posZ);

        if (world.getBlock(posX, posY, posZ).getRenderType() != 9 &&
            world.getBlock(posX, posY - 1, posZ).getRenderType() != 9) {
            return -3;
        }
        return 0;
    }

    @Unique
    private static int optimizationsAndTweaks$checkOtherBlockConditions(World world, Entity entity, int x, int y, int z,
                                                                        boolean canOpenDoors, Block block, int renderType, boolean hasTrapdoor) {
        if (!block.getBlocksMovement(world, x, y, z) && (!canOpenDoors || block != Blocks.wooden_door)) {
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
        return 0;
    }
}
