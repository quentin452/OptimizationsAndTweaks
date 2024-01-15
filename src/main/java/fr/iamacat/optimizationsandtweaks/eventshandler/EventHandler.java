package fr.iamacat.optimizationsandtweaks.eventshandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventHandler {
    // Remove all EntityItem during initial chunk generation.
    // To prevent lags caused by a large amount of EntityItem on mod packs at initial chunk loading.
    // Inspired by Tidy Chunk mod from 1.12.2.
    private static final Set<Chunk> PROCESSED_CHUNKS = new HashSet<>();
    private static final Set<Chunk> CHUNKS_TO_CLEAN_UP = new HashSet<>();

    // todo fix not all EntityItem get removed

    @SubscribeEvent
    public void onPopulateChunkPost(PopulateChunkEvent.Post event) {
        World world = event.world;
        int chunkX = event.chunkX;
        int chunkZ = event.chunkZ;
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (chunk != null && !PROCESSED_CHUNKS.contains(chunk)) {
            optimizationsAndTweaks$clearEntityItemsInChunk(chunk);
            PROCESSED_CHUNKS.add(chunk);
            CHUNKS_TO_CLEAN_UP.add(chunk);
        }
    }
    // todo fix ServerTickEvent is never triggered
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        System.out.println("onServerTick triggered");
        if (event.phase == TickEvent.Phase.END) {
            int currentTick = FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();
            if (currentTick % (20 * 3) == 0) {
                for (Chunk chunk : CHUNKS_TO_CLEAN_UP) {
                    optimizationsAndTweaks$clearEntityItemsInChunk(chunk);
                }
                CHUNKS_TO_CLEAN_UP.clear();
            }
        }
    }

    private void optimizationsAndTweaks$clearEntityItemsInChunk(Chunk chunk) {
        World world = chunk.worldObj;

        if (!PROCESSED_CHUNKS.contains(chunk)) {
            List<Entity> entitiesToRemove = new ArrayList<>();

            for (Object entity : world.loadedEntityList) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) {
                    int entityX = MathHelper.floor_double(((Entity) entity).posX);
                    int entityZ = MathHelper.floor_double(((Entity) entity).posZ);

                    int chunkX = entityX >> 4;
                    int chunkZ = entityZ >> 4;

                    if (chunkX == chunk.xPosition && chunkZ == chunk.zPosition) {
                        System.out.println("Checking EntityItem: " + entity + " at (" + entityX + ", " + entityZ + ")");
                        entitiesToRemove.add((Entity) entity);
                    }
                }
            }

            for (Entity entity : entitiesToRemove) {
                entity.setDead();
                world.removeEntity(entity);
            }

            PROCESSED_CHUNKS.add(chunk);
        }
    }
}
