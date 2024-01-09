package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minefactoryreloaded;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class WorldGenLakesMeta2  {

    private final Block _block;
    public WorldGenLakesMeta2(Block var1) {
        this._block = var1;
    }
    private static int optimizationsAndTweaks$lakeGenerationCounter = 0;
    private static final int MAX_LAKE_GENERATIONS_PER_TICK = 5;

    public void generate(World world, Random random, int x, int y, int z) {
        if (optimizationsAndTweaks$lakeGenerationCounter >= MAX_LAKE_GENERATIONS_PER_TICK) {
            return;
        }

        optimizationsAndTweaks$lakeGenerationCounter++;
        if (random.nextInt(100) >= 10) {
            return;
        }

        x -= 8;

        while (y > 5 && world.isAirBlock(x, y, z)) {
            --y;
        }

        if (y <= 4) {
        } else {
            y -= 4;
            boolean[] aboolean = new boolean[2048];
            int l = random.nextInt(4) + 4;
            int i1;

            for (i1 = 0; i1 < l; ++i1) {
                double d0 = random.nextDouble() * 6.0D + 3.0D;
                double d1 = random.nextDouble() * 4.0D + 2.0D;
                double d2 = random.nextDouble() * 6.0D + 3.0D;
                double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                for (int j1 = 1; j1 < 15; ++j1) {
                    for (int k1 = 1; k1 < 15; ++k1) {
                        for (int l1 = 1; l1 < 7; ++l1) {
                            double d6 = (j1 - d3) / (d0 / 2.0D);
                            double d7 = (l1 - d4) / (d1 / 2.0D);
                            double d8 = (k1 - d5) / (d2 / 2.0D);
                            double d9 = d6 * d6 + d7 * d7 + d8 * d8;

                            if (d9 < 1.0D) {
                                aboolean[(j1 * 16 + k1) * 8 + l1] = true;
                            }
                        }
                    }
                }
            }

            int i2;
            int j2;

            // Continue with block placement
            for (i1 = 0; i1 < 16; ++i1) {
                for (j2 = 0; j2 < 16; ++j2) {
                    for (i2 = 0; i2 < 8; ++i2) {
                        if (aboolean[(i1 * 16 + j2) * 8 + i2]) {
                            int blockX = x + i1;
                            int blockY = y + i2;
                            int blockZ = z + j2;

                            Material material = world.getBlock(blockX, blockY, blockZ)
                                .getMaterial();

                            if (i2 >= 4 && material.isLiquid()) {
                                return;
                            }

                            if (i2 < 4 && !material.isSolid()
                                && !world.getBlock(blockX, blockY, blockZ)
                                .equals(_block)) {
                                return;
                            }
                        }
                    }
                }
            }

            for (i1 = 0; i1 < 16; ++i1) {
                for (j2 = 0; j2 < 16; ++j2) {
                    for (i2 = 4; i2 < 8; ++i2) {
                        if (aboolean[(i1 * 16 + j2) * 8 + i2] && world.getBlock(x + i1, y + i2 - 1, z + j2)
                            .equals(Blocks.dirt)
                            && world.getSavedLightValue(EnumSkyBlock.Sky, x + i1, y + i2, z + j2) > 0) {
                            BiomeGenBase biomegenbase = world.getBiomeGenForCoords(x + i1, z + j2);

                            world.setBlock(x + i1, y + i2 - 1, z + j2, biomegenbase.topBlock, 0, 2);
                        }
                    }
                }
            }
        }
    }
}
