package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Random;

import net.minecraft.block.BlockGrass;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {

    @Inject(method = "updateTick", cancellable = true, at = @At(value = "HEAD"))
    public void updateTick(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk chunk = worldIn.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return;
        }
        if (!worldIn.isRemote) {
            fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minecraft.BlockGrass
                .updateTick(worldIn, x, y, z, random, ci);
        }
        ci.cancel();
    }
}
