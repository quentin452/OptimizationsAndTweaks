package fr.iamacat.multithreading.mixins.client.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntitiesRendering {

    private final ExecutorService executorService = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("Entity-Rendering-%d")
            .build());
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    protected abstract void myBindEntityTexture(Entity entity);

    protected abstract void myRenderShadow(Entity entity, double x, double y, double z, float yaw, float partialTicks);

    @Inject(method = "doRender", at = @At("HEAD"))
    public void onDoRender(Entity entity, double x, double y, double z, float yaw, float partialTicks,
        CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFireTick) {
            // Skip rendering if entity is not visible or if rendering is already in progress
            if (!entity.shouldRenderInPass(MinecraftForgeClient.getRenderPass())
                || MinecraftForgeClient.getRenderPass() == -1
                || entity.isInvisible()) {
                return;
            }

            // Submit rendering task to the thread pool
            executorService.submit(() -> {
                try {
                    // Bind entity texture
                    myBindEntityTexture(entity);

                    // Render entity shadow
                    myRenderShadow(entity, x, y, z, yaw, partialTicks);

                    // Render entity
                    renderEntities(
                        entity.worldObj,
                        Collections.singletonList(entity),
                        RenderManager.instance,
                        partialTicks);
                } catch (Exception e) {
                    // Handle exceptions as appropriate
                }
            });
        }
    }

    public void renderEntities(World world, Collection<Entity> entities, RenderManager renderManager, float tickDelta) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesRendering) {
            List<Entity> entityList = new ArrayList<>(entities);
            int entityCount = entityList.size();
            if (entityCount == 0) {
                return;
            }

            // Compute the number of tasks to use based on the number of available processors
            int taskCount = Math.min(entityCount, ForkJoinPool.getCommonPoolParallelism());
            int batchSize = (int) Math.ceil((double) entityCount / taskCount);

            // Split entities into batches
            List<List<Entity>> entityBatches = new ArrayList<>();
            for (int i = 0; i < entityCount; i += batchSize) {
                int end = Math.min(i + batchSize, entityCount);
                entityBatches.add(entityList.subList(i, end));
            }

            // Submit rendering tasks to the ForkJoinPool
            ForkJoinPool.commonPool()
                .execute(() -> {
                    entityBatches.parallelStream()
                        .forEach(entityBatch -> {
                            for (Entity entity : entityBatch) {
                                renderManager.renderEntityWithPosYaw(
                                    entity,
                                    entity.posX - RenderManager.renderPosX,
                                    entity.posY - RenderManager.renderPosY,
                                    entity.posZ - RenderManager.renderPosZ,
                                    entity.rotationYaw,
                                    tickDelta);
                            }
                        });
                });
        }
    }
}
