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
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import scala.reflect.internal.Importers;

@Mixin(value = WorldServer.class, priority = 1000)
    public abstract class MixinEntityUpdate {

    // Limit the number of entities updated per tick to 50
    private static final int MAX_ENTITIES_PER_TICK = 50;
    private int availableProcessors;
    private ThreadPoolExecutor executorService;
    private int maxPoolSize;
    private AtomicReference<WorldServer> world;
    private BlockingQueue<Entity> entitiesToUpdate = new LinkedBlockingQueue<>();
    private CopyOnWriteArrayList<Entity> loadedEntities;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        availableProcessors = Runtime.getRuntime().availableProcessors();
        executorService = new ThreadPoolExecutor(availableProcessors, availableProcessors, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
            Thread t = new Thread(r);
            t.setName("Mob-Spawner-" + this.hashCode() + "-" + t.getId());
            return t;
        });
        world = new AtomicReference<>((WorldServer) (Object) this);
        loadedEntities = new CopyOnWriteArrayList<>(getWorldServer().loadedEntityList);
    }

    public synchronized WorldServer getWorldServer() {
        return world.get();
    }

    private synchronized void addEntityToUpdateQueue(Entity entity) throws InterruptedException {
        entitiesToUpdate.put(entity);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!MultithreadingandtweaksConfig.enableMixinEntityUpdate) {
            List<Entity> entitiesToUpdateBatch = new ArrayList<>();

            // Limit the number of entities updated per tick
            int count = 0;

            // Process entities in batches
            while (!entitiesToUpdate.isEmpty() && count < MAX_ENTITIES_PER_TICK) {
                Entity entity = entitiesToUpdate.poll();
                entitiesToUpdateBatch.add(entity);
                count++;
            }

            if (!entitiesToUpdateBatch.isEmpty()) {
                executorService.execute(() -> {
                    for (Entity entity : entitiesToUpdateBatch) {
                        entity.onEntityUpdate();
                    }
                });
            }
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        executorService.shutdown();
    }
}

