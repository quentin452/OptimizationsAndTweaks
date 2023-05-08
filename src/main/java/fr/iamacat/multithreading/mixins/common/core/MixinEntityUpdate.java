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

    private void processEntityUpdates(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity.isEntityAlive()) {
                entity.onEntityUpdate();
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase livingEntity = (EntityLivingBase) entity;
                    livingEntity.getHealth();
                    livingEntity.getDataWatcher()
                        .getWatchableObjectFloat(3);
                }
            }
            if (entity.isInWater()) {
                entitiesInWater.add(entity);
            }
            if (entity instanceof EntityLiving) {
                int posX = (int) entity.posX;
                int posY = (int) entity.posY;
                int posZ = (int) entity.posZ;
                Chunk chunk = entity.worldObj.getChunkFromBlockCoords(posX, posZ);
                entityLivingMap.computeIfAbsent(chunk, k -> new ArrayList<>())
                    .add((EntityLiving) entity);
            }
        }
    }

    private void processChunks(List<Chunk> chunks) {
        int numChunks = Math.min(chunks.size(), 8);
        List<Future<?>> futures = new ArrayList<>(numChunks);
        for (int i = 0; i < numChunks; i++) {
            Chunk chunk = chunks.get(i);
            futures.add(updateExecutor.submit(() -> {
                List<EntityLiving> batchEntities = entityLivingMap.get(chunk);
                for (EntityLiving entity : batchEntities) {
                    chunk.addEntity(entity);
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

// Add comments to explain which methods are affected by the mixin
    /**
     * Mixin method that modifies the behavior of the Entity class.
     * This method is called by the WorldServer class every tick to update all entities in the world.
     * The mixin optimizes the update process by using thread-safe data structures and parallel execution.
     * The mixin also limits the number of entities and chunks that are updated per tick, and only processes chunks that have
     * living entities.
     * The mixin affects the following methods of the Entity class: addEntity, removeEntity.
     */
    @Inject(method = "updateEntities", at = @At("HEAD"))
    private void onUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            entitiesInWater.clear();
            entityLivingMap.clear();
            int numEntities = Math.min(entitiesToUpdate.size(), MAX_ENTITIES_PER_TICK);
            processEntityUpdates(entitiesToUpdate.subList(0, numEntities));
            List<Chunk> chunks = new ArrayList<>(entityLivingMap.keySet());
            processChunks(chunks);
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
