package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityUpdate {
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final ConcurrentLinkedQueue<Entity> entitiesToUpdate = new ConcurrentLinkedQueue<>();
    private final ForkJoinPool updateExecutor = ForkJoinPool.commonPool();
    private final Map<Chunk, List<EntityLiving>> entityLivingMap = new ConcurrentHashMap<>();
    private final Set<Entity> entitiesInWater = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private ChunkProviderServer chunkProvider;
    private World world;
    private long lastUpdateTime;

    public MixinEntityUpdate(World world, ChunkProviderServer chunkProvider) {
        this.chunkProvider = chunkProvider;
        this.world = world;
        this.lastUpdateTime = world.getTotalWorldTime();
    }

    private void processEntityUpdates(long time) {
        for (int numEntities = 0; numEntities < MAX_ENTITIES_PER_TICK && !entitiesToUpdate.isEmpty(); numEntities++) {
            Entity entity = entitiesToUpdate.poll();
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
                Chunk chunk = world.getChunkFromBlockCoords(posX, posZ);
                entityLivingMap.compute(chunk, (k, v) -> {
                    if (v == null) {
                        v = new ArrayList<>();
                    }
                    v.add((EntityLiving) entity);
                    return v;
                });
            }
        }
        this.lastUpdateTime = time;
        entitiesToUpdate.clear();
    }

    private void processChunks(long time) {
        // Batch multiple chunk updates
        List<Chunk> chunksToUpdate = new ArrayList<>(entityLivingMap.keySet());
        entityLivingMap.clear();

        updateExecutor.submit(() -> {
            try {
                // Process chunks
            } catch (Exception e) {
                e.printStackTrace();
                // Log exception
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
                // Process chunks asynchronously in a separate thread
                processChunks(time);
            }
        }
    }

    @Inject(method = "updateEntityWithOptionalForce", at = @At(value = "HEAD", target = "Lnet/minecraft/entity/EntityLivingBase;onLivingUpdate()V"))
    private void enqueueEntityUpdate(double x, double y, double z, boolean doBlockCollisions, boolean canBePushed, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            entitiesToUpdate.add((Entity) (Object) this);
        }
    }

    @ModifyArg(method = "updateEntityWithOptionalForce", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
    private Entity LivingOnUpdate(Entity entity) {
        return entity != (Entity) (Object) this ? entity : null;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void batchOnLivingUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            // Update entities in batches
            while (entitiesToUpdate.size() > 0 && entitiesToUpdate.size() <= MAX_ENTITIES_PER_TICK) {
                Entity entity = entitiesToUpdate.poll();
                updateEntityBatch(entity);
            }
        }
    }

    private void updateEntityBatch(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).onLivingUpdate();
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
