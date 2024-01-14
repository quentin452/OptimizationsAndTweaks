package fr.iamacat.optimizationsandtweaks.eventshandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventHandler {
    // Remove all EntityItem during initial chunk generation.
    // to prevent lags caused by a large amount of EntityItem on mod packs at initial chunk loading.
    // inspired by Tidy Chunk mod from 1.12.2.
    // todo fix not all EntityItem don't get removed
    // todo probably remove the set
    private static final Set<Chunk> processedChunks = new HashSet<>();

    @SubscribeEvent
    public void onPopulateChunkPost(PopulateChunkEvent.Post event) {
        World world = event.world;
        int chunkX = event.chunkX;
        int chunkZ = event.chunkZ;
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        if (chunk != null) {
            optimizationsAndTweaks$clearEntityItemsAtChunkPopulatePost(chunk);
        }
    }
    private void optimizationsAndTweaks$clearEntityItemsAtChunkPopulatePost(Chunk chunk) {
        World world = chunk.worldObj;

        if (!processedChunks.contains(chunk)) {
            processedChunks.add(chunk);

            List<EntityItem> entitiesToRemove = new ArrayList<>();

            for (Object obj : world.loadedEntityList) {
                if (obj instanceof EntityItem) {
                    EntityItem entityItem = (EntityItem) obj;
                    int entityX = MathHelper.floor_double(entityItem.posX);
                    int entityZ = MathHelper.floor_double(entityItem.posZ);

                    int chunkX = entityX >> 4;
                    int chunkZ = entityZ >> 4;

                    if (chunkX == chunk.xPosition && chunkZ == chunk.zPosition) {
                        System.out.println("Checking EntityItem: " + entityItem + " at (" + entityX + ", " + entityZ + ")");
                        entitiesToRemove.add(entityItem);
                    }
                }
            }

            for (EntityItem entity : entitiesToRemove) {
                entity.setDead();
                world.removeEntity(entity);
            }
        }
    }

  /*  @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        Chunk chunk = event.getChunk();
        optimizationsAndTweaks$clearEntityItemsAtChunkPopulatePost(chunk);
    }

   */
}
