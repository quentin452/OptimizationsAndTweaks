package fr.iamacat.optimizationsandtweaks.mixins.common.automagy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import tuhljin.automagy.lib.TjUtil;
import tuhljin.automagy.lib.events.AutomagyEventHandler;
import tuhljin.automagy.lib.inventory.HashableItemWithoutSize;

@Mixin(AutomagyEventHandler.class)
public class MixinAutomagyEventHandler {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public EntityPlayer getNearbyPlayerWithItem(HashableItemWithoutSize item, World world, double x, double y, double z, double maxDistance) {
        for (Object obj : world.playerEntities) {
            if (obj instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer) obj;
                double distanceSquared = entityPlayer.getDistanceSq(x, y, z);
                if ((maxDistance < 0.0 || distanceSquared < maxDistance * maxDistance) && TjUtil.playerHasItem(entityPlayer, item)) {
                    return entityPlayer;
                }
            }
        }
        return null;
    }
}
