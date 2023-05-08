package fr.iamacat.multithreading.mixins.common.core;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public class MixinEntitiesUpdateTimeandLight {

    private ExecutorService executor;

    @Inject(method = "updateTimeLightAndEntities", at = @At("HEAD"))
    private void updateTimeLightAndEntitiesBatched(boolean p_147456_1_, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesUpdateTimeandLight) {
            WorldServer worldServer = (WorldServer) (Object) this;
            int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize; // adjust this to suit your needs
            int entityCount = worldServer.loadedEntityList.size();
            int batchCount = (entityCount + batchSize - 1) / batchSize;

            // create a thread pool to run the batches
            this.executor = Executors.newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

            // submit each batch to the thread pool
            for (int i = 0; i < batchCount; i++) {
                int startIndex = i * batchSize;
                int endIndex = Math.min(startIndex + batchSize, entityCount);
                final List<Entity> batch = worldServer.loadedEntityList.subList(startIndex, endIndex);
                this.executor.submit(new Runnable() {

                    @Override
                    public void run() {
                        for (Entity entity : batch) {
                            if (entity != null) {
                                try {
                                    entity.onUpdate();
                                    if (entity instanceof EntityPlayer) {
                                        EntityPlayer player = (EntityPlayer) entity;
                                        player.sendPlayerAbilities();
                                    }
                                } catch (Throwable throwable) {
                                    CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Ticking entity");
                                    CrashReportCategory crashReportCategory = crashReport
                                        .makeCategory("Entity being ticked");
                                    entity.addEntityCrashInfo(crashReportCategory);
                                    throw new ReportedException(crashReport);
                                }
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
        if (this.executor != null) {
            this.executor.shutdown();
            this.executor = null;
        }
    }
}
