package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.thetitan;

import net.minecraft.entity.EntityLiving;

public interface IMinionHealer {

    EntityLiving getEntityToHeal();

    void setEntityToHeal(EntityLiving entity);
}
