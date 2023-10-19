package fr.iamacat.multithreading.mixins.common.core.pathfinding;

import fr.iamacat.multithreading.Multithreaded;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.pathfinding.PathPoint2;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathPoint.class)
public class MixinPathPoint {
    // todo WIP

    /** The x coordinate of this point */
    @Shadow
    public final int xCoord;
    /** The y coordinate of this point */
    @Shadow
    public final int yCoord;
    /** The z coordinate of this point */
    @Shadow
    public final int zCoord;
    /** A hash of the coordinates used to identify this point */
    @Shadow
    private final int hash;
    /** The index of this point in its assigned path */
    @Shadow
    int index = -1;
    /** The distance along the path to this point */
    @Shadow
    public float totalPathDistance;
    /** The linear distance to the next point */
    @Shadow
    public float distanceToNext;
    /** The distance to the target */
    @Shadow
    public float distanceToTarget;
    /** The point preceding this in its assigned path */
    @Unique
    public PathPoint2 previous;
    /** Indicates this is the origin */
    @Shadow
    public boolean isFirst;

    public MixinPathPoint(int p_i2135_1_, int p_i2135_2_, int p_i2135_3_)
    {
        this.xCoord = p_i2135_1_;
        this.yCoord = p_i2135_2_;
        this.zCoord = p_i2135_3_;
        this.hash = makeHash(p_i2135_1_, p_i2135_2_, p_i2135_3_);
    }

    private static int makeHash(int p_75830_0_, int p_75830_1_, int p_75830_2_)
    {
        return p_75830_1_ & 255 | (p_75830_0_ & 32767) << 8 | (p_75830_2_ & 32767) << 24 | (p_75830_0_ < 0 ? Integer.MIN_VALUE : 0) | (p_75830_2_ < 0 ? 32768 : 0);
    }

    /**
     * Returns the linear distance to another path point
     */
  //  @Inject(method = "distanceTo", at = @At("HEAD"), cancellable = true)
    public float distanceTo(PathPoint2 p_75829_1_, CallbackInfo ci)
    {
        if (MultithreadingandtweaksConfig.enableMixinPathFinding){
        float f = (float)(p_75829_1_.xCoord - this.xCoord);
        float f1 = (float)(p_75829_1_.yCoord - this.yCoord);
        float f2 = (float)(p_75829_1_.zCoord - this.zCoord);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }
        ci.cancel();
        return 0;
    }

    /**
     * Returns the squared distance to another path point
     */
    public float distanceToSquared(PathPoint2 p_75832_1_)
    {
        float f = (float)(p_75832_1_.xCoord - this.xCoord);
        float f1 = (float)(p_75832_1_.yCoord - this.yCoord);
        float f2 = (float)(p_75832_1_.zCoord - this.zCoord);
        return f * f + f1 * f1 + f2 * f2;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (!(p_equals_1_ instanceof PathPoint2))
        {
            return false;
        }
        else
        {
            PathPoint2 pathpoint = (PathPoint2)p_equals_1_;
            return this.hash == pathpoint.hash && this.xCoord == pathpoint.xCoord && this.yCoord == pathpoint.yCoord && this.zCoord == pathpoint.zCoord;
        }
    }

    public int hashCode()
    {
        return this.hash;
    }

    /**
     * Returns true if this point has already been assigned to a path
     */
    public boolean isAssigned()
    {
        return this.index >= 0;
    }

    public String toString()
    {
        return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}
