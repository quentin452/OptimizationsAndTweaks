package fr.iamacat.optimizationsandtweaks.mixins.common.themistsofriov;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import sheenrox82.RioV.src.world.mineable.WorldGenBalance;

@Mixin(WorldGenBalance.class)
public class MixinWorldGenBalance {

    @Inject(
        method = "func_76484_a",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;func_147437_c(III)Z"
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD,
        remap = false
    )
    private void preventCascadingGen(World world, java.util.Random random, int x, int y, int z, 
                                   CallbackInfoReturnable<Boolean> cir, int l, int i1, int j1, int k1) {
        int chunkX = i1 >> 4;
        int chunkZ = k1 >> 4;
        
        if (!world.getChunkProvider().chunkExists(chunkX, chunkZ)) {
            cir.setReturnValue(true);
            return;
        }
        
        int originalChunkX = x >> 4;
        int originalChunkZ = z >> 4;
        
        if (Math.abs(chunkX - originalChunkX) > 1 || Math.abs(chunkZ - originalChunkZ) > 1) {
            cir.setReturnValue(true);
            return;
        }
    }
}