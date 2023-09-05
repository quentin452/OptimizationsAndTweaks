package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = EntityLivingBase.class, priority = 1100)
public abstract class MixinEntityLivingUpdate {

    // Fixme todo
    private EntityLivingBase entityObject;

    @Shadow
    public abstract float getHealth();

    private final int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final List<MixinEntityLivingUpdate> batchedEntities = new ArrayList<>();
    private final List<CompletableFuture<Void>> updateFutures = new ArrayList<>();

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
    // private static final int batchsize = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final CopyOnWriteArrayList<MixinEntityLivingUpdate> entitiesToUpdate = new CopyOnWriteArrayList<MixinEntityLivingUpdate>();
    private ChunkProviderServer chunkProvider;
    private World world;
    private long lastUpdateTime;
    private Set<MixinEntityLivingUpdate> processedEntities = ConcurrentHashMap.newKeySet();

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
        this.processedEntities = new HashSet<>();
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

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityLivingUpdate) {
            try {
                processEntityUpdates(world.getTotalWorldTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private synchronized void processEntityUpdates(long time) {
        try {
            // Get the list of entities to process
            List<MixinEntityLivingUpdate> entitiesToProcess = new ArrayList<>(entitiesToUpdate);

            for (MixinEntityLivingUpdate entity : entitiesToProcess) {

                // Add entity to batch for processing
                batchedEntities.add(entity);

                // Check if the batch size has been reached
                if (batchedEntities.size() >= batchSize) {

                    // Process the batched entities asynchronously
                    CompletableFuture future = CompletableFuture
                        .runAsync(() -> processEntities(batchedEntities), executorService);

                    // Add the future to the list
                    updateFutures.add(future);

                    // Clear the batched list
                    batchedEntities.clear();
                }
            }
            processedEntities.addAll(entitiesToProcess);
            this.lastUpdateTime = time;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    public void onLivingUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityLivingUpdate) {
            // System.out.println("Cancelling vanilla onLivingUpdate()");
            boolean needsUpdate = strafe != 0 || forward != 0 || friction != 0;

            if (needsUpdate) {
                // Call the updated method to handle movement and updates
                executeOnLivingUpdate();
            }

            // Cancel the vanilla method if needed
            // ci.cancel();
        }
    }

    private void processEntities(List<MixinEntityLivingUpdate> entities) {
        for (MixinEntityLivingUpdate entity : entities) {
            try {
                // Example processing action: Set the entity's health to 0
                entity.entityObject.setHealth(0);

                // Perform additional actions on the entity as needed

                // Example: Print a message for the processed entity
                System.out.println("Entity processed!");

                // Add your custom processing logic here

            } catch (Exception e) {
                // Handle any exceptions that occur during entity processing
                e.printStackTrace();
            }
        }
    }

    private void processUpdates() {
        executorService.submit(() -> {
            try {
                // Process entity updates here
                // Assuming you have a processEntity method, process the batched entities
                processEntities(batchedEntities);
                batchedEntities.clear();
            } catch (Exception e) {
                // Handle exception
            }
        });
    }

    private void executeOnLivingUpdate() {
        try {
            doUpdate();
            strafe = 0;
            forward = 0;
            friction = 0;
            moveEntities(strafe, forward, friction);
        } catch (Exception e) {
            e.printStackTrace();
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
