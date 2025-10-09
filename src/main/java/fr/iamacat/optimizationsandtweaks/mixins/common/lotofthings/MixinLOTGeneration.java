package fr.iamacat.optimizationsandtweaks.mixins.common.lotofthings;

import com.superdextor.LOT.LOTGeneration;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LOTGeneration.class)
public class MixinLOTGeneration {

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGenerate(Random random, int chunkX, int chunkZ, World world, 
                           IChunkProvider chunkGenerator, IChunkProvider chunkProvider, 
                           CallbackInfo ci) {
        if (world.isRemote || !world.getChunkProvider().chunkExists(chunkX, chunkZ)) {
            ci.cancel();
            return;
        }
    }
}
