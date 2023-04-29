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

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(value = WorldServer.class, priority = 1000)
public abstract class MixinEntityUpdate {

    private int availableProcessors;
    private ThreadPoolExecutor executorService;
    private int maxPoolSize;
    private WorldServer world;
    // Store the entities to be updated in a thread-safe queue
    private LinkedBlockingQueue<Entity> entitiesToUpdate;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setNameFormat("Mob-Spawner-" + this.hashCode() + "-%d");
        executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool(builder.build());
        world = (WorldServer) (Object) this;
    }

    // Define a getter method for the world object
    public WorldServer getWorldServer() {
        return world;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // Create a new queue of entities to be updated for this tick
        entitiesToUpdate = new LinkedBlockingQueue<>();

        if (!MultithreadingandtweaksConfig.enableMixinEntityUpdate) {
            // Dynamically set the maximum pool size
            int newMaxPoolSize = Math.max(availableProcessors, 1);
            if (newMaxPoolSize != maxPoolSize) {
                maxPoolSize = newMaxPoolSize;
                executorService.setMaximumPoolSize(maxPoolSize);
            }
        }

        // Add all entities to the queue to be updated
        for (Object e : ((WorldServer) (Object) this).loadedEntityList) {
            entitiesToUpdate.offer((Entity) e);
        }
        for (Object e : ((WorldServer) (Object) this).weatherEffects) {
            entitiesToUpdate.offer((Entity) e);
        }

        // Update all entities in the queue
        while (!entitiesToUpdate.isEmpty()) {
            Entity e = entitiesToUpdate.poll();
            ((WorldServer) (Object) this).updateEntity(e);
            e.onEntityUpdate();
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        // Shutdown the executorService to avoid a memory leak
        executorService.shutdown();
    }
}
