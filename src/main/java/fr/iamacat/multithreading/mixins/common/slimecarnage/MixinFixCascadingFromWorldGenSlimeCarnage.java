package fr.iamacat.multithreading.mixins.common.slimecarnage;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraft.world.biome.BiomeGenPlains;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.common.IWorldGenerator;
import supremopete.SlimeCarnage.commom.LogHelper;
import supremopete.SlimeCarnage.worldgen.*;
import supremopete.SlimeCarnage.worldgen.WorldGenSlimeCarnage;

@Mixin(WorldGenSlimeCarnage.class)
public class MixinFixCascadingFromWorldGenSlimeCarnage implements IWorldGenerator {

    @Unique
    public int oldCave;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
                         IChunkProvider chunkProvider) {
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

    @Unique
    private void generateSurface(World world, Random random, int chunkX, int chunkZ) {
        // Cache the biome for this chunk
        BiomeGenBase biomegenbase = world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16);

        int Ycoord1;
        int Xcoord2;
        int Xcoord5;
        if (this.oldCave == 0) {
            int Xcoord1 = chunkX * 16 + random.nextInt(16);
            Ycoord1 = 64 + random.nextInt(16);
            Xcoord2 = chunkZ * 16 + random.nextInt(16);
            if ((new WorldGenOldManCave()).func_76484_a(world, random, Xcoord1, Ycoord1, Xcoord2)) {
                ++this.oldCave;
                LogHelper.info("Cave at: " + Xcoord1 + " " + Ycoord1 + " " + Xcoord2);
            }
        }

        Random rand = new Random(world.getSeed());
        rand.nextInt(); 

        Ycoord1 = rand.nextInt(20);
        int scrub2;
        int scrub4;
        if (Ycoord1 == 0) {
            Xcoord2 = chunkX * 16 + random.nextInt(16);
            scrub2 = 64 + random.nextInt(6);
            scrub4 = chunkZ * 16 + random.nextInt(16);
            (new WorldGenSewers()).func_76484_a(world, random, Xcoord2, scrub2, scrub4);
        }

        int Xcoord4;
        int scrub5;
        if (biomegenbase instanceof BiomeGenDesert) {
            scrub2 = rand.nextInt(10);
            if (scrub2 == 0) {
                scrub4 = chunkX * 16 + random.nextInt(16);
                Xcoord4 = 66 + random.nextInt(12);
                scrub5 = chunkZ * 16 + random.nextInt(16);
                (new WorldGenDesertRuins()).func_76484_a(world, random, scrub4, Xcoord4, scrub5);
            }
        }

        if (biomegenbase instanceof BiomeGenPlains) {
            scrub4 = rand.nextInt(4);
            if (scrub4 == 0) {
                Xcoord4 = chunkX * 16 + random.nextInt(16);
                scrub5 = 66 + random.nextInt(12);
                Xcoord5 = chunkZ * 16 + random.nextInt(16);
                (new WorldGenStoneRuins()).func_76484_a(world, random, Xcoord4, scrub5, Xcoord5);
            }
        }

        scrub4 = rand.nextInt(16);
        if (scrub4 == 0) {
            Xcoord4 = chunkX * 16 + random.nextInt(16);
            scrub5 = 66 + random.nextInt(6);
            Xcoord5 = chunkZ * 16 + random.nextInt(16);
            (new WorldGenMadLab()).func_76484_a(world, random, Xcoord4, scrub5, Xcoord5);
        }

        if (biomegenbase instanceof BiomeGenDesert) {
            scrub5 = rand.nextInt(10);
            if (scrub5 == 0) {
                Xcoord5 = chunkX * 16 + random.nextInt(16);
                int Ycoord5 = 64 + random.nextInt(8);
                int Zcoord5 = chunkZ * 16 + random.nextInt(16);
                (new WorldGenDesertTomb()).func_76484_a(world, random, Xcoord5, Ycoord5, Zcoord5);
            }
        }
    }

    @Unique
    private void generateNether(World world, Random random, int chunkX, int chunkZ) {}

    @Unique
    private void generateEnd(World world, Random random, int chunkX, int chunkZ) {}
}
