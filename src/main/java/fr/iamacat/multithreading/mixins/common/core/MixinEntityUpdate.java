package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = EntityLivingBase.class, priority = 1100)
public abstract class MixinEntityUpdate {
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void onLivingUpdate() {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            executeOnLivingUpdate();
        }
    }

    @Shadow
    public abstract float getHealth();
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void enqueueLivingUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            executorService.execute(this::executeOnLivingUpdate);
        }
    }
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        r -> {
            Runnable wrappedRunnable = () -> {
                System.out.println("Executing task"); // Add your println statement here
                r.run();
            };
            return new Thread(wrappedRunnable, "Entity-Update-%d" + MixinEntityUpdate.this.hashCode());
        });


    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final CopyOnWriteArrayList<EntityLivingBase> entitiesToUpdate = new CopyOnWriteArrayList<>();
    private final Map<Chunk, List<EntityLiving>> entityLivingMap = new ConcurrentHashMap<>();

    private ChunkProviderServer chunkProvider;

    private World world;
    private long lastUpdateTime;
    private Set<Entity> processedEntities = new HashSet<>();
    private final ConcurrentLinkedQueue<EntityLivingBase> entitiesToMove = new ConcurrentLinkedQueue<>();
    private static final int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private double strafe;
    private double forward;
    private float friction;

    public MixinEntityUpdate(World world, ChunkProviderServer chunkProvider) {
        System.out.println("MixinEntityUpdatetry");
        this.chunkProvider = chunkProvider;
        this.world = world;
        this.lastUpdateTime = world.getTotalWorldTime();
        System.out.println("MixinEntityUpdatecatch");
    }

    private void processEntityUpdates(long time) {
        List<EntityLivingBase> entitiesToRemove = new ArrayList<>();
        System.out.println("processEntityUpdates1");
        int numEntities = 0;

        for (EntityLivingBase entity : entitiesToUpdate) {
            // Check if the entity has already been processed
            if (processedEntities.contains(entity)) {
                // Skip processing this entity
                continue;
            }

            try {
                // Process the entity
                // ...
            } catch (Exception e) {
                // Log exception
                e.printStackTrace();
            }

            // Add the entity to the processed set
            processedEntities.add(entity);

            // Add the entity to the removal list
            entitiesToRemove.add(entity);

            numEntities++;

            if (numEntities >= MAX_ENTITIES_PER_TICK) {
                break; // Exit the loop if the maximum number of entities per tick is reached
            }
        }

        // Remove the entities that were processed
        entitiesToUpdate.removeAll(entitiesToRemove);

        this.lastUpdateTime = time;
        System.out.println("processEntityUpdates2");
    }

    private void processChunks(long time) {
        System.out.println("processChunks1");
        // Batch multiple chunk updates
        List<Chunk> chunksToUpdate = new ArrayList<>(entityLivingMap.keySet());
        entityLivingMap.clear();

        try {
            // Process chunks asynchronously
            executorService.submit(() -> {
                for (Chunk chunk : chunksToUpdate) {
                    try {
                        // Process each chunk
                    } catch (Exception e) {
                        // Log exception
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            // Log exception
            e.printStackTrace();
            System.out.println("processChunks2");
        }
    }

    @Inject(method = "updateEntities", at = @At("HEAD"), require = 1)
    private void onUpdateEntities(CallbackInfo ci) {
        System.out.println("onUpdateEntities1");
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            long time = world.getTotalWorldTime();
            long timeElapsed = time - lastUpdateTime;
            int numEntitiesPerTick = (int) (timeElapsed * MAX_ENTITIES_PER_TICK / 20L);
            processEntityUpdates(time);
            if (!entityLivingMap.isEmpty()) {
                // Process chunks asynchronously using the executorService
                executorService.submit(() -> processChunks(time));
                System.out.println("onUpdateEntities2");
            }
        }
    }

    @Inject(
        method = "updateEntityWithOptionalForce",
        at = @At(value = "HEAD", target = "Lnet/minecraft/entity/EntityLivingBase;onLivingUpdate()V"), require = 1)
    private void enqueueEntityUpdate(double x, double y, double z, boolean doBlockCollisions, boolean canBePushed,
        CallbackInfo ci) {
        System.out.println("enqueueEntityUpdate1");
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            entitiesToUpdate.add((EntityLivingBase) (Object) this);
            System.out.println("enqueueEntityUpdate2");
        }
    }

    @ModifyArg(
        method = "updateEntityWithOptionalForce",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"), require = 1)
    private Entity LivingOnUpdate(Entity entity) {
        System.out.println("LivingOnUpdate1");
        return entity != (Entity) (Object) this ? entity : null;
    }

    private void batchOnLivingUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate && getHealth() > 0) {
            executorService.execute(this::executeOnLivingUpdate);
        }
    }

    private void executeOnLivingUpdate() {
        try {
            onLivingUpdate();
        } catch (Exception e) {
            // Log exception
            e.printStackTrace();
        }
    }

    private void moveEntities(List<EntityLivingBase> entities, double strafe, double forward, float friction) {
        executorService.submit(() -> {
            System.out.println("moveEntities1");
            try {
                for (EntityLivingBase entity : entities) {
                    entity.moveEntity(strafe, forward, friction);
                }
            } catch (Exception e) {
                // Log exception
                e.printStackTrace();
            }
            System.out.println("moveEntities2");
        });
    }

    @Inject(method = "updateEntities", at = @At("TAIL"), require = 1)
    private void moveBatchedEntities(CallbackInfo ci) {
        System.out.println("moveBatchedEntities1");
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            while (!entitiesToMove.isEmpty()) {
                List<EntityLivingBase> batch = new ArrayList<>();
                for (int i = 0; i < batchSize && !entitiesToMove.isEmpty(); i++) {
                    EntityLivingBase entity = entitiesToMove.poll();
                    batch.add(entity);
                }
                moveEntities(batch, strafe, forward, friction);
                System.out.println("moveBatchedEntities2");
                shutdownExecutorService();
            }
        }
    }

    @Inject(method = "moveEntity", at = @At("HEAD"), require = 1, cancellable = true)
    public void batchMoveEntity(float strafe, float forward, float friction, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            System.out.println("batchMoveEntity1");
            this.strafe = strafe;
            this.forward = forward;
            this.friction = friction;
            entitiesToMove.add((EntityLivingBase) (Object) this);
            ci.cancel();
            System.out.println("batchMoveEntity2");
        }
    }

    @Inject(method = "addEntity", at = @At("RETURN"), require = 1)
    private void onAddEntity(Entity entity, CallbackInfo ci) {
        System.out.println("onAddEntity1");
        entitiesToUpdate.add((EntityLivingBase) entity);
        System.out.println("onAddEntity2");
    }

    @Inject(method = "removeEntity", at = @At("RETURN"), require = 1)
    private void onRemoveEntity(Entity entity, CallbackInfo ci) {
        System.out.println("onRemoveEntity1");
        entitiesToUpdate.remove(entity);
        System.out.println("onRemoveEntity2");
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"), require = 1)
    private void shutdownExecutorService() {
    executorService.submit(() -> {
        System.out.println("Shutting down executor service");
        executorService.shutdown();
    });
}
}
