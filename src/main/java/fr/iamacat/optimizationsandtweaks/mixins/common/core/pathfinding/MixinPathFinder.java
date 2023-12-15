package fr.iamacat.optimizationsandtweaks.mixins.common.core.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PathFinder.class)
public class MixinPathFinder {

    @Shadow
    private boolean isMovementBlockAllowed;
    @Shadow
    private boolean isPathingInWater;
    @Shadow
    private boolean isWoddenDoorAllowed;

    @Shadow
    private IntHashMap pointMap = new IntHashMap();

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

    /**
     * @author
     * @reason
     */
    @Overwrite
    private PathPoint getSafePoint(Entity entity, int x, int y, int z, PathPoint currentPoint, int yOffset) {
        int verticalOffset = this.getVerticalOffset(entity, x, y, z, currentPoint);

        if (verticalOffset == 2 || verticalOffset == 1) {
            return this.openPoint(x, y, z);
        }

        PathPoint safePoint = null;

        if (yOffset > 0 && verticalOffset != -3 && verticalOffset != -4) {
            int newY = y + yOffset;
            int yOffsetIndex = this.getVerticalOffset(entity, x, newY, z, currentPoint);

            if (yOffsetIndex == 1) {
                safePoint = this.openPoint(x, newY, z);
                y = newY;
            }
        }

        if (safePoint != null) {
            int tries = 0;

            int offset = 0;
            while (y > 0) {
                offset = this.getVerticalOffset(entity, x, y - 1, z, currentPoint);

                if (this.isPathingInWater && offset == -1) {
                    return null;
                }

                if (offset != 1) {
                    break;
                }

                if (tries++ >= entity.getMaxSafePointTries()) {
                    return null;
                }

                --y;

                if (y > 0) {
                    safePoint = this.openPoint(x, y, z);
                }
            }

            if (offset == -2) {
                return null;
            }
        }

        return safePoint;
    }
    @Shadow
    private final PathPoint openPoint(int p_75854_1_, int p_75854_2_, int p_75854_3_)
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

    @Shadow
    public int getVerticalOffset(Entity p_75855_1_, int p_75855_2_, int p_75855_3_, int p_75855_4_, PathPoint p_75855_5_)
    {
        return func_82565_a(p_75855_1_, p_75855_2_, p_75855_3_, p_75855_4_, p_75855_5_, this.isPathingInWater, this.isMovementBlockAllowed, this.isWoddenDoorAllowed);
    }


}
