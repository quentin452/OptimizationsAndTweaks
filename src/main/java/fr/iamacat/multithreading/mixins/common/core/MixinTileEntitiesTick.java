package fr.iamacat.multithreading.mixins.common.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Iterables.partition;

@Mixin(WorldServer.class)
public abstract class MixinTileEntitiesTick {
    public abstract List<TileEntity> loadedTileEntityList();
    private final ExecutorService executorService = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("TileEntity-Tick-%d").build()
    );

    private CopyOnWriteArrayList<TileEntity> tileEntityList = new CopyOnWriteArrayList<>();

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    @Inject(method = "updateEntities", at = @At("HEAD"))
    private void onPreUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinTileEntitiesTick) {
            // Save a copy of the tile entity list to avoid concurrent modification
            tileEntityList = new CopyOnWriteArrayList<>(this.loadedTileEntityList());
        }
    }

    @Inject(method = "updateEntities", at = @At("RETURN"))
    private void onPostUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinTileEntitiesTick) {
            // Submit tile entity ticking tasks to the executor service using parallel stream
            int batchSize = BATCH_SIZE;
            Map<Class<? extends TileEntity>, List<TileEntity>> groupedTileEntities = new ConcurrentHashMap<>();

            for (TileEntity tileEntity : tileEntityList) {
                if (!tileEntity.isInvalid()) {
                    Class<? extends TileEntity> tileEntityClass = tileEntity.getClass();
                    if (!groupedTileEntities.containsKey(tileEntityClass)) {
                        groupedTileEntities.put(tileEntityClass, new ArrayList<>());
                    }
                    groupedTileEntities.get(tileEntityClass).add(tileEntity);
                }
            }

            for (List<TileEntity> batch : groupedTileEntities.values()) {
                for (List<TileEntity> subBatch : partition(batch, batchSize)) {
                    executorService.submit(() -> {
                        for (TileEntity tileEntity : subBatch) {
                            try {
                                if (!tileEntity.isInvalid()) {
                                    tileEntity.updateEntity();
                                }
                            } catch (Throwable t) {
                                // Catch and print any exceptions thrown during tile entity ticking
                                t.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }
}
