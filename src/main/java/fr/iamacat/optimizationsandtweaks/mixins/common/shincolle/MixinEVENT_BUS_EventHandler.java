package fr.iamacat.optimizationsandtweaks.mixins.common.shincolle;

import com.lulan.shincolle.entity.BasicEntityShip;
import com.lulan.shincolle.entity.ExtendPlayerProps;
import com.lulan.shincolle.entity.ExtendShipProps;
import com.lulan.shincolle.handler.EVENT_BUS_EventHandler;
import com.lulan.shincolle.utility.LogHelper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EVENT_BUS_EventHandler.class)
public class MixinEVENT_BUS_EventHandler {
    /**
     * @author iamacatfr
     * @reason fix stackoverflow between Custom Mob Spawner and ShinColle
     */
    @Overwrite(remap = false)
    @SubscribeEvent(
        priority = EventPriority.NORMAL,
        receiveCanceled = true
    )
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof BasicEntityShip && event.entity.getExtendedProperties("ShipExtProps") == null) {
            LogHelper.info("DEBUG : entity constructing: on ship constructing " + event.entity.getEntityId());
            event.entity.registerExtendedProperties("ShipExtProps", new ExtendShipProps());
        }

        if (event.entity instanceof EntityPlayer && !(event.entity instanceof FakePlayer) && event.entity.getExtendedProperties("TeitokuExtProps") == null) {
            LogHelper.info("DEBUG : entity constructing: on player constructing " + event.entity.getEntityId() + " " + event.entity.getClass().getSimpleName());
            EntityPlayer player = (EntityPlayer)event.entity;
            if (player.getExtendedProperties("TeitokuExtProps") == null) {
                player.registerExtendedProperties("TeitokuExtProps", new ExtendPlayerProps());
            }
        }
    }
}
