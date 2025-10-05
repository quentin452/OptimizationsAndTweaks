package fr.iamacat.optimizationsandtweaks.mixins.common.eternalfrost.classcastexceptionfix;

import eternalfrost.tileentity.TileEntitySnowbarkChestRenderer;
import eternalfrost.tileentity.TileEntitySnowbarkChest;
import eternalfrost.blocks.BlockSnowbarkChest;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntitySnowbarkChestRenderer.class)
public class MixinTileEntitySnowbarkChestRenderer {

    @Inject(method = "renderTileEntityChestAt", at = @At("HEAD"), cancellable = true, remap = false)
    private void fixInvalidBlockCast(TileEntitySnowbarkChest chest, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        if (chest == null || !chest.hasWorldObj()) return;

        Block block = chest.getBlockType();

        if (!(block instanceof BlockSnowbarkChest)) {
            ci.cancel();
        }
    }
}
