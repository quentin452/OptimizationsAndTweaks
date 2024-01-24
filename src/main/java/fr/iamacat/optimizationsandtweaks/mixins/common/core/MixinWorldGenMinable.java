package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldGenMinable.class)
public class MixinWorldGenMinable extends WorldGenerator {

    @Shadow
    private Block field_150519_a;
    /** The number of blocks to generate. */
    @Shadow
    private int numberOfBlocks;
    @Shadow
    private Block field_150518_c;
    @Shadow
    private int mineableBlockMeta;

    @Unique
    private static final int SIN_COS_PRECISION = 360;
    @Unique
    private static final float[] SIN_TABLE = new float[SIN_COS_PRECISION];
    @Unique
    private static final float[] COS_TABLE = new float[SIN_COS_PRECISION];

    static {
        for (int i = 0; i < SIN_COS_PRECISION; i++) {
            float angle = (float) Math.toRadians(i);
            SIN_TABLE[i] = MathHelper.sin(angle);
            COS_TABLE[i] = MathHelper.cos(angle);
        }
    }

    /**
     * @author iamacatfr
     * @reason reducing TPS lags during worldgen
     */
    @Overwrite
    public boolean generate(World world, Random random, int x, int y, int z) {
        float angle = random.nextFloat() * (float) Math.PI;
        double centerX = x + 8 + SIN_TABLE[MathHelper.floor_float(angle * SIN_COS_PRECISION) & (SIN_COS_PRECISION - 1)] * numberOfBlocks / 8.0f;
        double centerY = y + random.nextInt(3) - 2.0;
        double centerZ = z + 8 + COS_TABLE[MathHelper.floor_float(angle * SIN_COS_PRECISION) & (SIN_COS_PRECISION - 1)] * numberOfBlocks / 8.0f;

        optimizationsAndTweaks$generateEllipse(world, random, centerX, centerY, centerZ);

        return true;
    }

    @Unique
    private void optimizationsAndTweaks$generateEllipse(World world, Random random, double centerX, double centerY, double centerZ) {
        for (int l = 0; l <= numberOfBlocks; ++l) {
            double ellipseX = centerX - SIN_TABLE[l] * numberOfBlocks / 8.0;
            double ellipseY = centerY + random.nextDouble() * numberOfBlocks / 16.0;
            double ellipseZ = centerZ - COS_TABLE[l] * numberOfBlocks / 8.0;

            optimizationsAndTweaks$generateEllipseLayer(world, ellipseX, ellipseY, ellipseZ);
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateEllipseLayer(World world, double ellipseX, double ellipseY, double ellipseZ) {
        int minX = MathHelper.floor_double(ellipseX - 1.0);
        int maxX = MathHelper.floor_double(ellipseX + 1.0);
        int minY = MathHelper.floor_double(ellipseY - 1.0);
        int maxY = MathHelper.floor_double(ellipseY + 1.0);
        int minZ = MathHelper.floor_double(ellipseZ - 1.0);
        int maxZ = MathHelper.floor_double(ellipseZ + 1.0);

        optimizationsAndTweaks$iterateOverEllipse(world, ellipseX, ellipseY, ellipseZ, minX, maxX, minY, maxY, minZ, maxZ);
    }

    @Unique
    private void optimizationsAndTweaks$iterateOverEllipse(World world, double ellipseX, double ellipseY, double ellipseZ,
                                                           int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        Block oreGenBlock = field_150518_c;

        if (oreGenBlock != null) {
            for (int k2 = minX; k2 <= maxX; ++k2) {
                double d12 = (k2 + 0.5D - ellipseX);
                for (int l2 = minY; l2 <= maxY; ++l2) {
                    double d13 = (l2 + 0.5D - ellipseY);
                    for (int i3 = minZ; i3 <= maxZ; ++i3) {
                        double d14 = (i3 + 0.5D - ellipseZ);
                        if (optimizationsAndTweaks$isInsideEllipse(d12, d13, d14)) {
                            optimizationsAndTweaks$processBlock(world, k2, l2, i3);
                        }
                    }
                }
            }
        }
    }
    @Unique
    private void optimizationsAndTweaks$processBlock(World world, int x, int y, int z) {
        Block oreGenBlock = optimizationsAndTweaks$getField_150518_c();
        Block block = optimizationsAndTweaks$getBlock(world, x, y, z);

        if (optimizationsAndTweaks$isReplaceableOreGen(world, x, y, z, oreGenBlock, block)) {
            optimizationsAndTweaks$replaceBlock(world, x, y, z);
        }
    }

    @Unique
    private Block optimizationsAndTweaks$getField_150518_c() {
        return field_150518_c;
    }
    @Unique
    private Block optimizationsAndTweaks$getBlock(World world, int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    @Unique
    private boolean optimizationsAndTweaks$isReplaceableOreGen(World world, int x, int y, int z, Block oreGenBlock, Block block) {
        return block.isReplaceableOreGen(world, x, y, z, oreGenBlock);
    }

    @Unique
    private boolean optimizationsAndTweaks$isInsideEllipse(double d12, double d13, double d14) {
        return d12 * d12 + d13 * d13 + d14 * d14 < 1.0D;
    }

    @Unique
    private void optimizationsAndTweaks$replaceBlock(World world, int x, int y, int z) {
        Block oreGenBlock = field_150518_c;
        Block replaceBlock = field_150519_a;

        if (replaceBlock != null && oreGenBlock != null && world != null) {
            int currentBlockMeta = world.getBlockMetadata(x, y, z);

            if (world.isAirBlock(x, y, z) || world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, oreGenBlock)) {
                world.setBlock(x, y, z, replaceBlock, mineableBlockMeta, 2);
                world.setBlockMetadataWithNotify(x, y, z, currentBlockMeta, 2);
            }
        }
    }
}
