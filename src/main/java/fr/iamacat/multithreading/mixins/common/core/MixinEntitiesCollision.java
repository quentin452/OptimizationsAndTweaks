package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinEntitiesCollision {

    // Fixme todo
    @Unique
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private AxisAlignedBB boundingBox;

    @Inject(at = @At("HEAD"), method = "collideWithNearbyEntities")
    private void batchCollisions(World world, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesCollision) {
            // Cancel vanilla method
            ci.cancel();

            // Perform batched collisions asynchronously
            try {
                batchCollisionsAsync();
            } catch (InterruptedException e) {
                // Handle the interruption
                e.printStackTrace();
            }
        }
    }

    @Unique
    private final ExecutorService collisionExecutor = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    @Unique
    private void batchCollisionsAsync() throws InterruptedException {
        // Get entities within bounding box
        List<Entity> entities = getEntitiesWithinAABBExcludingEntity();

        // Batch entities
        List<List<Entity>> entityBatches = createEntityBatches(entities, BATCH_SIZE);

        // Process each batch in parallel
        for (List<Entity> batch : entityBatches) {
            collisionExecutor.submit(() -> collideWithEntitiesBatch(batch));
        }

        // Shutdown executor
        collisionExecutor.shutdown();
        collisionExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    @Unique
    private void collideWithEntitiesBatch(List<Entity> batch) {
        for (Entity entity : batch) {
            // Custom collision logic
            entity.applyEntityCollision((EntityLivingBase) (Object) this);
        }
    }

    @Unique
    private void collideWithNearbyEntitiesAsync() {
        World world = ((EntityLivingBase) (Object) this).worldObj;
        List<Entity> entities = getEntitiesWithinAABBExcludingEntity();
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

    @Unique
    private void checkCollisions(World world, AxisAlignedBB boundingBox) {
        // Get nearby entities
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, boundingBox);

        // Batch entities into lists of size BATCH_SIZE
        List<List<Entity>> batches = createEntityBatches(entities, BATCH_SIZE);

        // Check collisions for each batch
        for (List<Entity> batch : batches) {
            collideWithBatch(batch);
        }
    }

    @Unique
    private void collideWithBatch(List<Entity> batch) {
        for (Entity entity : batch) {
            if (!entity.equals(this)) {
                // Check collision
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "collideWithNearbyEntities")
    private void cancelVanillaCollision(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesCollision) {

            // Cancel vanilla collision logic
            ci.cancel();
        }
    }

    @Unique
    private List<Entity> getEntitiesWithinAABBExcludingEntity() {
        World world = ((EntityLivingBase) (Object) this).worldObj;
        AxisAlignedBB boundingBox = ((EntityLivingBase) (Object) this).boundingBox.expand(0.2, 0.2, 0.2);

        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, boundingBox);
        entities.remove((Entity) (Object) this);

        return entities;
    }

    @Unique
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
