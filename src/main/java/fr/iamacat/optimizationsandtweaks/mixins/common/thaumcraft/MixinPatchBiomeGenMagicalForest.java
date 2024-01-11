package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.utilsformods.thaumcraft.BiomeGenMagicalForest2;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.WorldGenManaPods;
import thaumcraft.common.lib.world.biomes.BiomeGenMagicalForest;

@Mixin(BiomeGenMagicalForest.class)
public abstract class MixinPatchBiomeGenMagicalForest extends BiomeGenBase {

    @Shadow
    private static final WorldGenBlockBlob blobs;

    public MixinPatchBiomeGenMagicalForest(int p_i1971_1_) {
        super(p_i1971_1_);
    }

    /**
     * @author iamacatfr
     * @reason reduce tps lags caused by func_76728_a
     */
    @Override
    public void decorate(World world, Random random, int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return;
        }
        optimizationsAndTweaks$generateBlobs(world, random, x, z);
        optimizationsAndTweaks$generateBigMushrooms(world, random, x, z);
        optimizationsAndTweaks$generateManaPods(world, random, x, z);
        optimizationsAndTweaks$generateCustomPlants(world, random, x, z);

        super.decorate(world, random, x, z);
    }

    @Unique
    private void optimizationsAndTweaks$generateBlobs(World world, Random random, int x, int z) {
        for (int i = 0; i < 3; ++i) {
            int posX = x + random.nextInt(16) + 8;
            int posZ = z + random.nextInt(16) + 8;
            int posY = world.getHeightValue(posX, posZ);
            blobs.generate(world, random, posX, posY, posZ);
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateBigMushrooms(World world, Random random, int x, int z) {
        for (int k = 0; k < 4; ++k) {
            for (int l = 0; l < 4; ++l) {
                int posX = x + k * 4 + 1 + 8 + random.nextInt(3);
                int posZ = z + l * 4 + 1 + 8 + random.nextInt(3);
                int posY = world.getHeightValue(posX, posZ);
                if (random.nextInt(40) == 0) {
                    WorldGenBigMushroom worldgenbigmushroom = new WorldGenBigMushroom();
                    worldgenbigmushroom.generate(world, random, posX, posY, posZ);
                }
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateManaPods(World world, Random random, int x, int z) {
        WorldGenManaPods worldgenpods = new WorldGenManaPods();
        for (int i = 0; i < 10; ++i) {
            int posX = x + random.nextInt(16) + 8;
            int posY = 64;
            int posZ = z + random.nextInt(16) + 8;
            worldgenpods.func_76484_a(world, random, posX, posY, posZ);
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateCustomPlants(World world, Random random, int x, int z) {
        int worldHeight = world.getHeightValue(x, z);

        for (int i = 0; i < 8; ++i) {
            int posX = x + random.nextInt(16);
            int posZ = z + random.nextInt(16);

            while (worldHeight > 50 && world.getBlock(posX, worldHeight, posZ) != Blocks.grass) {
                worldHeight--;
            }

            if (world.getBlock(posX, worldHeight, posZ) == Blocks.grass && world.isAirBlock(posX, worldHeight + 1, posZ)
                && BiomeGenMagicalForest2.optimizationsAndTweaks$isAdjacentToWood(world, posX, worldHeight + 1, posZ)) {

                world.setBlock(posX, worldHeight + 1, posZ, ConfigBlocks.blockCustomPlant, 5, 2);
            }
        }
    }

    static {
        blobs = new WorldGenBlockBlob(Blocks.mossy_cobblestone, 0);
    }
}
