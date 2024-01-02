package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Unique;

public class ThaumcraftWorldGenerator {
    @Unique
    private static final int SEARCH_RADIUS = 5;

    @Unique
    public static int countBlocksAroundPlayer(World world, int cx, int cy, int cz, Material material) {
        int chunkX = cx >> 4;
        int chunkZ = cz >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return 0;
        }

        int radius = SEARCH_RADIUS;
        int count = 0;

        int minX = Math.max(cx - radius, chunkX << 4);
        int maxX = Math.min(cx + radius, (chunkX << 4) + 15);
        int minZ = Math.max(cz - radius, chunkZ << 4);
        int maxZ = Math.min(cz + radius, (chunkZ << 4) + 15);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int maxY = world.getHeightValue(x, z);

                for (int y = 0; y < maxY; y++) {
                    Block block = chunk.getBlock(x & 15, y, z & 15);

                    if (block.getMaterial() == material) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    @Unique
    public static int optimizationsAndTweaks$countFoliageAroundPlayer(World world, int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return 0;
        }

        int count = 0;


        for (int xOffset = -SEARCH_RADIUS; xOffset <= SEARCH_RADIUS; ++xOffset) {
            for (int yOffset = -SEARCH_RADIUS; yOffset <= SEARCH_RADIUS; ++yOffset) {
                for (int zOffset = -SEARCH_RADIUS; zOffset <= SEARCH_RADIUS; ++zOffset) {
                    if (optimizationsAndTweaks$isWithinRadius(xOffset, yOffset, zOffset) && (world.getBlock(x + xOffset, y + yOffset, z + zOffset).isLeaves(world, x + xOffset, y + yOffset, z + zOffset))) {
                            count++;
                    }
                }
            }
        }

        return count;
    }

    @Unique
    private static boolean optimizationsAndTweaks$isWithinRadius(int xOffset, int yOffset, int zOffset) {
        return xOffset * xOffset + yOffset * yOffset + zOffset * zOffset <= SEARCH_RADIUS * SEARCH_RADIUS;
    }
}
