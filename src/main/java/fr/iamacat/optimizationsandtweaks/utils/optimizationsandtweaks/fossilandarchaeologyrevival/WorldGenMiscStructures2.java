package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.fossilandarchaeologyrevival;

import fossilsarcheology.Revival;
import fossilsarcheology.server.gen.feature.*;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class WorldGenMiscStructures2 {
    int counter;
    int Zcoord1;
    int base;
    BiomeGenBase biome;

    public void generate(Random random, int chunkX, int chunkZ, World world) {
        random.setSeed(world.getSeed() * (long) chunkX * (long) chunkZ);
        if (Revival.CONFIG.generateHellShips && random.nextInt(100) == 0) {
            optimizationsAndTweaks$generateHellShips(random, chunkX, chunkZ, world);
        }
        if (Revival.CONFIG.generateMoai) {
            optimizationsAndTweaks$generateMoaiStructures(random, chunkX, chunkZ, world);
        }
    }

    public void optimizationsAndTweaks$generateHellShips(Random random, int chunkX, int chunkZ, World world) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = 32;
        if (world.provider.isHellWorld && world.getBlock(counter, 31, Zcoord1) == Blocks.lava) {
            (new HellBoatWorldGen()).func_76484_a(world, random, counter, base, Zcoord1);
        }
    }

    public void optimizationsAndTweaks$generateMoaiStructures(Random random, int chunkX, int chunkZ, World world) {
        if (random.nextInt(100) == 0) {
            optimizationsAndTweaks$generate1(random, chunkX, chunkZ, world);
        }

        if (Revival.CONFIG.generateAztecWeaponShops && random.nextInt(65) == 0) {
            optimizationsAndTweaks$generateAztecWeaponShops(random, chunkX, chunkZ, world);
        }

        if (Revival.CONFIG.generateTarSites && random.nextInt(3200) == 0) {
            optimizationsAndTweaks$generateTarSites(random, chunkX, chunkZ, world);
        }

        if (Revival.CONFIG.generateFossilSites && random.nextInt(3200) == 0) {
            optimizationsAndTweaks$generateFossilSites(random, chunkX, chunkZ, world);
        }
        int chunkAtMinus70X = -70 >> 4;
        int chunkAtMinus70Z = -70 >> 4;
        int chunkAtMinus80X = -80 >> 4;
        int chunkAtMinus80Z = -120 >> 4;

        int currentDimensionId = world.provider.dimensionId;

        if (chunkX == chunkAtMinus70X && chunkZ == chunkAtMinus70Z && currentDimensionId == Revival.CONFIG.dimensionIDDarknessLair) {
            optimizationsAndTweaks$generate2(random, world);
        }

        if (chunkX == chunkAtMinus80X && chunkZ == chunkAtMinus80Z && currentDimensionId == Revival.CONFIG.dimensionIDTreasure) {
            optimizationsAndTweaks$generate3(random, world);
        }
    }

    private void optimizationsAndTweaks$generate1(Random random, int chunkX, int chunkZ, World world) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = world.getHeightValue(counter, Zcoord1);
        biome = world.getWorldChunkManager().getBiomeGenAt(counter, Zcoord1);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.BEACH) && !world.provider.hasNoSky
            && !world.provider.isHellWorld && world.getBlock(counter, base - 1, Zcoord1) == Blocks.sand) {
            (new MoaiWorldGen()).func_76484_a(world, random, counter, base - random.nextInt(4), Zcoord1);
        }
    }


    private void optimizationsAndTweaks$generateAztecWeaponShops(Random random, int chunkX, int chunkZ, World world) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = world.getHeightValue(counter, Zcoord1);
        biome = world.getWorldChunkManager().getBiomeGenAt(counter, Zcoord1);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.JUNGLE) && !world.provider.hasNoSky
            && !world.provider.isHellWorld && biome.topBlock == Blocks.grass
            && world.getBlock(counter, base - 1, Zcoord1) == biome.topBlock) {
            (new AztecWeaponsShopWorldGen()).func_76484_a(world, random, counter, base - 4, Zcoord1);
        }
    }

    private void optimizationsAndTweaks$generateTarSites(Random random, int chunkX, int chunkZ, World world) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = world.getHeightValue(counter, Zcoord1);
        biome = world.getWorldChunkManager().getBiomeGenAt(counter, Zcoord1);
        if (!world.provider.hasNoSky && !world.provider.isHellWorld && biome.topBlock == Blocks.grass
            && world.getBlock(counter, base - 1, Zcoord1) == biome.topBlock) {
            (new TarSiteWorldGen()).func_76484_a(world, random, counter, base - 3, Zcoord1);
        }
    }

    private void optimizationsAndTweaks$generateFossilSites(Random random, int chunkX, int chunkZ, World world) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = world.getHeightValue(counter, Zcoord1);
        biome = world.getWorldChunkManager().getBiomeGenAt(counter, Zcoord1);
        if (!world.provider.hasNoSky && !world.provider.isHellWorld && biome.topBlock == Blocks.grass
            && world.getBlock(counter, base - 1, Zcoord1) == biome.topBlock) {
            (new FossilSiteWorldGen()).func_76484_a(world, random, counter, base - 3, Zcoord1);
        }
    }

    private void optimizationsAndTweaks$generate2(Random random, World world) {
        counter = 0;
        counter = counter + 1;
        (new AnuCastleWorldGen()).func_76484_a(world, random, -70, 61, -70);
        int structurebase = 140;
        base = 14;

        for (int y = 50; y < 63; ++y) {
            --base;

            for (int x = -70 - base; x < structurebase - 70 + base; ++x) {
                for (int z = -70 - base; z < structurebase - 70 + base; ++z) {
                    world.setBlock(x, y, z, Blocks.netherrack);
                }
            }
        }
    }

    private void optimizationsAndTweaks$generate3(Random random, World world) {
        counter = 0;
        counter = (byte) (counter + 1);
        (new AncientChestWorldGen()).func_76484_a(world, random, -80, 63, -120);
    }
}
