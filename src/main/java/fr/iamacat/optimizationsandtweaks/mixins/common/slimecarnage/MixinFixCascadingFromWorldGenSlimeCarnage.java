package fr.iamacat.optimizationsandtweaks.mixins.common.slimecarnage;

import cpw.mods.fml.common.IWorldGenerator;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraft.world.biome.BiomeGenPlains;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import supremopete.SlimeCarnage.worldgen.*;

import java.util.Random;

@Mixin(WorldGenSlimeCarnage.class)
public class MixinFixCascadingFromWorldGenSlimeCarnage implements IWorldGenerator {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
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

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void generateSurface(World world, Random random, int chunkX, int chunkZ) {
        if (optimizationsAndTweaks$generateCave(world, random, chunkX, chunkZ)) {
            return;
        }

        int biomeCheck = optimizationsAndTweaks$getBiomeCheck(world, random, chunkX, chunkZ);
        if (biomeCheck == 1) {
            optimizationsAndTweaks$generateDesertRuins(world, random, chunkX, chunkZ);
        } else if (biomeCheck == 2) {
            optimizationsAndTweaks$generateStoneRuins(world, random, chunkX, chunkZ);
        }

        if (optimizationsAndTweaks$generateMadLab(random)) {
            optimizationsAndTweaks$generateMadLab(world, random, chunkX, chunkZ);
        }

        if (biomeCheck == 1 && optimizationsAndTweaks$generateDesertTomb(random)) {
            optimizationsAndTweaks$generateDesertTomb(world, random, chunkX, chunkZ);
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$generateCave(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord2 = chunkX + rand.nextInt(16);
        int Ycoord2 = 64 + rand.nextInt(6);
        int Zcoord2 = chunkZ + rand.nextInt(16);

        return new WorldGenSewers().func_76484_a(world, rand, Xcoord2, Ycoord2, Zcoord2);
    }

    @Unique
    private int optimizationsAndTweaks$getBiomeCheck(World world, Random rand, int chunkX, int chunkZ) {
        BiomeGenBase biomegenbase = world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16);
        if (biomegenbase instanceof BiomeGenDesert) {
            return rand.nextInt(10);
        } else if (biomegenbase instanceof BiomeGenPlains) {
            return rand.nextInt(4);
        }
        return 0;
    }

    @Unique
    private void optimizationsAndTweaks$generateDesertRuins(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord4 = chunkX + rand.nextInt(16);
        int scrub4 = 66 + rand.nextInt(12);
        int Xcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenDesertRuins()).func_76484_a(world, rand, Xcoord4, scrub4, Xcoord5);
    }

    @Unique
    private void optimizationsAndTweaks$generateStoneRuins(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord4 = chunkX + rand.nextInt(16);
        int Xcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenStoneRuins()).func_76484_a(world, rand, Xcoord4, 66 + rand.nextInt(12), Xcoord5);
    }

    @Unique
    private boolean optimizationsAndTweaks$generateMadLab(Random rand) {
        return rand.nextInt(16) == 0;
    }

    @Unique
    private void optimizationsAndTweaks$generateMadLab(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord4 = chunkX + rand.nextInt(16);
        int scrub5 = 66 + rand.nextInt(6);
        int Xcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenMadLab()).func_76484_a(world, rand, Xcoord4, scrub5, Xcoord5);
    }

    @Unique
    private boolean optimizationsAndTweaks$generateDesertTomb(Random rand) {
        return rand.nextInt(10) == 0;
    }

    @Unique
    private void optimizationsAndTweaks$generateDesertTomb(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord5 = chunkX + rand.nextInt(16);
        int Ycoord5 = 64 + rand.nextInt(8);
        int Zcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenDesertTomb()).func_76484_a(world, rand, Xcoord5, Ycoord5, Zcoord5);
    }

    @Shadow
    private void generateNether(World world, Random random, int chunkX, int chunkZ) {}

    @Shadow
    private void generateEnd(World world, Random random, int chunkX, int chunkZ) {}
}
