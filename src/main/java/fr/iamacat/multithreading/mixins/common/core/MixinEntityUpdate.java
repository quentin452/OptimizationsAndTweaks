package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.falsepattern.lib.compat.BlockPos;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
@Mixin(World.class)
public abstract class MixinEntityUpdate {

    private IChunkProvider chunkProvider;
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private static final int CHUNK_BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final List<Entity> entitiesToUpdate = new ArrayList<>();
    private final ExecutorService updateExecutor = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        0L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(MAX_ENTITIES_PER_TICK)
    );
    private final Map<Chunk, List<EntityLiving>> entityLivingMap = new ConcurrentHashMap<>();
    private final Set<Entity> entitiesInWater = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private World world;

    public MixinEntityUpdate(World world, IChunkProvider chunkProvider) {
        this.chunkProvider = chunkProvider;
        this.world = world;
    }

    @Inject(method = "updateEntities", at = @At("HEAD"))
    private void onUpdateEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            entitiesInWater.clear();
            entityLivingMap.clear();
            for (Entity entity : entitiesToUpdate) {
                entity.isInWater();
                if (entity.isEntityAlive()) {
                    entity.onEntityUpdate();
                    if (entity instanceof EntityLivingBase) {
                        EntityLivingBase livingEntity = (EntityLivingBase) entity;
                        livingEntity.getHealth();
                        livingEntity.getDataWatcher().getWatchableObjectFloat(6);
                    }
                }
                if (entity.isInWater()) {
                    entitiesInWater.add(entity);
                }
                if (entity instanceof EntityLiving) {
                    int posX = MathHelper.floor_double(entity.posX);
                    int posY = MathHelper.floor_double(entity.posY);
                    int posZ = MathHelper.floor_double(entity.posZ);
                    Chunk chunk = entity.worldObj.getChunkFromBlockCoords(posX, posZ);
                    entityLivingMap.computeIfAbsent(chunk, k -> new ArrayList<>()).add((EntityLiving) entity);
                }
            }
            List<Future<?>> futures = new ArrayList<>(entitiesToUpdate.size());
            for (Entity entity : entitiesToUpdate) {
                futures.add(updateExecutor.submit(() -> {
                    if (entitiesInWater.contains(entity)) {
                        entity.isInWater();
                    }
                }));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            int numEntitiesToUpdate = Math.min(entitiesToUpdate.size(), MAX_ENTITIES_PER_TICK);
            futures.clear();
            for (int i = 0; i < numEntitiesToUpdate; i++) {
                Entity entity = entitiesToUpdate.get(i);
                futures.add(updateExecutor.submit(() -> {
                    if (entity instanceof EntityLivingBase) {
                        EntityLivingBase livingEntity = (EntityLivingBase) entity;
                        livingEntity.getHealth();
                        livingEntity.getDataWatcher().getWatchableObjectFloat(6);
                    }
                }));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            List<Chunk> chunks = new ArrayList<>(entityLivingMap.keySet());
            int numChunks = chunks.size();
            futures.clear();
            for (int i = 0; i < numChunks; i += CHUNK_BATCH_SIZE) {
                int endIndex = Math.min(i + CHUNK_BATCH_SIZE, numChunks);
                List<Chunk> batchChunks = chunks.subList(i, endIndex);
                futures.add(updateExecutor.submit(() -> {
                    for (Chunk chunk : batchChunks) {
                        List<EntityLiving> batchEntities = entityLivingMap.get(chunk);
                        for (EntityLiving entity : batchEntities) {
                            chunk.addEntity(entity);
                        }
                    }
                }));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Inject(method = "addEntity", at = @At("RETURN"))
    private void onAddEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.add(entity);
    }

    @Inject(method = "removeEntity", at = @At("RETURN"))
    private void onRemoveEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.remove(entity);
    }
}
