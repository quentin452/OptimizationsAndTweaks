package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.tidychunkbackport;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;

import com.falsepattern.lib.compat.ChunkPos;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.hash.TObjectLongHashMap;

public class TidyChunkBackportWorldContext {

    // Map to store the time when chunks were added
    public final TObjectLongHashMap<ChunkPos> chunks = new TObjectLongHashMap<>();
    private int removeCount = 0;

    // Check if an entity is an EntityItem
    public static boolean isTargetEntity(@Nonnull final Entity e) {
        return e instanceof EntityItem;
    }

    // Add a chunk to the map with the current world time
    public void add(ChunkPos pos, World world) {
        long currentTime = world.getTotalWorldTime();
        this.chunks.put(pos, currentTime);
    }

    // Search for and remove EntityItems that meet certain criteria
    public void searchAndDestroy(@Nonnull final World world) {
        if (!this.chunks.isEmpty()) {
            List<EntityItem> entitiesToRemove = new ArrayList<>();

            for (Object entityObject : world.loadedEntityList) {
                if (entityObject instanceof EntityItem) {
                    EntityItem itemEntity = (EntityItem) entityObject;

                    if (isTargetEntity(itemEntity) && isContained(itemEntity)) {
                        entitiesToRemove.add(itemEntity);
                    }
                }
            }

            removeEntities(entitiesToRemove, world);
        }
    }

    private void removeEntities(List<EntityItem> entities, World world) {
        for (EntityItem entity : entities) {
            if (OptimizationsandTweaksConfig.enableTidyChunkBackportDebugger) {
                System.out.println("Entity meets criteria, removing... (" + entity + ")");
            }

            removeEntity(entity, world);
        }

        if (this.removeCount > 0) {
            this.removeCount = 0;
        }
    }

    // Remove old chunks based on configured tick span
    public void removeOldContext(World world) {
        int span = OptimizationsandTweaksConfig.TidyChunkBackportPostTick;
        long currentTime = world.getTotalWorldTime();
        TObjectLongHashMap<ChunkPos> copy = new TObjectLongHashMap<>(this.chunks);
        for (TObjectLongIterator<ChunkPos> iterator = copy.iterator(); iterator.hasNext();) {
            iterator.advance();
            long time = iterator.value();
            if (currentTime - time > span) {
                this.chunks.remove(iterator.key());
            }
        }
    }

    // Remove an entity from the world
    public void removeEntity(Entity entity, World world) {
        entity.setDead();
        world.removeEntity(entity);
        ++this.removeCount;
    }

    // Check if an EntityItem is contained within a TidyChunk
    public boolean isContained(@Nonnull final Entity entity) {
        if (entity instanceof EntityItem && entity.isEntityAlive()) {
            EntityItem item = (EntityItem) entity;
            ChunkPos chunkPos = new ChunkPos(item.chunkCoordX, item.chunkCoordZ);
            return this.chunks.containsKey(chunkPos);
        }

        return false;
    }
}
