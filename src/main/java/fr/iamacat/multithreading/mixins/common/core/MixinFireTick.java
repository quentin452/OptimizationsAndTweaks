package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(BlockFire.class)
public abstract class MixinFireTick {

    // Use a shared thread pool with a fixed number of threads
    private static final ExecutorService executorService = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("Fire-Tick-%d").build()
    );

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {}

    @Inject(method = "updateTick", at = @At("HEAD"))
    public void updateTick(World world, int x, int y, int z, Random random, CallbackInfo info) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFireTick) {

            // Process fire ticks using breadth-first search
            boolean[][][] visited = new boolean[3][3][3]; // 3x3x3 grid centered at (x, y, z)
            visited[1][1][1] = true;
            BlockingQueue<int[]> blocksToUpdate = new LinkedBlockingQueue<>();
            blocksToUpdate.add(new int[] { x, y, z });

            List<Future<Void>> futures = new ArrayList<>();

            while (!blocksToUpdate.isEmpty()) {
                int[] coords = blocksToUpdate.poll();
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) {
                                continue; // skip the center block
                            }
                            int fx = coords[0] + dx;
                            int fy = coords[1] + dy;
                            int fz = coords[2] + dz;

                            Block block = world.getBlock(fx, fy, fz);
                            if (block == null) {
                                continue;
                            }
                            Material material = block.getMaterial();
                            if (material == null || !material.isReplaceable()) {
                                continue; // skip non-replaceable blocks
                            }
                            if (visited[dx + 1][dy + 1][dz + 1]) {
                                continue; // skip already visited blocks
                            }
                            visited[dx + 1][dy + 1][dz + 1] = true; // mark block as visited
                            if (world.getBlock(fx, fy, fz) instanceof BlockFire) {
                                blocksToUpdate.add(new int[] { fx, fy, fz }); // add adjacent fire block to update list
                            }
                        }
                    }
                }

                int[] blockToUpdate = blocksToUpdate.poll();
                if (blockToUpdate != null) {
                    futures.add(executorService.submit(() -> {
                        // Code to update block at coords goes here
                        return null;
                    }));
                }
            }

            for (Future<Void> future : futures) {
                try {
                    future.get(); // Wait for all tasks to complete
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Properly shut down the executor service when it's no longer needed
    private static void shutdownExecutorService() {
        executorService.shutdown();
    }
}
