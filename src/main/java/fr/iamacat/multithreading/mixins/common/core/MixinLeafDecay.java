package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = BlockLeavesBase.class, priority = 900)
public abstract class MixinLeafDecay {
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Leaf-Decay-%d").build());

    private final List<BlockPos> decayQueue = new ArrayList<>();
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    private final int numberOfCPUs = MultithreadingandtweaksMultithreadingConfig.numberofcpus;

    @Inject(method = "updateLeaves", at = @At("RETURN"))
    private void onUpdateLeaves(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinLeafDecay) {
            // Add leaf blocks that need to decay to the queue
            WorldServer worldServer = (WorldServer) world;
            ChunkProviderServer chunkProvider = worldServer.theChunkProviderServer;

            List<Chunk> loadedChunks = new ArrayList<>(chunkProvider.loadedChunks);

            loadedChunks.parallelStream()
                .forEach(chunk -> {
                    IntStream.range(chunk.xPosition * 16, chunk.xPosition * 16 + 16)
                        .parallel()
                        .forEach(i -> {
                            IntStream.range(0, 256)
                                .parallel()
                                .forEach(j -> {
                                    IntStream.range(chunk.zPosition * 16, chunk.zPosition * 16 + 16)
                                        .parallel()
                                        .forEach(k -> {
                                            Block block = world.getBlock(i, j, k);
                                            if (block instanceof BlockLeaves) {
                                                decayQueue.add(new BlockPos(i, j, k));
                                            }
                                        });
                                });
                        });
                });
        }

        int queueSize = decayQueue.size();
        int numThreads = Math.min(queueSize, numberOfCPUs);
        List<List<BlockPos>> batches = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            int startIndex = i * BATCH_SIZE;
            int endIndex = Math.min(startIndex + BATCH_SIZE, queueSize);
            List<BlockPos> batch = new ArrayList<>(decayQueue.subList(startIndex, endIndex));
            batches.add(batch);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>(numThreads);
        for (List<BlockPos> batch : batches) {
            futures.add(
                CompletableFuture.runAsync(
                    () -> processLeafBlocks(new ArrayList<>(batch), world),
                    executorService));
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to process leaf blocks", e);
        }

        decayQueue.clear();
    }

    public void processLeafBlocks(List<BlockPos> batch, World world) {
        List<BlockPos> neighborPositions = new ArrayList<>();
        for (BlockPos pos : batch) {
            try {
                Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
                if (block instanceof BlockLeaves) {
                    if (shouldDecay(pos, world)) {
                        world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                        getNeighborPositions(pos, neighborPositions);
                        for (BlockPos neighbor : neighborPositions) {
                            int chunkX = neighbor.getX() >> 4;
                            int chunkZ = neighbor.getZ() >> 4;
                            Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                            if (chunk != null && chunk.isChunkLoaded) {
                                Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                                if (neighborBlock instanceof BlockLeaves) {
                                    decayQueue.add(neighbor.toImmutable());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LogManager.getLogger()
                    .error("Failed to process leaf block at " + pos, e);
            }
        }
    }

    public boolean shouldDecay(BlockPos pos, World world) {
        Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        if (block instanceof BlockLeaves) {
            return !((BlockLeaves) block).shouldCheckWeakPower(world, pos.getX(), pos.getY(), pos.getZ(), 1);
        }
        return false;
    }

    public List<BlockPos> getNeighborPositions(BlockPos pos, List<BlockPos> neighborPositions) {
        neighborPositions.clear();
        for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
            for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
                for (int z = pos.getZ() - 1; z <= pos.getZ() + 1; z++) {
                    if (x != pos.getX() || y != pos.getY() || z != pos.getZ()) {
                        neighborPositions.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return neighborPositions;
    }

}
