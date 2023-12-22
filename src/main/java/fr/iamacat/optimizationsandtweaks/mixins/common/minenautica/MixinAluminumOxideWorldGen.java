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
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(AluminumOxideWorldGen.class)
public class MixinAluminumOxideWorldGen implements IWorldGenerator {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        switch (world.provider.dimensionId) {
            case 0:
                this.generateSurface(world, random, chunkX * 16, chunkZ * 16);
                break;
            case 10:
                this.generateMinenautica(world, random, chunkX * 16, chunkZ * 16);
        }

    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void generateSurface(World world, Random random, int x, int z) {
        BiomeGenBase biome = world.getChunkFromChunkCoords(x / 16, z / 16).getBiomeGenForWorldCoords(x / 16 & 15, z / 16 & 15, world.getWorldChunkManager());

        int Xpos;
        int Ypos;
        int Zpos;
        for(int j = 0; j < 5; ++j) {
            do {
                Xpos = x + random.nextInt(16);
                Ypos = random.nextInt(32);
                Zpos = z + random.nextInt(16);
            } while((biome instanceof BiomeGenJungle || biome instanceof BiomeGenDesert) && !this.makeVein(world, Xpos, Ypos, Zpos, random.nextInt(2) + 3, Blocks.stone, BlocksAndItems.aluminumOxideOre, random));
        }

    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
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
        } else {
            world.setBlock(x, y, z, blockToPlace);

            for (int blocksNeeded = veinSize - 1; blocksNeeded > 0; --blocksNeeded) {
                int[] coords = {x, y, z};
                for (int i = 0; i < 3; ++i) {
                    int direction = random.nextInt(6);
                    coords[i] += direction % 3 == 0 ? direction - 3 : direction % 3 == 1 ? -1 : 1;
                }
                if (world.getBlock(coords[0], coords[1], coords[2]) == blockToReplace) {
                    world.setBlock(coords[0], coords[1], coords[2], blockToPlace);
                }
            }
            return true;
        }
    }
}
