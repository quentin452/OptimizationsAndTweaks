/*
 * FalseTweaks
 * Copyright (C) 2022 FalsePattern
 * All Rights Reserved
 * The above copyright notice, this permission notice and the word "SNEED"
 * shall be included in all copies or substantial portions of the Software.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(value = BlockLeavesBase.class, priority = 999)
public abstract class MixinLeafDecay {
    private BlockingQueue<ChunkCoordinates> decayQueue;
    private ExecutorService decayExecutorService = new ThreadPoolExecutor(
        2, // Minimum number of threads
        4, // Maximum number of threads
        60L, // Thread idle time before termination
        TimeUnit.SECONDS, // Time unit for idle time
        new LinkedBlockingQueue<>(1000) // Blocking queue for tasks
    );

    private static final Comparator<ChunkCoordinates> DISTANCE_COMPARATOR = new Comparator<ChunkCoordinates>() {
        @Override
        public int compare(ChunkCoordinates o1, ChunkCoordinates o2) {
            return Double.compare(o1.getDistanceSquared(0, 0, 0), o2.getDistanceSquared(0, 0, 0));
        }
    };
    private static final int BATCH_SIZE = 15;
    @Shadow
    private WorldClient world;
    public abstract boolean func_147477_a(Block block, int x, int y, int z, boolean decay);

    @Inject(method = "updateLeafDecay", at = @At("RETURN"))
    private void onUpdateEntities(CallbackInfo ci) {
        if (Multithreaded.MixinLeafDecay) {
            // Initialize decay queue if it doesn't exist
            if (decayQueue == null) {
                decayQueue = new LinkedBlockingQueue<>(1000);
            }
            // Add leaf blocks that need to decay to the queue
            for (int x = -64; x <= 64; x++) {
                for (int z = -64; z <= 64; z++) {
                    for (int y = 0; y <= 255; y++) {
                        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
                        if (world.getChunkProvider().chunkExists(pos.posX >> 4, pos.posZ >> 4)) {
                            Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);
                            if (block instanceof BlockLeavesBase) {
                                decayQueue.offer(pos);
                            }
                        }
                    }
                }
            }

            // Process leaf blocks in batches using executor service
            while (!decayQueue.isEmpty()) {
                List<ChunkCoordinates> batch = new ArrayList<>();
                int batchSize = Math.max(Math.min(decayQueue.size(), BATCH_SIZE), 1);
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
