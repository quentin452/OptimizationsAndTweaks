package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinGrassSpread {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private static final ExecutorService EXECUTOR_SERVICE = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);
    private final Queue<BlockPos> spreadQueue = new ConcurrentLinkedQueue<>();

    @Inject(method = "updateTick", at = @At("RETURN"))
    private void onUpdateTick(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinGrassSpread) {

            // Calculate the distance threshold (in chunks) from the player's position
            EntityPlayer player = world.getClosestPlayer(x, y, z, -1);
            int distanceThreshold = 8; // Change this value to adjust the distance threshold
            int chunkDistanceThreshold = MathHelper.ceiling_double_int(distanceThreshold / 16.0);

            // Add grass blocks that need to spread to the queue, but only if they are within the distance threshold
            List loadedChunks = ((ChunkProviderServer) world.getChunkProvider()).loadedChunks;
            for (Object obj : loadedChunks) {
                Chunk chunk = (Chunk) obj;
                int chunkX = chunk.xPosition * 16;
                int chunkZ = chunk.zPosition * 16;
                if (Math.abs(chunkX - (int) (player.posX)) <= chunkDistanceThreshold * 16
                    && Math.abs(chunkZ - (int) (player.posZ)) <= chunkDistanceThreshold * 16) {
                    ExtendedBlockStorage[] storageArrays = chunk.getBlockStorageArray();
                    for (ExtendedBlockStorage storage : storageArrays) {
                        if (storage == null) continue;
                        for (int blockX = 0; blockX < 16; blockX++) {
                            for (int blockY = 0; blockY < 16; blockY++) {
                                for (int blockZ = 0; blockZ < 16; blockZ++) {
                                    Block block = storage.getBlockByExtId(blockX, blockY, blockZ);
                                    if (block == Blocks.dirt && world.getFullBlockLightValue(
                                        chunkX + blockX,
                                        storage.getYLocation() + blockY + 1,
                                        chunkZ + blockZ) >= 9) {
                                        spreadQueue.add(
                                            new BlockPos(
                                                chunkX + blockX,
                                                storage.getYLocation() + blockY + 1,
                                                chunkZ + blockZ));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int queueSize = spreadQueue.size();
            int numThreads = Math
                .min(queueSize / BATCH_SIZE + 1, MultithreadingandtweaksMultithreadingConfig.numberofcpus);
            List<List<BlockPos>> batches = new ArrayList<>(numThreads);
            Iterator<BlockPos> iterator = spreadQueue.iterator();
            for (int i = 0; i < numThreads; i++) {
                List<BlockPos> batch = new ArrayList<>(BATCH_SIZE);
                for (int j = 0; j < BATCH_SIZE && iterator.hasNext(); j++) {
                    batch.add(iterator.next());
                }
                batches.add(batch);
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>(numThreads);
            for (List<BlockPos> batch : batches) {
                futures.add(CompletableFuture.runAsync(() -> processGrassBlocks(batch, world), EXECUTOR_SERVICE));
            }

            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to process grass blocks", e);
            }

            spreadQueue.clear();
        }
    }

    private void processGrassBlocks(List<BlockPos> batch, World world) {
        List<BlockPos> updates = new ArrayList<>();
        for (BlockPos pos : batch) {
            Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (block == Blocks.dirt && world.getBlockLightValue(pos.getX(), pos.getY() + 1, pos.getZ()) >= 9) {
                updates.add(pos);
            }
        }
        if (!updates.isEmpty()) {
            BlockPos first = updates.get(0);
            BlockPos last = updates.get(updates.size() - 1);
            world.markBlockRangeForRenderUpdate(
                first.getX(),
                first.getY(),
                first.getZ(),
                last.getX(),
                last.getY(),
                last.getZ());
            for (BlockPos pos : updates) {
                world.setBlock(pos.getX(), pos.getY(), pos.getZ(), Blocks.grass);
            }
        }
    }
}
