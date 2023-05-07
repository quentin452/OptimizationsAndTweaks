package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = BlockLeavesBase.class, priority = 900)
public abstract class MixinLeafDecay {
    private final ExecutorService executorService = Executors.newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);
    private final Queue<BlockPos> decayQueue = new ConcurrentLinkedQueue<>();
    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;

    @Inject(method = "updateLeaves", at = @At("RETURN"))
    private void onUpdateLeaves(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinLeafDecay) {
            return;
        }

        // Add leaf blocks that need to decay to the queue
        List loadedChunks = Collections.singletonList(world.getChunkProvider().getLoadedChunkCount());
        for (Object obj : loadedChunks) {
            Chunk chunk = (Chunk)obj;
            for (int i = chunk.xPosition * 16; i < chunk.xPosition * 16 + 16; i++) {
                for (int j = 0; j < 256; j++) {
                    for (int k = chunk.zPosition * 16; k < chunk.zPosition * 16 + 16; k++) {
                        Block block = world.getBlock(i, j, k);
                        if (block != null && block.isLeaves(world, i, j, k)) {
                            decayQueue.add(new BlockPos(i, j, k));
                        }
                    }
                }
            }
        }

        int queueSize = decayQueue.size();
        int numThreads = Math.min(queueSize / BATCH_SIZE + 1, MultithreadingandtweaksMultithreadingConfig.numberofcpus);
        List<List<BlockPos>> batches = new ArrayList<>(numThreads);
        Iterator<BlockPos> iterator = decayQueue.iterator();
        for (int i = 0; i < numThreads; i++) {
            List<BlockPos> batch = new ArrayList<>(BATCH_SIZE);
            for (int j = 0; j < BATCH_SIZE && iterator.hasNext(); j++) {
                batch.add(iterator.next());
            }
            batches.add(batch);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>(numThreads);
        for (List<BlockPos> batch : batches) {
            futures.add(CompletableFuture.runAsync(() -> processLeafBlocks(batch, world), executorService));
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to process leaf blocks", e);
        }

        decayQueue.clear();
    }


    public void processLeafBlocks(List<BlockPos> batch, World world) {
        List<BlockPos> neighborPositions = new ArrayList<>();
        for (BlockPos pos : batch) {
            try {
                if (shouldDecay(pos, world)) {
                    world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                    getNeighborPositions(pos, neighborPositions);
                    for (BlockPos neighbor : neighborPositions) {
                        Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                        if (neighborBlock instanceof BlockLeaves) {
                            decayQueue.add(neighbor.toImmutable());
                        }
                    }
                }
            } catch (Exception e) {
                LogManager.getLogger().error("Failed to process leaf block at " + pos, e);
            }
            neighborPositions.clear();
        }
    }

    public List<BlockPos> getNeighborPositions(BlockPos pos, List<BlockPos> neighborPositions) {
        List<BlockPos> neighbors = new ArrayList<>();
        for (BlockPos neighbor : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (!neighbor.equals(pos)) {
                neighbors.add(new BlockPos(neighbor));
            }
        }
        return neighbors;
    }

    private boolean shouldDecay(BlockPos pos, World world) {
        Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        if (block == null || !(block instanceof BlockLeaves)) {
            return false;
        }
        int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
        return (meta & 4) == 0;
    }
}
