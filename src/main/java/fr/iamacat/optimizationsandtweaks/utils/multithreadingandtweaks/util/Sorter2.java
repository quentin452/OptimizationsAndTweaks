package fr.iamacat.optimizationsandtweaks.utils.multithreadingandtweaks.util;

import net.minecraft.entity.Entity;

import java.util.Comparator;

public class Sorter2 implements Comparator {
    private final Entity theEntity;

    public Sorter2(Entity p_i1662_1) {
        this.theEntity = p_i1662_1;
    }

    public int compare(Entity p_compare_1_, Entity p_compare_2_)
    {
        double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
        double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
        return Double.compare(d0, d1);
    }
    @Override
    public int compare(Object o1, Object o2) {
        Entity e1 = (Entity) o1;
        Entity e2 = (Entity) o2;

        return compare(e1, e2);
    }
}
