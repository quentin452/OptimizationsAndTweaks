package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.UP;

@Mixin(Block.class)
public class MixinBlock {

    @Unique
    private Block block;
    @Shadow
    protected boolean opaque;

    @Shadow
    protected final Material blockMaterial;

    public MixinBlock(Block block, Material blockMaterial) {
        this.block = block;
        this.blockMaterial = blockMaterial;
    }

    @Unique
    public boolean func_149730_j()
    {
        return this.opaque;
    }

    @Inject(method = "isSideSolid", at = @At("HEAD"), remap = false, cancellable = true)
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side, CallbackInfoReturnable<Boolean> cir) {
        if (!MultithreadingandtweaksConfig.enableMixinBlock) {
            return false;
        }

        int meta = world.getBlockMetadata(x, y, z);
        boolean isSolid = false;

        if (block instanceof BlockSlab) {
            isSolid = ((meta & 8) == 8 && side == UP) || func_149730_j();
        } else if (block instanceof BlockFarmland) {
            isSolid = side != DOWN && side != UP;
        } else if (block instanceof BlockStairs) {
            boolean flipped = (meta & 4) != 0;
            isSolid = (meta & 3) + side.ordinal() == 5 || (side == UP && flipped);
        } else if (block instanceof BlockSnow) {
            isSolid = (meta & 7) == 7;
        } else if (block instanceof BlockHopper) {
            if (side == UP) {
                isSolid = true;
            }
        } else if (block instanceof BlockCompressedPowered) {
            isSolid = true;
        } else {
            isSolid = isNormalCube(world, x, y, z);
        }
        cir.setReturnValue(isSolid);
        cir.setReturnValue(false);
        return true;
    }

    @Unique
    public boolean isNormalCube()
    {
        return this.blockMaterial.isOpaque() && this.renderAsNormalBlock() && !this.canProvidePower();
    }

    @Unique
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z)
    {
        return getMaterial().isOpaque() && renderAsNormalBlock() && !canProvidePower();
    }
    @Unique
    public boolean renderAsNormalBlock()
    {
        return true;
    }
    @Unique
    public Material getMaterial()
    {
        return this.blockMaterial;
    }
    @Unique
    public boolean canProvidePower()
    {
        return false;
    }
}
