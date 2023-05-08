package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;

    private final List<Entity> entitiesToUpdate = new CopyOnWriteArrayList<>();
    private final ThreadPoolExecutor updateExecutor = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        1L, TimeUnit.MINUTES,
        new LinkedBlockingQueue<Runnable>());

    private final Map<Chunk, List<EntityLiving>> entityLivingMap = new ConcurrentHashMap<>();
    private final Set<Entity> entitiesInWater = Collections.newSetFromMap(new ConcurrentHashMap<>());
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
                entityLivingMap.compute(chunk, (k, v) -> {
                    if (v == null) v = new ArrayList<>();
                    v.add((EntityLiving) entity);
                    return v;
                });
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
                throw new RuntimeException(e);
            }
        }
    }
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
    @Inject(method = "addEntity", at = @At("RETURN"))
    private void onAddEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.add(entity);
    }

    @Inject(method = "removeEntity", at = @At("RETURN"))
    private void onRemoveEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.remove(entity);
    }
}
