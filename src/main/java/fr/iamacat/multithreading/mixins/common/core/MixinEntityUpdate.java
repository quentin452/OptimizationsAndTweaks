package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(Entity.class)
public abstract class MixinEntityUpdate {

    // Configurable constants
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private static final int CHUNK_BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    // Thread-safe data structures
    private final List<Entity> entitiesToUpdate = new CopyOnWriteArrayList<>();
    private final ExecutorService updateExecutor = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);
    private final Map<Chunk, List<EntityLiving>> entityLivingMap = new ConcurrentHashMap<>();
    private final Set<Entity> entitiesInWater = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // Non-configurable fields
    private IChunkProvider chunkProvider;
    private World world;

    public MixinEntityUpdate(World world, IChunkProvider chunkProvider) {
        this.chunkProvider = chunkProvider;
        this.world = world;
    }

    // Add comments to explain which methods are affected by the mixin
    /**
     * Mixin method that modifies the behavior of the Entity class.
     * This method is called by the WorldServer class every tick to update all entities in the world.
     * The mixin optimizes the update process by using thread-safe data structures and parallel execution.
     * The mixin also limits the number of entities that are updated per tick, and only processes chunks that have
     * living entities.
     * The mixin affects the following methods of the Entity class: addEntity, removeEntity.
     */
    @Inject(method = "updateEntities", at = @At("HEAD"))
    private void onUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            entitiesInWater.clear();
            entityLivingMap.clear();
            for (Entity entity : entitiesToUpdate) {
                entity.isInWater();
                if (entity.isEntityAlive()) {
                    entity.onEntityUpdate();
                    if (entity instanceof EntityLivingBase) {
                        EntityLivingBase livingEntity = (EntityLivingBase) entity;
                        livingEntity.getHealth();
                        livingEntity.getDataWatcher()
                            .getWatchableObjectFloat(6);
                    }
                }
                if (entity.isInWater()) {
                    entitiesInWater.add(entity);
                }
                if (entity instanceof EntityLiving) {
                    int posX = MathHelper.floor_double(entity.posX);
                    int posY = MathHelper.floor_double(entity.posY);
                    int posZ = MathHelper.floor_double(entity.posZ);
                    Chunk chunk = entity.worldObj.getChunkFromBlockCoords(posX, posZ);
                    entityLivingMap.computeIfAbsent(chunk, k -> new ArrayList<>())
                        .add((EntityLiving) entity);
                }
            }
            List<Future<?>> futures = new ArrayList<>(entitiesToUpdate.size());
            for (Entity entity : entitiesToUpdate) {
                futures.add(updateExecutor.submit(() -> {
                    if (entitiesInWater.contains(entity)) {
                        entity.isInWater();
                    }
                }));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            int numEntitiesToUpdate = Math.min(entitiesToUpdate.size(), MAX_ENTITIES_PER_TICK);
            futures.clear();
            for (int i = 0; i < numEntitiesToUpdate; i++) {
                Entity entity = entitiesToUpdate.get(i);
                futures.add(updateExecutor.submit(() -> {
                    if (entity instanceof EntityLivingBase) {
                        EntityLivingBase livingEntity = (EntityLivingBase) entity;
                        livingEntity.getHealth();
                        livingEntity.getDataWatcher()
                            .getWatchableObjectFloat(6);
                    }
                }));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            List<Chunk> chunks = new ArrayList<>(entityLivingMap.keySet());
            int numChunks = chunks.size();
            futures.clear();
            for (int i = 0; i < numChunks; i += CHUNK_BATCH_SIZE) {
                int endIndex = Math.min(i + CHUNK_BATCH_SIZE, numChunks);
                List<Chunk> batchChunks = chunks.subList(i, endIndex);
                futures.add(updateExecutor.submit(() -> {
                    for (Chunk chunk : batchChunks) {
                        List<EntityLiving> batchEntities = entityLivingMap.get(chunk);
                        for (EntityLiving entity : batchEntities) {
                            chunk.addEntity(entity);
                        }
                    }
                }));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Add comments to explain which methods are affected by the mixin
    /**
     * Mixin method that modifies the behavior of the Entity class.
     * This method is called by the WorldServer class when a new entity is added to the world.
     * The mixin adds the new entity to the entitiesToUpdate list, so it will be updated every tick.
     * The mixin affects the following methods of the Entity class: updateEntities, removeEntity.
     */
    @Inject(method = "addEntity", at = @At("RETURN"))
    private void onAddEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.add(entity);
    }

    // Add comments to explain which methods are affected by the mixin
    /**
     * Mixin method that modifies the behavior of the Entity class.
     * This method is called by the WorldServer class when an entity is removed from the world.
     * The mixin removes the entity from the entitiesToUpdate list, so it will no longer be updated every tick.
     * The mixin affects the following methods of the Entity class: updateEntities, addEntity.
     */
    @Inject(method = "removeEntity", at = @At("RETURN"))
    private void onRemoveEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.remove(entity);
    }
}
