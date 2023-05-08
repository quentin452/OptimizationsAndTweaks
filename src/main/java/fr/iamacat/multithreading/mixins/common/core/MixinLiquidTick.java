package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(BlockLiquid.class)
public abstract class MixinLiquidTick {

    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Liquid-Tick-%d")
            .build());

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        batchQueue = new LinkedBlockingQueue<>();
        blockMaterialMap = new ConcurrentHashMap<>();
    }

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;;
    private LinkedBlockingQueue<List<ChunkCoordinates>> batchQueue;
    private ConcurrentHashMap<ChunkCoordinates, Material> blockMaterialMap;

    public void updateTick(World world, int x, int y, int z, Random random) {
// Access and modify shared state in the World object here
        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
        Material currentMaterial = null;
        blockMaterialMap.get(pos);
        Material newMaterial = world.getBlock(x, y, z)
            .getMaterial();
        if (newMaterial == Material.water || newMaterial == Material.lava) {
            if (currentMaterial != newMaterial) {
                blockMaterialMap.put(pos, newMaterial);
                addToBatch(pos);
            }
        } else {
            blockMaterialMap.remove(pos);
        }
    }

    private void addToBatch(ChunkCoordinates pos) {
        try {
            List<ChunkCoordinates> batch = batchQueue.peek();
            if (batch == null || batch.size() >= BATCH_SIZE) {
                batch = new ArrayList<>(BATCH_SIZE);
                batchQueue.put(batch);
            }
            batch.add(pos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private void processBatch(List<ChunkCoordinates> batch, World world) {
        if (batch == null || batch.isEmpty()) {
            return;
        }
        lock.readLock()
            .lock();
        try {
            batch.parallelStream()
                .forEach(pos -> updateTick(world, pos.posX, pos.posY, pos.posZ, world.rand));
        } finally {
            lock.readLock()
                .unlock();
        }
    }

    @Inject(method = "liquidTick", at = @At("RETURN"))
    private void onLiquidTick(World world, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinliquidTick) {
            int batchSize = BATCH_SIZE;
            int numThreads = Math.max(
                1,
                Runtime.getRuntime()
                    .availableProcessors() - 1);
            ((ThreadPoolExecutor) executorService).setMaximumPoolSize(numThreads);
            List<ChunkCoordinates> liquidPositions = new ArrayList<>();
            for (int x = -64; x <= 64; x++) {
                for (int z = -64; z <= 64; z++) {
                    for (int y = 0; y <= 255; y++) {
                        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
                        if (world.getChunkProvider()
                            .chunkExists(pos.posX >> 4, pos.posZ >> 4)) {
                            if (world.getBlock(pos.posX, pos.posY, pos.posZ)
                                .getMaterial() == Material.water
                                || world.getBlock(pos.posX, pos.posY, pos.posZ)
                                .getMaterial() == Material.lava) {
                                liquidPositions.add(pos);
                            }
                        }
                    }
                }
                int numPositions = liquidPositions.size();
                int numBatches = (int) Math.ceil((double) numPositions / batchSize);
                List<CompletableFuture<Void>> taskBatch = new ArrayList<>(numBatches);
                ReadWriteLock lock = new ReentrantReadWriteLock();
                for (int i = 0; i < numBatches; i++) {
                    int startIndex = i * batchSize;
                    int endIndex = Math.min(startIndex + batchSize, numPositions);
                    List<ChunkCoordinates> batch = liquidPositions.subList(startIndex, endIndex);
                    taskBatch.add(CompletableFuture.runAsync(() -> {
                        lock.readLock()
                            .lock();
                        try {
                            processBatch(batch, world);
                        } finally {
                            lock.readLock()
                                .unlock();
                        }
                    }, executorService));
                }
                CompletableFuture.allOf(taskBatch.toArray(new CompletableFuture[0]))
                    .join();
                executorService.shutdown();
            }
        }
    }
}
