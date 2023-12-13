package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.lib.world.WorldGenCustomFlowers;
import thaumcraft.common.lib.world.WorldGenSilverwoodTrees;

import java.util.Random;

@Mixin(WorldGenSilverwoodTrees.class)
public class MixinWorldGenSilverwoodTrees extends WorldGenAbstractTree {
    @Shadow
    private final int minTreeHeight;
    @Shadow
    private final int randomTreeHeight;
    @Shadow
    boolean worldgen = false;

    public MixinWorldGenSilverwoodTrees(boolean doBlockNotify, int minTreeHeight, int randomTreeHeight) {
        super(doBlockNotify);
        this.worldgen = !doBlockNotify;
        this.minTreeHeight = minTreeHeight;
        this.randomTreeHeight = randomTreeHeight;
    }
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(this.randomTreeHeight) + this.minTreeHeight;

        if (!optimizationsAndTweaks$isValidTreePosition(world, x, y, z, height)) {
            return false;
        }

        if (!optimizationsAndTweaks$checkSoilAndHeight(world, x, y, z, height)) {
            return false;
        }

        optimizationsAndTweaks$generateTrunk(world, random, x, y, z, height);
        if (this.worldgen) {
            optimizationsAndTweaks$generateCustomFlowers(world, random, x, y, z);
        }

        return true;
    }
    @Unique
    private boolean optimizationsAndTweaks$isValidTreePosition(World world, int x, int y, int z, int height) {
        return y >= 1 && y + height + 1 <= 256;
    }
    @Unique
    private boolean optimizationsAndTweaks$checkSoilAndHeight(World world, int x, int y, int z, int height) {
        Block block = world.getBlock(x, y - 1, z);
        boolean isSoil = block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (BlockSapling) Blocks.sapling);
        return isSoil && y < 256 - height - 1;
    }
    @Unique
    private void optimizationsAndTweaks$generateTrunk(World world, Random random, int x, int y, int z, int height) {
        int start = y + height - 5;
        int end = y + height + 3 + random.nextInt(3);

        for (int k2 = start; k2 <= end; ++k2) {
            optimizationsAndTweaks$generateLeaves(world, random, x, y, z, height, k2);
        }

        int chance = (int) (height * 1.5);
        boolean lastblock = false;

        for (int k2 = 0; k2 < height; ++k2) {
            optimizationsAndTweaks$generateLogBlocks(world, random, x, y, z, height, k2, chance, lastblock);
        }
    }
    @Unique
    private void optimizationsAndTweaks$generateLeaves(World world, Random random, int x, int y, int z, int height, int k2) {
        int cty = MathHelper.clamp_int(k2, y + height - 3, y + height);

        for (int xx = x - 5; xx <= x + 5; ++xx) {
            for (int zz = z - 5; zz <= z + 5; ++zz) {
                double d3 = (double) xx - x;
                double d4 = (double) k2 - cty;
                double d5 = (double) zz - z;
                double dist = d3 * d3 + d4 * d4 + d5 * d5;
                if (dist < (10 + random.nextInt(8)) && world.getBlock(xx, cty, zz).canBeReplacedByLeaves(world, xx, cty, zz)) {
                    this.setBlockAndNotifyAdequately(world, xx, cty, zz, ConfigBlocks.blockMagicalLeaves, 1);
                }
            }
        }
    }
    @Unique
    private void optimizationsAndTweaks$generateLogBlocks(World world, Random random, int x, int y, int z, int height, int k2, int chance, boolean lastblock) {
        Block magicalLog = ConfigBlocks.blockMagicalLog;

        for (int xOffset = -2; xOffset <= 2; xOffset++) {
            for (int zOffset = -2; zOffset <= 2; zOffset++) {
                if (Math.abs(xOffset) == 2 || Math.abs(zOffset) == 2) {
                    int logMeta = (Math.abs(xOffset) == 2 && Math.abs(zOffset) == 2) ? 5 : 9;
                    this.setBlockAndNotifyAdequately(world, x + xOffset, y, z + zOffset, magicalLog, logMeta);
                    this.setBlockAndNotifyAdequately(world, x + xOffset, y - 1, z + zOffset, magicalLog, 1);
                }
            }
        }

        for (int yOffset = 0; yOffset < height; yOffset++) {
            Block block = world.getBlock(x, y + yOffset, z);

            if (block.isAir(world, x, y + yOffset, z) || block.isLeaves(world, x, y + yOffset, z) || block.isReplaceable(world, x, y + yOffset, z)) {
                boolean generateNode = k2 > 0 && !lastblock && random.nextInt(chance) == 0;

                if (generateNode) {
                    this.setBlockAndNotifyAdequately(world, x, y + yOffset, z, magicalLog, 2);
                    ThaumcraftWorldGenerator.createRandomNodeAt(world, x, y + yOffset, z, random, true, false, false);
                } else {
                    this.setBlockAndNotifyAdequately(world, x, y + yOffset, z, magicalLog, 1);
                }

                // Place logs around the central log if the conditions are met
                if (yOffset > 0) {
                    if (random.nextInt(3) != 0) {
                        this.setBlockAndNotifyAdequately(world, x - 1, y + yOffset, z - 1, magicalLog, 1);
                    }

                    if (random.nextInt(3) != 0) {
                        this.setBlockAndNotifyAdequately(world, x + 1, y + yOffset, z + 1, magicalLog, 1);
                    }

                    if (random.nextInt(3) != 0) {
                        this.setBlockAndNotifyAdequately(world, x - 1, y + yOffset, z + 1, magicalLog, 1);
                    }

                    if (random.nextInt(3) != 0) {
                        this.setBlockAndNotifyAdequately(world, x + 1, y + yOffset, z - 1, magicalLog, 1);
                    }
                }

                // Place additional logs near the top if conditions are met
                if (yOffset >= height - 4) {
                    if (random.nextInt(3) == 0) {
                        this.setBlockAndNotifyAdequately(world, x - 1, y + yOffset, z - 1, magicalLog, 1);
                    }

                    if (random.nextInt(3) == 0) {
                        this.setBlockAndNotifyAdequately(world, x + 1, y + yOffset, z + 1, magicalLog, 1);
                    }

                    if (random.nextInt(3) == 0) {
                        this.setBlockAndNotifyAdequately(world, x - 1, y + yOffset, z + 1, magicalLog, 1);
                    }

                    if (random.nextInt(3) == 0) {
                        this.setBlockAndNotifyAdequately(world, x + 1, y + yOffset, z - 1, magicalLog, 1);
                    }
                }
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateCustomFlowers(World world, Random random, int x, int y, int z) {
        WorldGenerator flowers = new WorldGenCustomFlowers(ConfigBlocks.blockCustomPlant, 2);
        flowers.generate(world, random, x, y, z);
    }
}
