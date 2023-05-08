package fr.iamacat.multithreading.mixins.client.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(TileEntity.class)
public abstract class MixinTileEntities {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private static final ExecutorService THREAD_POOL = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    private World world;
    private List<TileEntity> tileEntities;

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        this.world = world;
        this.tileEntities = new ArrayList<>();
    }

    public void addTileEntity(TileEntity tileEntity) {
        this.tileEntities.add(tileEntity);
    }

    public void processTileEntities() {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinTileEntities) {
        // Split the tile entities into batches
        List<List<TileEntity>> batches = splitIntoBatches(this.tileEntities);

        // Process each batch using a thread pool
        List<Future<?>> futures = new ArrayList<>();
        for (List<TileEntity> batch : batches) {
            futures.add(THREAD_POOL.submit(() -> processBatch(batch)));
        }

        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                }
            }
        }
    }

    private void processBatch(List<TileEntity> batch) {
        for (TileEntity tileEntity : batch) {
            // Process each tile entity
            tileEntity.updateEntity();
            NBTTagCompound tag = new NBTTagCompound();
            tileEntity.writeToNBT(tag);
            this.world.setTileEntity(
                tileEntity.xCoord,
                tileEntity.yCoord,
                tileEntity.zCoord,
                TileEntity.createAndLoadEntity(tag));
        }
    }

    private static <T> List<List<T>> splitIntoBatches(List<T> list) {
        int numBatches = (int) Math.ceil((double) list.size() / BATCH_SIZE);
        return IntStream.range(0, numBatches)
            .mapToObj(i -> list.subList(i * BATCH_SIZE, Math.min(list.size(), (i + 1) * BATCH_SIZE)))
            .collect(Collectors.toList());
    }

    @Inject(method = "renderTileEntities", at = @At("HEAD"))
    public void renderTileEntities(World world, List<TileEntity> tileEntities, CallbackInfo ci) {
        // Process each tile entity in parallel using a thread pool
        List<Future<?>> futures = new ArrayList<>();
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity != null) {
                futures.add(THREAD_POOL.submit(() -> renderTileEntity(tileEntity)));
            }
        }

        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void renderTileEntity(TileEntity tileentity) {
        GL11.glPushMatrix();
        GL11.glTranslated(
            tileentity.xCoord + 0.5 - RenderManager.renderPosX,
            tileentity.yCoord + 0.5 - RenderManager.renderPosY,
            tileentity.zCoord + 0.5 - RenderManager.renderPosZ);
        TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tileentity);
        if (renderer != null) {
            renderer.renderTileEntityAt(tileentity, 0, 0, 0, 0);
        }
        GL11.glPopMatrix();
    }
}
