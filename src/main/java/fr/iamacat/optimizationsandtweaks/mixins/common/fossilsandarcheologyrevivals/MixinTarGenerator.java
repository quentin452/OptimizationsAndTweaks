package fr.iamacat.optimizationsandtweaks.mixins.common.fossilsandarcheologyrevivals;

import cpw.mods.fml.common.IWorldGenerator;
import fossilsarcheology.server.block.FABlockRegistry;
import fossilsarcheology.server.gen.TarGenerator;
import fossilsarcheology.server.gen.feature.TarPitWorldGen;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenSwamp;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(TarGenerator.class)
public class MixinTarGenerator implements IWorldGenerator {
    @Shadow
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
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
    public void generateSurface(World world, Random random, int i, int j) {
        BiomeGenBase biomegenbase = world.getWorldChunkManager().getBiomeGenAt(i, j);
        if (biomegenbase instanceof BiomeGenSwamp) {
            for (int k = 0; k < 10; ++k) {
                int l = i + random.nextInt(9);
                int i1 = random.nextInt(128);
                int j1 = j + random.nextInt(9);
                (new TarPitWorldGen(FABlockRegistry.INSTANCE.tar)).func_76484_a(world, random, l, i1, j1);
            }
        }
    }
    @Shadow
    private void generateNether(World world, Random random, int blockX, int blockZ) {
    }
}
