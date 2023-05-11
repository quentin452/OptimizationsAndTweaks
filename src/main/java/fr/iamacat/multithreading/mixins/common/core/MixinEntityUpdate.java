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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityUpdate {
    @Shadow
    public abstract void onLivingUpdate();

    @Shadow
    public abstract float getHealth();

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        r -> new Thread(r, "Entity-Update-%d" + MixinEntityUpdate.this.hashCode()));

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
        this.chunkProvider = chunkProvider;
        this.world = world;
        this.lastUpdateTime = world.getTotalWorldTime();
    }

    private void processEntityUpdates(long time) {
        List<EntityLivingBase> entitiesToRemove = new ArrayList<>();
        int numEntities = 0;

        Iterator<EntityLivingBase> iterator = entitiesToUpdate.iterator();
        while (iterator.hasNext() && numEntities < MAX_ENTITIES_PER_TICK) {
            EntityLivingBase entity = iterator.next();

            // Check if the entity has already been processed
            if (processedEntities.contains(entity)) {
                // Skip processing this entity
                continue;
            }

            // Process the entity
            // ...

            // Add the entity to the processed set
            processedEntities.add(entity);

            // Add the entity to the removal list
            entitiesToRemove.add(entity);

            numEntities++;
        }


        // Remove the entities that were processed
        entitiesToUpdate.removeAll(entitiesToRemove);

        this.lastUpdateTime = time;
    }

    private void processChunks(long time) {
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
                        e.printStackTrace();
                        // Log exception
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // Log exception
        }
    }

    @Inject(method = "updateEntities", at = @At("HEAD"))
    private void onUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            long time = world.getTotalWorldTime();
            long timeElapsed = time - lastUpdateTime;
            int numEntitiesPerTick = (int) (timeElapsed * MAX_ENTITIES_PER_TICK / 20L);
            processEntityUpdates(time);
            if (!entityLivingMap.isEmpty()) {
                // Process chunks asynchronously using the executorService
                executorService.submit(() -> processChunks(time));
            }
        }
    }

    @Inject(
        method = "updateEntityWithOptionalForce",
        at = @At(value = "HEAD", target = "Lnet/minecraft/entity/EntityLivingBase;onLivingUpdate()V"))
    private void enqueueEntityUpdate(double x, double y, double z, boolean doBlockCollisions, boolean canBePushed,
        CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            entitiesToUpdate.add((EntityLivingBase) (Object) this);
        }
    }

    @ModifyArg(
        method = "updateEntityWithOptionalForce",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
    private Entity LivingOnUpdate(Entity entity) {
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
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    private void moveEntities(List<EntityLivingBase> entities, double strafe, double forward, float friction) {
        executorService.submit(() -> {
            try {
                for (EntityLivingBase entity : entities) {
                    entity.moveEntity(strafe, forward, friction);
                }
            } catch (Exception e) {
                // Log exception
            }
        });
    }
    @Inject(method = "updateEntities", at = @At("TAIL"))
    private void moveBatchedEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            while (!entitiesToMove.isEmpty()) {
                List<EntityLivingBase> batch = new ArrayList<>();
                for (int i = 0; i < batchSize && !entitiesToMove.isEmpty(); i++) {
                    EntityLivingBase entity = entitiesToMove.poll();
                    batch.add(entity);
                }
                moveEntities(batch, strafe, forward, friction);
            }
        }
    }

    @Inject(method = "moveEntity", at = @At("HEAD"), cancellable = true)
    public void batchMoveEntity(float strafe, float forward, float friction, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            this.strafe = strafe;
            this.forward = forward;
            this.friction = friction;
            entitiesToMove.add((EntityLivingBase) (Object) this);
            ci.cancel();
        }
    }

    @Inject(method = "addEntity", at = @At("RETURN"))
    private void onAddEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.add((EntityLivingBase) entity);
    }

    @Inject(method = "removeEntity", at = @At("RETURN"))
    private void onRemoveEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.remove(entity);
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void shutdownExecutorService(CallbackInfo ci) {
        executorService.shutdown();
    }
}
