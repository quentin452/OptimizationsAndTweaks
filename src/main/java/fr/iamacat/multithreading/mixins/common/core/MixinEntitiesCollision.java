package fr.iamacat.multithreading.mixins.common.core;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(Entity.class)
public abstract class MixinEntitiesCollision {

    private static final ExecutorService threadPool = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    @Inject(method = "collideWithNearbyEntities", at = @At("HEAD"), cancellable = true)
    private void collideWithNearbyEntitiesMultithreaded(CallbackInfo callbackInfo) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesCollision) {
            Entity entity = (Entity) (Object) this;
            AxisAlignedBB boundingBox = entity.boundingBox.expand(0.2, 0.2, 0.2);
            threadPool.execute(() -> {
                List<Entity> nearbyEntities = entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, boundingBox);

                for (Entity nearbyEntity : nearbyEntities) {
                    if (entity.boundingBox.intersectsWith(nearbyEntity.boundingBox)) {}
                }
            });
        }
        callbackInfo.cancel();
    }
}
