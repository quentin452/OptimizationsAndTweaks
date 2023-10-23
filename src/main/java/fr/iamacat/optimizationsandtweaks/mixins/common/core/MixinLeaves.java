package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockLeaves.class)
public class MixinLeaves extends Block {

    @Shadow
    int[] field_150128_a;

    protected MixinLeaves(Material materialIn) {
        super(materialIn);
    }

    /**
     * @author iamacatfr
     * @reason reduce tps lags caused by BlockLeaves
     */
    @Overwrite
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (worldIn.isRemote) {
            return;
        }

        int l = worldIn.getBlockMetadata(x, y, z);

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

            if (worldIn.checkChunksExist(x - i1, y - i1, z - i1, x + i1, y + i1, z + i1)) {
                for (int i2 = -b0; i2 <= b0; ++i2) {
                    for (int j2 = -b0; j2 <= b0; ++j2) {
                        for (int k2 = -b0; k2 <= b0; ++k2) {
                            int index = (i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1;
                            Block block = worldIn.getBlock(x + i2, y + j2, z + k2);

                            if (!block.canSustainLeaves(worldIn, x + i2, y + j2, z + k2)) {
                                this.field_150128_a[index] = block.isLeaves(worldIn, x + i2, y + j2, z + k2) ? -2 : -1;
                            } else {
                                this.field_150128_a[index] = 0;
                            }
                        }
                    }
                }

                for (l1 = 1; l1 <= 4; ++l1) {
                    for (int i2 = -b0; i2 <= b0; ++i2) {
                        for (int j2 = -b0; j2 <= b0; ++j2) {
                            for (int k2 = -b0; k2 <= b0; ++k2) {
                                int index = (i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1;

                                if (this.field_150128_a[index] == l1 - 1) {
                                    int[] nearbyIndices = new int[] { (i2 + k1 - 1) * j1 + (j2 + k1) * b1 + k2 + k1,
                                        (i2 + k1 + 1) * j1 + (j2 + k1) * b1 + k2 + k1,
                                        (i2 + k1) * j1 + (j2 + k1 - 1) * b1 + k2 + k1,
                                        (i2 + k1) * j1 + (j2 + k1 + 1) * b1 + k2 + k1,
                                        (i2 + k1) * j1 + (j2 + k1) * b1 + (k2 + k1 - 1),
                                        (i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1 + 1 };

                                    for (int nearbyIndex : nearbyIndices) {
                                        if (this.field_150128_a[nearbyIndex] == -2) {
                                            this.field_150128_a[nearbyIndex] = l1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            l1 = this.field_150128_a[k1 * j1 + k1 * b1 + k1];

            if (l1 >= 0) {
                worldIn.setBlockMetadataWithNotify(x, y, z, l & -9, 4);
            } else {
                this.multithreadingandtweaks$removeLeaves(worldIn, x, y, z);
            }
        }
    }

    @Unique
    private void multithreadingandtweaks$removeLeaves(World p_150126_1_, int p_150126_2_, int p_150126_3_,
        int p_150126_4_) {
        this.dropBlockAsItem(
            p_150126_1_,
            p_150126_2_,
            p_150126_3_,
            p_150126_4_,
            p_150126_1_.getBlockMetadata(p_150126_2_, p_150126_3_, p_150126_4_),
            0);
        p_150126_1_.setBlockToAir(p_150126_2_, p_150126_3_, p_150126_4_);
    }
}
