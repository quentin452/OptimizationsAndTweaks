package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public class MixinBlock {
    @Shadow
    protected double minX;
    @Shadow
    protected double minY;
    @Shadow
    protected double minZ;
    @Shadow
    protected double maxX;
    @Shadow
    protected double maxY;
    @Shadow
    protected double maxZ;

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
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider) {
        AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
        if (axisalignedbb1 != null && mask.intersectsWith(axisalignedbb1)) {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(axisalignedbb1);
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
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
