package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinEntitiesCollision {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    @Inject(at = @At("HEAD"), method = "collideWithNearbyEntities")
    private void batchCollisions(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesCollision) {
            ci.cancel();
            collisionExecutor.execute(this::batchCollisionsAsync);
        }
    }
    private final ExecutorService collisionExecutor = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    private void batchCollisionsAsync() {
        // Get entities within bounding box
        List<Entity> entities = getEntitiesWithinAABBExcludingEntity();

        // Batch entities
        List<List<Entity>> entityBatches = createEntityBatches(entities);

        // Process each batch in parallel
        entityBatches.parallelStream().forEach(this::collideWithEntitiesBatch);

        // Shutdown executor
        collisionExecutor.shutdown();
    }
    private void collideWithNearbyEntitiesAsync() {
        World world = ((EntityLivingBase) (Object) this).worldObj;
        AxisAlignedBB boundingBox = ((EntityLivingBase) (Object) this).boundingBox.expand(0.2, 0.2, 0.2);

        List<Entity> entities = getEntitiesWithinAABBExcludingEntity(world, boundingBox);
        List<List<Entity>> entityBatches = createEntityBatches(entities, BATCH_SIZE);

        entityBatches.parallelStream()
            .forEach(this::collideWithEntitiesBatch); // Process batches in parallel

        // Shutdown the executor service to release resources
        collisionExecutor.shutdown();
        try {
            collisionExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // Handle the interruption
        }
    }
    private List<Entity> getEntitiesWithinAABBExcludingEntity(World world, AxisAlignedBB boundingBox) {
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, boundingBox);
        entities.remove((Entity) (Object) this);

        return entities;
    }

    private List<Entity> getEntitiesWithinAABBExcludingEntity() {
        World world = ((EntityLivingBase) (Object) this).worldObj;
        AxisAlignedBB boundingBox = ((EntityLivingBase) (Object) this).boundingBox.expand(0.2, 0.2, 0.2);

        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, boundingBox);
        entities.remove((Entity) (Object) this);

        return entities;
    }

    private List<List<Entity>> createEntityBatches(List<Entity> entities) {
        List<List<Entity>> entityBatches = new ArrayList<>();

        int numBatches = (int) Math.ceil(entities.size() / (double) BATCH_SIZE);
        for (int i = 0; i < numBatches; i++) {
            int fromIndex = i * BATCH_SIZE;
            int toIndex = Math.min(fromIndex + BATCH_SIZE, entities.size());
            List<Entity> batch = entities.subList(fromIndex, toIndex);
            entityBatches.add(batch);
        }

        return entityBatches;
    }

    private List<List<Entity>> createEntityBatches(List<Entity> entities, int batchSize) {
        List<List<Entity>> entityBatches = new ArrayList<>();
        int numBatches = (int) Math.ceil(entities.size() / (double) batchSize);

        for (int i = 0; i < numBatches; i++) {
            int fromIndex = i * batchSize;
            int toIndex = Math.min(fromIndex + batchSize, entities.size());
            List<Entity> batch = entities.subList(fromIndex, toIndex);
            entityBatches.add(batch);
        }

        return entityBatches;
    }

    private void collideWithEntitiesBatch(List<Entity> batch) {
        for (Entity entity : batch) {
            if (entity != null && entity.isEntityAlive() && entity.canBeCollidedWith()) {
                // Custom collision logic
                if (entity instanceof EntityHorse || entity instanceof EntityPig
                    || entity instanceof EntityMinecart
                    || entity instanceof EntityBoat
                    || entity instanceof EntityBat) {
                    if (entity.riddenByEntity != null) {
                        continue; // Skip collision if the entity is rideable and already being ridden
                    }
                }

                // Cancel vanilla collision logic
                entity.applyEntityCollision((EntityLivingBase) (Object) this);
            }
        }
    }
}
