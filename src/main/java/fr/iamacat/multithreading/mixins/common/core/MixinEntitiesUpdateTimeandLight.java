package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinEntitiesUpdateTimeandLight {

    ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Entity-Time-Light-%d")
            .build(),
        new ThreadPoolExecutor.CallerRunsPolicy() // Execute rejected tasks in the caller thread
    );

    @Inject(method = "updateTimeLightAndEntities", at = @At("HEAD"))
    private void updateTimeLightAndEntitiesBatched(boolean p_147456_1_, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesUpdateTimeandLight) {
            WorldServer worldServer = (WorldServer) (Object) this;
            int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize * 3;

            List<Entity> entities = new ArrayList<>(worldServer.loadedEntityList); // Create a copy of the entity list

            int entityCount = entities.size();
            int batchCount = (entityCount + batchSize - 1) / batchSize;

            for (int i = 0; i < batchCount; i++) {
                int startIndex = i * batchSize;
                if (entityCount <= startIndex) {
                    break; // No more entities to process
                }
                int endIndex = Math.min(startIndex + batchSize, entityCount);

                if (startIndex >= endIndex) {
                    break; // No more entities to process
                }

                final List<Entity> batch = entities.subList(startIndex, endIndex);

                this.executorService.submit(() -> {
                    for (Entity entity : batch) {
                        if (entity != null && entity.isEntityAlive()) {
                            try {
                                entity.onUpdate();
                                if (entity instanceof EntityPlayer) {
                                    EntityPlayer player = (EntityPlayer) entity;
                                    player.sendPlayerAbilities();
                                }
                            } catch (Exception e) {
                                CrashReport crashReport = CrashReport.makeCrashReport(e, "Ticking entity");
                                CrashReportCategory crashReportCategory = crashReport
                                    .makeCategory("Entity being ticked");
                                entity.addEntityCrashInfo(crashReportCategory);
                                throw new ReportedException(crashReport);
                            }
                        }
                    }
                });
            }
        }
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At("RETURN"))
    private void updateTimeLightAndEntitiesCleanup(boolean p_147456_1_, CallbackInfo ci) {
        // shut down the thread pool when done
        if (this.executorService != null) {
            this.executorService.shutdown();
            this.executorService = null;
        }
    }

    @Inject(method = "updateEntities", at = @At("HEAD"))
    private void updateEntitiesBatched(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesUpdateTimeandLight) {
            World world = (World) (Object) this;
            int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;

            List<Entity> entities = new ArrayList<>(world.loadedEntityList); // Create a copy of the entity list

            int entityCount = entities.size();
            int batchCount = (entityCount + batchSize - 1) / batchSize;

            if (!entities.isEmpty()) { // Check if the entities list is not empty
                for (int i = 0; i < batchCount; i++) {
                    int startIndex = i * batchSize;
                    int endIndex = Math.min(startIndex + batchSize, entityCount);

                    if (startIndex >= endIndex) {
                        break; // No more entities to process
                    }

                    final List<Entity> batch = new ArrayList<>(entities.subList(startIndex, endIndex));

                    this.executorService.submit(() -> {
                        for (Entity entity : batch) {
                            if (entity != null && entity.isEntityAlive()) { // Check for null and entity liveness
                                try {
                                    // entity.onUpdate();
                                } catch (Exception e) {
                                    CrashReport crashReport = CrashReport.makeCrashReport(e, "Entity update");
                                    CrashReportCategory crashReportCategory = crashReport
                                        .makeCategory("Entity being updated");
                                    entity.addEntityCrashInfo(crashReportCategory);
                                    throw new ReportedException(crashReport);
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
