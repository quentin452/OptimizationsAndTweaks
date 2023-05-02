package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.SharedThreadPool;

@Mixin(World.class)
public abstract class MixinEntities {

    private final Map<Class<? extends Entity>, List<Entity>> entityMap = new ConcurrentHashMap<>();
    private final List<Entity> loadedEntityList = new CopyOnWriteArrayList<>();

    public List<Entity> getEntitiesWithinAABB(Class<? extends Entity> clazz, AxisAlignedBB aabb) {
        List<Entity> entities = entityMap.get(clazz);
        if (entities == null) {
            return Collections.emptyList();
        }

        List<Entity> results = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getBoundingBox()
                .intersectsWith(aabb)) {
                results.add(entity);
            }
        }
        return results;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        SharedThreadPool.getExecutorService()
            .submit(() -> { SharedThreadPool.getExecutorService(); });
    }

    public void addEntityToMap(Entity entity) {
        Class<? extends Entity> clazz = entity.getClass();
        entityMap.computeIfAbsent(clazz, k -> new ArrayList<>())
            .add(entity);
        loadedEntityList.add(entity);
    }

    public void removeEntityFromMap(Entity entity) {
        Class<? extends Entity> clazz = entity.getClass();
        List<Entity> entities = entityMap.get(clazz);
        if (entities != null) {
            entities.remove(entity);
        }
        loadedEntityList.remove(entity);
    }

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

    public void trackEntityInMap(Entity entity) {
        addEntityToMap(entity);
    }

    public void untrackEntityInMap(Entity entity) {
        removeEntityFromMap(entity);
    }

    public void updateEntityInMap(Entity entity) {
        removeEntityFromMap(entity);
        addEntityToMap(entity);
    }

    public void updateMap() {
        List<Entity> entitiesToUpdate = new ArrayList<>(loadedEntityList);
        entityMap.clear();
        entitiesToUpdate.parallelStream()
            .forEach(this::addEntityToMap);
    }
}
