package fr.iamacat.optimizationsandtweaks.mixins.common.steamcraft2;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import steamcraft.common.init.InitBlocks;
import steamcraft.common.worldgen.trees.WorldGenBrassTree;

@Mixin(WorldGenBrassTree.class)
public class MixinFixCascadingFromWorldGenBrassTree {

    @Inject(method = "func_76484_a", at = @At("HEAD"), cancellable = true, remap = false)
    public void onGenerate(World world, Random rand, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        int l = rand.nextInt(3) + rand.nextInt(2) + 6;
        boolean flag = true;
        if (y >= 1 && y + l + 1 <= 256) {
            int j1;
            int k1;
            for (int i1 = y; i1 <= y + 1 + l; ++i1) {
                byte b0 = 1;
                if (i1 == y) {
                    b0 = 0;
                }

                if (i1 >= y + 1 + l - 2) {
                    b0 = 2;
                }

                for (j1 = x - b0; j1 <= x + b0 && flag; ++j1) {
                    for (k1 = z - b0; k1 <= z + b0 && flag; ++k1) {
                        if (i1 >= 0 && i1 < 256) {
                            world.getBlock(j1, i1, k1);
                            if (!this.multithreadingandtweaks$isReplaceable(world, j1, i1, k1)) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                cir.setReturnValue(false);
            } else {
                Block block2 = world.getBlock(x, y - 1, z);
                boolean isSoil = block2
                    .canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (BlockSapling) Blocks.sapling);
                if (isSoil && y < 256 - l - 1) {
                    this.multithreadingandtweaks$onPlantGrow(world, x, y - 1, z, x, y, z);
                    this.multithreadingandtweaks$onPlantGrow(world, x + 1, y - 1, z, x, y, z);
                    this.multithreadingandtweaks$onPlantGrow(world, x + 1, y - 1, z + 1, x, y, z);
                    this.multithreadingandtweaks$onPlantGrow(world, x, y - 1, z + 1, x, y, z);
                    int j3 = rand.nextInt(4);
                    j1 = l - rand.nextInt(4);
                    k1 = 2 - rand.nextInt(3);
                    int k3 = x;
                    int l1 = z;
                    int i2 = 0;

                    int j2;
                    int k2;
                    for (j2 = 0; j2 < l; ++j2) {
                        k2 = y + j2;
                        if (j2 >= j1 && k1 > 0) {
                            k3 += Direction.offsetX[j3];
                            l1 += Direction.offsetZ[j3];
                            --k1;
                        }

                        Block block1 = world.getBlock(k3, k2, l1);
                        if (block1.isAir(world, k3, k2, l1) || block1.isLeaves(world, k3, k2, l1)) {
                            this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                world,
                                k3,
                                k2,
                                l1,
                                InitBlocks.blockBrassLog,
                                0);
                            this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                world,
                                k3 + 1,
                                k2,
                                l1,
                                InitBlocks.blockBrassLog,
                                0);
                            this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                world,
                                k3,
                                k2,
                                l1 + 1,
                                InitBlocks.blockBrassLog,
                                0);
                            this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                world,
                                k3 + 1,
                                k2,
                                l1 + 1,
                                InitBlocks.blockBrassLog,
                                0);
                            i2 = k2;
                        }
                    }

                    int l2;
                    int l3;
                    for (j2 = -2; j2 <= 0; ++j2) {
                        for (k2 = -2; k2 <= 0; ++k2) {
                            l3 = -1;
                            this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                world,
                                k3 + j2,
                                i2 + l3,
                                l1 + k2,
                                InitBlocks.blockBrassLeaves,
                                0);
                            this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                world,
                                1 + k3 - j2,
                                i2 + l3,
                                l1 + k2,
                                InitBlocks.blockBrassLeaves,
                                0);
                            this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                world,
                                k3 + j2,
                                i2 + l3,
                                1 + l1 - k2,
                                InitBlocks.blockBrassLeaves,
                                0);
                            this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                world,
                                1 + k3 - j2,
                                i2 + l3,
                                1 + l1 - k2,
                                InitBlocks.blockBrassLeaves,
                                0);
                            if ((j2 > -2 || k2 > -1) && (j2 != -1 || k2 != -2)) {
                                l2 = 1;
                                this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                    world,
                                    k3 + j2,
                                    i2 + l2,
                                    l1 + k2,
                                    InitBlocks.blockBrassLeaves,
                                    0);
                                this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                    world,
                                    1 + k3 - j2,
                                    i2 + l2,
                                    l1 + k2,
                                    InitBlocks.blockBrassLeaves,
                                    0);
                                this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                    world,
                                    k3 + j2,
                                    i2 + l2,
                                    1 + l1 - k2,
                                    InitBlocks.blockBrassLeaves,
                                    0);
                                this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                    world,
                                    1 + k3 - j2,
                                    i2 + l2,
                                    1 + l1 - k2,
                                    InitBlocks.blockBrassLeaves,
                                    0);
                            }
                        }
                    }

                    if (rand.nextBoolean()) {
                        this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                            world,
                            k3,
                            i2 + 2,
                            l1,
                            InitBlocks.blockBrassLeaves,
                            0);
                        this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                            world,
                            k3 + 1,
                            i2 + 2,
                            l1,
                            InitBlocks.blockBrassLeaves,
                            0);
                        this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                            world,
                            k3 + 1,
                            i2 + 2,
                            l1 + 1,
                            InitBlocks.blockBrassLeaves,
                            0);
                        this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                            world,
                            k3,
                            i2 + 2,
                            l1 + 1,
                            InitBlocks.blockBrassLeaves,
                            0);
                    }

                    for (j2 = -3; j2 <= 4; ++j2) {
                        for (k2 = -3; k2 <= 4; ++k2) {
                            if ((j2 != -3 || k2 != -3) && (j2 != -3 || k2 != 4)
                                && (j2 != 4 || k2 != -3)
                                && (j2 != 4 || k2 != 4)
                                && (Math.abs(j2) < 3 || Math.abs(k2) < 3)) {
                                this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                    world,
                                    k3 + j2,
                                    i2,
                                    l1 + k2,
                                    InitBlocks.blockBrassLeaves,
                                    0);
                            }
                        }
                    }

                    for (j2 = -1; j2 <= 2; ++j2) {
                        for (k2 = -1; k2 <= 2; ++k2) {
                            if ((j2 < 0 || j2 > 1 || k2 < 0 || k2 > 1) && rand.nextInt(3) <= 0) {
                                l3 = rand.nextInt(3) + 2;

                                for (l2 = 0; l2 < l3; ++l2) {
                                    this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                        world,
                                        x + j2,
                                        i2 - l2 - 1,
                                        z + k2,
                                        InitBlocks.blockBrassLog,
                                        0);
                                }

                                int i3;
                                for (l2 = -1; l2 <= 1; ++l2) {
                                    for (i3 = -1; i3 <= 1; ++i3) {
                                        this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                            world,
                                            k3 + j2 + l2,
                                            i2 - 0,
                                            l1 + k2 + i3,
                                            InitBlocks.blockBrassLeaves,
                                            0);
                                    }
                                }

                                for (l2 = -2; l2 <= 2; ++l2) {
                                    for (i3 = -2; i3 <= 2; ++i3) {
                                        if (Math.abs(l2) != 2 || Math.abs(i3) != 2) {
                                            this.multithreadingandtweaks$setBlockAndNotifyAdequately(
                                                world,
                                                k3 + j2 + l2,
                                                i2 - 1,
                                                l1 + k2 + i3,
                                                InitBlocks.blockBrassLeaves,
                                                0);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        } else {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private void multithreadingandtweaks$setBlockAndNotifyAdequately(World world, int x, int y, int z, Block block,
        int metadata) {
        world.setBlock(x, y, z, block, metadata, 3);
        world.notifyBlockChange(x, y, z, block);
    }

    @Unique
    private void multithreadingandtweaks$onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY,
        int sourceZ) {
        world.getBlock(x, y, z)
            .onPlantGrow(world, x, y, z, sourceX, sourceY, sourceZ);
    }

    @Unique
    private boolean multithreadingandtweaks$isReplaceable(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block.isReplaceable(world, x, y, z);
    }
}
