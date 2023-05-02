package fr.iamacat.multithreading.mixins.client.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Tile-Entity-%d").build());

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    private World world;
    private List<TileEntity> tileEntities;
    private final Object lock = new Object();

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        this.world = world;
        this.tileEntities = new ArrayList<>();
    }

    @Inject(method = "func_147455_a", at = @At("RETURN"))
    public void addTileEntity(TileEntity tileEntity) {
        synchronized (lock) {
            this.tileEntities.add(tileEntity);
        }
    }

    @Inject(method = "process", at = @At("RETURN"))
    public void process(CallbackInfo ci) {
        // Split the tile entities into batches
        List<List<TileEntity>> batches = splitIntoBatches(this.tileEntities);

        // Process each batch using a for loop
        for (List<TileEntity> batch : batches) {
            processBatch(batch);
        }
    }

    private void processBatch(List<TileEntity> batch) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinTileEntities) {
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
    }

    private static <T> List<List<T>> splitIntoBatches(List<T> list) {
        int numBatches = (int) Math.ceil((double) list.size() / BATCH_SIZE);
        return IntStream.range(0, numBatches)
            .mapToObj(i -> list.subList(i * BATCH_SIZE, Math.min(list.size(), (i + 1) * BATCH_SIZE)))
            .collect(Collectors.toList());
    }

    @Inject(method = "renderTileEntities", at = @At("HEAD"))
    public void renderTileEntities(World world, List<TileEntity> tileEntities, CallbackInfo ci) {
        // Use a ForkJoinPool with the number of threads equal to the number of available processors
        ForkJoinPool pool = new ForkJoinPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

        // Process each tile entity in parallel
        pool.submit(
            () -> tileEntities.parallelStream()
                .forEach(tileEntity -> {
                    if (tileEntity != null) {
                        world.addTileEntity(tileEntity);
                    }
                }))
            .join();

        pool.shutdown();
    }

    public void render(TileEntity tileentity, float partialTicks, int destroyStage, double x, double y, double z) {
        double dx = tileentity.xCoord - x;
        double dy = tileentity.yCoord - y;
        double dz = tileentity.zCoord - z;
        double distanceSq = dx * dx + dy * dy + dz * dz;
        if (distanceSq < 4096.0D) {
            // Use multithreaded rendering
            List<TileEntity> tileEntities = Collections.singletonList(tileentity);
            tileEntities.parallelStream()
                .forEach(te -> {
                    GL11.glPushMatrix();
                    GL11.glTranslated(te.xCoord + 0.5 - x, te.yCoord + 0.5 - y, te.zCoord + 0.5 - z);
                    TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(te);
                    if (renderer != null) {
                        if (te.shouldRenderInPass(destroyStage)) {
                            renderer.renderTileEntityAt(te, x, y, z, partialTicks);
                        }
                    }
                    GL11.glPopMatrix();
                });
        }
    }
}
