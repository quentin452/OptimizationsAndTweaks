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

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityUpdate {

    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final Queue<Entity> entitiesToUpdate = new ConcurrentLinkedQueue<>();
    private final ThreadPoolExecutor updateExecutor = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        1L,
        TimeUnit.MINUTES,
        new LinkedBlockingQueue<Runnable>());

    private final Map<Chunk, List<EntityLiving>> entityLivingMap = new HashMap<>();
    private final Set<Entity> entitiesInWater = Collections.newSetFromMap(new HashMap<>());
    private IChunkProvider chunkProvider;
    private World world;

    private long lastUpdateTime;

    public MixinEntityUpdate(World world, IChunkProvider chunkProvider) {
        this.chunkProvider = chunkProvider;
        this.world = world;
        this.lastUpdateTime = world.getTotalWorldTime();
    }

    private void processEntityUpdates(long time) {
        int numEntities = 0;
        while (!entitiesToUpdate.isEmpty() && numEntities < MAX_ENTITIES_PER_TICK) {
            Entity entity = entitiesToUpdate.poll();{
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase livingEntity = (EntityLivingBase) entity;
                    if (livingEntity.getHealth() > 0) {
                        livingEntity.onLivingUpdate();
                    }
                } else {
                    entity.onEntityUpdate();
                }
                if (entity.isInWater()) {
                    entitiesInWater.add(entity);
                }
                if (entity instanceof EntityLiving) {
                    int posX = (int) entity.posX;
                    int posY = (int) entity.posY;
                    int posZ = (int) entity.posZ;
                    Chunk chunk = entity.worldObj.getChunkFromBlockCoords(posX, posZ);
                    Entity finalEntity = entity;
                    entityLivingMap.compute(chunk, (k, v) -> {
                        if (v == null) {
                            v = new ArrayList<>();
                        }
                        v.add((EntityLiving) finalEntity);
                        return v;
                    });
                }
            }
            numEntities++;
        }
        this.lastUpdateTime = time;
    }

    private void processChunks(long time) {
        // Batch multiple chunk updates
        List<Chunk> chunksToUpdate = new ArrayList<>(entityLivingMap.keySet());
        entityLivingMap.clear();

        updateExecutor.submit(() -> {
            for (Chunk chunk : chunksToUpdate) {
                List<EntityLiving> entities = entityLivingMap.get(chunk);
                for (EntityLiving entity : entities) {
                    chunk.addEntity(entity);
                }
            }
        });
    }

    @Inject(method = "updateEntities", at = @At("HEAD"))
    private void onUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            long time = world.getTotalWorldTime();
            long timeElapsed = time - lastUpdateTime;
            int numEntitiesPerTick = (int) (timeElapsed * MAX_ENTITIES_PER_TICK / 20L);
            processEntityUpdates(time);
            if (!entityLivingMap.isEmpty()) {
                // Process chunks sequentially
                for (Chunk chunk : entityLivingMap.keySet()) {
                    List<EntityLiving> entities = entityLivingMap.get(chunk);
                    for (EntityLiving entity : entities) {
                        chunk.addEntity(entity);
                    }
                }
                entityLivingMap.clear();
            }
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
