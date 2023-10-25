package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = AxisAlignedBB.class,priority = 999)
public class MixinAxisAlignedBB {
    @Unique
    private AxisAlignedBB axisAlignedBB;
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

    protected MixinAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        this.minX = x1;
        this.minY = y1;
        this.minZ = z1;
        this.maxX = x2;
        this.maxY = y2;
        this.maxZ = z2;
    }

    /**
     * Sets the bounds of the bounding box. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    @Overwrite
    public AxisAlignedBB setBounds(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        this.minX = x1;
        this.minY = y1;
        this.minZ = z1;
        this.maxX = x2;
        this.maxY = y2;
        this.maxZ = z2;
        return axisAlignedBB;
    }

    /**
     * Adds the coordinates to the bounding box extending it if the point lies outside the current ranges. Args: x, y, z
     */
    @Overwrite
    public AxisAlignedBB addCoord(double x, double y, double z)
    {
        double d3 = this.minX;
        double d4 = this.minY;
        double d5 = this.minZ;
        double d6 = this.maxX;
        double d7 = this.maxY;
        double d8 = this.maxZ;

        if (x < 0.0D)
        {
            d3 += x;
        }

        if (x > 0.0D)
        {
            d6 += x;
        }

        if (y < 0.0D)
        {
            d4 += y;
        }

        if (y > 0.0D)
        {
            d7 += y;
        }

        if (z < 0.0D)
        {
            d5 += z;
        }

        if (z > 0.0D)
        {
            d8 += z;
        }

        return AxisAlignedBB.getBoundingBox(d3, d4, d5, d6, d7, d8);
    }

    /**
     * Returns a bounding box expanded by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    @Overwrite
    public AxisAlignedBB expand(double x, double y, double z)
    {
        double d3 = this.minX - x;
        double d4 = this.minY - y;
        double d5 = this.minZ - z;
        double d6 = this.maxX + x;
        double d7 = this.maxY + y;
        double d8 = this.maxZ + z;
        return AxisAlignedBB.getBoundingBox(d3, d4, d5, d6, d7, d8);
    }

    @Overwrite
    public AxisAlignedBB func_111270_a(AxisAlignedBB other)
    {
        double d0 = Math.min(this.minX, other.minX);
        double d1 = Math.min(this.minY, other.minY);
        double d2 = Math.min(this.minZ, other.minZ);
        double d3 = Math.max(this.maxX, other.maxX);
        double d4 = Math.max(this.maxY, other.maxY);
        double d5 = Math.max(this.maxZ, other.maxZ);
        return AxisAlignedBB.getBoundingBox(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Returns a bounding box offseted by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    @Overwrite
    public AxisAlignedBB getOffsetBoundingBox(double x, double y, double z)
    {
        return AxisAlignedBB.getBoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    @Overwrite
    public double calculateXOffset(AxisAlignedBB other, double p_72316_2_)
    {
        if (other.maxY > this.minY && other.minY < this.maxY)
        {
            if (other.maxZ > this.minZ && other.minZ < this.maxZ)
            {
                double d1;

                if (p_72316_2_ > 0.0D && other.maxX <= this.minX)
                {
                    d1 = this.minX - other.maxX;

                    if (d1 < p_72316_2_)
                    {
                        p_72316_2_ = d1;
                    }
                }

                if (p_72316_2_ < 0.0D && other.minX >= this.maxX)
                {
                    d1 = this.maxX - other.minX;

                    if (d1 > p_72316_2_)
                    {
                        p_72316_2_ = d1;
                    }
                }

                return p_72316_2_;
            }
            else
            {
                return p_72316_2_;
            }
        }
        else
        {
            return p_72316_2_;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    @Overwrite
    public double calculateYOffset(AxisAlignedBB other, double p_72323_2_)
    {
        if (other.maxX > this.minX && other.minX < this.maxX)
        {
            if (other.maxZ > this.minZ && other.minZ < this.maxZ)
            {
                double d1;

                if (p_72323_2_ > 0.0D && other.maxY <= this.minY)
                {
                    d1 = this.minY - other.maxY;

                    if (d1 < p_72323_2_)
                    {
                        p_72323_2_ = d1;
                    }
                }

                if (p_72323_2_ < 0.0D && other.minY >= this.maxY)
                {
                    d1 = this.maxY - other.minY;

                    if (d1 > p_72323_2_)
                    {
                        p_72323_2_ = d1;
                    }
                }

                return p_72323_2_;
            }
            else
            {
                return p_72323_2_;
            }
        }
        else
        {
            return p_72323_2_;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    @Overwrite
    public double calculateZOffset(AxisAlignedBB other, double p_72322_2_)
    {
        if (other.maxX > this.minX && other.minX < this.maxX)
        {
            if (other.maxY > this.minY && other.minY < this.maxY)
            {
                double d1;

                if (p_72322_2_ > 0.0D && other.maxZ <= this.minZ)
                {
                    d1 = this.minZ - other.maxZ;

                    if (d1 < p_72322_2_)
                    {
                        p_72322_2_ = d1;
                    }
                }

                if (p_72322_2_ < 0.0D && other.minZ >= this.maxZ)
                {
                    d1 = this.maxZ - other.minZ;

                    if (d1 > p_72322_2_)
                    {
                        p_72322_2_ = d1;
                    }
                }

                return p_72322_2_;
            }
            else
            {
                return p_72322_2_;
            }
        }
        else
        {
            return p_72322_2_;
        }
    }

    /**
     * Returns whether the given bounding box intersects with this one. Args: axisAlignedBB
     */
    @Overwrite
    public boolean intersectsWith(AxisAlignedBB other)
    {
        return other.maxX > this.minX && other.minX < this.maxX ? (other.maxY > this.minY && other.minY < this.maxY ? other.maxZ > this.minZ && other.minZ < this.maxZ : false) : false;
    }

    /**
     * Offsets the current bounding box by the specified coordinates. Args: x, y, z
     */
    @Overwrite
    public AxisAlignedBB offset(double x, double y, double z)
    {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
        return axisAlignedBB;
    }

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    @Overwrite
    public boolean isVecInside(Vec3 vec)
    {
        return vec.xCoord > this.minX && vec.xCoord < this.maxX ? (vec.yCoord > this.minY && vec.yCoord < this.maxY ? vec.zCoord > this.minZ && vec.zCoord < this.maxZ : false) : false;
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    @Overwrite
    public double getAverageEdgeLength()
    {
        double d0 = this.maxX - this.minX;
        double d1 = this.maxY - this.minY;
        double d2 = this.maxZ - this.minZ;
        return (d0 + d1 + d2) / 3.0D;
    }

    /**
     * Returns a bounding box that is inset by the specified amounts
     */
    @Overwrite
    public AxisAlignedBB contract(double x, double y, double z)
    {
        double d3 = this.minX + x;
        double d4 = this.minY + y;
        double d5 = this.minZ + z;
        double d6 = this.maxX - x;
        double d7 = this.maxY - y;
        double d8 = this.maxZ - z;
        return AxisAlignedBB.getBoundingBox(d3, d4, d5, d6, d7, d8);
    }

    /**
     * Returns a copy of the bounding box.
     */
    @Overwrite
    public AxisAlignedBB copy()
    {
        return AxisAlignedBB.getBoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public MovingObjectPosition calculateIntercept(Vec3 p_72327_1_, Vec3 p_72327_2_)
    {
        Vec3 vec32 = p_72327_1_.getIntermediateWithXValue(p_72327_2_, this.minX);
        Vec3 vec33 = p_72327_1_.getIntermediateWithXValue(p_72327_2_, this.maxX);
        Vec3 vec34 = p_72327_1_.getIntermediateWithYValue(p_72327_2_, this.minY);
        Vec3 vec35 = p_72327_1_.getIntermediateWithYValue(p_72327_2_, this.maxY);
        Vec3 vec36 = p_72327_1_.getIntermediateWithZValue(p_72327_2_, this.minZ);
        Vec3 vec37 = p_72327_1_.getIntermediateWithZValue(p_72327_2_, this.maxZ);

        if (!this.isVecInYZ(vec32))
        {
            vec32 = null;
        }

        if (!this.isVecInYZ(vec33))
        {
            vec33 = null;
        }

        if (!this.isVecInXZ(vec34))
        {
            vec34 = null;
        }

        if (!this.isVecInXZ(vec35))
        {
            vec35 = null;
        }

        if (!this.isVecInXY(vec36))
        {
            vec36 = null;
        }

        if (!this.isVecInXY(vec37))
        {
            vec37 = null;
        }

        Vec3 vec38 = null;

        if (vec32 != null && (vec38 == null || p_72327_1_.squareDistanceTo(vec32) < p_72327_1_.squareDistanceTo(vec38)))
        {
            vec38 = vec32;
        }

        if (vec33 != null && (vec38 == null || p_72327_1_.squareDistanceTo(vec33) < p_72327_1_.squareDistanceTo(vec38)))
        {
            vec38 = vec33;
        }

        if (vec34 != null && (vec38 == null || p_72327_1_.squareDistanceTo(vec34) < p_72327_1_.squareDistanceTo(vec38)))
        {
            vec38 = vec34;
        }

        if (vec35 != null && (vec38 == null || p_72327_1_.squareDistanceTo(vec35) < p_72327_1_.squareDistanceTo(vec38)))
        {
            vec38 = vec35;
        }

        if (vec36 != null && (vec38 == null || p_72327_1_.squareDistanceTo(vec36) < p_72327_1_.squareDistanceTo(vec38)))
        {
            vec38 = vec36;
        }

        if (vec37 != null && (vec38 == null || p_72327_1_.squareDistanceTo(vec37) < p_72327_1_.squareDistanceTo(vec38)))
        {
            vec38 = vec37;
        }

        if (vec38 == null)
        {
            return null;
        }
        else
        {
            byte b0 = -1;

            if (vec38 == vec32)
            {
                b0 = 4;
            }

            if (vec38 == vec33)
            {
                b0 = 5;
            }

            if (vec38 == vec34)
            {
                b0 = 0;
            }

            if (vec38 == vec35)
            {
                b0 = 1;
            }

            if (vec38 == vec36)
            {
                b0 = 2;
            }

            if (vec38 == vec37)
            {
                b0 = 3;
            }

            return new MovingObjectPosition(0, 0, 0, b0, vec38);
        }
    }

    /**
     * Checks if the specified vector is within the YZ dimensions of the bounding box. Args: Vec3D
     */
    @Overwrite
    private boolean isVecInYZ(Vec3 vec)
    {
        return vec == null ? false : vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    /**
     * Checks if the specified vector is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    @Overwrite
    private boolean isVecInXZ(Vec3 vec)
    {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    /**
     * Checks if the specified vector is within the XY dimensions of the bounding box. Args: Vec3D
     */
    @Overwrite
    private boolean isVecInXY(Vec3 vec)
    {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
    }

    /**
     * Sets the bounding box to the same bounds as the bounding box passed in. Args: axisAlignedBB
     */
    @Overwrite
    public void setBB(AxisAlignedBB other)
    {
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
    public String toString()
    {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
}
