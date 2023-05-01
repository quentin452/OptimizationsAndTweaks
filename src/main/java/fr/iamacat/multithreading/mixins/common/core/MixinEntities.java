package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.Lists;

import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinEntities {
    private static final int NUM_CPUS = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
    private List<Entity> loadedEntityList;
    private final Map<Class<? extends Entity>, List<Entity>> entityMap = new HashMap<>();

    public List<Entity> getEntitiesWithinAABBQuadTree(Class<? extends Entity> clazz, AxisAlignedBB aabb) {
        List<Entity> entities = entityMap.get(clazz);
        if (entities == null) {
            return Collections.emptyList();
        }

        int numCores = Math.min(Runtime.getRuntime().availableProcessors(), NUM_CPUS);
        int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;

        return IntStream.range(0, (entities.size() + batchSize - 1) / batchSize)
            .parallel()
            .mapToObj(i -> entities.subList(i * batchSize, Math.min(entities.size(), (i + 1) * batchSize)))
            .flatMap(List::stream)
            .filter(entity -> entity.getBoundingBox().intersectsWith(aabb))
            .collect(Collectors.toList());
    }


    public void addEntityToMap(Entity entity) {
        Class<? extends Entity> clazz = entity.getClass();
        List<Entity> entities = entityMap.get(clazz);
        if (entities == null) {
            entities = new ArrayList<>();
            entityMap.put(clazz, entities);
        }
        entities.add(entity);
    }

    public void removeEntityFromMap(Entity entity) {
        Class<? extends Entity> clazz = entity.getClass();
        List<Entity> entities = entityMap.get(clazz);
        if (entities != null) {
            entities.remove(entity);
        }
    }

    // this method is called from WorldServer::getEntityByID and WorldServer::getEntityByUuid
    public Entity getEntityFromMap(int entityId) {
        for (List<Entity> entities : entityMap.values()) {
            for (Entity entity : entities) {
                if (entity.getEntityId() == entityId) {
                    return entity;
                }
            }
        }
        return null;
    }

    // this method is called from EntityTracker::trackEntity
    public void trackEntityInMap(Entity entity) {
        addEntityToMap(entity);
    }

    // this method is called from EntityTracker::untrackEntity
    public void untrackEntityInMap(Entity entity) {
        removeEntityFromMap(entity);
    }

    // this method is called from Chunk::addEntity and Chunk::removeEntity
    public void updateEntityInMap(Entity entity) {
        removeEntityFromMap(entity);
        addEntityToMap(entity);
    }

    // this method is called from WorldServer::tick and WorldServer::updateEntities
    public void updateMap() {
        entityMap.clear();
        for (Entity entity : this.loadedEntityList) {
            addEntityToMap(entity);
        }
    }
}
