package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public class MixinBlock {

    @Shadow
    protected final Material blockMaterial;

    public MixinBlock(Material blockMaterial) {
        this.blockMaterial = blockMaterial;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isNormalCube() {
        return !this.canProvidePower() && this.blockMaterial.isOpaque() && this.renderAsNormalBlock();
    }

    @Shadow
    public boolean canProvidePower() {
        return false;
    }

    @Shadow
    public boolean renderAsNormalBlock() {
        return true;
    }
}
