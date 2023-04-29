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

package fr.iamacat.multithreading.mixins.common.core;


import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.*;

@Mixin(EntityLightningBolt.class)
public abstract class MixinLightningBolt {

    private static final int THREAD_COUNT_THRESHOLD = 50;
    private World worldObj;
    private double posX;
    private double posY;
    private double posZ;

    public MixinLightningBolt(World world, double x, double y, double z, boolean effectOnly) {
        super();
        this.worldObj = world;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(World world, double x, double y, double z, boolean effectOnly, CallbackInfo ci) {
        MultithreadingandtweaksConfig.enableMixinChunkPopulating = world.loadedEntityList != null && world.loadedEntityList.size() > THREAD_COUNT_THRESHOLD;
    }


    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void onOnUpdate(CallbackInfo ci) {
        if (!MultithreadingandtweaksConfig.enableMixinChunkPopulating) {
            World world = this.worldObj;
            if (world == null || world.isRemote || world.loadedEntityList == null) {
                return;
            }
            // Use a thread-safe collection to store the entities
            ConcurrentLinkedQueue<Entity> entityQueue = new ConcurrentLinkedQueue<>(world.loadedEntityList);

            // Use a thread pool with the number of threads equal to the number of available processors
            int numThreads = Runtime.getRuntime().availableProcessors();
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            CompletionService<Void> completionService = new ExecutorCompletionService<>(executorService);

            for (int i = 0; i < numThreads; i++) {
                completionService.submit(() -> {
                    Entity entity;
                    while ((entity = entityQueue.poll()) != null) {
                        if (entity instanceof EntityLivingBase) {
                            double distanceSq = entity.getDistanceSq(posX, posY, posZ);
                            if (distanceSq <= 256.0D) {
                                ((EntityLivingBase) entity).onStruckByLightning((EntityLightningBolt) (Object) this);
                            }
                        }
                    }
                    return null;
                });
            }

            try {
                for (int i = 0; i < numThreads; i++) {
                    completionService.take().get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            ci.cancel();
            executorService.shutdown();
        }
    }
}
