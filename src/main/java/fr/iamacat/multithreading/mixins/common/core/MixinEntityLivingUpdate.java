package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = EntityLivingBase.class, priority = 1100)
public abstract class MixinEntityLivingUpdate {
    private EntityLivingBase entityObject;
    @Shadow
    public abstract float getHealth();

    private double strafe;
    private double forward;
    private float friction;

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        r -> {
            Runnable wrappedRunnable = () -> {
                try {
                    r.run();
                } catch (Exception e) {
                    // Handle exception
                    e.printStackTrace();
                }
            };
            return new Thread(wrappedRunnable, "Entity-Living-Update-%d" + MixinEntityLivingUpdate.this.hashCode());
        });
    private final Map<net.minecraft.world.chunk.Chunk, List<EntityLiving>> entityLivingMap = new ConcurrentHashMap<>();
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;

    private final CopyOnWriteArrayList<MixinEntityLivingUpdate> entitiesToUpdate = new CopyOnWriteArrayList<MixinEntityLivingUpdate>();
    private ChunkProviderServer chunkProvider;

    private World world;
    private long lastUpdateTime;
    private Set<MixinEntityLivingUpdate> processedEntities;
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(World worldIn, CallbackInfo ci) {
        this.entityObject = (EntityLivingBase) (Object) this;
        processedEntities = new HashSet<>();
    }

    public MixinEntityLivingUpdate(EntityLivingBase entity, World world, ChunkProviderServer chunkProvider) {
        this.entityObject = entity;
        this.chunkProvider = chunkProvider;
        this.world = world;
        this.lastUpdateTime = world.getTotalWorldTime();
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    private void onLivingUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityLivingUpdate) {
            // Cancel vanilla method
            ci.cancel();
            // Process entity updates
            // cause crash when enabled
          // processEntityUpdates(world.getTotalWorldTime());
        }
    }

   private void processUpdates() {
        executorService.submit(() -> {
            try {
                // Process entity updates here
            } catch (Exception e) {
                // Handle exception
            }
        });
    }


    private void moveEntities(double strafe, double forward, float friction) {
        executorService.submit(() -> {
            try {
                for (MixinEntityLivingUpdate entity : entitiesToUpdate) {
                    if (entity != null) {
                        entity.entityObject.moveEntity(strafe, forward, friction);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
   }

private void processEntityUpdates(long time) {
    // Get the list of entities to process
    List<MixinEntityLivingUpdate> entitiesToProcess = new ArrayList<>(entitiesToUpdate);

    for (MixinEntityLivingUpdate entity : entitiesToProcess) {
        // Check if entity has already been processed
        if (processedEntities.contains(entity)) continue;
        try {
            // Process entity...
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Add entity to processed set (do not remove from list)
        if (processedEntities == null) {
            processedEntities = new HashSet<>();
        }
        processedEntities.add(entity);
    }
    this.lastUpdateTime = time;
}

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityLivingUpdate) {
            processChunks(world.getTotalWorldTime());
        }
    }
    private void processChunks(long time) {
        // Remove processed entities from list (thread-safe in pool)
        entitiesToUpdate.removeAll(processedEntities);

        // Clear processed set
        processedEntities.clear();

        // Get the chunks to update
        List<net.minecraft.world.chunk.Chunk> chunksToUpdate = new ArrayList<>(entityLivingMap.keySet());

        // Process chunk asynchronously
        executorService.submit(() -> {
            for (net.minecraft.world.chunk.Chunk chunk : chunksToUpdate) {
                // Process chunk
            }
        });
    }


    private void executeOnLivingUpdate() {
        try {
            // Call custom update method
            doUpdate();

            // Actual async update logic
            moveEntities(strafe, forward, friction);
        } catch (Exception e) {
            // Handle exception
        }
    }


    @Inject(method = "doUpdate", at = @At("HEAD"), cancellable = true)
    private void doUpdateInject(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityLivingUpdate && getHealth() > 0) {
            ci.cancel();
        }
    }

    private void doUpdate() {
        entityObject.moveEntity(this.strafe, this.forward, this.friction);
    }
}
