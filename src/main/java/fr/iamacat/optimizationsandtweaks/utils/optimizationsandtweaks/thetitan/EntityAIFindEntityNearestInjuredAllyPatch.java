package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.thetitan;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.titan.minion.EnumMinionType;
import net.minecraft.entity.titan.minion.IMinion;

public class EntityAIFindEntityNearestInjuredAllyPatch extends EntityAIBase {

    private final EntityLiving entityLiving;
    private EntityLivingBase entityTarget;
    private final float maxTargetRange;

    public EntityAIFindEntityNearestInjuredAllyPatch(EntityLiving entityLiving, float maxTargetRange) {
        this.entityLiving = entityLiving;
        this.maxTargetRange = maxTargetRange;
    }

    @Override
    public boolean shouldExecute() {
        if (!(entityLiving instanceof IMinion)) return false;
        if (!entityLiving.isEntityAlive()) return false;

        IMinion minion = (IMinion) entityLiving;

        if (minion.getMinionType() == EnumMinionType.LOYALIST) return false;
        if (entityLiving.getAttackTarget() != null) return false;
        if (entityTarget != null) return false;

        double range = maxTargetRange();
        List<EntityLiving> list = entityLiving.worldObj
            .getEntitiesWithinAABB(EntityLiving.class, entityLiving.boundingBox.expand(range, range, range));

        list.removeIf(e -> !(e instanceof IMinion) || ((IMinion) e).getMinionType() == EnumMinionType.LOYALIST);

        if (list.isEmpty()) return false;

        for (EntityLiving e : list) {
            if (e.getHealth() < e.getMaxHealth() && e.isEntityAlive()) {
                entityTarget = e;
                break;
            }
        }

        return entityTarget != null;
    }

    @Override
    public boolean continueExecuting() {
        if (!(entityLiving instanceof IMinionHealer)) return false;

        EntityLiving entitylivingbase = ((IMinionHealer) entityLiving).getEntityToHeal();
        if (entitylivingbase == null) return false;
        if (!entitylivingbase.isEntityAlive()) return false;
        if (entitylivingbase.getHealth() >= entitylivingbase.getMaxHealth()) return false;

        double d0 = this.maxTargetRange();
        double dx = entityLiving.posX - entitylivingbase.posX;
        double dy = entityLiving.posY - entitylivingbase.posY;
        double dz = entityLiving.posZ - entitylivingbase.posZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;

        return distanceSq <= d0 * d0;
    }

    @Override
    public void startExecuting() {
        if (entityLiving instanceof IMinionHealer) {
            ((IMinionHealer) entityLiving).setEntityToHeal((EntityLiving) this.entityTarget);
        }
        super.startExecuting();
    }

    @Override
    public void resetTask() {
        if (entityLiving instanceof IMinionHealer) {
            ((IMinionHealer) entityLiving).setEntityToHeal(null);
        }
        entityTarget = null;
        super.resetTask();
    }

    @Override
    public void updateTask() {
        if (!(entityLiving instanceof IMinionHealer)) return;

        EntityLiving entityToHeal = ((IMinionHealer) entityLiving).getEntityToHeal();
        if (entityToHeal != null) {
            double dx = entityLiving.posX - entityToHeal.posX;
            double dy = entityLiving.posY - entityToHeal.posY;
            double dz = entityLiving.posZ - entityToHeal.posZ;
            double distanceSq = dx * dx + dy * dy + dz * dz;

            if (distanceSq > 16.0 * 16.0) {
                entityLiving.getNavigator()
                    .tryMoveToEntityLiving(entityToHeal, 1.0);
                entityLiving.getLookHelper()
                    .setLookPositionWithEntity(entityToHeal, 10.0f, (float) entityLiving.getVerticalFaceSpeed());
            }
        }
    }

    protected double maxTargetRange() {
        return maxTargetRange;
    }
}
