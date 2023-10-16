package fr.iamacat.multithreading.mixins.common.thaumcraft;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.WorldGenManaPods;
import thaumcraft.common.lib.world.biomes.BiomeGenMagicalForest;

import java.util.Random;

@Mixin(BiomeGenMagicalForest.class)
public abstract class MixinPatchBiomeGenMagicalForest extends BiomeGenBase {

    @Shadow
    private static final WorldGenBlockBlob blobs;

    public MixinPatchBiomeGenMagicalForest(int p_i1971_1_) {
        super(p_i1971_1_);
    }

    @Inject(method = "func_76728_a", at = @At("HEAD"), remap = false, cancellable = true)
    public void decorate(World world, Random random, int x, int z, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinPatchBiomeGenMagicalForest) {

        int k = random.nextInt(3);

        int l, i1, j1, k1;
        int a;

        for (l = 0; l < k; ++l) {
            i1 = x + random.nextInt(16) + 8;
            j1 = z + random.nextInt(16) + 8;
            k1 = world.getHeightValue(i1, j1);
            blobs.generate(world, random, i1, k1, j1);
        }

        for (k = 0; k < 4; ++k) {
            for (l = 0; l < 4; ++l) {
                i1 = x + k * 4 + 1 + 8 + random.nextInt(3);
                j1 = z + l * 4 + 1 + 8 + random.nextInt(3);
                k1 = world.getHeightValue(i1, j1);

                if (random.nextInt(40) == 0) {
                    new WorldGenBigMushroom().generate(world, random, i1, k1, j1);
                }
            }
        }

        WorldGenManaPods worldgenpods = new WorldGenManaPods();

        for (k = 0; k < 10; ++k) {
            l = x + random.nextInt(16) + 8;
            a = 64;
            i1 = z + random.nextInt(16) + 8;
            worldgenpods.func_76484_a(world, random, l, a, i1);
        }

        for (a = 0; a < 8; ++a) {
            int xx = x + random.nextInt(16);
            int zz = z + random.nextInt(16);

            int yy = world.getHeightValue(xx, zz);

            for (; yy > 50 && world.getBlock(xx, yy, zz) != Blocks.grass; --yy) {
            }

            Block l1 = world.getBlock(xx, yy, zz);

            if (l1 == Blocks.grass && world.getBlock(xx, yy + 1, zz).isReplaceable(world, xx, yy + 1, zz) && multithreadingandtweaks$isBlockAdjacentToWood(world, xx, yy + 1, zz)) {
                world.setBlock(xx, yy + 1, zz, ConfigBlocks.blockCustomPlant, 5, 2);
            }
        }
        }
        ci.cancel();
    }

    @Unique
    private boolean multithreadingandtweaks$isBlockAdjacentToWood(IBlockAccess world, int x, int y, int z) {
        for (int xx = -1; xx <= 1; ++xx) {
            for (int yy = -1; yy <= 1; ++yy) {
                for (int zz = -1; zz <= 1; ++zz) {
                    if ((xx != 0 || yy != 0 || zz != 0) && Utils.isWoodLog(world, xx + x, yy + y, zz + z)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static {
        blobs = new WorldGenBlockBlob(Blocks.mossy_cobblestone, 0);
    }
}
