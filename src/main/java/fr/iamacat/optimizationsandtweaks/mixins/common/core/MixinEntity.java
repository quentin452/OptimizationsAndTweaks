package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Unique
    private Entity entity;
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

    /**
     * @author
     * @reason
     */
    @Unique
    private String optimizationsAndTweaks$cachedEntityName = null;

    /**
     * @author
     * @reason
     */
    @Shadow
    public abstract String getEntityString();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public String getCommandSenderName() {
        if (optimizationsAndTweaks$cachedEntityName != null) {
            return optimizationsAndTweaks$cachedEntityName;
        }

        String entityName = getEntityString();
        String translatedName = (entityName != null) ?
            StatCollector.translateToLocal("entity." + entityName + ".name") :
            StatCollector.translateToLocal("entity.generic.name");

        optimizationsAndTweaks$cachedEntityName = translatedName;

        return translatedName;
    }
}
