package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {
    /**
     * @author
     * @reason
     */
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    public void updateTick(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        if (worldIn.isRemote) {
            return;
        }

        if (optimizationsAndTweaks$isGrassTurningToDirt(worldIn, x, y, z)) {
            worldIn.setBlock(x, y, z, Blocks.dirt);
        } else if (optimizationsAndTweaks$canSpreadGrass(worldIn, x, y, z)) {
            optimizationsAndTweaks$spreadGrass(worldIn, x, y, z, random);
        }
        ci.cancel();
    }

    @Unique
    private boolean optimizationsAndTweaks$isGrassTurningToDirt(World worldIn, int x, int y, int z) {
        return worldIn.getBlockLightValue(x, y + 1, z) < 4 && worldIn.getBlockLightOpacity(x, y + 1, z) > 2;
    }

    @Unique
    private boolean optimizationsAndTweaks$canSpreadGrass(World worldIn, int x, int y, int z) {
        return worldIn.getBlockLightValue(x, y + 1, z) >= 9;
    }

    @Unique
    private void optimizationsAndTweaks$spreadGrass(World worldIn, int x, int y, int z, Random random) {
        for (int l = 0; l < 4; ++l) {
            int i1 = x + random.nextInt(3) - 1;
            int j1 = y + random.nextInt(5) - 3;
            int k1 = z + random.nextInt(3) - 1;

            if (worldIn.getBlock(i1, j1, k1) == Blocks.dirt
                && worldIn.getBlockMetadata(i1, j1, k1) == 0
                && worldIn.getBlockLightValue(i1, j1 + 1, k1) >= 4
                && worldIn.getBlockLightOpacity(i1, j1 + 1, k1) <= 2) {
                worldIn.setBlock(i1, j1, k1, Blocks.grass);
            }
        }
    }
}
