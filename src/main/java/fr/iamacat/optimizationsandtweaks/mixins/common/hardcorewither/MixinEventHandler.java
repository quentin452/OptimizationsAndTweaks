package fr.iamacat.optimizationsandtweaks.mixins.common.hardcorewither;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.boss.EntityWither;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import thor12022.hardcorewither.EventHandler;
import thor12022.hardcorewither.powerUps.PowerUpManager;

@Mixin(EventHandler.class)
public class MixinEventHandler {
    @Shadow
    private PowerUpManager powerUpManager;
    /**
     * @author iamacatfr
     * @reason fix null crash caused by onLivingUpdate from Hardcore Wither mod
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity != null && !event.entity.worldObj.isRemote && event.entityLiving != null && event.entityLiving.getClass() == EntityWither.class) {
            this.powerUpManager.update((EntityWither) event.entityLiving);
        }
    }
}
