package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(value = EntityLivingBase.class, priority = 1100)
public abstract class MixinEntityLivingUpdate extends Entity {

    // Fixme todo
    @Unique
    private EntityLivingBase entityObject;

    @Unique
    public abstract float multithreadingandtweaks$getHealth();

    @Unique
    private final int batchSize = MultithreadingandtweaksConfig.batchsize;
    @Unique
    private final List<MixinEntityLivingUpdate> batchedEntities = new ArrayList<>();
    @Unique
    private final List<CompletableFuture<Void>> updateFutures = new ArrayList<>();

    @Unique
    private double strafe;
    @Unique
    private double forward;
    @Unique
    private float friction;

    @Unique
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksConfig.numberofcpus,
        MultithreadingandtweaksConfig.numberofcpus,
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
    @Unique
    private final Map<net.minecraft.world.chunk.Chunk, List<EntityLiving>> entityLivingMap = new ConcurrentHashMap<>();
    // private static final int batchsize = MultithreadingandtweaksConfig.batchsize;
    @Unique
    private final CopyOnWriteArrayList<MixinEntityLivingUpdate> entitiesToUpdate = new CopyOnWriteArrayList<MixinEntityLivingUpdate>();
    @Unique
    private ChunkProviderServer chunkProvider;
    @Unique
    private World world;
    @Unique
    private long lastUpdateTime;
    @Unique
    private Set<MixinEntityLivingUpdate> processedEntities = ConcurrentHashMap.newKeySet();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(World worldIn, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntityLivingUpdate) {
            this.entityObject = (EntityLivingBase) (Object) this;
            processedEntities = new HashSet<>();
        }
    }

    public MixinEntityLivingUpdate(EntityLivingBase entity, World world, ChunkProviderServer chunkProvider) {
        super(world);
        this.entityObject = entity;
        this.chunkProvider = chunkProvider;
        this.world = world;
        this.lastUpdateTime = world.getTotalWorldTime();
        this.processedEntities = new HashSet<>();
    }

    @Unique
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
        if (MultithreadingandtweaksConfig.enableMixinEntityLivingUpdate) {
            try {
                processEntityUpdates(world.getTotalWorldTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Unique
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

    @Unique
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
        if (MultithreadingandtweaksConfig.enableMixinEntityLivingUpdate) {
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

    @Unique
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

    @Unique
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

    @Unique
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
        if (MultithreadingandtweaksConfig.enableMixinEntityLivingUpdate && multithreadingandtweaks$getHealth() > 0) {
            ci.cancel();
        }
    }

    @Unique
    private void doUpdate() {
        entityObject.moveEntity(this.strafe, this.forward, this.friction);
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    @Overwrite
    protected void updateFallState(double distanceFallenThisTick, boolean isOnGround)
    {
        if (!this.isInWater())
        {
            this.handleWaterMovement();
        }

        if (isOnGround && this.fallDistance > 0.0F)
        {
            double posX = this.posX;
            double posY = this.posY - 0.20000000298023224D - (double)this.yOffset;
            double posZ = this.posZ;
            int i = MathHelper.floor_double(posX);
            int j = MathHelper.floor_double(posY);
            int k = MathHelper.floor_double(posZ);
            Block block = this.worldObj.getBlock(i, j, k);

            if (block.getMaterial() == Material.air)
            {
                int l = this.worldObj.getBlock(i, j - 1, k).getRenderType();

                if (l == 11 || l == 32 || l == 21)
                {
                    block = this.worldObj.getBlock(i, j - 1, k);
                }
            }
            else if (!this.worldObj.isRemote && this.fallDistance > 3.0F)
            {
                this.worldObj.playAuxSFX(2006, i, j, k, MathHelper.ceiling_float_int(this.fallDistance - 3.0F));
            }

            block.onFallenUpon(this.worldObj, i, j, k, this, this.fallDistance);
        }

        super.updateFallState(distanceFallenThisTick, isOnGround);
    }
}
