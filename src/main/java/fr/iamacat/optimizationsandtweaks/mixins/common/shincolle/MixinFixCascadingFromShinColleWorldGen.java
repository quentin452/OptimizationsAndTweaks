package fr.iamacat.optimizationsandtweaks.mixins.common.shincolle;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.lulan.shincolle.handler.ConfigHandler;
import com.lulan.shincolle.init.ModBlocks;
import com.lulan.shincolle.worldgen.ShinColleWorldGen;
import com.lulan.shincolle.worldgen.WorldGenPolyGravel;

import cpw.mods.fml.common.IWorldGenerator;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(ShinColleWorldGen.class)
public class MixinFixCascadingFromShinColleWorldGen implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        int dimension = world.provider.dimensionId;
        if (OptimizationsandTweaksConfig.enableMixinFixCascadingFromShinColleWorldGen) {

            switch (dimension) {
                case -1:
                case 1:
                    break;

                default:
                    generateSurface(world, random, chunkX * 16, chunkZ * 16);
                    generateSea(world, random, chunkX * 16, chunkZ * 16);
            }
        }
    }

    @Unique
    private void oreGenerator(WorldGenerator genOres, World world, Random rand, int blockX, int blockZ, int spawnNum,
        int minY, int maxY) {
        int spawnN = spawnNum;
        if (BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(blockX, blockZ), BiomeDictionary.Type.OCEAN)) {
            spawnN = spawnNum * 3;
        }

        for (int i = 0; i < spawnN; ++i) {
            int x = blockX + rand.nextInt(16);
            int z = blockZ + rand.nextInt(16);
            int y = minY + rand.nextInt(maxY - minY);

            // Generate the block directly without threading
            genOres.generate(world, rand, x, y, z);
        }
    }

    @Unique
    private void generateSurface(World world, Random rand, int x, int z) {
        WorldGenerator genPolymetal = new WorldGenMinable(ModBlocks.BlockPolymetalOre, 4 + rand.nextInt(4));
        oreGenerator(genPolymetal, world, rand, x, z, ConfigHandler.polyOreBaseRate, 3, 50);
    }

    @Unique
    private void generateSea(World world, Random rand, int x, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.OCEAN)) {
            WorldGenerator genPolyGravel = new WorldGenPolyGravel(2 + rand.nextInt(2));
            for (int i = 0; i < ConfigHandler.polyGravelBaseRate; ++i) {
                int posX = x + rand.nextInt(16);
                int posZ = z + rand.nextInt(16);
                int posY = world.getTopSolidOrLiquidBlock(posX, posZ);

                // Generate the block directly without threading
                genPolyGravel.generate(world, rand, posX, posY, posZ);
            }
        }
    }
}
