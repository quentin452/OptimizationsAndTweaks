package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.wands.IWandable;

@Mixin(TileNode.class)
public abstract class MixinTileNode extends TileThaumcraft implements INode, IWandable {

    @Inject(
        method = "handleDarkNode",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void optimizationsandtweaks_fixNullBiome(boolean change, CallbackInfoReturnable<Boolean> cir) {
        try {
            TileNode self = (TileNode) (Object) this;
            BiomeGenBase bg = self.getWorldObj().getBiomeGenForCoords(self.xCoord, self.zCoord);
            if (bg == null) {
                cir.setReturnValue(change);
                return;
            }
        } catch (Throwable ignored) {
            cir.setReturnValue(change);
        }
    }
}

