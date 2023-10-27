package fr.iamacat.optimizationsandtweaks.utils.multithreadingandtweaks.util;

import java.util.Comparator;

import net.minecraft.entity.Entity;

public class Sorter2 implements Comparator {

    private final Entity theEntity;

    public Sorter2(Entity p_i1662_1_) {
        this.theEntity = p_i1662_1_;
    }

    public int compare(Entity p_compare_1_, Entity p_compare_2_) {
        double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
        double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
        return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
    }

    public int compare(Object p_compare_1_, Object p_compare_2_) {
        return this.compare((Entity) p_compare_1_, (Entity) p_compare_2_);
    }
}
