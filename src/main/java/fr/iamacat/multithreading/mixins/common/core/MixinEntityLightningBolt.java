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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(EntityLightningBolt.class)
public abstract class MixinEntityLightningBolt {

    private World worldObj;
    private double posX;
    private double posY;
    private double posZ;

    private MixinEntityLightningBolt(World world, double x, double y, double z, boolean effectOnly) {
        super();
        this.worldObj = world;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(World world, double x, double y, double z, CallbackInfo ci) {
        MultithreadingandtweaksMultithreadingConfig.enableMixinChunkPopulating = world.loadedEntityList != null
            && world.loadedEntityList.size() > MultithreadingandtweaksMultithreadingConfig.numberofcpus;
        SharedThreadPool.getExecutorService();
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinEntityLightningBolt) {
            World world = this.worldObj;
            if (world == null || world.isRemote || world.loadedEntityList == null) {
                return;
            }
            // Use a thread-safe collection to store the entities
            List<Entity> entities = new ArrayList<>(world.loadedEntityList);
            ConcurrentLinkedQueue<List<Entity>> entityBatches = new ConcurrentLinkedQueue<>();

            // Split the entities into batches of size batch_size
            int batch_size = MultithreadingandtweaksMultithreadingConfig.batchsize;;
            for (int i = 0; i < entities.size(); i += batch_size) {
                int end = Math.min(i + batch_size, entities.size());
                List<Entity> batch = entities.subList(i, end);
                entityBatches.add(batch);
            }

            // Use a ForkJoinPool with the number of threads equal to the number of available processors
            int numThreads = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
            ForkJoinPool forkJoinPool = new ForkJoinPool(numThreads);

            // Process each batch in parallel
            forkJoinPool.submit(
                () -> entityBatches.parallelStream()
                    .forEach(batch -> {
                        for (Entity entity : batch) {
                            if (entity instanceof EntityLivingBase) {
                                double distanceSq = entity.getDistanceSq(posX, posY, posZ);
                                if (distanceSq <= 256.0D) {
                                    ((EntityLivingBase) entity)
                                        .onStruckByLightning((EntityLightningBolt) (Object) this);
                                }
                            }
                        }
                    }))
                .join();

            ci.cancel();
            forkJoinPool.shutdown();
        }
    }
}
