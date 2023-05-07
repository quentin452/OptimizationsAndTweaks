package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinEntityLightningBolt {

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Lightning-Bolt-%d")
            .build());

    private World worldObj;
    private double posX;
    private double posY;
    private double posZ;

    private MixinEntityLightningBolt(World world, double x, double y, double z, boolean effectOnly) {
        super();
        this.worldObj = world;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(World world, double x, double y, double z, CallbackInfo ci) {
        MultithreadingandtweaksMultithreadingConfig.enableMixinChunkPopulating = world.loadedEntityList != null
            && world.loadedEntityList.size() > MultithreadingandtweaksMultithreadingConfig.numberofcpus;
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityLightningBolt) {
            World world = this.worldObj;
            if (world == null || world.isRemote || world.loadedEntityList == null) {
                return;
            }
            // Use a thread-safe collection to store the entities
            List<Entity> entities = new ArrayList<>(world.loadedEntityList);
            ConcurrentLinkedQueue<List<Entity>> entityBatches = new ConcurrentLinkedQueue<>();

            // Split the entities into batches of size batch_size
            int batch_size = MultithreadingandtweaksMultithreadingConfig.batchsize;;
            for (int i = 0; i < entities.size(); i += batch_size) {
                int end = Math.min(i + batch_size, entities.size());
                List<Entity> batch = entities.subList(i, end);
                entityBatches.add(batch);
            }

            // Use a ForkJoinPool with the number of threads equal to the number of available processors
            int numThreads = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
            ForkJoinPool forkJoinPool = new ForkJoinPool(numThreads);

            // Process each batch in parallel
            forkJoinPool.submit(
                () -> entityBatches.parallelStream()
                    .forEach(batch -> {
                        for (Entity entity : batch) {
                            if (entity instanceof EntityLivingBase) {
                                double distanceSq = entity.getDistanceSq(posX, posY, posZ);
                                if (distanceSq <= 256.0D) {
                                    ((EntityLivingBase) entity)
                                        .onStruckByLightning((EntityLightningBolt) (Object) this);
                                }
                            }
                        }
                    }))
                .join();

            ci.cancel();
            forkJoinPool.shutdown();
        }
    }
}
