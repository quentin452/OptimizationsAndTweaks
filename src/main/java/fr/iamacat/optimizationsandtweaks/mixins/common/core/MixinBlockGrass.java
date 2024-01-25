package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Random;

import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.BlockGrass2.optimizationsAndTweaks$spreadGrass;

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
        if (!worldIn.blockExists(x, y + 1, z)) {
            return false;
        }

        return worldIn.getBlockLightValue(x, y + 1, z) < 4 && worldIn.getBlockLightOpacity(x, y + 1, z) > 2;
    }

    @Unique
    private boolean optimizationsAndTweaks$canSpreadGrass(World worldIn, int x, int y, int z) {
        if (!worldIn.blockExists(x, y + 1, z)) {
            return false;
        }

        return worldIn.getBlockLightValue(x, y + 1, z) >= 9;
    }
}
