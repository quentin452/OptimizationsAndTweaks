package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.tidychunkbackport;

import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TidyChunkBackportWorldContext {
    private final TObjectLongHashMap<ChunkPosition> chunks = new TObjectLongHashMap<>();
    private int removeCount = 0;

    public static boolean isTargetEntity(@Nonnull final Entity e) {
        return EntityItem.class.isAssignableFrom(e.getClass());
    }

    public void add(ChunkPosition pos, World world) {
        this.chunks.put(pos, world.getTotalWorldTime());
    }

    public void searchAndDestroy(@Nonnull final World world) {
        if (!this.chunks.isEmpty()) {
            Set<ChunkPosition> keys = new HashSet<>(this.chunks.keySet());
            for (ChunkPosition coords : keys) {
                processChunkEntities(world, coords);
                this.chunks.remove(coords);
            }
            printEntitiesWiped();
        }
    }

    private void processChunkEntities(World world, ChunkPosition coords) {
        int chunkX = coords.chunkPosX >> 4;
        int chunkZ = coords.chunkPosZ >> 4;
        AxisAlignedBB bounds = createChunkBounds(chunkX, chunkZ, world);

        List<Entity> entitiesList = getEntitiesInBounds(world, bounds);

        for (Entity entity : entitiesList) {
            processEntity(entity);
        }
    }

    private AxisAlignedBB createChunkBounds(int chunkX, int chunkZ, World world) {
        return AxisAlignedBB.getBoundingBox(
            chunkX * 16, 0, chunkZ * 16,
            chunkX * 16 + 16, world.getHeight(),
            chunkZ * 16 + 16
        );
    }

    private List<Entity> getEntitiesInBounds(World world, AxisAlignedBB bounds) {
        List<Entity> entitiesList = new ArrayList<>();
        for (Object obj : world.getEntitiesWithinAABB(EntityItem.class, bounds)) {
            if (obj instanceof Entity) {
                entitiesList.add((Entity) obj);
            }
        }
        return entitiesList;
    }

    private void processEntity(Entity entity) {
        System.out.println("Inside processEntity for entity: " + entity);
        System.out.println("isTargetEntity: " + isTargetEntity(entity));
        System.out.println("isContained: " + isContained(entity));

        if (isTargetEntity(entity) && isContained(entity)) {
            removeEntity(entity,entity.worldObj);
        }
    }
    // printEntitiesWiped seem to not function correctly (none of these prints are in logs)
    private void printEntitiesWiped() {
        System.out.println("Inside printEntitiesWiped");
        if (this.removeCount > 0) {
            System.out.println("Entities wiped: " + this.removeCount);
            this.removeCount = 0;
        }
    }

    public void removeOldContext(World world) {
        int span = 15;
        TObjectLongIterator<ChunkPosition> iterator = this.chunks.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            long time = iterator.value();
            if (world.getTotalWorldTime() - time > span) {
                iterator.remove();
            }
        }
    }

    // removeEntity seem to not function correctly (none of these prints are in logs)
    public void removeEntity(Entity entity,World world) {
        System.out.println("Removing entity: " + entity);
        entity.setDead();
        world.removeEntity(entity);
        System.out.println("Entity removed from the world");
        ++this.removeCount;
        System.out.println("Remove count incremented");

        System.out.println("After removal - isEntityAlive: " + entity.isEntityAlive());
        System.out.println("After removal - removeCount: " + this.removeCount);
    }
    public boolean isContained(Entity entity) {
        if (entity instanceof EntityItem && entity.isEntityAlive()) {
            EntityItem item = (EntityItem) entity;
            int posX = MathHelper.floor_double(item.posX);
            int posY = MathHelper.floor_double(item.posY);
            int posZ = MathHelper.floor_double(item.posZ);
            ChunkPosition chunkPos = new ChunkPosition(posX, posY, posZ);
            return chunks.containsKey(chunkPos);
        }
        return false;
    }
}

