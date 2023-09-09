package fr.iamacat.multithreading.mixins.common.pamsharvestcraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import com.pam.harvestcraft.WorldGenPamFruitTree;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(WorldGenPamFruitTree.class)
public class MixinFixWorldGenPamFruitTree {

    // todo fixme , it seem that there is a NullPointerException here
    @Unique
    private final int minTreeHeight;

    @Unique
    private final int metaWood;

    @Unique
    private final int metaLeaves;

    @Unique
    private final Block fruitType;

    public MixinFixWorldGenPamFruitTree(int par2, int par3, int par4, Block par5) {
        super();
        this.minTreeHeight = par2;
        this.metaWood = par3;
        this.metaLeaves = par4;
        this.fruitType = par5;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFixWorldGenPamFruitTree) {
            try {
                int l = par2Random.nextInt(3) + this.minTreeHeight;
                boolean flag = true;
                if (par4 >= 1 && par4 + l + 1 <= 256) {
                    byte b0;
                    int k1;
                    for (int i1 = par4; i1 <= par4 + 1 + l; ++i1) {
                        b0 = 1;
                        if (i1 == par4) {
                            b0 = 0;
                        }

                        if (i1 >= par4 + 1 + l - 2) {
                            b0 = 2;
                        }

                        for (int j1 = par3 - b0; j1 <= par3 + b0 && flag; ++j1) {
                            for (k1 = par5 - b0; k1 <= par5 + b0 && flag; ++k1) {
                                if (i1 >= 0 && i1 < 256) {
                                    par1World.getBlock(j1, i1, k1);
                                    if (!this.fruitType.isReplaceable(par1World, j1, i1, k1)) {
                                        flag = false;
                                    }
                                } else {
                                    flag = false;
                                }
                            }
                        }
                    }

                    if (!flag) {
                        return false;
                    } else {
                        Block block2 = par1World.getBlock(par3, par4 - 1, par5);
                        boolean isSoil = block2.canSustainPlant(
                            par1World,
                            par3,
                            par4 - 1,
                            par5,
                            ForgeDirection.UP,
                            (IPlantable) Blocks.sapling);
                        if (isSoil && par4 < 256 - l - 1) {
                            block2.onPlantGrow(par1World, par3, par4 - 1, par5, par3, par4, par5);
                            b0 = 3;
                            int b1 = 0;

                            for (k1 = par4 - b0 + l; k1 <= par4 + l; ++k1) {
                                int i3 = k1 - (par4 + l);
                                int l1 = b1 + 1 - i3 / 2;

                                for (int i2 = par3 - l1; i2 <= par3 + l1; ++i2) {
                                    int j2 = i2 - par3;

                                    for (int k2 = par5 - l1; k2 <= par5 + l1; ++k2) {
                                        int l2 = k2 - par5;
                                        Block block1;
                                        if ((Math.abs(j2) != l1 || Math.abs(l2) != l1
                                            || par2Random.nextInt(2) != 0 && i3 != 0)
                                            && ((block1 = par1World.getBlock(i2, k1, k2)).isAir(par1World, i2, k1, k2)
                                                || block1.isLeaves(par1World, i2, k1, k2))) {
                                            par1World.setBlock(i2, k1, k2, Blocks.leaves, this.metaLeaves, 2);

                                            if (par1World.getBlock(i2, k1 - 1, k2) == Blocks.air
                                                && par1World.getBlock(i2, k1 - 2, k2) == Blocks.air
                                                && k1 > 2
                                                && par2Random.nextInt(4) == 0) {
                                                par1World.setBlock(i2, k1 - 1, k2, this.fruitType, 0, 2);
                                            }
                                        }
                                    }
                                }
                            }

                            for (k1 = 0; k1 < l; ++k1) {
                                Block block = par1World.getBlock(par3, par4 + k1, par5);
                                if (block.isAir(par1World, par3, par4 + k1, par5)
                                    || block.isLeaves(par1World, par3, par4 + k1, par5)
                                    || block == this.fruitType) {
                                    par1World.setBlock(par3, par4 + k1, par5, Blocks.log, this.metaWood, 2);

                                }
                            }

                            return true;
                        } else {
                            return false;
                        }
                    }

                } else {
                    return false;
                }
            } catch (NullPointerException e) {
                // Handle the NullPointerException here, e.g., log it or perform other actions.
                return false; // You can also return false if an exception occurs.
            }
        }
        return false;
    }
}
