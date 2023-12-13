package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.world.WorldGenGreatwoodTrees;

import java.util.Random;

@Mixin(WorldGenGreatwoodTrees.class)
public abstract class MixinWorldGenGreatwoodTrees extends WorldGenAbstractTree {

    @Shadow
    static final byte[] otherCoordPairs = new byte[]{2, 0, 0, 1, 2, 1};
    @Shadow
    Random rand = new Random();
    @Shadow
    World worldObj;
    @Shadow
    int[] basePos = new int[]{0, 0, 0};
    @Shadow
    int heightLimit = 0;
    @Shadow
    int height;
    @Shadow
    double heightAttenuation = 0.618;
    @Shadow
    double branchDensity = 1.0;
    @Shadow
    double branchSlope = 0.38;
    @Shadow
    double scaleWidth = 1.2;
    @Shadow
    double leafDensity = 0.9;
    @Shadow
    int trunkSize = 2;
    @Shadow
    int heightLimitLimit = 11;
    @Shadow
    int leafDistanceLimit = 4;
    @Shadow
    int[][] leafNodes;

    public MixinWorldGenGreatwoodTrees(boolean par1) {
        super(par1);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int checkBlockLine(int[] par1ArrayOfInteger, int[] par2ArrayOfInteger) {
        int[] var3 = new int[]{0, 0, 0};
        byte var4 = 0;

        byte var5;
        for(var5 = 0; var4 < 3; ++var4) {
            var3[var4] = par2ArrayOfInteger[var4] - par1ArrayOfInteger[var4];
            if (Math.abs(var3[var4]) > Math.abs(var3[var5])) {
                var5 = var4;
            }
        }

        if (var3[var5] == 0) {
            return -1;
        } else {
            byte var6 = otherCoordPairs[var5];
            byte var7 = otherCoordPairs[var5 + 3];
            byte var8;
            if (var3[var5] > 0) {
                var8 = 1;
            } else {
                var8 = -1;
            }

            double var9 = (double)var3[var6] / (double)var3[var5];
            double var11 = (double)var3[var7] / (double)var3[var5];
            int[] var13 = new int[]{0, 0, 0};
            int var14 = 0;

            int var15;
            for(var15 = var3[var5] + var8; var14 != var15; var14 += var8) {
                var13[var5] = par1ArrayOfInteger[var5] + var14;
                var13[var6] = MathHelper.floor_double((double)par1ArrayOfInteger[var6] + (double)var14 * var9);
                var13[var7] = MathHelper.floor_double((double)par1ArrayOfInteger[var7] + (double)var14 * var11);

                try {
                    Block var16 = this.worldObj.getBlock(var13[0], var13[1], var13[2]);
                    if (var16 != Blocks.air && var16 != ConfigBlocks.blockMagicalLeaves) {
                        break;
                    }
                } catch (Exception var17) {
                }
            }

            return var14 == var15 ? -1 : Math.abs(var14);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generateLeafNodeList() {
        this.height = (int)(this.heightLimit * this.heightAttenuation);
        if (this.height >= this.heightLimit) {
            this.height = this.heightLimit - 1;
        }

        int var1 = (int)(1.382 + Math.pow(this.leafDensity * this.heightLimit / 13.0, 2.0));
        if (var1 < 1) {
            var1 = 1;
        }

        int[][] var2 = new int[var1 * this.heightLimit][4];
        int var3 = this.basePos[1] + this.heightLimit - this.leafDistanceLimit;
        int var4 = 1;
        int var5 = this.basePos[1] + this.height;
        int var6 = var3 - this.basePos[1];
        var2[0][0] = this.basePos[0];
        var2[0][1] = var3;
        var2[0][2] = this.basePos[2];
        var2[0][3] = var5;
        --var3;

        while(true) {
            while(var6 >= 0) {
                int var7 = 0;
                float var8 = this.layerSize(var6);
                if (!(var8 < 0.0F)) {
                    for (double var9 = 0.5; var7 < var1; ++var7) {
                        double var11 = this.scaleWidth * var8 * ( this.rand.nextFloat() + 0.328);
                        double var13 = this.rand.nextFloat() * 2.0 * Math.PI;
                        int var15 = MathHelper.floor_double(var11 * Math.sin(var13) + this.basePos[0] + var9);
                        int var16 = MathHelper.floor_double(var11 * Math.cos(var13) + this.basePos[2] + var9);
                        int[] var17 = new int[]{var15, var3, var16};
                        int[] var18 = new int[]{var15, var3 + this.leafDistanceLimit, var16};
                        if (this.checkBlockLine(var17, var18) == -1) {
                            int[] var19 = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};
                            double var20 = Math.sqrt(Math.pow(Math.abs(this.basePos[0] - var17[0]), 2.0) + Math.pow(Math.abs(this.basePos[2] - var17[2]), 2.0));
                            double var22 = var20 * this.branchSlope;
                            if (var17[1] - var22 > var5) {
                                var19[1] = var5;
                            } else {
                                var19[1] = (int) (var17[1] - var22);
                            }

                            if (this.checkBlockLine(var19, var17) == -1) {
                                var2[var4][0] = var15;
                                var2[var4][1] = var3;
                                var2[var4][2] = var16;
                                var2[var4][3] = var19[1];
                                ++var4;
                            }
                        }
                    }

                }
                --var3;
                --var6;
            }

            this.leafNodes = new int[var4][4];
            System.arraycopy(var2, 0, this.leafNodes, 0, var4);
            return;
        }
    }


    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5, boolean spiders) {
        this.worldObj = par1World;
        long var6 = par2Random.nextLong();
        this.rand.setSeed(var6);
        this.basePos[0] = par3;
        this.basePos[1] = par4;
        this.basePos[2] = par5;
        if (this.heightLimit == 0) {
            this.heightLimit = this.heightLimitLimit + this.rand.nextInt(this.heightLimitLimit);
        }

        boolean valid = false;

        int a;
        label77:
        for(a = -1; a < 2; ++a) {
            label75:
            for(a = -1; a < 2; ++a) {
                for(int x = 0; x < this.trunkSize; ++x) {
                    for(int z = 0; z < this.trunkSize; ++z) {
                        if (!this.validTreeLocation(x + a, z + a)) {
                            continue label75;
                        }
                    }
                }

                valid = true;
                this.basePos[0] += a;
                this.basePos[2] += a;
                break label77;
            }
        }

        if (!valid) {
            return false;
        } else {
            this.generateLeafNodeList();
            this.generateLeaves();
            this.generateLeafNodeBases();
            this.generateTrunk();
            this.scaleWidth = 1.66;
            this.basePos[0] = par3;
            this.basePos[1] = par4 + this.height;
            this.basePos[2] = par5;
            this.generateLeafNodeList();
            this.generateLeaves();
            this.generateLeafNodeBases();
            this.generateTrunk();
            if (spiders) {
                this.worldObj.setBlock(par3, par4 - 1, par5, Blocks.mob_spawner, 0, 3);
                TileEntityMobSpawner var14 = (TileEntityMobSpawner)par1World.getTileEntity(par3, par4 - 1, par5);
                if (var14 != null) {
                    var14.func_145881_a().setEntityName("CaveSpider");

                    for(a = 0; a < 50; ++a) {
                        int xx = par3 - 7 + par2Random.nextInt(14);
                        int yy = par4 + par2Random.nextInt(10);
                        int zz = par5 - 7 + par2Random.nextInt(14);
                        if (par1World.isAirBlock(xx, yy, zz) && (BlockUtils.isBlockTouching(par1World, xx, yy, zz, ConfigBlocks.blockMagicalLeaves) || BlockUtils.isBlockTouching(par1World, xx, yy, zz, ConfigBlocks.blockMagicalLog))) {
                            this.worldObj.setBlock(xx, yy, zz, Blocks.web, 0, 3);
                        }
                    }

                    par1World.setBlock(par3, par4 - 2, par5, Blocks.chest, 0, 3);
                    TileEntityChest var16 = (TileEntityChest)par1World.getTileEntity(par3, par4 - 2, par5);
                    if (var16 != null) {
                        ChestGenHooks loot = ChestGenHooks.getInfo("dungeonChest");
                        WeightedRandomChestContent.generateChestContents(this.rand, loot.getItems(this.rand), var16, loot.getCount(this.rand));
                    }
                }
            }

            return true;
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    float layerSize(int par1) {
        if (par1 < (this.heightLimit) * 0.3) {
            return -1.618F;
        } else {
            float var2 = this.heightLimit / 2.0F;
            float var3 = this.heightLimit / 2.0F - par1;
            float var4;
            if (var3 == 0.0F) {
                var4 = var2;
            } else if (Math.abs(var3) >= var2) {
                var4 = 0.0F;
            } else {
                var4 = (float)Math.sqrt(Math.pow(Math.abs(var2), 2.0) - Math.pow(Math.abs(var3), 2.0));
            }

            var4 *= 0.5F;
            return var4;
        }
    }
    @Shadow
    boolean validTreeLocation(int x, int z) {
        int[] var1 = new int[]{this.basePos[0] + x, this.basePos[1], this.basePos[2] + z};
        int[] var2 = new int[]{this.basePos[0] + x, this.basePos[1] + this.heightLimit - 1, this.basePos[2] + z};

        try {
            Block var3 = this.worldObj.getBlock(this.basePos[0] + x, this.basePos[1] - 1, this.basePos[2] + z);
            boolean isSoil = var3.canSustainPlant(this.worldObj, this.basePos[0] + x, this.basePos[1] - 1, this.basePos[2] + z, ForgeDirection.UP, (BlockSapling)Blocks.sapling);
            if (!isSoil) {
                return false;
            } else {
                int var4 = this.checkBlockLine(var1, var2);
                if (var4 == -1) {
                    return true;
                } else if (var4 < 6) {
                    return false;
                } else {
                    this.heightLimit = var4;
                    return true;
                }
            }
        } catch (Exception var8) {
            return false;
        }
    }
    @Shadow
    void generateLeaves() {
        int var1 = 0;

        for(int var2 = this.leafNodes.length; var1 < var2; ++var1) {
            int var3 = this.leafNodes[var1][0];
            int var4 = this.leafNodes[var1][1];
            int var5 = this.leafNodes[var1][2];
            this.generateLeafNode(var3, var4, var5);
        }

    }
    @Shadow
    void generateLeafNode(int par1, int par2, int par3) {
        int var4 = par2;

        for(int var5 = par2 + this.leafDistanceLimit; var4 < var5; ++var4) {
            float var6 = this.leafSize(var4 - par2);
            this.genTreeLayer(par1, var4, par3, var6, (byte)1, ConfigBlocks.blockMagicalLeaves);
        }

    }
    @Shadow
    float leafSize(int par1) {
        return par1 >= 0 && par1 < this.leafDistanceLimit ? (par1 != 0 && par1 != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    void genTreeLayer(int par1, int par2, int par3, float par4, byte par5, Block par6) {
        int var7 = (int)(par4 + 0.618D);
        byte var8 = otherCoordPairs[par5];
        byte var9 = otherCoordPairs[par5 + 3];
        int[] var10 = new int[]{par1, par2, par3};
        int[] var11 = new int[]{0, 0, 0};
        int var12 = -var7;
        int var13 = -var7;

        for(var11[par5] = var10[par5]; var12 <= var7; ++var12) {
            var11[var8] = var10[var8] + var12;
            var13 = -var7;

            while(var13 <= var7) {
                double var15 = Math.pow(Math.abs(var12) + 0.5D, 2.0D) + Math.pow(Math.abs(var13) + 0.5D, 2.0D);
                if (!(var15 > (par4 * par4))) {
                    try {
                        var11[var9] = var10[var9] + var13;
                        Block block = this.worldObj.getBlock(var11[0], var11[1], var11[2]);
                        if ((block == Blocks.air || block == ConfigBlocks.blockMagicalLeaves) && (block == null || block.canBeReplacedByLeaves(this.worldObj, var11[0], var11[1], var11[2]))) {
                            this.setBlockAndNotifyAdequately(this.worldObj, var11[0], var11[1], var11[2], par6, 0);
                        }
                    } catch (Exception var17) {
                        ;
                    }

                }
                ++var13;
            }
        }

    }
    @Shadow
    void generateLeafNodeBases() {
        int var1 = 0;
        int var2 = this.leafNodes.length;

        for(int[] var3 = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]}; var1 < var2; ++var1) {
            int[] var4 = this.leafNodes[var1];
            int[] var5 = new int[]{var4[0], var4[1], var4[2]};
            var3[1] = var4[3];
            int var6 = var3[1] - this.basePos[1];
            if (this.leafNodeNeedsBase(var6)) {
                this.placeBlockLine(var3, var5, ConfigBlocks.blockMagicalLog);
            }
        }

    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    boolean leafNodeNeedsBase(int par1) {
        return par1 >= this.heightLimit * 0.2;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    void placeBlockLine(int[] par1ArrayOfInteger, int[] par2ArrayOfInteger, Block par3) {
        int[] var4 = new int[]{0, 0, 0};
        byte var5 = 0;

        byte var6;
        for(var6 = 0; var5 < 3; ++var5) {
            var4[var5] = par2ArrayOfInteger[var5] - par1ArrayOfInteger[var5];
            if (Math.abs(var4[var5]) > Math.abs(var4[var6])) {
                var6 = var5;
            }
        }

        if (var4[var6] != 0) {
            byte var7 = otherCoordPairs[var6];
            byte var8 = otherCoordPairs[var6 + 3];
            byte var9;
            if (var4[var6] > 0) {
                var9 = 1;
            } else {
                var9 = -1;
            }

            double var10 = (double)var4[var7] / (double)var4[var6];
            double var12 = (double)var4[var8] / (double)var4[var6];
            int[] var14 = new int[]{0, 0, 0};
            int var15 = 0;

            for(int var16 = var4[var6] + var9; var15 != var16; var15 += var9) {
                var14[var6] = MathHelper.floor_double((par1ArrayOfInteger[var6] + var15) + 0.5);
                var14[var7] = MathHelper.floor_double(par1ArrayOfInteger[var7] + var15 * var10 + 0.5);
                var14[var8] = MathHelper.floor_double(par1ArrayOfInteger[var8] + var15 * var12 + 0.5);
                byte var17 = 0;
                int var18 = Math.abs(var14[0] - par1ArrayOfInteger[0]);
                int var19 = Math.abs(var14[2] - par1ArrayOfInteger[2]);
                int var20 = Math.max(var18, var19);
                if (var20 > 0) {
                    if (var18 == var20) {
                        var17 = 4;
                    } else if (var19 == var20) {
                        var17 = 8;
                    }
                }

                this.setBlockAndNotifyAdequately(this.worldObj, var14[0], var14[1], var14[2], par3, var17);
            }
        }

    }
    @Shadow
    void generateTrunk() {
        int var1 = this.basePos[0];
        int var2 = this.basePos[1];
        int var3 = this.basePos[1] + this.height;
        int var4 = this.basePos[2];
        int[] var5 = new int[]{var1, var2, var4};
        int[] var6 = new int[]{var1, var3, var4};
        this.placeBlockLine(var5, var6, ConfigBlocks.blockMagicalLog);
        if (this.trunkSize == 2) {
            int var10002 = var5[0]++;
            var10002 = var6[0]++;
            this.placeBlockLine(var5, var6, ConfigBlocks.blockMagicalLog);
            var10002 = var5[2]++;
            var10002 = var6[2]++;
            this.placeBlockLine(var5, var6, ConfigBlocks.blockMagicalLog);
            var5[0] += -1;
            var6[0] += -1;
            this.placeBlockLine(var5, var6, ConfigBlocks.blockMagicalLog);
        }

    }
}
