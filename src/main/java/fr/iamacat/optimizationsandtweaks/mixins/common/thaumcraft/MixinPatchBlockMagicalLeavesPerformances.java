package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.config.MultithreadingandtweaksConfig;
import thaumcraft.common.blocks.BlockMagicalLeaves;
import thaumcraft.common.config.ConfigBlocks;

@Mixin(BlockMagicalLeaves.class)
public class MixinPatchBlockMagicalLeavesPerformances {

    @Unique
    int[] field_150128_a;

    /**
     * @author imacatfr
     * @reason Reduce tps lags caused by updateTick method from BlockMagicalLeaves class
     */

    @Inject(method = "func_149674_a", at = @At("HEAD"), remap = false, cancellable = true)
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random, CallbackInfo ci) {
        {
            if (MultithreadingandtweaksConfig.enableMixinPatchBlockMagicalLeavesPerformances) {
                if (!par1World.isRemote) {
                    int l = par1World.getBlockMetadata(par2, par3, par4);

                    if ((l & 8) != 0 && (l & 4) == 0) {
                        byte b0 = 4;
                        int i1 = b0 + 1;
                        byte b1 = 32;
                        int j1 = b1 * b1;
                        int k1 = b1 / 2;

                        if (this.field_150128_a == null) {
                            this.field_150128_a = new int[b1 * b1 * b1];
                        }

                        int l1;

                        if (par1World
                            .checkChunksExist(par2 - i1, par3 - i1, par4 - i1, par2 + i1, par3 + i1, par4 + i1)) {
                            int i2;
                            int j2;

                            for (l1 = -b0; l1 <= b0; ++l1) {
                                for (i2 = -b0; i2 <= b0; ++i2) {
                                    for (j2 = -b0; j2 <= b0; ++j2) {
                                        Block block = par1World.getBlock(par2 + l1, par3 + i2, par4 + j2);

                                        if (!block.canSustainLeaves(par1World, par2 + l1, par3 + i2, par4 + j2)) {
                                            if (block.isLeaves(par1World, par2 + l1, par3 + i2, par4 + j2)) {
                                                this.field_150128_a[(l1 + k1) * j1 + (i2 + k1) * b1 + j2 + k1] = -2;
                                            } else {
                                                this.field_150128_a[(l1 + k1) * j1 + (i2 + k1) * b1 + j2 + k1] = -1;
                                            }
                                        } else {
                                            this.field_150128_a[(l1 + k1) * j1 + (i2 + k1) * b1 + j2 + k1] = 0;
                                        }
                                    }
                                }
                            }

                            for (l1 = 1; l1 <= 4; ++l1) {
                                for (i2 = -b0; i2 <= b0; ++i2) {
                                    for (j2 = -b0; j2 <= b0; ++j2) {
                                        for (int k2 = -b0; k2 <= b0; ++k2) {
                                            if (this.field_150128_a[(i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1]
                                                == l1 - 1) {
                                                if (this.field_150128_a[(i2 + k1 - 1) * j1 + (j2 + k1) * b1 + k2 + k1]
                                                    == -2) {
                                                    this.field_150128_a[(i2 + k1 - 1) * j1 + (j2 + k1) * b1
                                                        + k2
                                                        + k1] = l1;
                                                }

                                                if (this.field_150128_a[(i2 + k1 + 1) * j1 + (j2 + k1) * b1 + k2 + k1]
                                                    == -2) {
                                                    this.field_150128_a[(i2 + k1 + 1) * j1 + (j2 + k1) * b1
                                                        + k2
                                                        + k1] = l1;
                                                }

                                                if (this.field_150128_a[(i2 + k1) * j1 + (j2 + k1 - 1) * b1 + k2 + k1]
                                                    == -2) {
                                                    this.field_150128_a[(i2 + k1) * j1 + (j2 + k1 - 1) * b1
                                                        + k2
                                                        + k1] = l1;
                                                }

                                                if (this.field_150128_a[(i2 + k1) * j1 + (j2 + k1 + 1) * b1 + k2 + k1]
                                                    == -2) {
                                                    this.field_150128_a[(i2 + k1) * j1 + (j2 + k1 + 1) * b1
                                                        + k2
                                                        + k1] = l1;
                                                }

                                                if (this.field_150128_a[(i2 + k1) * j1 + (j2 + k1) * b1 + (k2 + k1 - 1)]
                                                    == -2) {
                                                    this.field_150128_a[(i2 + k1) * j1 + (j2 + k1) * b1
                                                        + (k2 + k1 - 1)] = l1;
                                                }

                                                if (this.field_150128_a[(i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1 + 1]
                                                    == -2) {
                                                    this.field_150128_a[(i2 + k1) * j1 + (j2 + k1) * b1
                                                        + k2
                                                        + k1
                                                        + 1] = l1;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        l1 = this.field_150128_a[k1 * j1 + k1 * b1 + k1];

                        if (l1 >= 0) {
                            par1World.setBlockMetadataWithNotify(par2, par3, par4, l & -9, 4);
                        } else {
                            this.multithreadingandtweaks$removeLeaves(par1World, par2, par3, par4);
                        }
                    }
                }
                ci.cancel();
            }
        }
    }

    @Unique
    private void multithreadingandtweaks$removeLeaves(final World par1World, final int par2, final int par3,
        final int par4) {
        par1World.setBlock(par2, par3, par4, Blocks.air, 0, 2);
    }

    @Unique
    private static boolean multithreadingandtweaks$canSustainLeaves(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block == ConfigBlocks.blockMagicalLog;
    }
}
