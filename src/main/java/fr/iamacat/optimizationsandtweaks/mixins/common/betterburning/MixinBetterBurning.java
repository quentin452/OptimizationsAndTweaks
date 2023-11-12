package fr.iamacat.optimizationsandtweaks.mixins.common.betterburning;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.darkhax.betterburning.BetterBurning;
import net.darkhax.betterburning.Config;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BetterBurning.class)
public class MixinBetterBurning {
    @Shadow
    private final Config configuration = new Config();

    /**
     * @author iamacatfr
     * @reason fix null crash caused by onLivingTick
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        if (event != null && event.entityLiving != null && event.entityLiving.worldObj != null && (this.configuration.shouldFireResExtinguish() && !event.entityLiving.worldObj.isRemote && event.entityLiving.isBurning() && event.entityLiving.isPotionActive(Potion.fireResistance))) {
                event.entityLiving.extinguish();

        }
    }
}
