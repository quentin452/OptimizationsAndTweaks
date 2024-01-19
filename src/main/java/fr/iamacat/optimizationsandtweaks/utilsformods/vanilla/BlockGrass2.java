package fr.iamacat.optimizationsandtweaks.utilsformods.vanilla;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class BlockGrass2 {
    public static void optimizationsAndTweaks$spreadGrass(World worldIn, int x, int y, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        Chunk chunk = worldIn.getChunkFromChunkCoords(chunkX, chunkZ);
        if(chunk == null) {
            return;
        }
        if(!chunk.isChunkLoaded) {
            return;
        }
        if (worldIn.getTotalWorldTime() % 10 == 0) {
            for (int l = 0; l < 4; ++l) {
                int i1 = x + random.nextInt(3) - 1;
                int j1 = y + random.nextInt(5) - 3;
                int k1 = z + random.nextInt(3) - 1;
                Block block = chunk.getBlock(i1 & 15, j1, k1 & 15);
                int metadata = chunk.getBlockMetadata(i1 & 15, j1, k1 & 15);

                int lightValue = worldIn.getBlockLightValue(i1, j1 + 1, k1);
                int lightOpacity = worldIn.getBlockLightOpacity(i1, j1 + 1, k1);

                if (block == Blocks.dirt && metadata == 0 && lightValue >= 4 && lightOpacity <= 2) {
                    worldIn.setBlock(i1, j1, k1, Blocks.grass, 0, 2);
                }
            }
        }
    }
}
