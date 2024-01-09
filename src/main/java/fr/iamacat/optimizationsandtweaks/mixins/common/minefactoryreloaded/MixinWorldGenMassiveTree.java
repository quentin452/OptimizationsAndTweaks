package fr.iamacat.optimizationsandtweaks.mixins.common.minefactoryreloaded;

import cpw.mods.fml.common.registry.GameRegistry;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;
import powercrystals.minefactoryreloaded.world.WorldGenMassiveTree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Mixin(WorldGenMassiveTree.class)
public abstract class MixinWorldGenMassiveTree extends WorldGenerator {
    @Shadow
    private static final byte[] otherCoordPairs = new byte[]{2, 0, 0, 1, 2, 1};
    @Shadow
    private static final float PI = 3.1415927F;
    @Shadow
    private Random rand = new Random();
    @Shadow
    private World worldObj;
    @Shadow
    private int[] basePos = new int[]{0, 0, 0};
    @Shadow
    private int heightLimit = 0;
    @Shadow
    private int minHeight = -1;
    @Shadow
    private int height;
    @Shadow
    private int leafBases;
    @Shadow
    private int density;
    @Shadow
    private float heightAttenuation = 0.45F;
    @Shadow
    private float branchSlope = 0.45F;
    @Shadow
    private float scaleWidth = 4.0F;
    @Shadow
    private float branchDensity = 3.0F;
    @Shadow
    private int trunkSize = 11;
    @Shadow
    private boolean slopeTrunk = false;
    @Shadow
    private boolean safeGrowth = false;
    @Shadow
    private int heightLimitLimit = 250;
    @Shadow
    private int leafDistanceLimit = 4;
    @Shadow
    private int leafNodesLength;
    @Shadow
    private int[][] leafNodes;
    @Shadow
    public Block leaves;
    @Shadow
    public Block log;
    @Shadow
    private int[] placeScratch;
    @Shadow
    private int[] checkScratch;
    @Shadow
    private TLongObjectHashMap<Chunk> chunkMap;
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int checkBlockLine(int[] var1, int[] var2) {
        int[] var4 = this.checkScratch;
        byte var5 = 0;
        Set<Integer> visitedCoordinates = new HashSet<>();
        for (byte var6 = 0; var6 < 3; ++var6) {
            var4[var6] = var2[var6] - var1[var6];
            if (Math.abs(var4[var6]) > Math.abs(var4[var5])) {
                var5 = var6;
            }
        }
        if (var4[var5] == 0) {
            return -1;
        }
        byte var6 = otherCoordPairs[var5];
        byte var18 = otherCoordPairs[var5 + 3];
        byte var19 = (byte) ((var4[var5] > 0) ? 1 : -1);
        float var9 = (float) var4[var6] / (float) var4[var5];
        float var10 = (float) var4[var18] / (float) var4[var5];
        int var11 = 0;
        int var12 = var4[var5] + var19;
        for (; var11 != var12; var11 += var19) {
            var4[var5] = var1[var5] + var11;
            var4[var6] = MathHelper.floor_float(var1[var6] + var11 * var9);
            var4[var18] = MathHelper.floor_float(var1[var18] + var11 * var10);
            int coordinateHash = Arrays.hashCode(var4);
            if (visitedCoordinates.contains(coordinateHash)) {
                break;
            } else {
                visitedCoordinates.add(coordinateHash);
            }
            int var14 = var4[0] + var11;
            int var15 = var4[1];
            int var16 = var4[2];
            Block var17 = this.worldObj.getBlock(var14, var15, var16);
            if (optimizationsAndTweaks$shouldBreakLoop(var17, var14, var15, var16)) {
                break;
            }
        }
        return (var11 == var12) ? -1 : ((var11 < 0) ? -var11 : var11);
    }

    @Unique
    private boolean optimizationsAndTweaks$shouldBreakLoop(Block block, int x, int y, int z) {
        if (this.safeGrowth) {
            return !(block.isAir(this.worldObj, x, y, z) || block.isReplaceable(this.worldObj, x, y, z) ||
                block.canBeReplacedByLeaves(this.worldObj, x, y, z) || block.isLeaves(this.worldObj, x, y, z) ||
                block.isWood(this.worldObj, x, y, z) || block instanceof BlockSapling);
        } else {
            return block == Blocks.bedrock;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
         int chunkX = var3 >> 4;
         int chunkZ = var5 >> 4;
         Chunk chunk = var1.getChunkFromChunkCoords(chunkX, chunkZ);
         if(!chunk.isChunkLoaded) {
         return false;
         }
        this.worldObj = var1;
        long var6 = var2.nextLong();
        this.rand.setSeed(var6);
        this.basePos[0] = var3;
        this.basePos[1] = var4;
        this.basePos[2] = var5;
        if (this.heightLimit == 0) {
            this.heightLimit = this.heightLimitLimit;
        }

        if (this.minHeight == -1) {
            this.minHeight = 80;
        }

        if (!this.validTreeLocation()) {
            return false;
        } else {
            this.setup();
            this.generateLeafNodeList();
            this.generateLeaves();
            this.generateLeafNodeBases();
            this.generateTrunk();
            TLongObjectIterator<Chunk> var8 = this.chunkMap.iterator();

            while(var8.hasNext()) {
                var8.advance();
                MineFactoryReloadedCore.proxy.relightChunk(var8.value());
            }

            return true;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void generateLeafNodeList() {
        int var1 = this.density;
        int[] var2 = this.basePos;
        int[][] var3 = new int[var1 * this.heightLimit][4];
        int var4 = var2[1] + this.heightLimit - this.leafDistanceLimit;
        int var5 = 1;
        int var6 = var2[1] + this.height;
        int var7 = var4 - var2[1];
        var3[0][0] = var2[0];
        var3[0][1] = var4;
        var3[0][2] = var2[2];
        var3[0][3] = var6;
        --var4;

        while (var7 >= 0) {
            int var8 = 0;
            float var9 = this.layerSize(var7);
            if (var9 > 0.0F) {
                for (float var10 = 0.5F; var8 < var1; ++var8) {
                    float var11 = this.scaleWidth * var9 * (this.rand.nextFloat() + 0.328F);
                    float var12 = this.rand.nextFloat() * 2.0F * 3.1415927F;
                    int var13 = MathHelper.floor_double(var11 * Math.sin(var12) + var2[0] + var10);
                    int var14 = MathHelper.floor_double(var11 * Math.cos(var12) + var2[2] + var10);

                    int var17;
                    double var18 = Math.sqrt((var17 = var2[0] - var13) * var17 + (var17 = var2[2] - var14) * var17);
                    int var20 = (int)(var18 * this.branchSlope);
                    int[] var21 = new int[]{var2[0], Math.min(var4 - var20, var6), var2[2]};
                    var3[var5][0] = var13;
                    var3[var5][1] = var4;
                    var3[var5][2] = var14;
                    var3[var5][3] = var21[1];
                    ++var5;
                }
            }

            --var4;
            --var7;
        }

        this.leafNodes = var3;
        this.leafNodesLength = var5;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private boolean validTreeLocation() {
        int var1 = Math.min(this.heightLimit + this.basePos[1], 255) - this.basePos[1];

        if (var1 < this.minHeight) {
            return false;
        }

        this.heightLimit = var1;
        Block var2 = this.worldObj.getBlock(this.basePos[0], this.basePos[1] - 1, this.basePos[2]);

        if (!optimizationsAndTweaks$isValidSoilForSapling(var2)) {
            return false;
        }

        this.heightLimit = Math.min(this.heightLimit, this.heightLimitLimit);
        this.optimizationsAndTweaks$calculateAndSetHeight();

        return !this.safeGrowth || optimizationsAndTweaks$validateSafeGrowth();
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidSoilForSapling(Block block) {
        return block.canSustainPlant(this.worldObj, this.basePos[0], this.basePos[1] - 1, this.basePos[2], ForgeDirection.UP, MFRThings.rubberSaplingBlock);
    }

    @Unique
    private void optimizationsAndTweaks$calculateAndSetHeight() {
        this.height = (int) (this.heightLimit * this.heightAttenuation);
        if (this.height >= this.heightLimit) {
            this.height = this.heightLimit - 1;
        }
        this.height += this.rand.nextInt(this.heightLimit - this.height);
    }

    @Unique
    private boolean optimizationsAndTweaks$validateSafeGrowth() {
        int var5 = this.basePos[0];
        int var6 = this.basePos[1];
        int var7 = this.basePos[1] + this.height;
        int var8 = this.basePos[2];
        int[] var3 = new int[]{var5, var6, var8};
        int[] var4 = new int[]{var5, var7, var8};
        double var9 = (400.0F / this.trunkSize);

        for (int var11 = -this.trunkSize; var11 <= this.trunkSize; ++var11) {
            var3[0] = var5 + var11;
            var4[0] = var5 + var11;

            for (int var12 = -this.trunkSize; var12 <= this.trunkSize; ++var12) {
                if ((var12 * var12 + var11 * var11) * 4 < this.trunkSize * this.trunkSize * 5) {
                    var3[2] = var8 + var12;
                    var4[2] = var8 + var12;
                    if (this.slopeTrunk) {
                        var4[1] = var6 + sinc2(var9 * var11, var9 * var12, this.height);
                    }

                    int var13 = this.checkBlockLine(var3, var4);
                    if (var13 != -1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Shadow
    private static final int sinc2(double var0, double var2, int var4) {
        double var9 = Math.sqrt((var9 = var0 / Math.PI) * var9 + (var9 = var2 / Math.PI) * var9) * Math.PI / 180.0;
        return var9 == 0.0 ? var4 : (int)Math.round(var4 * ((Math.sin(var9) / var9 + Math.sin(var9 * 2.0943951023931953) / (var9 * 2.0943951023931953)) / 2.0));
    }
    @Shadow
    private float layerSize(int var1) {
        if (var1 < this.leafBases) {
            return -1.618F;
        } else {
            float var2 = this.heightLimit * 0.5F;
            float var3 = this.heightLimit * 0.5F - var1;
            float var4;
            if (var3 == 0.0F) {
                var4 = var2;
            } else {
                if (Math.abs(var3) >= var2) {
                    return 0.0F;
                }

                var4 = (float)Math.sqrt((var2 * var2 - var3 * var3));
            }

            var4 *= 0.5F;
            return var4;
        }
    }
    @Shadow
    private void setup() {
        this.leafBases = MathHelper.ceiling_float_int(this.heightLimit * this.heightAttenuation);
        this.density = Math.max(1, (int)(1.382 + Math.pow((this.branchDensity * this.heightLimit) / 13.0, 2.0)));
        this.chunkMap = new TLongObjectHashMap<>((int)(this.scaleWidth * this.heightLimit));
    }
    @Shadow
    private void generateLeaves() {
        int[][] var1 = this.leafNodes;
        int var2 = 0;

        for(int var3 = this.leafNodesLength; var2 < var3; ++var2) {
            int[] var4 = var1[var2];
            int var5 = var4[0];
            int var6 = var4[1];
            int var7 = var4[2];
            int var8 = 0;

            for(int var9 = var8 + this.leafDistanceLimit; var8 < var9; ++var8) {
                int var10 = var8 != 0 & var8 != this.leafDistanceLimit - 1 ? 3 : 2;
                this.genLeafLayer(var5, var6++, var7, var10);
            }
        }
    }
    // todo : fix cascading worldgens from here
    /*
    java.lang.Exception: Cascading world generation
    at net.minecraft.world.chunk.Chunk.logCascadingWorldGeneration(Chunk.java:4652) ~[apx.class:?]
    at net.minecraft.world.chunk.Chunk.handler$zfd000$archaicfix$savePopulatingChunk(Chunk.java:4658) ~[apx.class:?]
    at net.minecraft.world.chunk.Chunk.func_76624_a(Chunk.java) ~[apx.class:?]
    at net.minecraft.world.gen.ChunkProviderServer.originalLoadChunk(ChunkProviderServer.java:190) ~[ms.class:?]
    at net.minecraft.world.gen.ChunkProviderServer.loadChunk(ChunkProviderServer.java:131) ~[ms.class:?]
    at net.minecraft.world.gen.ChunkProviderServer.func_73158_c(ChunkProviderServer.java:101) ~[ms.class:?]
    at net.minecraft.world.gen.ChunkProviderServer.func_73154_d(ChunkProviderServer.java:199) ~[ms.class:?]
    at net.minecraft.world.World.func_72964_e(World.java:419) ~[ahb.class:?]
    at net.minecraft.world.World.func_147439_a(World.java:51213) ~[ahb.class:?]
    at powercrystals.minefactoryreloaded.world.WorldGenMassiveTree.genLeafLayer(WorldGenMassiveTree.java:182) ~[WorldGenMassiveTree.class:?]
    at powercrystals.minefactoryreloaded.world.WorldGenMassiveTree.generateLeaves(WorldGenMassiveTree.java:214) ~[WorldGenMassiveTree.class:?]
    at powercrystals.minefactoryreloaded.world.WorldGenMassiveTree.func_76484_a(WorldGenMassiveTree.java:813) ~[WorldGenMassiveTree.class:?]
    at powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen.optimizationsAndTweaks$generateMegaRubberTree(MineFactoryReloadedWorldGen.java:562) ~[MineFactoryReloadedWorldGen .class:?]
    at powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen.generateFeature(MineFactoryReloadedWorldGen.java:612) ~[MineFactoryReloadedWorldGen.class:?]
    at cofh.core.world.WorldHandler.generateWorld(WorldHandler.java:270) ~[WorldHandler.class:?]
    at cofh.core.world.WorldHandler.generate(WorldHandler.java:233) ~[WorldHandler.class:?]
    at cpw.mods.fml.common.registry.GameRegistry.generateWorld(GameRegistry.java:112) ~[GameRegistry .class:?]
    at net.minecraft.world.gen.ChunkProviderServer.func_73153_a(ChunkProviderServer.java:280) ~[ms.class:?]
    at net.minecraft.world.chunk.Chunk.func_76624_a(Chunk.java:1055) ~[apx.class:?]
    at net.minecraft.world.gen.ChunkProviderServer.originalLoadChunk(ChunkProviderServer.java:190) ~[ms.class:?]
    at net.minecraft.world.gen.ChunkProviderServer.loadChunk(ChunkProviderServer.java:131) ~[ms.class:?]
    at net.minecraft.world.gen.ChunkProviderServer.func_73158_c(ChunkProviderServer.java:101) ~[ms.class:?]
    at net.minecraft.server.MinecraftServer.func_71222_d(MinecraftServer.java:265) ~[MinecraftServer .class:?]
    at net.minecraft.server.integrated.IntegratedServer.func_71247_a(IntegratedServer.java:78) ~[bsx.class:?]
    at net.minecraft.server.integrated.IntegratedServer.func_71197_b(IntegratedServer.java:92) ~[bsx.class:?]
    at net.minecraft.server.MinecraftServer.run(MinecraftServer.java:1786) ~[MinecraftServer.class:?]
    at java.lang.Thread.run(Thread.java:750) [?:1.8.0_392]
   
     */
    @Shadow
    private void genLeafLayer(int var1, int var2, int var3, int var4) {
        int var6 = var1;
        int var7 = var3;
        float var8 = (var4 * var4);

        for(int var9 = -var4; var9 <= var4; ++var9) {
            var1 = var6 + var9;
            int var5;
            int var10 = var9 * var9 + (((var5 = var9 >> 31) ^ var9) - var5);

            for(int var11 = 0; var11 <= var4; ++var11) {
                float var12 = (var10 + var11 * var11 + var11) + 0.5F;
                if (var12 > var8) {
                    break;
                }

                byte var14 = -1;

                while(true) {
                    label40: {
                        var3 = var7 + var11 * var14;
                        Block var13 = this.worldObj.getBlock(var1, var2, var3);
                        if (this.safeGrowth) {
                            if (!var13.isAir(this.worldObj, var1, var2, var3) && !var13.isLeaves(this.worldObj, var1, var2, var3) && !var13.canBeReplacedByLeaves(this.worldObj, var1, var2, var3)) {
                                break label40;
                            }
                        } else if (var13 == Blocks.bedrock) {
                            break label40;
                        }

                        this.setBlockAndNotifyAdequately(this.worldObj, var1, var2, var3, this.leaves, 0);
                    }

                    if (var14 == 1) {
                        break;
                    }

                    var14 = 1;
                }
            }
        }
    }
    @Shadow
    private void generateTrunk() {
        int var1 = this.basePos[0];
        int var2 = this.basePos[1];
        int var3 = this.basePos[1] + this.height;
        int var4 = this.basePos[2];
        int[] var5 = new int[]{var1, var2, var4};
        int[] var6 = new int[]{var1, var3, var4};
        double var7 = (400.0F / this.trunkSize);

        for(int var9 = -this.trunkSize; var9 <= this.trunkSize; ++var9) {
            var5[0] = var1 + var9;
            var6[0] = var1 + var9;

            for(int var10 = -this.trunkSize; var10 <= this.trunkSize; ++var10) {
                if ((var10 * var10 + var9 * var9) * 4 < this.trunkSize * this.trunkSize * 5) {
                    var5[2] = var4 + var10;
                    var6[2] = var4 + var10;
                    if (this.slopeTrunk) {
                        var6[1] = var2 + sinc2(var7 * var9, var7 * var10, this.height) - (this.rand.nextInt(3) - 1);
                    }

                    this.placeBlockLine(var5, var6, this.log, 1);
                    this.setBlockAndNotifyAdequately(this.worldObj, var6[0], var6[1], var6[2], this.log, 13);
                    this.worldObj.getBlock(var5[0], var5[1] - 1, var5[2]).onPlantGrow(this.worldObj, var5[0], var5[1] - 1, var5[2], var1, var2, var4);
                }
            }
        }
    }
    @Shadow
    private void placeBlockLine(int[] var1, int[] var2, Block var3, int var4) {
        int[] var6 = this.placeScratch;
        byte var7 = 0;

        int var5;
        byte var8;
        for(var8 = 0; var8 < 3; ++var8) {
            int var9 = var2[var8] - var1[var8];
            int var10 = ((var5 = var9 >> 31) ^ var9) - var5;
            var6[var8] = var9;
            if (var10 > ((var9 = var6[var7]) ^ (var5 = var9 >> 31)) - var5) {
                var7 = var8;
            }
        }

        if (var6[var7] != 0) {
            var8 = otherCoordPairs[var7];
            byte var20 = otherCoordPairs[var7 + 3];
            byte var21;
            if (var6[var7] > 0) {
                var21 = 1;
            } else {
                var21 = -1;
            }

            float var11 = (float)var6[var8] / (float)var6[var7];
            float var12 = (float)var6[var20] / (float)var6[var7];
            int var13 = var6[var7] + var21;

            for(int var15 = 0; var15 != var13; var15 += var21) {
                var6[var7] = MathHelper.floor_float((var1[var7] + var15) + 0.5F);
                var6[var8] = MathHelper.floor_float(var1[var8] + var15 * var11 + 0.5F);
                var6[var20] = MathHelper.floor_float(var1[var20] + var15 * var12 + 0.5F);
                byte var16 = 0;
                int var17 = var6[0] - var1[0];
                var17 = ((var5 = var17 >> 31) ^ var17) - var5;
                int var18 = var6[2] - var1[2];
                var18 = ((var5 = var18 >> 31) ^ var18) - var5;
                int var19 = Math.max(var17, var18);
                if (var19 > 0) {
                    if (var17 == var19) {
                        var16 = 4;
                    } else {
                        var16 = 8;
                    }
                }

                this.setBlockAndNotifyAdequately(this.worldObj, var6[0], var6[1], var6[2], var3, var4 | var16);
            }
        }
    }
    @Shadow
    void generateLeafNodeBases() {
        int[] var1 = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};
        int[][] var2 = this.leafNodes;
        int var3 = (int)(this.heightLimit * 0.2F);
        int var4 = 0;

        for(int var5 = this.leafNodesLength; var4 < var5; ++var4) {
            int[] var6 = var2[var4];
            var1[1] = var6[3];
            int var7 = var1[1] - this.basePos[1];
            if (var7 >= var3) {
                this.placeBlockLine(var1, var6, this.log, 13);
            }
        }
    }
}
