package fr.iamacat.optimizationsandtweaks.mixins.common.core.pathfinding;

import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PathPoint.class)
public class MixinPathPoint {

    @Shadow
    public final int xCoord;

    @Shadow
    public final int yCoord;

    @Shadow
    public final int zCoord;

    @Shadow
    public final int hash;

    @Shadow
    public int index = -1;

    @Shadow
    public float totalPathDistance;

    @Shadow
    public float distanceToNext;

    @Shadow
    public float distanceToTarget;

    @Shadow
    public PathPoint previous;

    @Shadow
    public boolean isFirst;
   public MixinPathPoint(int x, int y, int z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.hash = makeHash(x, y, z);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static int makeHash(int p_75830_0_, int p_75830_1_, int p_75830_2_) {
        int xShort = p_75830_0_ & 32767;
        int yByte = p_75830_1_ & 255;
        int zShort = p_75830_2_ & 32767;
        int xFlag = (p_75830_0_ < 0) ? Integer.MIN_VALUE : 0;
        int zFlag = (p_75830_2_ < 0) ? 32768 : 0;
        return yByte | (xShort << 8) | (zShort << 24) | xFlag | zFlag;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public float distanceTo(PathPoint p_75829_1_) {
        float dx = (float) (p_75829_1_.xCoord - this.xCoord);
        float dy = (float) (p_75829_1_.yCoord - this.yCoord);
        float dz = (float) (p_75829_1_.zCoord - this.zCoord);
        return MathHelper.sqrt_float(dx * dx + dy * dy + dz * dz);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public float distanceToSquared(PathPoint p_75832_1_) {
        float dx = (float) (p_75832_1_.xCoord - this.xCoord);
        float dy = (float) (p_75832_1_.yCoord - this.yCoord);
        float dz = (float) (p_75832_1_.zCoord - this.zCoord);
        return dx * dx + dy * dy + dz * dz;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) return true;
        if (!(p_equals_1_ instanceof PathPoint)) return false;
        PathPoint other = (PathPoint) p_equals_1_;
        return this.xCoord == other.xCoord && this.yCoord == other.yCoord && this.zCoord == other.zCoord;
    }

    public int hashCode() {
        return this.hash;
    }

    /**
     * Returns true if this point has already been assigned to a path
     */
    public boolean isAssigned() {
        return this.index >= 0;
    }

    public String toString() {
        return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}
