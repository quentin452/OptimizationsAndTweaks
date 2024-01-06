package fr.iamacat.optimizationsandtweaks.mixins.common.fossilsandarcheologyrevivals;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import fossilsarcheology.Revival;
import fossilsarcheology.server.gen.WorldGenMiscStructures;
import fossilsarcheology.server.gen.feature.*;

@Mixin(WorldGenMiscStructures.class)
public class MixinWorldGenMiscStructures {

    @Unique
    int counter;
    @Unique
    int Zcoord1;
    @Unique
    int base;
    @Unique
    BiomeGenBase biome;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        chunkX = chunkX >> 4;
        chunkZ = chunkZ >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return;
        }

        random.setSeed(world.getSeed() * (long) chunkX * (long) chunkZ);

        if (Revival.CONFIG.generateHellShips && random.nextInt(100) == 0) {
            generateHellShips(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }

        if (Revival.CONFIG.generateMoai) {
            generateMoaiStructures(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }

    private void generateMoaiStructures(Random random, int chunkX, int chunkZ, World world,
        IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (random.nextInt(100) == 0) {
            generate1(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }

        if (Revival.CONFIG.generateAztecWeaponShops && random.nextInt(65) == 0) {
            generateAztecWeaponShops(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }

        if (Revival.CONFIG.generateTarSites && random.nextInt(3200) == 0) {
            generateTarSites(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }

        if (Revival.CONFIG.generateFossilSites && random.nextInt(3200) == 0) {
            generateFossilSites(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }

        if (world.getChunkFromChunkCoords(chunkX, chunkZ) == world.getChunkFromBlockCoords(-70, -70)
            && world.provider.dimensionId == Revival.CONFIG.dimensionIDDarknessLair) {
            generate2(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }

        if (world.getChunkFromChunkCoords(chunkX, chunkZ) == world.getChunkFromBlockCoords(-80, -120)
            && world.provider.dimensionId == Revival.CONFIG.dimensionIDTreasure) {
            generate3(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }

    public void generateHellShips(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = 32;
        if (world.provider.isHellWorld && world.getBlock(counter, 31, Zcoord1) == Blocks.lava) {
            (new HellBoatWorldGen()).func_76484_a(world, random, counter, base, Zcoord1);
        }
    }

    public void generate1(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = world.getHeightValue(counter, Zcoord1);
        biome = world.getWorldChunkManager()
            .getBiomeGenAt(counter, Zcoord1);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.BEACH) && !world.provider.hasNoSky
            && !world.provider.isHellWorld
            && world.getBlock(counter, base - 1, Zcoord1) == Blocks.sand) {
            (new MoaiWorldGen()).func_76484_a(world, random, counter, base - random.nextInt(4), Zcoord1);
        }
    }

    public void generateAztecWeaponShops(Random random, int chunkX, int chunkZ, World world,
        IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = world.getHeightValue(counter, Zcoord1);
        biome = world.getWorldChunkManager()
            .getBiomeGenAt(counter, Zcoord1);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.JUNGLE) && !world.provider.hasNoSky
            && !world.provider.isHellWorld
            && biome.topBlock == Blocks.grass
            && world.getBlock(counter, base - 1, Zcoord1) == biome.topBlock) {
            (new AztecWeaponsShopWorldGen()).func_76484_a(world, random, counter, base - 4, Zcoord1);
        }
    }

    public void generateTarSites(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = world.getHeightValue(counter, Zcoord1);
        biome = world.getWorldChunkManager()
            .getBiomeGenAt(counter, Zcoord1);
        if (!world.provider.hasNoSky && !world.provider.isHellWorld
            && biome.topBlock == Blocks.grass
            && world.getBlock(counter, base - 1, Zcoord1) == biome.topBlock) {
            (new TarSiteWorldGen()).func_76484_a(world, random, counter, base - 3, Zcoord1);
        }
    }

    public void generateFossilSites(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        counter = chunkX * 16 + random.nextInt(16);
        Zcoord1 = chunkZ * 16 + random.nextInt(16);
        base = world.getHeightValue(counter, Zcoord1);
        biome = world.getWorldChunkManager()
            .getBiomeGenAt(counter, Zcoord1);
        if (!world.provider.hasNoSky && !world.provider.isHellWorld
            && biome.topBlock == Blocks.grass
            && world.getBlock(counter, base - 1, Zcoord1) == biome.topBlock) {
            (new FossilSiteWorldGen()).func_76484_a(world, random, counter, base - 3, Zcoord1);
        }
    }

    public void generate2(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        counter = 0;
        counter = counter + 1;
        if (counter == 1) {
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
    }

    public void generate3(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        counter = 0;
        counter = (byte) (counter + 1);
        if (counter == 1) {
            (new AncientChestWorldGen()).func_76484_a(world, random, -80, 63, -120);
        }
    }
}
