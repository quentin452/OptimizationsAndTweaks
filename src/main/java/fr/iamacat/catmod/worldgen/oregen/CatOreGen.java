package fr.iamacat.catmod.worldgen.oregen;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

import static fr.iamacat.catmod.init.RegisterBlocks.catOre;

public class CatOreGen implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        switch(world.provider.dimensionId){
            case 1:
                generateEnd(world, random, chunkX, chunkZ);
                break;
            case 0:
                generateOverworld(world, random, chunkX, chunkZ);
                break;
            case -1:
                generateNether(world, random, chunkX, chunkZ);
                break;
        }
    }
    public void generateEnd(World world, Random rand,int x, int z){
    }
    public void generateOverworld(World world, Random rand,int x, int z){
        generateOre(catOre, world, rand, x ,z ,2,10,5,0,12, Blocks.stone);
    }
    public void generateNether(World world, Random rand,int x, int z){
    }
    public void generateOre(Block block, World world, Random random, int chunkX, int chunkZ, int minVienSize,
                            int maxVienSize, int chance, int minY, int maxY, Block generateIn){
        int vienSize = minVienSize + random.nextInt(maxVienSize - minVienSize);
        int heightRange = maxY - minY;
        WorldGenMinable gen = new WorldGenMinable(block, vienSize,generateIn);
        for(int i = 0; i < chance; i++){
            int xRand = chunkX * 16 + random.nextInt(16);
            int yRand = random.nextInt(heightRange) + minY;
            int zRand = chunkZ * 16 + random.nextInt(16);
            gen.generate(world, random,xRand, yRand, zRand);
        }
    }
}