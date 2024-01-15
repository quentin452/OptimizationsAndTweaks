package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.tidychunkbackport;

import com.falsepattern.lib.compat.ChunkPos;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

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
                        System.out.println("Entity meets criteria, removing...");
                        removeEntity(itemEntity, world);
                    } else {
                        if (!isTargetEntity(itemEntity)) {
                            // System.out.println("Reason: Not a target entity.");
                        }
                        if (!isContained(itemEntity)) {
                            System.out.println("Reason: Not in TidyChunk.");
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

        for (TObjectLongIterator<ChunkPos> iterator = this.chunks.iterator(); iterator.hasNext(); ) {
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
    /*public boolean isContained(Entity entity) {
        if (entity instanceof EntityItem && entity.isEntityAlive()) {
            EntityItem item = (EntityItem) entity;
            int posX = MathHelper.floor_double(item.posX);
            int posZ = MathHelper.floor_double(item.posZ);
            ChunkPos chunkPos = new ChunkPos(posX, posZ);
            if (chunks.containsKey(chunkPos)) {
                long timeAdded = chunks.get(chunkPos);
                long currentTime = item.worldObj.getTotalWorldTime();
                long maxTimeDifference = 100;
                if (currentTime - timeAdded <= maxTimeDifference) {
                    return true;
                } else {
                    removeChunkIfPresent(chunkPos);
                }
            }
        }
        return false;
    }

     */
    // todo fix : Reason: Not in TidyChunk.
    public boolean isContained(@Nonnull final Entity entity) {
        if (entity instanceof EntityItem && entity.isEntityAlive()) {
            EntityItem item = (EntityItem) entity;

            int posX = MathHelper.floor_double(item.posX);
            int posZ = MathHelper.floor_double(item.posZ);

            ChunkPos chunkPos = new ChunkPos(posX, posZ);

            return this.chunks.containsKey(chunkPos);
        }

        return false;
    }

    private void removeChunkIfPresent(ChunkPos chunkPos) {
        if (this.chunks.containsKey(chunkPos)) {
            this.chunks.remove(chunkPos);
            System.out.println("Chunk removed from the map");
        }
    }
}

