package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import fr.iamacat.optimizationsandtweaks.utils.apache.commons.math3.util.FastMath;

@Mixin(value = AxisAlignedBB.class, priority = 999)
public class MixinAxisAlignedBB {

    @Shadow
    public double minX;
    @Shadow
    public double minY;
    @Shadow
    public double minZ;
    @Shadow
    public double maxX;
    @Shadow
    public double maxY;
    @Shadow
    public double maxZ;

    protected MixinAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = x1;
        this.minY = y1;
        this.minZ = z1;
        this.maxX = x2;
        this.maxY = y2;
        this.maxZ = z2;
    }

    /**
     * Adds the coordinates to the bounding box extending it if the point lies outside the current ranges. Args: x, y, z
     */
    @Overwrite
    public AxisAlignedBB addCoord(double x, double y, double z) {
        double d3 = this.minX + (FastMath.min(x, 0.0D));
        double d4 = this.minY + (FastMath.min(y, 0.0D));
        double d5 = this.minZ + (FastMath.min(z, 0.0D));
        double d6 = this.maxX + (FastMath.max(x, 0.0D));
        double d7 = this.maxY + (FastMath.max(y, 0.0D));
        double d8 = this.maxZ + (FastMath.max(z, 0.0D));
        return AxisAlignedBB.getBoundingBox(d3, d4, d5, d6, d7, d8);
    }

    /**
     * Returns a bounding box expanded by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    @Overwrite
    public AxisAlignedBB expand(double x, double y, double z) {
        return AxisAlignedBB
            .getBoundingBox(this.minX - x, this.minY - y, this.minZ - z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public AxisAlignedBB func_111270_a(AxisAlignedBB other) {
        double d0 = FastMath.min(this.minX, other.minX);
        double d1 = FastMath.min(this.minY, other.minY);
        double d2 = FastMath.min(this.minZ, other.minZ);
        double d3 = FastMath.max(this.maxX, other.maxX);
        double d4 = FastMath.max(this.maxY, other.maxY);
        double d5 = FastMath.max(this.maxZ, other.maxZ);
        return AxisAlignedBB.getBoundingBox(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Returns a bounding box offseted by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    @Overwrite
    public AxisAlignedBB getOffsetBoundingBox(double x, double y, double z) {
        return AxisAlignedBB
            .getBoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension. return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset. Otherwise return the calculated offset.
     */
    @Overwrite
    public double calculateXOffset(AxisAlignedBB other, double p_72316_2_) {
        if (other.maxY <= this.minY || other.minY >= this.maxY || other.maxZ <= this.minZ || other.minZ >= this.maxZ) {
            return p_72316_2_;
        }

        if (p_72316_2_ > 0.0D && other.maxX <= this.minX) {
            double d1 = this.minX - other.maxX;
            if (d1 < p_72316_2_) {
                p_72316_2_ = d1;
            }
        }

        if (p_72316_2_ < 0.0D && other.minX >= this.maxX) {
            double d1 = this.maxX - other.minX;
            if (d1 > p_72316_2_) {
                p_72316_2_ = d1;
            }
        }

        return p_72316_2_;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public double calculateYOffset(AxisAlignedBB other, double p_72323_2_) {
        if (other.maxX <= this.minX || other.minX >= this.maxX || other.maxZ <= this.minZ || other.minZ >= this.maxZ) {
            return p_72323_2_;
        }

        if (p_72323_2_ > 0.0D && other.maxY <= this.minY) {
            double d1 = this.minY - other.maxY;
            if (d1 < p_72323_2_) {
                p_72323_2_ = d1;
            }
        }

        if (p_72323_2_ < 0.0D && other.minY >= this.maxY) {
            double d1 = this.maxY - other.minY;
            if (d1 > p_72323_2_) {
                p_72323_2_ = d1;
            }
        }

        return p_72323_2_;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public double calculateZOffset(AxisAlignedBB other, double p_72322_2_) {
        if (other.maxX <= this.minX || other.minX >= this.maxX || other.maxY <= this.minY || other.minY >= this.maxY) {
            return p_72322_2_;
        }

        if (p_72322_2_ > 0.0D) {
            double d1 = this.minZ - other.maxZ;
            if (d1 < p_72322_2_) {
                p_72322_2_ = d1;
            }
        }

        if (p_72322_2_ < 0.0D) {
            double d1 = this.maxZ - other.minZ;
            if (d1 > p_72322_2_) {
                p_72322_2_ = d1;
            }
        }

        return p_72322_2_;
    }

    /**
     * Returns whether the given bounding box intersects with this one. Args: axisAlignedBB
     */
    @Overwrite
    public boolean intersectsWith(AxisAlignedBB other) {
        return this.maxX > other.minX && this.minX < other.maxX
            && this.maxY > other.minY && this.minY < other.maxY
            && this.maxZ > other.minZ && this.minZ < other.maxZ;
    }

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    @Overwrite
    public boolean isVecInside(Vec3 vec) {
        return (vec.xCoord > this.minX && vec.xCoord < this.maxX) && (vec.yCoord > this.minY && vec.yCoord < this.maxY)
            && (vec.zCoord > this.minZ && vec.zCoord < this.maxZ);
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    @Overwrite
    public double getAverageEdgeLength() {
        return (this.maxX - this.minX + this.maxY - this.minY + this.maxZ - this.minZ) / 3.0D;
    }

    /**
     * Returns a bounding box that is inset by the specified amounts
     */
    @Overwrite
    public AxisAlignedBB contract(double x, double y, double z) {
        return AxisAlignedBB
            .getBoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX - x, this.maxY - y, this.maxZ - z);
    }

    /**
     * Returns a copy of the bounding box.
     */
    @Overwrite
    public AxisAlignedBB copy() {
        return AxisAlignedBB.getBoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public MovingObjectPosition calculateIntercept(Vec3 p_72327_1_, Vec3 p_72327_2_) {
        double minX = (this.minX - p_72327_1_.xCoord) / (p_72327_2_.xCoord - p_72327_1_.xCoord);
        double maxX = (this.maxX - p_72327_1_.xCoord) / (p_72327_2_.xCoord - p_72327_1_.xCoord);

        double minY = (this.minY - p_72327_1_.yCoord) / (p_72327_2_.yCoord - p_72327_1_.yCoord);
        double maxY = (this.maxY - p_72327_1_.yCoord) / (p_72327_2_.yCoord - p_72327_1_.yCoord);

        double minZ = (this.minZ - p_72327_1_.zCoord) / (p_72327_2_.zCoord - p_72327_1_.zCoord);
        double maxZ = (this.maxZ - p_72327_1_.zCoord) / (p_72327_2_.zCoord - p_72327_1_.zCoord);

        double tMin = Math.max(Math.max(Math.min(minX, maxX), Math.min(minY, maxY)), Math.min(minZ, maxZ));
        double tMax = Math.min(Math.min(Math.max(minX, maxX), Math.max(minY, maxY)), Math.max(minZ, maxZ));

        if (tMax < 0.0 || tMin > tMax) {
            return null;
        }

        Vec3 hit = p_72327_1_.addVector(tMin * (p_72327_2_.xCoord - p_72327_1_.xCoord), tMin * (p_72327_2_.yCoord - p_72327_1_.yCoord), tMin * (p_72327_2_.zCoord - p_72327_1_.zCoord));

        int sideHit = -1;
        if (tMin == minX) {
            sideHit = 4;
        }
        if (tMin == maxX) {
            sideHit = 5;
        }
        if (tMin == minY) {
            sideHit = 0;
        }
        if (tMin == maxY) {
            sideHit = 1;
        }
        if (tMin == minZ) {
            sideHit = 2;
        }
        if (tMin == maxZ) {
            sideHit = 3;
        }

        return new MovingObjectPosition(0, 0, 0, sideHit, hit);
    }

    /**
     * Checks if the specified vector is within the YZ dimensions of the bounding box. Args: Vec3D
     */
    @Overwrite
    private boolean isVecInYZ(Vec3 vec) {
        if (vec == null) {
            return false;
        }

        if (vec.yCoord < this.minY || vec.yCoord > this.maxY) {
            return false;
        }

        return vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }
    /**
     * Checks if the specified vector is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    @Overwrite
    private boolean isVecInXZ(Vec3 vec) {
        if (vec == null) {
            return false;
        }

        if (vec.xCoord < this.minX || vec.xCoord > this.maxX) {
            return false;
        }

        return vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private boolean isVecInXY(Vec3 vec) {
        if (vec == null) {
            return false;
        }

        if (vec.xCoord < this.minX || vec.xCoord > this.maxX) {
            return false;
        }

        return !(vec.yCoord < this.minY) && !(vec.yCoord > this.maxY);
    }

    /**
     * Sets the bounding box to the same bounds as the bounding box passed in. Args: axisAlignedBB
     */
    @Overwrite
    public void setBB(AxisAlignedBB other) {
        this.minX = other.minX;
        this.minY = other.minY;
        this.minZ = other.minZ;
        this.maxX = other.maxX;
        this.maxY = other.maxY;
        this.maxZ = other.maxZ;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public String toString() {
        return "box[" + this.minX + ", " +
            this.minY + ", " +
            this.minZ + " -> " +
            this.maxX + ", " +
            this.maxY + ", " +
            this.maxZ + "]";
    }
}
