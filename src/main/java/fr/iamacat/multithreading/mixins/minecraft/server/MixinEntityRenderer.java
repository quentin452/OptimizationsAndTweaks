package fr.iamacat.multithreading.mixins.minecraft.server;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    private WorldClient world;
    private ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private List<Entity> spawnQueue = new ArrayList<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // Process the spawn queue every 10 ticks
        if (world.getTotalWorldTime() % 10 == 0) {
            spawnMobsInQueue();
        }
    }

    @Redirect(method = "renderWorld",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;doRenderEntities(Lnet/minecraft/entity/Entity;DDDFFZ)V"))
    private void redirectDoRenderEntities(RenderGlobal renderGlobal, Entity renderViewEntity, double partialTicks,
                                          double cameraX, double cameraY, double cameraZ, float frameDelta, boolean isInFrustum) {
        // Add mobs to the spawn queue instead of spawning them immediately
        for (Object entity : world.loadedEntityList) {
            if (entity instanceof EntityMob) {
                spawnQueue.add((Entity) entity);
            }
        }
    }

    private void spawnMobsInQueue() {
        if (spawnQueue.isEmpty()) {
            return;
        }

        // Spawn mobs in batches of 10 using ThreadPoolExecutor
        int batchSize = 10;
        List<Entity> batch = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize && !spawnQueue.isEmpty(); i++) {
            batch.add(spawnQueue.remove(0));
        }

        for (Entity entity : batch) {
            executorService.execute(() -> entity.onEntityUpdate());
        }
    }

    @Final
    public void finalizeMixin() {
        executorService.shutdown();
    }
}
