package fr.iamacat.optimizationsandtweaks.eventshandler;

import com.falsepattern.lib.compat.ChunkPos;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.tidychunkbackport.TidyChunkBackportWorldContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.HashMap;
import java.util.Map;

public class TidyChunkBackportEventHandler {
    // Remove almost all EntityItem during initial chunk generation.
    // To prevent lags caused by a large amount of EntityItem on mod packs.
    // Backport of Tidy Chunk mod from 1.12.2 to 1.7.10.

    public static final Map<Integer, TidyChunkBackportWorldContext> worldData = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldUnload(WorldEvent.Unload evt) {
        World w = evt.world;
        if (!w.isRemote) {
            getWorldContext(w).searchAndDestroy(w);
            worldData.remove(w.provider.dimensionId);
        }
    }

    // onWorldUnload seem to not function correctly(never called) , so now we use injectInWorldTick which is work
   /* @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldTick(TickEvent.WorldTickEvent evt) {
        System.out.println("World Tick Event in World " + evt.world.provider.dimensionId);
        if (evt.phase == TickEvent.Phase.END) {
            WorldContext ctx = getWorldContext(evt.world);
            ctx.searchAndDestroy(evt.world);
            ctx.removeOldContext(evt.world);
        }
    }

    */
    @SubscribeEvent
    public void onChunkPopulate(PopulateChunkEvent.Pre evt) {
        TidyChunkBackportWorldContext ctx = getWorldContext(evt.world);
        ChunkPosition pos = new ChunkPosition(evt.chunkX, 0, evt.chunkZ);
        ctx.add(pos, evt.world);
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent evt) {
        Entity entity = evt.entity;
        World world = entity.worldObj;

        if (world == null || world.isRemote || !TidyChunkBackportWorldContext.isTargetEntity(entity)) {
            return;
        }

        final TidyChunkBackportWorldContext ctx = getWorldContext(world);

        if (ctx.isContained(entity)) {
            // this call does nothing, so probably need to add more prints statements
            ctx.removeEntity(entity,world);
            evt.setCanceled(true);
        }
    }

    private static TidyChunkBackportWorldContext createWorldContext(World w) {
        TidyChunkBackportWorldContext ctx;
        worldData.put(w.provider.dimensionId, ctx = new TidyChunkBackportWorldContext());
        return ctx;
    }

    private static TidyChunkBackportWorldContext getWorldContext(World w) {
        TidyChunkBackportWorldContext ctx = worldData.get(w.provider.dimensionId);
        return ctx == null ? createWorldContext(w) : ctx;
    }

    public static void injectInWorldTick(World world) {
        TidyChunkBackportWorldContext ctx = getWorldContext(world);

        if (ctx != null) {
            ctx.searchAndDestroy(world);
            ctx.removeOldContext(world);
        }
    }
}
