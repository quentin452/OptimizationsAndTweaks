package fr.iamacat.multithreading.mixins.common.thaumcraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import thaumcraft.common.blocks.BlockMagicalLeaves;
import thaumcraft.common.config.ConfigBlocks;

@Mixin(BlockMagicalLeaves.class)
public class MixinPatchBlockMagicalLeavesPerformances {

    /**
     * @author imacatfr
     * @reason Reduce tps lags caused by updateTick method from BlockMagicalLeaves class
     */

    @Inject(method = "func_149674_a", at = @At("HEAD"), remap = false, cancellable = true)
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinPatchBlockMagicalLeavesPerformances) {
            int radius = 30;
            int delay = 20;

            if (!par1World.isRemote) {
                if (par1World.getTotalWorldTime() % delay == 0) {
                    for (int offX = -radius; offX <= radius; ++offX) {
                        for (int offY = -radius; offY <= radius; ++offY) {
                            for (int offZ = -radius; offZ <= radius; ++offZ) {
                                int totaldist = Math.max(Math.max(Math.abs(offX), Math.abs(offY)), Math.abs(offZ));
                                if (totaldist <= 5) {
                                    Block adjacentBlock = par1World.getBlock(par2 + offX, par3 + offY, par4 + offZ);
                                    if (adjacentBlock != null && multithreadingandtweaks$canSustainLeaves(
                                        par1World,
                                        par2 + offX,
                                        par3 + offY,
                                        par4 + offZ)) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    this.multithreadingandtweaks$removeLeaves(par1World, par2, par3, par4);

                }
            }
            ci.cancel();
        }
    }

    @Unique
    private void multithreadingandtweaks$removeLeaves(final World par1World, final int par2, final int par3,
        final int par4) {
        par1World.setBlock(par2, par3, par4, Blocks.air, 0, 2);
    }

    @Unique
    private static boolean multithreadingandtweaks$canSustainLeaves(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block == ConfigBlocks.blockMagicalLog;
    }
}
