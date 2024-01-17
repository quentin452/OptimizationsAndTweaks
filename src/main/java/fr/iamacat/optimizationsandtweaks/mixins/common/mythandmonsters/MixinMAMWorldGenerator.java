package fr.iamacat.optimizationsandtweaks.mixins.common.mythandmonsters;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.hoopawolf.mam.biome.BiomeIceSheet;
import com.hoopawolf.mam.structure.IceBiomeStructure;
import com.hoopawolf.mam.structure.MAMWorldGenerator;
import com.hoopawolf.mam.structure.StructureForest;

@Mixin(MAMWorldGenerator.class)
public class MixinMAMWorldGenerator {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        switch (world.provider.dimensionId) {
            case -1:
                this.generateNether(world, random, chunkX * 16, chunkZ * 16);
            case 0:
                this.generateSurface(world, random, chunkX * 16, chunkZ * 16);
            default:
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void generateSurface(World world, Random random, int blockX, int blockZ) {
        BiomeGenBase biome = world.getWorldChunkManager()
            .getBiomeGenAt(blockX, blockZ);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST)
            && !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SNOWY)) {
            for (int a = 0; a < 6; ++a) {
                int RandPosX = blockX + random.nextInt(16);
                int RandPosY = random.nextInt(150);
                int RandPosZ = blockZ + random.nextInt(16);
                (new StructureForest()).func_76484_a(world, random, RandPosX, RandPosY, RandPosZ);
            }
        } else if (biome instanceof BiomeIceSheet) {
            (new IceBiomeStructure()).func_76484_a(world, random, blockX, 60, blockZ);
            // rewrote using Recurrent Complex, don't need anymore
            /*
             * } else if (biome instanceof BiomeGenOcean) {
             * (new StructureUnderWater()).func_76484_a(world, random, blockX, 45, blockZ);
             */
        }

    }

    @Shadow
    private void generateNether(World world, Random random, int blockX, int blockZ) {
        int var10000 = blockX + random.nextInt(16);
        int Ycoord = random.nextInt(60);
        var10000 = blockZ + random.nextInt(16);
    }

}
