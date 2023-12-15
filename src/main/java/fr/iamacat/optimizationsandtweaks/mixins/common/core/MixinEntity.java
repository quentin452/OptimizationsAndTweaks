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

    @Shadow
    public float getEyeHeight()
    {
        return 0.0F;
    }
}
