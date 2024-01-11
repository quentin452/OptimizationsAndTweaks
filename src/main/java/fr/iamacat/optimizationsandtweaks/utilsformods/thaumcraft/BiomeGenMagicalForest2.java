package fr.iamacat.optimizationsandtweaks.utilsformods.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BiomeGenMagicalForest2 {

    private static final int[][] PROBABLE_POSITIONS = { { 1, 0, 0 }, { 0, 0, 1 }, { -1, 0, 0 }, { 0, 0, -1 },
        { 1, 0, 1 }, { -1, 0, 1 }, { -1, 0, -1 }, { 1, 0, -1 }, { 0, 1, 0 }, { 0, -1, 0 } };

    public static boolean optimizationsAndTweaks$isAdjacentToWood(World world, int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return false;
        }

        for (int[] pos : PROBABLE_POSITIONS) {
            int xx = pos[0] + x;
            int yy = pos[1] + y;
            int zz = pos[2] + z;

            if (yy >= 0 && yy < 256) {
                Block block = chunk.getBlock(xx & 15, yy, zz & 15);

                if (block != null && block.isWood(world, xx, yy, zz)) {
                    return true;
                }
            }
        }

        return false;
    }
}
