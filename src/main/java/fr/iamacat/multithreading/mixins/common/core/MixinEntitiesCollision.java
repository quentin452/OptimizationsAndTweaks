package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.AxisAlignedBB;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinEntitiesCollision {
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final ExecutorService collisionExecutor = Executors.newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    @Inject(at = @At("HEAD"), method = "collideWithNearbyEntities")
    private void overrideCollideWithNearbyEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesCollision) {
            World world = ((EntityLivingBase) (Object) this).worldObj;
            AxisAlignedBB boundingBox = ((EntityLivingBase) (Object) this).boundingBox.expand(0.2, 0.2, 0.2);

            List<Entity> entities = getEntitiesWithinAABBExcludingEntity(world, boundingBox);
            List<List<Entity>> entityBatches = createEntityBatches(entities, BATCH_SIZE);

            entityBatches.forEach(batch -> collisionExecutor.submit(() -> collideWithEntitiesBatch(batch))); // Process batches in a separate thread
        }
    }
    private List<Entity> getEntitiesWithinAABBExcludingEntity(World world, AxisAlignedBB boundingBox) {
        // Implement your custom logic here to retrieve the entities within the AABB
        // excluding the current entity

        // Example implementation:
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, boundingBox);
        entities.remove((Entity) (Object) this);
        return entities;
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
                if (entity instanceof EntityHorse || entity instanceof EntityPig || entity instanceof EntityMinecart || entity instanceof EntityBoat || entity instanceof EntityBat) {
                    if (entity.riddenByEntity != null) {
                        continue; // Skip collision if the entity is rideable and already being ridden
                    }
                }

                // Vanilla collision logic
                entity.applyEntityCollision((EntityLivingBase) (Object) this);
            }
        }
    }
}
