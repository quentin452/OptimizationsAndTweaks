package fr.iamacat.optimizationsandtweaks.utilsformods.entity.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint2 {

    public final int xCoord;
    public final int yCoord;
    public final int zCoord;
    public final int hash;
    public int index = -1;
    public float totalPathDistance;
    public float distanceToNext;
    public float distanceToTarget;
    public PathPoint2 previous;
    public boolean isFirst;

    public PathPoint2(int x, int y, int z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.hash = makeHash(x, y, z);
    }

    public static int makeHash(int x, int y, int z) {
        int xShort = x & 32767;
        int yByte = y & 255;
        int zShort = z & 32767;
        int xFlag = (x < 0) ? Integer.MIN_VALUE : 0;
        int zFlag = (z < 0) ? 32768 : 0;
        return yByte | (xShort << 8) | (zShort << 24) | xFlag | zFlag;
    }

    public float distanceTo(PathPoint2 other) {
        float dx = (float) (other.xCoord - this.xCoord);
        float dy = (float) (other.yCoord - this.yCoord);
        float dz = (float) (other.zCoord - this.zCoord);
        return MathHelper.sqrt_float(dx * dx + dy * dy + dz * dz);
    }

    public float distanceToSquared(PathPoint2 other) {
        float dx = (float) (other.xCoord - this.xCoord);
        float dy = (float) (other.yCoord - this.yCoord);
        float dz = (float) (other.zCoord - this.zCoord);
        return dx * dx + dy * dy + dz * dz;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PathPoint2)) return false;
        PathPoint2 other = (PathPoint2) obj;
        return this.hash == other.hash && this.xCoord == other.xCoord
            && this.yCoord == other.yCoord
            && this.zCoord == other.zCoord;
    }

    public int hashCode() {
        return this.hash;
    }

    public boolean isAssigned() {
        return this.index >= 0;
    }

    public String toString() {
        return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}
