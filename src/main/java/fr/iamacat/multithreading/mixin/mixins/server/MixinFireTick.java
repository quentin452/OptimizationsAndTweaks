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
import net.minecraft.block.BlockFire;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(BlockFire.class)
public abstract class MixinFireTick {
    public int fireTickBatchSize = 15; // Default batch size of 15.
    private ExecutorService fireExecutorService = Executors.newCachedThreadPool();


    @Shadow
    public abstract void updateTick(WorldServer world, int x, int y, int z, Random random);

    @Inject(method = "updateTick", at = @At("HEAD"))
    private synchronized void onUpdateTick(WorldServer world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (Multithreaded.MixinFireTick) {
            // Process fire ticks using breadth-first search
            List<int[]> blocksToUpdate = new ArrayList<>();
            boolean[][][] visited = new boolean[3][3][3]; // 3x3x3 grid centered at (x, y, z)
            visited[1][1][1] = true; // mark the center block as visited
            blocksToUpdate.add(new int[]{x, y, z});
            int index = 0;
            while (index < blocksToUpdate.size()) {
                int[] blockCoords = blocksToUpdate.get(index++);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) {
                                continue; // skip the center block
                            }
                            int fx = blockCoords[0] + dx;
                            int fy = blockCoords[1] + dy;
                            int fz = blockCoords[2] + dz;
                            if (visited[dx+1][dy+1][dz+1] || !world.getBlock(fx, fy, fz).isReplaceable(world, fx, fy, fz)) {
                                continue; // skip already visited or non-replaceable blocks
                            }
                            visited[dx+1][dy+1][dz+1] = true; // mark block as visited
                            if (world.getBlock(fx, fy, fz) instanceof BlockFire) {
                                blocksToUpdate.add(new int[]{fx, fy, fz}); // add adjacent fire block to update list
                            }
                        }
                    }
                }
            }
            for (int[] blockCoords : blocksToUpdate) {
                fireExecutorService.submit(() -> updateTick(world, blockCoords[0], blockCoords[1], blockCoords[2], random));
            }
        }
    }
}
