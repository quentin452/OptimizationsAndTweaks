package fr.iamacat.optimizationsandtweaks.mixins.common.eternalfrost.mobspawningfix;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import eternalfrost.blocks.BlockMobSpawner2;

@Mixin(BlockMobSpawner2.class)
public class MixinBlockMobSpawner2 {

    /**
     * @reason replace the tile entity with vanilla's TileEntityMobSpawner. HEAD-cancel with a computed
     *         return value instead of a full-method replace so any other transform on this method still
     *         applies.
     */
    @Inject(method = "createNewTileEntity", at = @At("HEAD"), remap = false, cancellable = true)
    private void createNewTileEntity(World world, int metadata, CallbackInfoReturnable<TileEntity> cir) {
        cir.setReturnValue(new TileEntityMobSpawner());
    }
}
