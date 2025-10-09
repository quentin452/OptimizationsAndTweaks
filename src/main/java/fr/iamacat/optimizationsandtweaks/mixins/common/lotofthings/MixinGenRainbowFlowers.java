package fr.iamacat.optimizationsandtweaks.mixins.common.lotofthings;

import com.superdextor.LOT.worldgen.GenRainbowFlowers;
import com.superdextor.LOT.init.LOTBlocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(GenRainbowFlowers.class)
public class MixinGenRainbowFlowers {

    @Shadow
    private int chance;

    @Inject(
        method = "func_76484_a",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void optimizedGen(World world, Random random, int x, int y, int z, 
                              CallbackInfoReturnable<Boolean> cir) {

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        if (!world.getChunkProvider().chunkExists(chunkX, chunkZ)) {
            cir.setReturnValue(false);
            return;
        }

        for (int i = 0; i < chance; i++) {
            int spawnX = chunkX * 16 + random.nextInt(16);
            int spawnZ = chunkZ * 16 + random.nextInt(16);
            int spawnY = world.getHeightValue(spawnX, spawnZ);
            if (LOTBlocks.rainbow_flower.canBlockStay(world, spawnX, spawnY, spawnZ)) {
                world.setBlock(spawnX, spawnY, spawnZ, LOTBlocks.rainbow_flower, 0, 2);
            }
        }

        cir.setReturnValue(true);
    }
}
