package fr.iamacat.optimizationsandtweaks.mixins.common.aether;

import com.gildedgames.the_aether.entities.util.EntityHook;
import com.gildedgames.the_aether.player.PlayerAether;
import com.gildedgames.the_aether.player.PlayerAetherEvents;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PlayerAetherEvents.class)
public class MixinPlayerAetherEvents {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onPlayerAetherUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            PlayerAether playerAether = PlayerAether.get((EntityPlayer) event.entityLiving);
            if (playerAether != null) {
                playerAether.onUpdate();
            }
        } else if (event.entityLiving instanceof EntityLivingBase) {
            IExtendedEntityProperties properties = event.entityLiving.getExtendedProperties("aether_legacy:entity_hook");
            if (properties instanceof EntityHook) {
                ((EntityHook) properties).onUpdate();
            }
        }
    }
}
