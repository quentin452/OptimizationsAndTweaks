package fr.iamacat.multithreading.mixins.common.cofh;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureHandler;
import cofh.core.world.WorldHandler;
import cpw.mods.fml.common.IWorldGenerator;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(WorldHandler.class)
public class MixinFixCascadingworldgenBetweenCofhandothermods implements IWorldGenerator, IFeatureHandler {

    @Shadow
    private static List<IFeatureGenerator> features;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix) {
            for (IFeatureGenerator generator : features) {
                generator.generateFeature(random, chunkX, chunkZ, world, true);
            }
        }
    }

    @Override
    public boolean registerFeature(IFeatureGenerator feature) {
        return false;
    }

}
