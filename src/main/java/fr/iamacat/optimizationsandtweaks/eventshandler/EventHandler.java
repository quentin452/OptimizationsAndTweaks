package fr.iamacat.optimizationsandtweaks.eventshandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    // Remove all EntityItem during initial chunk generation.
    // to prevent lags caused by a large amount of EntityItem on mod packs at initial chunk loading.
    // inspired by Tidy Chunk mod from 1.12.2.
    // todo remove all loggings/try and catch when i am sure that this method is work well
    // todo clear EntityItem after isTerrainPopulated and not before to remove again more EntityItem
    @Unique
    private void optimizationsAndTweaks$clearEntityItemsAtInitialChunkload(Chunk chunk) {
        World world = chunk.worldObj;

        if (chunk.isTerrainPopulated) {
            return;
        }

        List<EntityItem> entitiesToRemove = new ArrayList<>();
        for (Object obj : world.loadedEntityList) {
            if (obj instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) obj;
                int entityX = MathHelper.floor_double(entityItem.posX);
                int entityZ = MathHelper.floor_double(entityItem.posZ);

                System.out.println("Checking EntityItem: " + entityItem + " at (" + entityX + ", " + entityZ + ")");
                entitiesToRemove.add(entityItem);
            }
        }

        for (EntityItem entity : entitiesToRemove) {
            world.removeEntity(entity);
            world.loadedEntityList.remove(entity);
            System.out.println("EntityItem removed: " + entity);
        }
    }
    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        Chunk chunk = event.getChunk();
        optimizationsAndTweaks$clearEntityItemsAtInitialChunkload(chunk);
    }
}
