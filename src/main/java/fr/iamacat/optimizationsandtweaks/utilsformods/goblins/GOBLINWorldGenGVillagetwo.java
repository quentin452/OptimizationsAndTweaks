package fr.iamacat.optimizationsandtweaks.utilsformods.goblins;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Random;

public class GOBLINWorldGenGVillagetwo {
    private static final int[][][] grassBlocks = new int[21][11][31];

    static {
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 11; j++) {
                for (int k = 0; k < 31; k++) {
                    grassBlocks[i][j][k] = (i == 0 && j == 0 && k == 0) ? 1 : 0;
                }
            }
        }
    }

    public static boolean canGenerateVillage(World world, int i, int j, int k) {
        if (!world.blockExists(i, j, k) || !world.blockExists(i + 21, j, k + 30) ||
            !world.blockExists(i + 21, j + 10, k + 30) || !world.blockExists(i, j + 10, k)) {
            return false;
        }

        Block block1 = world.getBlock(i, j, k);
        Block block2 = world.getBlock(i + 21, j, k + 30);
        Block block3 = world.getBlock(i + 21, j + 10, k + 30);
        Block block4 = world.getBlock(i, j + 10, k);

        return block1 == Blocks.grass &&
            block2 == Blocks.grass &&
            canGenerate(world, new Random(), i, j, k) &&
            block3 == Blocks.air &&
            block4 == Blocks.air;
    }

    public static boolean canGenerate(World world, Random rand, int i, int j, int k) {
        int countGrass = 0;
        for (int i1 = 0; i1 <= 20; ++i1) {
            for (int k1 = 0; k1 <= 30; ++k1) {
                for (int j1 = -1; j1 <= 1; ++j1) {
                    countGrass += grassBlocks[i1][j1 + 1][k1];
                }
            }
        }
        return countGrass > 1100;
    }
    public static void func_76484_a(World world, Random rand, int i, int j, int k) {
        int a = -15;

        while(true) {
            if (a > 45) {
                generateVillage(world, rand, i, j, k);
                break;
            }

            for(int b = -15; b <= 55; ++b) {
                if (world.getBlock(i + a, j + 3, k + b) == Blocks.planks || world.getBlock(i + a, j + 3, k + b) == Blocks.cobblestone || world.getBlock(i + a, j + 3, k + b) == Blocks.stonebrick) {
                    return;
                }
            }

            ++a;
        }
    }
    public static void generateVillage(World world, Random rand, int i, int j, int k) {
        int width;
        int boss;
        for (width = 1; width < 20; ++width) {
            for (boss = 4; boss < 26; ++boss) {
                if (world.getBlock(i + width, j - 1, k + boss) == Blocks.grass) {
                    world.setBlock(i + width, j, k + boss, Blocks.grass);
                    world.setBlock(i + width, j - 1, k + boss, Blocks.dirt);
                }

                if (world.getBlock(i + width, j - 2, k + boss) == Blocks.grass) {
                    world.setBlock(i + width, j - 1, k + boss, Blocks.grass);
                    world.setBlock(i + width, j - 2, k + boss, Blocks.dirt);
                }
            }
        }

        int height;
        for (width = 0; width < 5; ++width) {
            for (boss = 0; boss < 10; ++boss) {
                if (rand.nextInt(6) == 1) {
                    world.setBlock(i + 9 + width, j, k + 9 + boss, Blocks.mossy_cobblestone, 0, 2);
                } else {
                    world.setBlock(i + 9 + width, j, k + 9 + boss, Blocks.cobblestone, 0, 2);
                }

                for (height = 1; height <= 5; ++height) {
                    if (world.getBlock(i + 9 + width, j + height, k + 9 + boss) != Blocks.log) {
                        world.setBlock(i + 9 + width, j + height, k + 9 + boss, Blocks.air, 0, 2);
                    }
                }

                world.setBlock(i + 9 + width, j - 1, k + 9 + boss, Blocks.dirt, 0, 2);
            }
        }
    }
}
