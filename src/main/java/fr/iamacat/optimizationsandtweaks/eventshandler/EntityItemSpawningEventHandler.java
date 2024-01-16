package fr.iamacat.optimizationsandtweaks.eventshandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class EntityItemSpawningEventHandler {
    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent evt) {
        Entity entity = evt.entity;

        if (entity instanceof EntityItem) {
            EntityItem itemEntity = (EntityItem) entity;

            World world = itemEntity.worldObj;

            if (world != null && !world.isRemote) {
                ItemStack itemStack = itemEntity.getEntityItem();
                String itemName = itemStack.getUnlocalizedName();

                System.out.println("Item Name: " + itemName);
                System.out.println("Spawn Position: (" + entity.posX + ", " + entity.posY + ", " + entity.posZ + ")");
                System.out.println("Dimension: " + world.provider.getDimensionName());
            }
        }
    }
}
