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

    /**
     * @author iamacatfr
     * @reason reducing tps lags during worldgen
     */
    @Overwrite
    public boolean generate(World world, Random random, int x, int y, int z) {
        float f = random.nextFloat() * (float) Math.PI;
        double d0 = x + 8 + MathHelper.sin(f) * numberOfBlocks / 8.0f;
        double d1 = x + 8 - MathHelper.sin(f) * numberOfBlocks / 8.0f;
        double d2 = z + 8 + MathHelper.cos(f) * numberOfBlocks / 8.0f;
        double d3 = z + 8 - MathHelper.cos(f) * numberOfBlocks / 8.0f;
        double d4 = (double) y + random.nextInt(3) - 2;
        double d5 = (double) y + random.nextInt(3) - 2;

        for (int l = 0; l <= numberOfBlocks; ++l) {
            double d6 = d0 + (d1 - d0) * l / numberOfBlocks;
            double d7 = d4 + (d5 - d4) * l / numberOfBlocks;
            double d8 = d2 + (d3 - d2) * l / numberOfBlocks;
            double d9 = random.nextDouble() * numberOfBlocks / 16.0D;
            double d10 = (MathHelper.sin(l * (float) Math.PI / numberOfBlocks) + 1.0f) * d9 + 1.0D;
            double d11 = (MathHelper.sin(l * (float) Math.PI / numberOfBlocks) + 1.0f) * d9 + 1.0D;

            int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
            int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
            int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
            int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
            int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
            int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

            for (int k2 = i1; k2 <= l1; ++k2) {
                double d12 = (k2 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D) {
                    for (int l2 = j1; l2 <= i2; ++l2) {
                        double d13 = (l2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int i3 = k1; i3 <= j2; ++i3) {
                                double d14 = (i3 + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    Block block = world.getBlock(k2, l2, i3);
                                    if (block.isReplaceableOreGen(world, k2, l2, i3, field_150518_c)) {
                                        world.setBlock(k2, l2, i3, field_150519_a, mineableBlockMeta, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
