package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.obsgreenery;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.Classers;

public class BranchInfo {
    private final int x;
    private final int y;
    private final int z;
    private final int len;
    private final Classers.Quadrant quadrant;

    public BranchInfo(int x, int y, int z, int len, Classers.Quadrant quadrant) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.len = len;
        this.quadrant = quadrant;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getLen() {
        return len;
    }

    public Classers.Quadrant getQuadrant() {
        return quadrant;
    }
}
