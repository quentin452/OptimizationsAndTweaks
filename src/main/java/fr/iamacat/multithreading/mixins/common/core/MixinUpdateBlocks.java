package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinUpdateBlocks {

    @Unique
    private final ExecutorService executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        0L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(MultithreadingandtweaksMultithreadingConfig.batchsize),
        new ThreadFactoryBuilder().setNameFormat("MixinUpdateBlocks-worker-%d")
            .build());

    @Unique
    private World world;
    @Unique
    private final Queue<Chunk> updateQueue = new ConcurrentLinkedQueue<>();

    public MixinUpdateBlocks(World world) {
        this.world = world;
    }

    @Inject(method = "updateBlocks", at = @At("HEAD"))
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.world == world
            && MultithreadingandtweaksMultithreadingConfig.enableMixinUpdateBlocks) {
            int minX = -30000000;
            int minY = 0;
            int minZ = -30000000;
            int maxX = 30000000;
            int maxY = 255;
            int maxZ = 30000000;

            int chunkMinX = minX >> 4;
            int chunkMinZ = minZ >> 4;
            int chunkMaxX = maxX >> 4;
            int chunkMaxZ = maxZ >> 4;

            IntStream.rangeClosed(chunkMinX, chunkMaxX)
                .parallel()
                .forEach(chunkX -> {
                    IntStream.rangeClosed(chunkMinZ, chunkMaxZ)
                        .parallel()
                        .forEach(chunkZ -> {
                            final Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                            if (chunk != null) {
                                updateQueue.add(chunk);
                                if (updateQueue.size() >= MultithreadingandtweaksMultithreadingConfig.batchsize) {
                                    processChunkBatch();
                                }
                            }
                        });
                });
            processChunkBatch();
        }
    }

    @Unique
    private void processChunkBatch() {
        executorService.submit(() -> {
            List<Chunk> chunks = new ArrayList<>(MultithreadingandtweaksMultithreadingConfig.batchsize);
            Chunk chunk;
            while ((chunk = updateQueue.poll()) != null) {
                chunks.add(chunk);
                if (chunks.size() >= MultithreadingandtweaksMultithreadingConfig.batchsize) {
                    break;
                }
            }
            updateChunks(chunks);
        });
    }

    @Unique
    private void updateChunks(List<Chunk> chunks) {
        chunks.forEach(
            chunk -> updateChunk(
                chunk,
                -30000000,
                0,
                -30000000,
                30000000,
                255,
                30000000,
                chunk.xPosition,
                chunk.zPosition));
    }

    @Unique
    private void updateChunk(Chunk chunk, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int chunkX,
        int chunkZ) {
        int chunkMinX = chunkX << 4;
        int chunkMinY = minY < 0 ? 0 : minY;
        int chunkMinZ = chunkZ << 4;
        int chunkMaxX = chunkMinX | 15;
        int chunkMaxY = maxY > 255 ? 255 : maxY;
        int chunkMaxZ = chunkMinZ | 15;

        int xMin = Math.max(chunkMinX, minX);
        int yMin = Math.max(chunkMinY, minY);
        int zMin = Math.max(chunkMinZ, minZ);
        int xMax = Math.min(chunkMaxX, maxX);
        int yMax = Math.min(chunkMaxY, maxY);
        int zMax = Math.min(chunkMaxZ, maxZ);

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    Block block = chunk.getBlock(x & 15, y, z & 15);
                    int metadata = chunk.getBlockMetadata(x & 15, y, z & 15);
                    if (block.hasTileEntity(metadata)) {
                        TileEntity tileEntity = chunk.getTileEntityUnsafe(x & 15, y, z & 15);
                        if (tileEntity != null) {
                            tileEntity.updateContainingBlockInfo();
                        }
                    }
                }
            }
        }
    }
}
