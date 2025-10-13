package fr.iamacat.optimizationsandtweaks.mixins.common.thetitan;

import net.minecraft.entity.titan.minion.IMinion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.thetitan.EntityAIFindEntityNearestInjuredAllyPatch;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.thetitan.IMinionHealer;

@Mixin(value = {
    net.minecraft.entity.titan.minion.EntitySkeletonMinion.class,
    net.minecraft.entity.titan.minion.EntityZombieMinion.class,
    net.minecraft.entity.titan.minion.EntitySpiderMinion.class,
    net.minecraft.entity.titan.minion.EntityCreeperMinion.class,
    net.minecraft.entity.titan.minion.EntityBlazeMinion.class,
    net.minecraft.entity.titan.minion.EntityEndermanMinion.class,
    net.minecraft.entity.titan.minion.EntityGhastMinion.class,
    net.minecraft.entity.titan.minion.EntityPigZombieMinion.class,
    net.minecraft.entity.titan.minion.EntitySilverfishMinion.class,
    net.minecraft.entity.titan.minion.EntityCaveSpiderMinion.class
}, priority = 999)
public abstract class MixinFixMinionIaTitan extends EntityLiving implements IMinionHealer {

    @Shadow(remap = false) private EntityLiving entityToHeal;

    public MixinFixMinionIaTitan(net.minecraft.world.World world) {
        super(world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void replaceFindEntityNearestInjuredAlly(CallbackInfo ci) {
        tasks.taskEntries.removeIf(obj -> {
            if (obj instanceof EntityAITasks.EntityAITaskEntry) {
                EntityAITasks.EntityAITaskEntry entry = (EntityAITasks.EntityAITaskEntry) obj;
                return entry.action != null &&
                    entry.action.getClass().getSimpleName().equals("EntityAIFindEntityNearestInjuredAlly");
            }
            return false;
        });

        float targetRange = 32.0f;
        Class<?> clazz = this.getClass();
        if (clazz == net.minecraft.entity.titan.minion.EntityEndermanMinion.class) {
            targetRange = 48.0f;
        } else if (clazz == net.minecraft.entity.titan.minion.EntityGhastMinion.class) {
            targetRange = 100.0f;
        } else if (clazz == net.minecraft.entity.titan.minion.EntitySilverfishMinion.class) {
            targetRange = 24.0f;
        }
        tasks.addTask(0, new EntityAIFindEntityNearestInjuredAllyPatch(this, targetRange));
    }

    @Override
    public EntityLiving getEntityToHeal() {
        return entityToHeal;
    }

    @Override
    public void setEntityToHeal(EntityLiving entity) {
        this.entityToHeal = entity;
    }
}