package fr.iamacat.optimizationsandtweaks.mixins.common.diseasecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mc.Mitchellbrine.diseaseCraft.modules.med.recipe.MedicationRecipes;
import mc.Mitchellbrine.diseaseCraft.modules.med.util.MedUtils;

@Mixin(MedUtils.class)
public class MixinMedUtils {

    @Shadow
    public static DamageSource medication = (new DamageSource("medication")).setDamageBypassesArmor();

    @Shadow
    public static boolean areMedsActive(EntityLivingBase entity, String id) {
        return entity.getEntityData()
            .hasKey("block" + id)
            && entity.getEntityData()
                .getInteger("block" + id) > 0;
    }

    /**
     * @author iamacatfr
     * @reason fix lag caused by StringBuilder
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void medTimedown(LivingEvent.LivingUpdateEvent event) {
        if (!event.entityLiving.worldObj.isRemote) {
            for (String diseaseId : MedicationRecipes.diseaseRemoval.values()) {
                if (areMedsActive(event.entityLiving, diseaseId)) {
                    int newMeds = event.entityLiving.getEntityData()
                        .getInteger("block" + diseaseId) - 1;
                    event.entityLiving.getEntityData()
                        .setInteger("block" + diseaseId, newMeds);
                    if (newMeds > 24000) {
                        event.entityLiving.attackEntityFrom(medication, 1.0F);
                    }
                }
            }
        }
    }
}
