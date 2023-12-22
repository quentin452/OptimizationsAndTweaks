package fr.iamacat.optimizationsandtweaks.mixins.common.minenautica;

import com.minenautica.Minenautica.Biomes.BiomeGenGrassyPlateaus;
import com.minenautica.Minenautica.Biomes.BiomeGenKelpForest;
import com.minenautica.Minenautica.Biomes.BiomeGenSafeShallows;
import com.minenautica.Minenautica.Dimension.ChunkProviderMinenautica;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Random;

@Mixin(ChunkProviderMinenautica.class)
public abstract class MixinChunkProviderMinenautica implements IChunkProvider {
    @Shadow
    private Random rand;
    @Shadow
    private NoiseGeneratorOctaves noiseGen1;
    @Shadow
    private NoiseGeneratorOctaves noiseGen2;
    @Shadow
    private NoiseGeneratorOctaves noiseGen3;
    @Shadow
    private NoiseGeneratorPerlin noisePerl;
    @Shadow
    public NoiseGeneratorOctaves noiseGen5;
    @Shadow
    public NoiseGeneratorOctaves noiseGen6;
    @Shadow
    public NoiseGeneratorOctaves mobSpawnerNoise;
    @Shadow
    private World worldObj;
    @Shadow
    private final boolean mapFeaturesEnabled;
    @Shadow
    private WorldType worldType;
    @Shadow
    private final double[] noiseArray;
    @Shadow
    private final float[] parabolicField;
    @Shadow
    private double[] stoneNoise = new double[256];
    @Shadow
    private BiomeGenBase[] biomesForGeneration;
    @Shadow
    double[] noise3;
    @Shadow
    double[] noise1;
    @Shadow
    double[] noise2;
    @Shadow
    double[] noise5;
    @Shadow
    int[][] field_73219_j = new int[32][32];

    public MixinChunkProviderMinenautica(World world, long seed, boolean mapFeaturesEnabled) {
        this.worldObj = world;
        this.mapFeaturesEnabled = mapFeaturesEnabled;
        this.worldType = world.getWorldInfo().getTerrainType();
        this.rand = new Random(seed);
        this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noisePerl = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseArray = new double[825];
        this.parabolicField = new float[25];

        for(int j = -2; j <= 2; ++j) {
            for(int k = -2; k <= 2; ++k) {
                float f = 10.0F / MathHelper.sqrt_float((float)(j * j + k * k) + 0.2F);
                this.parabolicField[j + 2 + (k + 2) * 5] = f;
            }
        }

        NoiseGenerator[] noiseGens = new NoiseGenerator[]{this.noiseGen1, this.noiseGen2, this.noiseGen3, this.noisePerl, this.noiseGen5, this.noiseGen6, this.mobSpawnerNoise};
        noiseGens = TerrainGen.getModdedNoiseGenerators(world, this.rand, noiseGens);
        this.noiseGen1 = (NoiseGeneratorOctaves)noiseGens[0];
        this.noiseGen2 = (NoiseGeneratorOctaves)noiseGens[1];
        this.noiseGen3 = (NoiseGeneratorOctaves)noiseGens[2];
        this.noisePerl = (NoiseGeneratorPerlin)noiseGens[3];
        this.noiseGen5 = (NoiseGeneratorOctaves)noiseGens[4];
        this.noiseGen6 = (NoiseGeneratorOctaves)noiseGens[5];
        this.mobSpawnerNoise = (NoiseGeneratorOctaves)noiseGens[6];
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void func_147424_a(int par1, int par2, Block[] blocks) {
        byte b0 = 63;
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, par1 * 4 - 2, par2 * 4 - 2, 10, 10);
        this.func_147423_a(par1 * 4, 0, par2 * 4);

        for(int k = 0; k < 4; ++k) {
            int l = k * 5;
            int i1 = (k + 1) * 5;

            for(int j1 = 0; j1 < 4; ++j1) {
                int k1 = (l + j1) * 33;
                int l1 = (l + j1 + 1) * 33;
                int i2 = (i1 + j1) * 33;
                int j2 = (i1 + j1 + 1) * 33;

                for(int k2 = 0; k2 < 32; ++k2) {
                    double d0 = 0.125;
                    double d1 = this.noiseArray[k1 + k2];
                    double d2 = this.noiseArray[l1 + k2];
                    double d3 = this.noiseArray[i2 + k2];
                    double d4 = this.noiseArray[j2 + k2];
                    double d5 = (this.noiseArray[k1 + k2 + 1] - d1) * d0;
                    double d6 = (this.noiseArray[l1 + k2 + 1] - d2) * d0;
                    double d7 = (this.noiseArray[i2 + k2 + 1] - d3) * d0;
                    double d8 = (this.noiseArray[j2 + k2 + 1] - d4) * d0;

                    for(int l2 = 0; l2 < 8; ++l2) {
                        double d9 = 0.25;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for(int i3 = 0; i3 < 4; ++i3) {
                            int j3 = i3 + k * 4 << 12 | 0 + j1 * 4 << 8 | k2 * 8 + l2;
                            short short1 = 256;
                            j3 -= short1;
                            double d14 = 0.25;
                            double d16 = (d11 - d10) * d14;
                            double d15 = d10 - d16;

                            for(int k3 = 0; k3 < 4; ++k3) {
                                if ((d15 += d16) > 0.0) {
                                    blocks[j3 += short1] = Blocks.stone;
                                } else if (k2 * 8 + l2 < b0) {
                                    blocks[j3 += short1] = Blocks.water;
                                } else {
                                    blocks[j3 += short1] = null;
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }

    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void replaceBlocksForBiome(int par1, int par2, Block[] blocks, byte[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase) {
        ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, par1, par2, blocks, par3ArrayOfByte, par4ArrayOfBiomeGenBase);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() != Event.Result.DENY) {
            double d0 = 0.03125;
            this.stoneNoise = this.noisePerl.func_151599_a(this.stoneNoise, (double)(par1 * 16), (double)(par2 * 16), 16, 16, d0 * 2.0, d0 * 2.0, 1.0);

            int chunkXStart = par1 * 16;
            int chunkZStart = par2 * 16;
            int chunkXEnd = chunkXStart + 16;
            int chunkZEnd = chunkZStart + 16;

            for (int k = chunkXStart; k < chunkXEnd; ++k) {
                for (int l = chunkZStart; l < chunkZEnd; ++l) {
                    BiomeGenBase biome = par4ArrayOfBiomeGenBase[l - chunkZStart + (k - chunkXStart) * 16];

                    if (biome instanceof BiomeGenSafeShallows) {
                        BiomeGenSafeShallows biomegenbase = (BiomeGenSafeShallows) biome;
                        biomegenbase.func_150573_a(this.worldObj, this.rand, blocks, par3ArrayOfByte, k, l, this.stoneNoise[l - chunkZStart + (k - chunkXStart) * 16]);
                    }

                    if (biome instanceof BiomeGenGrassyPlateaus) {
                        BiomeGenGrassyPlateaus biomegenbase = (BiomeGenGrassyPlateaus)par4ArrayOfBiomeGenBase[l + k * 16];
                        biomegenbase.func_150573_a(this.worldObj, this.rand, blocks, par3ArrayOfByte, k, l, this.stoneNoise[l - chunkZStart + (k - chunkXStart) * 16]);
                    }

                    if (biome instanceof BiomeGenKelpForest) {
                        BiomeGenKelpForest biomegenbase = (BiomeGenKelpForest)par4ArrayOfBiomeGenBase[l + k * 16];
                        biomegenbase.func_150573_a(this.worldObj, this.rand, blocks, par3ArrayOfByte, k, l, this.stoneNoise[l - chunkZStart + (k - chunkXStart) * 16]);
                    }
                }
            }
        }
    }
    @Shadow
    public Chunk func_73158_c(int par1, int par2) {
        return this.func_73154_d(par1, par2);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public Chunk func_73154_d(int par1, int par2) {
        this.rand.setSeed((long)par1 * 341873128712L + (long)par2 * 132897987541L);
        Block[] ablock = new Block[65536];
        byte[] abyte = new byte[65536];
        this.func_147424_a(par1, par2, ablock);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
        this.replaceBlocksForBiome(par1, par2, ablock, abyte, this.biomesForGeneration);
        Chunk chunk = new Chunk(this.worldObj, ablock, abyte, par1, par2);
        byte[] abyte1 = chunk.getBiomeArray();

        for(int k = 0; k < abyte1.length; ++k) {
            abyte1[k] = (byte)this.biomesForGeneration[k].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void func_147423_a(int p_147423_1_, int p_147423_2_, int p_147423_3_) {
        this.noise5 = this.noiseGen6.generateNoiseOctaves(this.noise5, p_147423_1_, p_147423_3_, 5, 5, 200.0, 200.0, 0.5);
        this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, p_147423_1_, p_147423_2_, p_147423_3_, 5, 33, 5, 8.555150000000001, 4.277575000000001, 8.555150000000001);
        this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, p_147423_1_, p_147423_2_, p_147423_3_, 5, 33, 5, 684.412, 684.412, 684.412);
        this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, p_147423_1_, p_147423_2_, p_147423_3_, 5, 33, 5, 684.412, 684.412, 684.412);
        int l = 0;
        int i1 = 0;

        for(int j1 = 0; j1 < 5; ++j1) {
            for(int k1 = 0; k1 < 5; ++k1) {
                float f = 0.0F;
                float f1 = 0.0F;
                float f2 = 0.0F;
                byte b0 = 2;
                BiomeGenBase biomegenbase = this.biomesForGeneration[j1 + 2 + (k1 + 2) * 10];

                for(int l1 = -b0; l1 <= b0; ++l1) {
                    for(int i2 = -b0; i2 <= b0; ++i2) {
                        BiomeGenBase biomegenbase1 = this.biomesForGeneration[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
                        float f3 = biomegenbase1.rootHeight;
                        float f4 = biomegenbase1.heightVariation;
                        if (this.worldType == WorldType.AMPLIFIED && f3 > 0.0F) {
                            f3 = 1.0F + f3 * 2.0F;
                            f4 = 1.0F + f4 * 4.0F;
                        }

                        float f5 = this.parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F);
                        if (biomegenbase1.rootHeight > biomegenbase.rootHeight) {
                            f5 /= 2.0F;
                        }

                        f += f4 * f5;
                        f1 += f3 * f5;
                        f2 += f5;
                    }
                }

                f /= f2;
                f1 /= f2;
                f = f * 0.9F + 0.1F;
                f1 = (f1 * 4.0F - 1.0F) / 8.0F;
                double d12 = optimizationsAndTweaks$getD12(i1, this.noise5);

                ++i1;
                double d13 = (double)f1;
                double d14 = (double)f;
                d13 += d12 * 0.2;
                d13 = d13 * 8.5 / 8.0;
                double d5 = 8.5 + d13 * 4.0;

                for(int j2 = 0; j2 < 33; ++j2) {
                    double d6 = ((double)j2 - d5) * 12.0 * 128.0 / 256.0 / d14;
                    if (d6 < 0.0) {
                        d6 *= 4.0;
                    }

                    double d7 = this.noise1[l] / 512.0;
                    double d8 = this.noise2[l] / 512.0;
                    double d9 = (this.noise3[l] / 10.0 + 1.0) / 2.0;
                    double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;
                    if (j2 > 29) {
                        double d11 = (double)((float)(j2 - 29) / 3.0F);
                        d10 = d10 * (1.0 - d11) + -10.0 * d11;
                    }

                    this.noiseArray[l] = d10;
                    ++l;
                }
            }
        }

    }

    @Unique
    private static double optimizationsAndTweaks$getD12(int i1, double[] noise5) {
        double d12 = noise5[i1] / 8000.0;
        if (d12 < 0.0) {
            d12 = -d12 * 0.3;
        }

        d12 = d12 * 3.0 - 2.0;
        if (d12 < 0.0) {
            d12 /= 2.0;
            if (d12 < -1.0) {
                d12 = -1.0;
            }

            d12 /= 1.4;
            d12 /= 2.0;
        } else {
            if (d12 > 1.0) {
                d12 = 1.0;
            }

            d12 /= 8.0;
        }
        return d12;
    }

    @Shadow
    public boolean func_73149_a(int par1, int par2) {
        return true;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void func_73153_a(IChunkProvider par1IChunkProvider, int par2, int par3) {
        BlockFalling.fallInstantly = true;
        int k = par2 * 16;
        int l = par3 * 16;
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(k + 16, l + 16);
        this.rand.setSeed(this.worldObj.getSeed());
        long i1 = this.rand.nextLong() / 2L * 2L + 1L;
        long j1 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)par2 * i1 + (long)par3 * j1 ^ this.worldObj.getSeed());
        boolean flag = false;
        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(par1IChunkProvider, this.worldObj, this.rand, par2, par3, flag));
        biomegenbase.decorate(this.worldObj, this.rand, k, l);
        if (TerrainGen.populate(par1IChunkProvider, this.worldObj, this.rand, par2, par3, flag, PopulateChunkEvent.Populate.EventType.ANIMALS)) {
            SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomegenbase, k + 8, l + 8, 16, 16, this.rand);
        }

        k += 8;
        l += 8;
        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(par1IChunkProvider, this.worldObj, this.rand, par2, par3, flag));
        BlockFalling.fallInstantly = false;
    }
    @Shadow
    public boolean func_73151_a(boolean par1, IProgressUpdate par2IProgressUpdate) {
        return true;
    }
    @Shadow
    public void func_104112_b() {
    }
    @Shadow
    public boolean func_73156_b() {
        return false;
    }
    @Shadow
    public boolean func_73157_c() {
        return true;
    }
    @Shadow
    public String func_73148_d() {
        return "RandomLevelSource";
    }
    @Shadow
    public int func_73152_e() {
        return 0;
    }
    @Shadow
    public ChunkPosition func_147416_a(World world, String arg1, int arg2, int arg3, int arg4) {
        return null;
    }
    @Shadow
    public List func_73155_a(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_) {
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(p_73155_2_, p_73155_4_);
        return biomegenbase.getSpawnableList(p_73155_1_);
    }
    @Shadow
    public void func_82695_e(int p_82695_1_, int p_82695_2_) {
    }
}
