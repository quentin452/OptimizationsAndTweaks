package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {

    @Inject(method = "updateTick", cancellable = true, at = @At(value = "HEAD"))
    public void updateTick(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!worldIn.isRemote) {
            fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minecraft.BlockGrass.updateTick(worldIn, x, y, z, random, ci);
        }
        ci.cancel();
    }
}
