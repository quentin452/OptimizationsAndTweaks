package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    protected DataWatcher dataWatcher;
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
    /** Entity motion Y */
    @Shadow
    public double motionY;
    /** Entity motion Z */

    @Shadow
    public World worldObj;

    @Shadow
    public float width;

    public MixinEntity() {
        this.boundingBox = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    @Unique
    private String optimizationsAndTweaks$cachedEntityName = null;
    
    @Unique
    private boolean optimizationsAndTweaks$cachedRenderRange = false;
    @Unique
    private double optimizationsAndTweaks$lastCacheX = Double.MAX_VALUE;
    @Unique
    private double optimizationsAndTweaks$lastCacheY = Double.MAX_VALUE;
    @Unique
    private double optimizationsAndTweaks$lastCacheZ = Double.MAX_VALUE;
    @Unique
    private long optimizationsAndTweaks$lastCacheTime = 0;
    @Unique
    private static final int optimizationsAndTweaks$CACHE_INTERVAL_MS = 50; // Cache for 50ms

    @Shadow
    public abstract String getEntityString();
    
    @Shadow
    public double renderDistanceWeight;

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
        String translatedName = (entityName != null) ? StatCollector.translateToLocal("entity." + entityName + ".name")
            : StatCollector.translateToLocal("entity.generic.name");

        optimizationsAndTweaks$cachedEntityName = translatedName;

        return translatedName;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void setFlag(int flag, boolean set) {
        byte data = this.dataWatcher.getWatchableObjectByte(0);
        data = set ? (byte) (data | (1 << flag & 0xff)) : (byte) (data & ~(1 << flag & 0xff));
        this.dataWatcher.updateObject(0, data);
    }

    @Overwrite
    public float getBrightness(float p_70013_1_) {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posZ);
        if (this.worldObj.blockExists(i, 0, j)) {
            double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            int k = MathHelper.floor_double(this.posY - this.yOffset + d0);
            return this.worldObj.getLightBrightness(i, k, j);
        }
        return 0.0F;
    }

    /**
     * Optimized version of isInRangeToRender3d with caching to reduce lag
     * @reason Massive performance improvement by caching render distance calculations
     */
    @Overwrite
    public boolean isInRangeToRender3d(double x, double y, double z) {
        long currentTime = System.currentTimeMillis();
        
        // Check if cache is still valid (position hasn't changed much and time hasn't expired)
        boolean positionChanged = Math.abs(this.optimizationsAndTweaks$lastCacheX - x) > 1.0D ||
                                 Math.abs(this.optimizationsAndTweaks$lastCacheY - y) > 1.0D ||
                                 Math.abs(this.optimizationsAndTweaks$lastCacheZ - z) > 1.0D;
        
        boolean cacheExpired = (currentTime - this.optimizationsAndTweaks$lastCacheTime) > optimizationsAndTweaks$CACHE_INTERVAL_MS;
        
        if (!positionChanged && !cacheExpired) {
            return this.optimizationsAndTweaks$cachedRenderRange;
        }
        
        // Recalculate render range
        double distanceX = this.posX - x;
        double distanceY = this.posY - y;
        double distanceZ = this.posZ - z;
        double distanceSq = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
        
        // Use a more efficient calculation - average edge length approximation
        double averageEdge = (this.width + this.width) * 0.5D; // Use width as approximation
        double renderDistance = averageEdge * 64.0D * this.renderDistanceWeight;
        
        boolean inRange = distanceSq < renderDistance * renderDistance;
        
        this.optimizationsAndTweaks$cachedRenderRange = inRange;
        this.optimizationsAndTweaks$lastCacheX = x;
        this.optimizationsAndTweaks$lastCacheY = y;
        this.optimizationsAndTweaks$lastCacheZ = z;
        this.optimizationsAndTweaks$lastCacheTime = currentTime;
        return inRange;
    }
}
