package fr.iamacat.multithreading.mixins.common.battletowers;

import java.io.*;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;

import atomicstryker.battletowers.common.WorldGenHandler;
import cpw.mods.fml.common.IWorldGenerator;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(WorldGenHandler.class)
public class MixinFixCascadingFromWorldGenHandler implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFixCascadingFromWorldGenHandler) {

        }
    }
}
