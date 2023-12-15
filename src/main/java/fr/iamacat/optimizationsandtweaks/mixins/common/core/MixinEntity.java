package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.Classers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class MixinEntity {
    @Shadow
    public final AxisAlignedBB boundingBox;
    @Shadow
    public float yOffset;
    /** Entity position X */
    @Shadow
    public double posX;
    /** Entity position Y */
    @Shadow
    public double posY;
    /** Entity position Z */
    @Shadow
    public double posZ;
    /** Entity motion X */
    @Shadow
    public double motionX;
    /** Entity motion Y */
    @Shadow
    public double motionY;
    /** Entity motion Z */
    @Shadow
    public double motionZ;

    @Shadow
    public World worldObj;

    @Shadow
    public float width;

    public MixinEntity() {
        this.boundingBox = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    }


    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isInsideOfMaterial(Material materialIn) {
        double posYWithEyeHeight = this.posY + this.getEyeHeight();
        int posYRounded = MathHelper.floor_double(posYWithEyeHeight);
        int posXRounded = MathHelper.floor_double(this.posX);
        int posZRounded = MathHelper.floor_double(this.posZ);

        Block block = this.worldObj.getBlock(posXRounded, posYRounded, posZRounded);

        if (block.getMaterial() != materialIn) {
            return false;
        }

        if (block instanceof IFluidBlock) {
            IFluidBlock fluidBlock = (IFluidBlock) block;
            double filled = fluidBlock.getFilledPercentage(worldObj, posXRounded, posYRounded, posZRounded);
            return filled >= 0 ? posYWithEyeHeight < (posYRounded + filled) : posYWithEyeHeight > (posYRounded + (1 - filled));
        }

        return true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isEntityInsideOpaqueBlock() {
        double eyeHeight = this.getEyeHeight();
        float width = this.width * 0.8F;

        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                for (int k = -1; k <= 1; k += 2) {
                    double offsetX = i * width * 0.5F;
                    double offsetY = eyeHeight * 0.1F * j;
                    double offsetZ = k * width * 0.5F;

                    int x = MathHelper.floor_double(this.posX + offsetX);
                    int y = MathHelper.floor_double(this.posY + offsetY);
                    int z = MathHelper.floor_double(this.posZ + offsetZ);

                    if (this.worldObj.getBlock(x, y, z).isNormalCube()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public float getBrightness(float partialTicks) {
        int blockX = MathHelper.floor_double(this.posX);
        int blockZ = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(blockX, 0, blockZ)) {
            int blockY = MathHelper.floor_double(this.posY - this.yOffset + (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D);
            return this.worldObj.getBlockLightValue(blockX, blockY, blockZ);
        } else {
            return 0.0F;
        }
    }

    @Shadow
    public float getEyeHeight()
    {
        return 0.0F;
    }
}
