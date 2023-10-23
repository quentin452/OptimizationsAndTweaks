package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.config.MultithreadingandtweaksConfig;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.Utils;
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
    @Inject(method = "func_76728_a", at = @At("HEAD"), remap = false, cancellable = true)
    public void decorate(World world, Random random, int x, int z, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinPatchBiomeGenMagicalForest) {
            for (int i = 0; i < 3; ++i) {
                int posX = x + random.nextInt(16) + 8;
                int posZ = z + random.nextInt(16) + 8;
                int posY = world.getHeightValue(posX, posZ);
                blobs.generate(world, random, posX, posY, posZ);
            }

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

            WorldGenManaPods worldgenpods = new WorldGenManaPods();
            for (int i = 0; i < 10; ++i) {
                int posX = x + random.nextInt(16) + 8;
                int posY = 64;
                int posZ = z + random.nextInt(16) + 8;
                worldgenpods.func_76484_a(world, random, posX, posY, posZ);
            }

            for (int i = 0; i < 8; ++i) {
                int posX = x + random.nextInt(16);
                int posZ = z + random.nextInt(16);
                int posY = world.getHeightValue(posX, posZ);
                for (; posY > 50 && world.getBlock(posX, posY, posZ) != Blocks.grass; --posY) {}

                Block block = world.getBlock(posX, posY, posZ);
                if (block == Blocks.grass && world.getBlock(posX, posY + 1, posZ)
                    .isReplaceable(world, posX, posY + 1, posZ)
                    && multithreadingandtweaks$isBlockAdjacentToWood(world, posX, posY + 1, posZ)) {
                    world.setBlock(posX, posY + 1, posZ, ConfigBlocks.blockCustomPlant, 5, 2);
                }
            }

            super.decorate(world, random, x, z);
            ci.cancel();
        }

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
