package fr.iamacat.optimizationsandtweaks.mixins.common.structpro;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ternsip.structpro.universe.blocks.Classifier;
import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.blocks.UBlockState;
import com.ternsip.structpro.universe.blocks.UBlocks;
import com.ternsip.structpro.universe.world.UWorld;

@Mixin(UWorld.class)
public class MixinUWorld {

    @Shadow
    private World world;

    @Inject(method = "decorate", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsAndTweaks$preventCascadingDecorate(int chunkX, int chunkZ, CallbackInfo ci) {
        if (this.world == null) {
            ci.cancel();
            return;
        }

        if (!isChunkLoaded(this.world, chunkX, chunkZ) || !isChunkLoaded(this.world, chunkX + 1, chunkZ)
            || !isChunkLoaded(this.world, chunkX, chunkZ + 1)
            || !isChunkLoaded(this.world, chunkX + 1, chunkZ + 1)) {
            ci.cancel();
        }
    }

    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsAndTweaks$preventCascadingGetBlockState(UBlockPos pos,
        CallbackInfoReturnable<UBlockState> cir) {
        if (this.world == null) {
            cir.setReturnValue(UBlocks.AIR.getState());
            return;
        }

        if (!isChunkLoaded(this.world, pos.getX() >> 4, pos.getZ() >> 4)) {
            cir.setReturnValue(UBlocks.AIR.getState());
        }
    }

    @Inject(method = "getHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsAndTweaks$preventCascadingGetHeight(Classifier classifier, int x, int z,
        CallbackInfoReturnable<Integer> cir) {
        if (this.world == null) {
            cir.setReturnValue(64);
            return;
        }

        if (!isChunkLoaded(this.world, x >> 4, z >> 4)) {
            String dimName = ((UWorld) (Object) this).getDimensionName();
            int safeHeight = 64;
            if (dimName.equalsIgnoreCase("Nether")) {
                safeHeight = 32;
            } else if (dimName.equalsIgnoreCase("End")) {
                safeHeight = 60;
            }
            cir.setReturnValue(safeHeight);
        }
    }

    @Inject(method = "setBlockState", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsAndTweaks$preventCascadingSetBlockState(UBlockPos pos, UBlockState state,
        CallbackInfo ci) {
        if (this.world == null) {
            ci.cancel();
            return;
        }

        if (!isChunkLoaded(this.world, pos.getX() >> 4, pos.getZ() >> 4)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean isChunkLoaded(World world, int chunkX, int chunkZ) {
        if (!world.getChunkProvider()
            .chunkExists(chunkX, chunkZ)) {
            return false;
        }

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        return chunk != null && chunk.isChunkLoaded;
    }

    @Inject(method = "getStorage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void optimizationsAndTweaks$safeGetStorage(Chunk chunk, int y, CallbackInfoReturnable<Object> cir) {
        if (chunk == null || !chunk.isChunkLoaded) {
            cir.setReturnValue(null);
        }

        ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
        if (y < 0 || y >= 256) {
            cir.setReturnValue(null);
            return;
        }

        int i = y >> 4;
        if (storage[i] == null) {
            cir.setReturnValue(null);
        }
    }
}
