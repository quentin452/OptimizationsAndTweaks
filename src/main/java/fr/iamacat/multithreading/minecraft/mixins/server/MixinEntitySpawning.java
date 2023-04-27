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

package fr.iamacat.multithreading.minecraft.mixins.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.network.internal.EntitySpawnHandler;
import fr.iamacat.multithreading.Multithreaded;

@Mixin(EntitySpawnHandler.class)
public abstract class MixinEntitySpawning {
    private ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Mob-Spawner-%d").build());

    @Shadow
    private WorldClient world;
    private List<Entity> spawnQueue = new ArrayList<>();
    private int batchSize = 5;

    private int maxPoolSize = 8;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (Multithreaded.MixinEntitySpawning && world.getTotalWorldTime() % 10 == 0) {
            // Check if the world is saved
            if (world.getSaveHandler().getWorldDirectory().exists()) {
                // Disable batch spawning
                batchSize = 1;
                // Clear the spawn queue
                spawnQueue.clear();
                // Clear the entity list
                world.loadedEntityList.clear();
            } else {
                spawnMobsInQueue();
            }
        }
    }

    private void spawnMobsInQueue() {
        while (!spawnQueue.isEmpty()) {
            List<Entity> batch = new ArrayList<>();
            int batchSize = Math.min(spawnQueue.size(), this.batchSize);
            for (int i = 0; i < batchSize; i++) {
                Entity entity = spawnQueue.remove(0);
                if (entity instanceof EntityMob) {
                    batch.add(entity);
                }
            }
            if (!batch.isEmpty()) {
                executorService.submit(() -> {
                    batch.forEach(e -> world.spawnEntityInWorld(e));
                });
                // Increase the pool size if the queue is full
                if (executorService.getQueue().remainingCapacity() == 0 && executorService.getPoolSize() < maxPoolSize) {
                    executorService.setMaximumPoolSize(executorService.getMaximumPoolSize() + 1);
                }
            }
        }
    }

    @Redirect(
        method = "renderEntities",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/Render;doRender(Lnet/minecraft/entity/Entity;DDDFF)V"))
    private void redirectDoRenderEntities(Render render, Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        // Don't render entities during mob spawning
        if (!spawnQueue.isEmpty()) {
            return;
        }
        render.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Final
    public void close() {
        executorService.shutdown();
    }
}
