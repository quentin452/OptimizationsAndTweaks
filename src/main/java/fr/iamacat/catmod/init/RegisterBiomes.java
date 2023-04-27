package fr.iamacat.catmod.init;

import fr.iamacat.catmod.biomes.catbiome.CatBiome;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager;

public class RegisterBiomes {

    public static void init(){
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(CatBiome.INSTANCE, 10)); // use the INSTANCE static variable instead of specifying the biome ID
    }
}
