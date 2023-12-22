package fr.iamacat.optimizationsandtweaks.mixins.common.minenautica;

import com.minenautica.Minenautica.Biomes.BiomeGenGrassyPlateaus;
import com.minenautica.Minenautica.Biomes.GenerateCoral;
import com.minenautica.Minenautica.CustomRegistry.BlocksAndItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(BiomeGenGrassyPlateaus.class)
public abstract class MixinBiomeGenGrassyPlateaus extends BiomeGenBase{
    public MixinBiomeGenGrassyPlateaus(int p_i1971_1_) {
        super(p_i1971_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void func_76728_a(World world, Random random, int chunkX, int chunkZ) {
        super.decorate(world, random, chunkX, chunkZ);

        if (world.getChunkProvider().chunkExists(chunkX, chunkZ)) {
            int x = chunkX + random.nextInt(16);
            int y = 50;
            int z = chunkZ + random.nextInt(16);

            (new GenerateCoral()).generateGrassyPlateausCoral(world, random, x, y, z);
        }
    }


    @Shadow
    public BiomeGenBase.TempCategory func_150561_m() {
        return BiomeGenBase.TempCategory.OCEAN;
    }
    @Shadow
    public void func_150573_a(World p_150573_1_, Random p_150573_2_, Block[] p_150573_3_, byte[] p_150573_4_, int p_150573_5_, int p_150573_6_, double p_150573_7_) {
        this.genBiomeModdedTerrain(p_150573_1_, p_150573_2_, p_150573_3_, p_150573_4_, p_150573_5_, p_150573_6_, p_150573_7_);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void genBiomeModdedTerrain(World world, Random random, Block[] replacableBlock, byte[] aByte, int x, int y, double z) {
        boolean flag = true;
        Block block = this.topBlock;
        byte b0 = (byte)(this.field_150604_aj & 255);
        Block block1 = this.fillerBlock;
        int k = -1;
        int l = (int)(z / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int i1 = x & 15;
        int j1 = y & 15;
        int k1 = replacableBlock.length / 256;
        boolean replace = true;
        int below = 7;

        for(int l1 = 255; l1 >= 0; --l1) {
            int i2 = (j1 * 16 + i1) * k1 + l1;
            if (l1 >= 80) {
                replacableBlock[i2] = Blocks.air;
            } else if (l1 >= 76 && l1 <= 79) {
                replacableBlock[i2] = BlocksAndItems.saltWater;
            } else {
                if (replacableBlock[i2] == Blocks.water) {
                    replacableBlock[i2] = BlocksAndItems.saltWater;
                }

                if ((replacableBlock[i2] == null || replacableBlock[i2] == Blocks.air) && l1 < 80) {
                    replacableBlock[i2] = BlocksAndItems.saltWater;
                }

                if (l1 <= 0 + random.nextInt(5)) {
                    replacableBlock[i2] = BlocksAndItems.lavaBedrock;
                } else {
                    Block block2 = replacableBlock[i2];
                    if (block2 != null && block2.getMaterial() != Material.air) {
                        if (block2 == Blocks.stone) {
                            if (k == -1) {
                                replacableBlock[i2] = Blocks.sand;
                                replace = true;
                                below = 0;
                                k = 0;
                            } else if (k > 0) {
                                replacableBlock[i2] = BlocksAndItems.grassyPlateausRock;
                            }

                            if (!replace) {
                                block = null;
                                block1 = Blocks.stone;
                                replacableBlock[i2] = BlocksAndItems.rock;
                            }

                            replace = false;
                        }

                        if (replacableBlock[i2] == Blocks.dirt) {
                            block = null;
                            block1 = Blocks.dirt;
                            replacableBlock[i2] = Blocks.sand;
                        }

                        if (replacableBlock[i2] == Blocks.gravel) {
                            block = null;
                            block1 = Blocks.gravel;
                            replacableBlock[i2] = Blocks.sand;
                        }

                        if (replacableBlock[i2] == Blocks.clay) {
                            block = null;
                            block1 = Blocks.clay;
                            replacableBlock[i2] = Blocks.sand;
                        }

                        if (below == 0) {
                            ++below;
                        } else if (replacableBlock[i2] == Blocks.sand) {
                            below = 1;
                        } else if (below >= 1 && below < 6) {
                            block = null;
                            block1 = BlocksAndItems.rock;
                            replacableBlock[i2] = BlocksAndItems.grassyPlateausRock;
                            ++below;
                        } else if (below == 6 && replacableBlock[i2] != BlocksAndItems.rock) {
                            block = null;
                            Block var10000 = replacableBlock[i2];
                            replacableBlock[i2] = BlocksAndItems.saltWater;
                        }
                    } else {
                        k = -1;
                    }
                }
            }
        }
    }
}
