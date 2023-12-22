package fr.iamacat.optimizationsandtweaks.mixins.common.netherlicious;

import DelirusCrux.Netherlicious.Common.BlockItemUtility.ModBlocks;
import DelirusCrux.Netherlicious.World.Features.Terrain.RuptureSpike;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(RuptureSpike.class)
public abstract class MixinRuptureSpike extends WorldGenerator {
    @Shadow
    private int minWidth;
    @Shadow
    private int maxWidth;
    @Shadow
    private int MinHeight;
    @Shadow
    private int Height;
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        world.getBiomeGenForCoords(i, k);
        int boulderWidth = MathHelper.getRandomIntegerInRange(random, this.minWidth, this.maxWidth);
        int spheres = 1 + random.nextInt(boulderWidth + 1);

        for (int l = 0; l < spheres; ++l) {
            int posX = i + MathHelper.getRandomIntegerInRange(random, -boulderWidth, boulderWidth);
            int posZ = k + MathHelper.getRandomIntegerInRange(random, -boulderWidth, boulderWidth);
            int posY = this.MinHeight + random.nextInt(this.Height);
            int sphereWidth = MathHelper.getRandomIntegerInRange(random, this.minWidth, this.maxWidth);

            optimizationsAndTweaks$processSphere(world, random, posX, posY, posZ, sphereWidth);
        }

        return true;
    }

    @Unique
    private void optimizationsAndTweaks$processSphere(World world, Random random, int posX, int posY, int posZ, int sphereWidth) {
        for (int i1 = posX - sphereWidth; i1 <= posX + sphereWidth; ++i1) {
            for (int j1 = posY - sphereWidth; j1 <= posY + sphereWidth; ++j1) {
                for (int k1 = posZ - sphereWidth; k1 <= posZ + sphereWidth; ++k1) {
                    optimizationsAndTweaks$checkSphereArea( world, i1, j1, k1, sphereWidth, random);
                }
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$checkSphereArea(World world, int i1, int j1, int k1, int sphereWidth, Random random) {
        int dist = i1 * i1 + j1 * j1 + k1 * k1;

        if (dist < sphereWidth * sphereWidth || (dist < (sphereWidth + 1) * (sphereWidth + 1) && random.nextInt(3) == 0)) {
            int j3 = j1;
            while (j3 >= 0) {
                if (world.checkChunksExist(i1, j3, k1, i1, j3, k1)) {
                    if (!optimizationsAndTweaks$isOpaqueCube(world, i1, j3, k1) || optimizationsAndTweaks$isLava(world, i1, j3, k1)) {
                        j3--;
                        continue;
                    }

                    if (optimizationsAndTweaks$isAirOrNetherBrick(world, i1, j3, k1) || optimizationsAndTweaks$isLavaMaterial(world, i1, j3, k1)) {
                        placeRandomBlackstone(world, random, i1, j3, k1);
                    }
                }
                break;
            }
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$isOpaqueCube(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isOpaqueCube();
    }
    @Unique
    private boolean optimizationsAndTweaks$isLava(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == Blocks.lava;
    }
    @Unique
    private boolean optimizationsAndTweaks$isAirOrNetherBrick(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == Blocks.air && world.getBlock(x, y - 1, z) != Blocks.nether_brick;
    }
    @Unique
    private boolean optimizationsAndTweaks$isLavaMaterial(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).getMaterial().isLiquid();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void placeRandomBlackstone(World world, Random random, int x, int y, int z) {
        if (random.nextInt(7) == 0) {
            this.setBlockAndNotifyAdequately(world, x, y, z, ModBlocks.MagmaBlock, 1);
        } else {
            this.setBlockAndNotifyAdequately(world, x, y, z, Blocks.netherrack, 0);
        }
    }
}
