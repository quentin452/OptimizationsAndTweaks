package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

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
    private static final int[][] PROBABLE_POSITIONS = {
        {1, 0, 0}, {0, 0, 1}, {-1, 0, 0}, {0, 0, -1},
        {1, 0, 1}, {-1, 0, 1}, {-1, 0, -1}, {1, 0, -1},
        {0, 1, 0}, {0, -1, 0}
    };

    @Unique
    private void optimizationsAndTweaks$generateCustomPlants(World world, Random random, int x, int z) {
        for (int i = 0; i < 8; ++i) {
            int posX = x + random.nextInt(16);
            int posZ = z + random.nextInt(16);
            int posY = world.getHeightValue(posX, posZ);

            for (; posY > 50 && world.getBlock(posX, posY, posZ) != Blocks.grass; --posY) {}

            if (world.getBlock(posX, posY, posZ) == Blocks.grass &&
                world.getBlock(posX, posY + 1, posZ).isReplaceable(world, posX, posY + 1, posZ) &&
                optimizationsAndTweaks$isAdjacentToWood(world, posX, posY + 1, posZ)) {
                world.setBlock(posX, posY + 1, posZ, ConfigBlocks.blockCustomPlant, 5, 2);
            }
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$isAdjacentToWood(IBlockAccess world, int x, int y, int z) {
        for (int[] pos : PROBABLE_POSITIONS) {
            int xx = pos[0] + x;
            int yy = pos[1] + y;
            int zz = pos[2] + z;

            Block block = world.getBlock(xx, yy, zz);

            if (block != null && block.isWood(world, xx, yy, zz)) {
                return true;
            }
        }

        return false;
    }



    static {
        blobs = new WorldGenBlockBlob(Blocks.mossy_cobblestone, 0);
    }
}
