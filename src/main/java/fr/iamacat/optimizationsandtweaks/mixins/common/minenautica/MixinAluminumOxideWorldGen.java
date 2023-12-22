package fr.iamacat.optimizationsandtweaks.mixins.common.minenautica;

import com.minenautica.Minenautica.Block.WorldGenerator.AluminumOxideWorldGen;
import com.minenautica.Minenautica.CustomRegistry.BlocksAndItems;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Mixin(AluminumOxideWorldGen.class)
public class MixinAluminumOxideWorldGen implements IWorldGenerator {
    private static final int CHUNK_SIZE = 16;
    private static final int MAX_ITERATIONS = 5;
    private static final int[] DIRECTIONS = { 0, 1, 2, 3, 4, 5 };

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        int x = chunkX << 4;
        int z = chunkZ << 4;

        switch (world.provider.dimensionId) {
            case 0:
                this.generateSurface(world, random, x, z);
                break;
            case 10:
                this.generateMinenautica(world, random, x, z);
                break;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void generateSurface(World world, Random random, int x, int z) {
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(x & 15, z & 15, world.getWorldChunkManager());

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        for (int j = 0; j < MAX_ITERATIONS; ++j) {
            int Xpos, Ypos, Zpos;
            boolean isValidPosition = false;

            for (int iteration = 0; iteration < DIRECTIONS.length && !isValidPosition; iteration++) {
                Xpos = x + random.nextInt(CHUNK_SIZE);
                Ypos = random.nextInt(32);
                Zpos = z + random.nextInt(CHUNK_SIZE);

                if ((biome instanceof BiomeGenJungle || biome instanceof BiomeGenDesert)
                    && this.makeVein(world, Xpos, Ypos, Zpos, random.nextInt(2) + 3,
                    Blocks.stone, BlocksAndItems.aluminumOxideOre, random)
                    && Xpos >= chunkX * CHUNK_SIZE && Xpos < (chunkX + 1) * CHUNK_SIZE
                    && Zpos >= chunkZ * CHUNK_SIZE && Zpos < (chunkZ + 1) * CHUNK_SIZE) {
                    isValidPosition = true;
                }
            }
        }
    }

    @Shadow
    private void generateMinenautica(World world, Random random, int x, int z) {
    }
    /**
     * @author iamacatfr
     * @reason fix infinite loop that cause the server to freeze during generation on my modpack
     */
    @Overwrite(remap = false)
    private boolean makeVein(World world, int x, int y, int z, int veinSize, Block blockToReplace, Block blockToPlace, Random random) {
        if (world.getBlock(x, y, z) != blockToReplace) {
            return false;
        }

        Set<String> generatedBlocks = new HashSet<>();
        world.setBlock(x, y, z, blockToPlace);
        generatedBlocks.add(optimizationsAndTweaks$getBlockKey(x, y, z));

        for (int blocksNeeded = veinSize - 1; blocksNeeded > 0; --blocksNeeded) {
            int direction = random.nextInt(6);
            int[] coords = optimizationsAndTweaks$generateCoordinates(x, y, z, direction);

            if (optimizationsAndTweaks$isValidPosition(world, coords[0], coords[1], coords[2], generatedBlocks,blockToReplace)) {
                world.setBlock(coords[0], coords[1], coords[2], blockToPlace);
                generatedBlocks.add(optimizationsAndTweaks$getBlockKey(coords[0], coords[1], coords[2]));
            } else {
                break;
            }
        }
        return true;
    }

    @Unique
    private int[] optimizationsAndTweaks$generateCoordinates(int x, int y, int z, int direction) {
        int[] coords = {x, y, z};
        switch (direction) {
            case 0:
                coords[0]++;
                break;
            case 1:
                coords[0]--;
                break;
            case 2:
                coords[1]++;
                break;
            case 3:
                coords[1]--;
                break;
            case 4:
                coords[2]++;
                break;
            case 5:
                coords[2]--;
                break;
            default:
                break;
        }
        return coords;
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidPosition(World world, int x, int y, int z, Set<String> generatedBlocks, Block blockToReplace) {
        String blockKey = optimizationsAndTweaks$getBlockKey(x, y, z);
        return !generatedBlocks.contains(blockKey) && world.getBlock(x, y, z) == blockToReplace;
    }

    @Unique
    private String optimizationsAndTweaks$getBlockKey(int x, int y, int z) {
        return x + "," + y + "," + z;
    }
}
