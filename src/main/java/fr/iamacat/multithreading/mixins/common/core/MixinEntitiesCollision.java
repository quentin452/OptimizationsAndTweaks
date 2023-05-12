package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
    private void batchCollisions(World world, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesCollision) {
            // Cancel vanilla method
            ci.cancel();

            AxisAlignedBB boundingBox = ((EntityLivingBase) (Object) this).boundingBox.expand(0.2, 0.2, 0.2);
            List<Entity> entities = getEntitiesWithinAABBExcludingEntity();
            List<List<Entity>> entityBatches = createEntityBatches(entities, BATCH_SIZE);

            // Process entity batches in parallel
            entityBatches.parallelStream()
                .forEach(this::collideWithEntitiesBatch);

            // Shutdown executor service
            collisionExecutor.shutdown();
        }
    }

    private void collideWithEntitiesBatch(List<Entity> batch) {
        for (Entity entity : batch) {
            // Custom collision logic
            entity.applyEntityCollision((EntityLivingBase) (Object) this);
        }
    }

    private final ExecutorService collisionExecutor = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    private void batchCollisionsAsync() throws InterruptedException {
        // Get entities within bounding box
        List<Entity> entities = getEntitiesWithinAABBExcludingEntity();

        // Batch entities
        List<List<Entity>> entityBatches = createEntityBatches(entities);

        // Process each batch in parallel
        entityBatches.parallelStream()
            .forEach(this::collideWithEntitiesBatch);

        // Shutdown executor
        collisionExecutor.shutdown();
        collisionExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
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
}
