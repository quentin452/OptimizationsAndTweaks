package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderGlobal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.batching.BatchedParticles;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(RenderGlobal.class)
public abstract class MixinParticleManager {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    private final BlockingQueue<EntityFX> particleQueue = new LinkedBlockingQueue<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        SharedThreadPool.getExecutorService()
            .execute(this::drawLoop);
    }

    private void drawLoop() {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinParticle) {
            while (!Thread.currentThread()
                .isInterrupted()) {
                EntityFX[] particles = new EntityFX[BATCH_SIZE];
                final int[] count = { 0 };

                List<EntityFX> particleList = new ArrayList<>();
                particleQueue.drainTo(particleList, BATCH_SIZE);

                particleList.parallelStream()
                    .forEach(particle -> { particles[count[0]++] = particle; });

                if (count[0] > 0) {
                    try {
                        BatchedParticles.drawBatch(particles, count[0]);
                    } catch (Exception e) {
                        // Log the exception to the console
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void onAddEffect(EntityFX particle, CallbackInfo ci) {
        particle.setParticleTextureIndex(particle.getFXLayer());
        particleQueue.offer(particle);
        ci.cancel();
    }
}
