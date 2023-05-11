package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

import javax.swing.*;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntitiesCollision {
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Collision-Thread-%d").build()
    );

    @Shadow
    public abstract void collideWithEntity(Entity entity);

    @Inject(at = @At("RETURN"), method = "collideWithNearbyEntities")
    private void getNearbyEntitiesMultithreaded(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesCollision) {
            World world = ((EntityLivingBase) (Object) this).worldObj;
            AxisAlignedBB boundingBox = ((EntityLivingBase) (Object) this).boundingBox.expand(0.2, 0.2, 0.2);

            List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity((EntityLivingBase) (Object) this, boundingBox);
            List<List<Entity>> entityBatches = createEntityBatches(entities, BATCH_SIZE);

            for (List<Entity> batch : entityBatches) {
                executorService.execute(() -> collideWithEntitiesBatch(batch));
            }
        }
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
                if (entity instanceof EntityHorse || entity instanceof EntityPig || entity instanceof EntityMinecart || entity instanceof EntityBoat || entity instanceof EntityBat) {
                    if (entity.riddenByEntity != null) {
                        continue; // Skip collision if the entity is rideable and already being ridden
                    }
                }

                collideWithEntity(entity);
            }
        }
    }

    public void dispose() {
        executorService.shutdown();
    }
}
