package fr.iamacat.multithreading.mixins.client.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import net.minecraft.block.BlockFire;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntitiesRendering {

    private final ExecutorService executorService = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("Entity-Rendering-%d").build());
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    @Shadow
    protected abstract void bindEntityTexture(Entity entity);

    @Shadow
    public abstract void renderShadow(Entity entity, double x, double y, double z, float yaw, float partialTicks);


    @Inject(method = "doRender", at = @At("HEAD"))
    public void onDoRender(Entity entity, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFireTick) {
        // Skip rendering if entity is not visible or if rendering is already in progress
        if (!entity.shouldRenderInPass(MinecraftForgeClient.getRenderPass()) || MinecraftForgeClient.getRenderPass() == -1 || entity.isInvisible()) {
            return;
        }

        // Submit rendering task to the thread pool
        executorService.submit(() -> {
            try {
                // Bind entity texture
                bindEntityTexture(entity);

                // Render entity shadow
                renderShadow(entity, x, y, z, yaw, partialTicks);

                // Render entity
                renderEntities(entity.worldObj, Collections.singletonList(entity), RenderManager.instance, partialTicks);
            } catch (Exception e) {
                // Handle exceptions as appropriate
            }
        });
    }
    }
    public void renderEntities(World world, Collection<Entity> entities, RenderManager renderManager, float tickDelta) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFireTick) {
            int batchSize = BATCH_SIZE; // The maximum number of entities to render per batch
            List<List<Entity>> entityBatches = splitEntitiesIntoBatches(entities);

            // Submit rendering tasks to the thread pool using parallel stream
            List<CompletableFuture<Void>> renderingFutures = entityBatches.parallelStream()
                .map(entityBatch -> CompletableFuture.runAsync(() -> {
                    for (Entity entity : entityBatch) {
                        renderManager.renderEntityWithPosYaw(entity, entity.posX - RenderManager.renderPosX, entity.posY - RenderManager.renderPosY, entity.posZ - RenderManager.renderPosZ, entity.rotationYaw, tickDelta);
                    }
                }, executorService))
                .collect(Collectors.toList());

            // Wait for all rendering tasks to complete
            CompletableFuture.allOf(renderingFutures.toArray(new CompletableFuture[0]))
                .join();
        }
    }
    private List<List<Entity>> splitEntitiesIntoBatches(Collection<Entity> entities) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFireTick) {
            int batchSize = BATCH_SIZE; // The maximum number of entities to render per batch
            List<List<Entity>> entityBatches = new ArrayList<>();
            List<Entity> entityBatch = new ArrayList<>();
            for (Entity entity : entities) {
                entityBatch.add(entity);
                if (entityBatch.size() == batchSize) {
                    entityBatches.add(entityBatch);
                    entityBatch = new ArrayList<>();
                }
            }
            if (!entityBatch.isEmpty()) {
                entityBatches.add(entityBatch);
            }
            return entityBatches;
        }
        return new ArrayList<>(); // Return an empty list if MultithreadingandtweaksMultithreadingConfig.enableMixinFireTick is false
    }
}
