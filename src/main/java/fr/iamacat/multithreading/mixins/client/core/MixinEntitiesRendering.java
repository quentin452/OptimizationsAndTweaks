package fr.iamacat.multithreading.mixins.client.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import cpw.mods.fml.common.FMLLog;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntitiesRendering {

    // Fixme todo
    @Unique
    private final ConcurrentLinkedQueue<Entity> entityQueue = new ConcurrentLinkedQueue<>();
    @Unique
    private final ExecutorService executorService = Executors.newFixedThreadPool(
        MultithreadingandtweaksConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("Entity-Rendering-%d")
            .build());
    @Unique
    private static final int BATCH_SIZE = MultithreadingandtweaksConfig.batchsize;
    @Unique
    private long lastRenderTime = System.nanoTime();

    @Unique
    private int tickCounter;

    @Inject(method = "doRender", at = @At("HEAD"), remap = false)
    public void onDoRender(Entity entity, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntitiesRendering) {
            // Skip rendering if entity is not visible or if rendering is already in progress
            if (!entity.shouldRenderInPass(MinecraftForgeClient.getRenderPass())
                || MinecraftForgeClient.getRenderPass() == -1
                || entity.isInvisible()) {
                return;
            }

            // Add entity to queue
            entityQueue.add(entity);

            // Submit rendering task to the thread pool if not already running
            if (entityQueue.size() >= BATCH_SIZE) {
                executorService.submit(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            renderEntities(entityQueue);
                        } catch (Exception e) {
                            FMLLog.getLogger()
                                .error("Error rendering entities", e);
                            throw e;
                        } finally {
                            entityQueue.clear();
                        }
                    }
                });
            }
        }
    }

    @Unique
    public void renderEntities(Collection<Entity> entities) {
        // Divide entities into batches
        List<List<Entity>> entityBatches = new ArrayList<>();
        int entityCount = entities.size();
        int batchSize = (int) Math.ceil((double) entityCount / BATCH_SIZE);
        Iterator<Entity> entityIterator = entities.iterator();
        for (int i = 0; i < BATCH_SIZE; i++) {
            List<Entity> entityBatch = new ArrayList<>();
            for (int j = 0; j < batchSize; j++) {
                if (entityIterator.hasNext()) {
                    entityBatch.add(entityIterator.next());
                }
            }
            if (!entityBatch.isEmpty()) {
                entityBatches.add(entityBatch);
            }
        }

        // Render entities in batches
        RenderManager renderManager = RenderManager.instance;
        tickCounter++; // Increment the tick counter
        long currentTime = System.nanoTime();
        float tickDelta = (currentTime - lastRenderTime) / 1000000000.0f;
        lastRenderTime = currentTime;
        for (List<Entity> entityBatch : entityBatches) {
            renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            renderManager.renderEngine.bindTexture(TextureMap.locationItemsTexture);
            GL11.glPushMatrix();
            for (Entity entity : entityBatch) {
                renderManager.renderEntityStatic(entity, tickDelta, true);
            }
            GL11.glPopMatrix();
        }
    }
}
