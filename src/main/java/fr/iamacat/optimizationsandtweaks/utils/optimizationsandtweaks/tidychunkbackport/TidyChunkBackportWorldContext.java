package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.tidychunkbackport;

import com.falsepattern.lib.compat.ChunkPos;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TidyChunkBackportWorldContext {
    public final TObjectLongHashMap<ChunkPos> chunks = new TObjectLongHashMap<>();
    private int removeCount = 0;

    public static boolean isTargetEntity(@Nonnull final Entity e) {
        return EntityItem.class.isAssignableFrom(e.getClass());
    }

    public void add(ChunkPos pos, World world) {
        long currentTime = world.getTotalWorldTime();
        this.chunks.put(pos, currentTime);
    }

    public void searchAndDestroy(@Nonnull final World world) {
        if (!this.chunks.isEmpty()) {
            List loadedEntities = world.getLoadedEntityList();

            for (Object entityObject : loadedEntities) {
                if (entityObject instanceof EntityItem) {
                    EntityItem itemEntity = (EntityItem) entityObject;

                    if (isTargetEntity(itemEntity) && isContained(itemEntity)) {
                        System.out.println("Entity meets criteria, removing... (" + itemEntity + ")");
                        removeEntity(itemEntity, world);
                    } else {
                        if (!isContained(itemEntity)) {
                            System.out.println("Reason: Not in TidyChunk. (" + itemEntity + ")");
                        }
                    }
                }
            }

            if (this.removeCount > 0) {
                this.removeCount = 0;
            }
        }
    }

    public void removeOldContext(World world) {
        int span = 100;

        List<ChunkPos> chunksToRemove = new ArrayList<>();

        TObjectLongHashMap<ChunkPos> tempMap = new TObjectLongHashMap<>(this.chunks);

        for (TObjectLongIterator<ChunkPos> iterator = tempMap.iterator(); iterator.hasNext(); ) {
            iterator.advance();
            long time = iterator.value();
            if (world.getTotalWorldTime() - time > span) {
                chunksToRemove.add(iterator.key());
            }
        }

        for (ChunkPos chunkPos : chunksToRemove) {
            this.chunks.remove(chunkPos);
        }
    }

    public void removeEntity(Entity entity, World world) {
        entity.setDead();
        world.removeEntity(entity);
        ++this.removeCount;
    }
    // todo fix : Reason: Not in TidyChunk.
    public boolean isContained(@Nonnull final Entity entity) {
        if (entity instanceof EntityItem && entity.isEntityAlive()) {
            EntityItem item = (EntityItem) entity;
            Chunk chunk = item.worldObj.getChunkFromChunkCoords(item.chunkCoordX, item.chunkCoordZ);
            if (chunk != null) {
                ChunkPos chunkPos = new ChunkPos(item.chunkCoordX, item.chunkCoordZ);
                return this.chunks.containsKey(chunkPos);
            }
        }

        return false;
    }
}

