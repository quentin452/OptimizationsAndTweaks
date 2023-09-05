package fr.iamacat.multithreading.mixins.common.slimecarnage;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraft.world.biome.BiomeGenPlains;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.lulan.shincolle.utility.LogHelper;

import cpw.mods.fml.common.IWorldGenerator;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import supremopete.SlimeCarnage.worldgen.*;
import supremopete.SlimeCarnage.worldgen.WorldGenSlimeCarnage;

@Mixin(WorldGenSlimeCarnage.class)
public class MixinFixCascadingFromWorldGenSlimeCarnage implements IWorldGenerator {

    @Unique
    public int oldCave;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFixCascadingFromWorldGenSlimeCarnage) {
            switch (world.provider.dimensionId) {
                case -1:
                    generateNether(world, random, chunkX, chunkZ);
                    break;

                case 0:
                    generateSurface(world, random, chunkX, chunkZ);
                    break;

                case 1:
                    generateEnd(world, random, chunkX, chunkZ);
                    break;

                default:
                    break;
            }
        }
    }

    @Unique
    private void generateSurface(World world, Random random, int chunkX, int chunkZ) {
        // Use the same Random instance for consistency
        Random rand = random;

        if (oldCave == 0 && generateCave(world, rand, chunkX, chunkZ)) {
            return;
        }

        int biomeCheck = getBiomeCheck(world, rand, chunkX, chunkZ);
        if (biomeCheck == 1) {
            generateDesertRuins(world, rand, chunkX, chunkZ);
        } else if (biomeCheck == 2) {
            generateStoneRuins(world, rand, chunkX, chunkZ);
        }

        if (generateMadLab(rand)) {
            generateMadLab(world, rand, chunkX, chunkZ);
        }

        if (biomeCheck == 1 && generateDesertTomb(rand)) {
            generateDesertTomb(world, rand, chunkX, chunkZ);
        }
    }

    @Unique
    private boolean generateCave(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord2 = chunkX + rand.nextInt(16);
        int Ycoord2 = 64 + rand.nextInt(6);
        int Zcoord2 = chunkZ + rand.nextInt(16);
        if ((new WorldGenSewers().func_76484_a(world, rand, Xcoord2, Ycoord2, Zcoord2))) {
            ++this.oldCave;
            LogHelper.info("Cave at: " + Xcoord2 + " " + Ycoord2 + " " + Zcoord2);
            return true;
        }
        return false;
    }

    @Unique
    private int getBiomeCheck(World world, Random rand, int chunkX, int chunkZ) {
        BiomeGenBase biomegenbase = world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16);
        if (biomegenbase instanceof BiomeGenDesert) {
            return rand.nextInt(10);
        } else if (biomegenbase instanceof BiomeGenPlains) {
            return rand.nextInt(4);
        }
        return 0;
    }

    @Unique
    private void generateDesertRuins(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord4 = chunkX + rand.nextInt(16);
        int scrub4 = 66 + rand.nextInt(12);
        int Xcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenDesertRuins()).func_76484_a(world, rand, Xcoord4, scrub4, Xcoord5);
    }

    @Unique
    private void generateStoneRuins(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord4 = chunkX + rand.nextInt(16);
        int Xcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenStoneRuins()).func_76484_a(world, rand, Xcoord4, 66 + rand.nextInt(12), Xcoord5);
    }

    @Unique
    private boolean generateMadLab(Random rand) {
        return rand.nextInt(16) == 0;
    }

    @Unique
    private void generateMadLab(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord4 = chunkX + rand.nextInt(16);
        int scrub5 = 66 + rand.nextInt(6);
        int Xcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenMadLab()).func_76484_a(world, rand, Xcoord4, scrub5, Xcoord5);
    }

    @Unique
    private boolean generateDesertTomb(Random rand) {
        return rand.nextInt(10) == 0;
    }

    @Unique
    private void generateDesertTomb(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord5 = chunkX + rand.nextInt(16);
        int Ycoord5 = 64 + rand.nextInt(8);
        int Zcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenDesertTomb()).func_76484_a(world, rand, Xcoord5, Ycoord5, Zcoord5);
    }

    @Unique
    private void generateNether(World world, Random random, int chunkX, int chunkZ) {}

    @Unique
    private void generateEnd(World world, Random random, int chunkX, int chunkZ) {}
}
