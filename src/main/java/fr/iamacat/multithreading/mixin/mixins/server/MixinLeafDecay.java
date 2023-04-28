package fr.iamacat.multithreading.mixin.mixins.server;

import fr.iamacat.multithreading.Multithreaded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(value = BlockLeavesBase.class, priority = 999)
public abstract class MixinLeafDecay {
    private PriorityQueue<ChunkCoordinates> decayQueue;
    private ExecutorService decayExecutorService = Executors.newFixedThreadPool(2);

    @Shadow
    private WorldClient world;
    public abstract boolean func_147477_a(Block block, int x, int y, int z, boolean decay);

    @Inject(method = "updateEntities", at = @At("RETURN"))
    private void onUpdateEntities(CallbackInfo ci) {
        if (Multithreaded.MixinLeafDecay) {
            // Initialize decay queue if it doesn't exist
            if (decayQueue == null) {
                decayQueue = new PriorityQueue<>(1000, new Comparator<ChunkCoordinates>() {
                    @Override
                    public int compare(ChunkCoordinates o1, ChunkCoordinates o2) {
                        return Double.compare(o1.getDistanceSquared(0, 0, 0), o2.getDistanceSquared(0, 0, 0));
                    }
                });
            }
            // Add leaf blocks that need to decay to the queue
            for (int x = -64; x <= 64; x++) {
                for (int z = -64; z <= 64; z++) {
                    for (int y = 0; y <= 255; y++) {
                        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
                        Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);
                    }
                }
            }

            // Process leaf blocks in batches using executor service
            while (!decayQueue.isEmpty()) {
                List<ChunkCoordinates> batch = new ArrayList<>();
                int batchSize = Math.max(Math.min(decayQueue.size(), 50), 1);
                for (int i = 0; i < batchSize; i++) {
                    batch.add(decayQueue.poll());
                }
                if (!batch.isEmpty()) {
                    decayExecutorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            for (ChunkCoordinates pos : batch) {
                            }
                        }
                    });
                }
            }
        }
        // Shutdown decay executor service after it is used
        decayExecutorService.shutdown();
    }

}
