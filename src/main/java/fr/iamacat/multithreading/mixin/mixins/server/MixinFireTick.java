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
    private ExecutorService fireExecutorService = Executors.newFixedThreadPool(2);

    @Shadow
    public abstract void updateTick(WorldServer world, int x, int y, int z, Random random);

    @Inject(method = "updateTick", at = @At("HEAD"))
    private void onUpdateTick(WorldServer world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (Multithreaded.MixinFireTick) {
            // Process fire ticks in batches using executor service
            List<int[]> batches = new ArrayList<>();
            int numBlocks = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        int fx = x + dx;
                        int fy = y + dy;
                        int fz = z + dz;
                        if (world.getBlock(fx, fy, fz).isReplaceable(world, fx, fy, fz)) {
                            numBlocks++;
                            if (numBlocks % 50 == 0) {
                                batches.add(new int[]{x, y, z});
                            }
                        }
                    }
                }
            }
            if (numBlocks % 50 != 0) {
                batches.add(new int[]{x, y, z});
            }
            for (int[] blockCoords : batches) {
                fireExecutorService.submit(() -> updateTick(world, blockCoords[0], blockCoords[1], blockCoords[2], random));
            }
        }
    }
}
