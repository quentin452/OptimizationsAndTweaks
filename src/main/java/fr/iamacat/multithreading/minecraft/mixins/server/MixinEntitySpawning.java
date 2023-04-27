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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOGGER = LogManager.getLogger(MixinEntitySpawning.class);
    @Shadow
    private WorldClient world;
    private ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private List<Entity> spawnQueue = new ArrayList<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        LOGGER.debug("MixinEntitySpawning tick method called.");
        LOGGER.debug("Multithreaded config is {}", Multithreaded.MixinEntitySpawning);
        LOGGER.debug("World total time is {}", world.getTotalWorldTime());
        if (Multithreaded.MixinEntitySpawning && world.getTotalWorldTime() % 10 == 0) {
            LOGGER.debug("Spawning mobs in queue.");
            spawnMobsInQueue();
        }
    }


    @Redirect(
        method = "renderWorld",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;doRenderEntities(Lnet/minecraft/entity/Entity;DDDFFZ)V"))
    private void redirectDoRenderEntities(RenderGlobal renderGlobal, Entity renderViewEntity, double partialTicks,
                                          double cameraX, double cameraY, double cameraZ, float frameDelta, boolean isInFrustum) {
        // Add mobs to the spawn queue instead of spawning them immediately
        for (Object entity : world.loadedEntityList) {
            if (entity instanceof EntityMob) {
                LOGGER.debug("Adding mob to spawn queue: {}", entity);
                spawnQueue.add((Entity) entity);
            }
        }
    }


    // doesn't work
    private void spawnMobsInQueue() {
        if (Multithreaded.MixinEntitySpawning && spawnQueue.isEmpty()) {
            LOGGER.debug("Spawn queue is empty.");
            return;
        }

        // Spawn mobs in batches of 10 using ThreadPoolExecutor
        int batchSize = Runtime.getRuntime()
            .availableProcessors() * 2;
        List<Entity> batch = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize && !spawnQueue.isEmpty(); i++) {
            batch.add(spawnQueue.remove(0));
        }
        for (Entity entity : batch) {
            executorService.execute(() -> entity.onEntityUpdate());

        }
    }
}
