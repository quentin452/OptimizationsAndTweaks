package fr.iamacat.optimizationsandtweaks.eventshandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkEvent;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventHandler {

    // Remove all EntityItem during initial chunk generation.
    // to prevent lags caused by a large amount of EntityItem on mod packs at initial chunk loading.
    // inspired by Tidy Chunk mod from 1.12.2.
    // todo remove all loggings/try and catch when i am sure that this method is work well
    // todo fix he try to clear EntityItem every time i load chunk even when the chunk is old (potentially fixed)
    private void optimizationsAndTweaks$clearEntityItemsAtChunkPopulatePost(Chunk chunk) {
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
            entity.setDead();
            world.loadedEntityList.remove(entity);
            System.out.println("EntityItem removed: " + entity);
        }
    }

    @SubscribeEvent
    public void onPopulateChunkPost(PopulateChunkEvent.Post event) {
        World world = event.world;
        int chunkX = event.chunkX;
        int chunkZ = event.chunkZ;
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        if(chunk != null) {
            optimizationsAndTweaks$clearEntityItemsAtChunkPopulatePost(chunk);
        }
    }
}
