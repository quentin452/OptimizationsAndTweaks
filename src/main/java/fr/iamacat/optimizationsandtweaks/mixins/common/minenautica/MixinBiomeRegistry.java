package fr.iamacat.optimizationsandtweaks.mixins.common.minenautica;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.minenautica.Minenautica.Biomes.BiomeGenGrassyPlateaus;
import com.minenautica.Minenautica.Biomes.BiomeGenKelpForest;
import com.minenautica.Minenautica.Biomes.BiomeGenSafeShallows;
import com.minenautica.Minenautica.Biomes.BiomeRegistry;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minenautica.MinenauticaBiomeIDConfig;

@Mixin(BiomeRegistry.class)
public class MixinBiomeRegistry {

    @Shadow
    public static BiomeGenBase safeShallows;
    @Shadow
    public static final int safeShallowsID = MinenauticaBiomeIDConfig.safeShallows;
    @Shadow
    public static BiomeGenBase grassyPlateaus;
    @Shadow
    public static final int grassyPlateausID = MinenauticaBiomeIDConfig.grassyPlateaus;
    @Shadow
    public static BiomeGenBase kelpForest;
    @Shadow
    public static final int kelpForestID = MinenauticaBiomeIDConfig.kelpForest;

    @Shadow
    public static void mainRegistry() {
        registerBiome();
        initializeBiome();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void registerBiome() {
        safeShallows = (new BiomeGenSafeShallows(MinenauticaBiomeIDConfig.safeShallows)).setBiomeName("Safe Shallows")
            .setHeight(new BiomeGenBase.Height(-0.4F, 0.3F));
        grassyPlateaus = (new BiomeGenGrassyPlateaus(MinenauticaBiomeIDConfig.grassyPlateaus))
            .setBiomeName("Grassy Plateaus")
            .setHeight(new BiomeGenBase.Height(-1.4F, 0.2F));
        kelpForest = (new BiomeGenKelpForest(MinenauticaBiomeIDConfig.kelpForest)).setBiomeName("Kelp Forest")
            .setHeight(new BiomeGenBase.Height(-1.9F, 0.45F));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void initializeBiome() {
        BiomeDictionary.registerBiomeType(safeShallows, BiomeDictionary.Type.OCEAN);
        BiomeManager.addSpawnBiome(safeShallows);
        BiomeDictionary.registerBiomeType(grassyPlateaus, BiomeDictionary.Type.OCEAN);
        BiomeManager.addSpawnBiome(grassyPlateaus);
        BiomeDictionary.registerBiomeType(kelpForest, BiomeDictionary.Type.OCEAN);
        BiomeManager.addSpawnBiome(kelpForest);
    }
}
