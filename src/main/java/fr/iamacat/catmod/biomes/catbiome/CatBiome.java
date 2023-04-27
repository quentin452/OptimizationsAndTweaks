package fr.iamacat.catmod.biomes.catbiome;

import fr.iamacat.catmod.init.RegisterBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

public class CatBiome extends BiomeGenBase {
    public static final CatBiome INSTANCE = new CatBiome(100); // declare the biome instance as a static variable
    public CatBiome(int p_i1971_1_) {
        super(p_i1971_1_);
        this.topBlock = RegisterBlocks.catBlock;
        this.fillerBlock = Blocks.stone;
        this.setBiomeName("Cat Biome"); // set the name of the biome
        this.enableRain = true;
        this.enableSnow = false;
        this.waterColorMultiplier = 8336128;//color of water in hexadecimal to decimal : https://fr.calcuworld.com/calculs-mathematiques/calculatrice-hexadecimal/ fact : the watercolormultiplier is only work if the enablerain = false
        //this.addDefaultFlowers();

    }
    public void decorate(World p_76728_1_, Random p_76728_2_, int p_76728_3_, int p_76728_4_)
    {

    }
}
