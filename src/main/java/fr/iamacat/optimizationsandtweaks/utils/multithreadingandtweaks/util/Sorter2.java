package fr.iamacat.optimizationsandtweaks.utils.multithreadingandtweaks.util;

import net.minecraft.entity.Entity;

import java.util.Comparator;

public class Sorter2 implements Comparator<Entity> {
    private final Entity theEntity;

    public Sorter2(Entity p_i1662_1) {
        this.theEntity = p_i1662_1;
    }

    @Override
    public int compare(Entity p_compare_1, Entity p_compare_2) {
        double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1);
        double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2);
        return Double.compare(d0, d1);
    }
}
