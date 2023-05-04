package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinTileEntitiesTick {

    private final ExecutorService executorService = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("TileEntity-Tick-%d")
            .build());

    private List<TileEntity> tileEntitiesToTick = new ArrayList<>();
    private int tickIndex = 0;
    private int tickPerBatch = MultithreadingandtweaksMultithreadingConfig.batchsize;

    @Inject(method = "updateTileEntities", at = @At("HEAD"))
    public void onUpdateTileEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinTileEntitiesTick) {
            WorldServer world = (WorldServer) (Object) this;

            synchronized (tileEntitiesToTick) {
                if (tileEntitiesToTick.isEmpty()) {
                    // Get all tile entities in the world
                    for (Object obj : world.loadedTileEntityList) {
                        if (obj instanceof TileEntity) {
                            tileEntitiesToTick.add((TileEntity) obj);
                        }
                    }
                }

                int endIndex = Math.min(tickIndex + tickPerBatch, tileEntitiesToTick.size());
                List<TileEntity> currentBatch = tileEntitiesToTick.subList(tickIndex, endIndex);

                for (TileEntity tileEntity : currentBatch) {
                    executorService.submit(() -> {
                        try {
                            tileEntity.updateEntity();
                        } catch (Throwable t) {
                            // Handle exceptions as appropriate
                        }
                    });
                }

                tickIndex += tickPerBatch;
                if (tickIndex >= tileEntitiesToTick.size()) {
                    tickIndex = 0;
                    tileEntitiesToTick.clear();
                }
            }
        }
    }
}
