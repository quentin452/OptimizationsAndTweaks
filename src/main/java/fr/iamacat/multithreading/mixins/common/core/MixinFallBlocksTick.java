package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinFallBlocksTick {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private static final ExecutorService THREAD_POOL = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    @Inject(method = "updateFallBlocks", at = @At("HEAD"))
    private void updateFallBlocks(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFallBlocksTick) {
            List<Entity> entities = ((World) (Object) this).loadedEntityList;
            List<Entity> fallingBlocks = new ArrayList<>();

            for (Entity entity : entities) {
                if (entity instanceof EntityFallingBlock) {
                    fallingBlocks.add(entity);
                }
            }

            for (int i = 0; i < fallingBlocks.size(); i += BATCH_SIZE) {
                final int start = i;
                final int end = Math.min(i + BATCH_SIZE, fallingBlocks.size());
                THREAD_POOL.execute(() -> tickFallingBlocks(fallingBlocks.subList(start, end)));
            }
        }
    }

    private void tickFallingBlocks(List<Entity> fallingBlocks) {
        for (Entity entity : fallingBlocks) {
            entity.onUpdate();
        }
    }
}
