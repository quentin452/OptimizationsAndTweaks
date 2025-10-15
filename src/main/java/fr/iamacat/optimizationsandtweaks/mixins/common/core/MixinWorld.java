package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.AxisAlignedBB;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfindingBridge;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.eventshandler.TidyChunkBackportEventHandler;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.CachedEntitySearch;

import fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfinding;

@Mixin(World.class)
public abstract class MixinWorld {

    @Unique
    private static final Map<Integer, CachedEntitySearch> entitySearchCache = new ConcurrentHashMap<>();

    @Unique
    private static final int CACHE_DURATION_TICKS = 40;

    @Unique
    private static long lastCacheCleanup = 0;

    @Unique
    private static final int CLEANUP_INTERVAL = 200;

    @Inject(method = "tick", at = @At(value = "INVOKE"))
    private void onTickInject(CallbackInfo info) {
        if (OptimizationsandTweaksConfig.enableTidyChunkBackport) {
            TidyChunkBackportEventHandler.injectInWorldTick((World) (Object) this);
        }
        if (OptimizationsandTweaksConfig.enablePathFindingOptimizations) {
            long worldTime = ((World) (Object) this).getTotalWorldTime();
            if (worldTime % 200 == 0 && RustPathfinding.isAvailable()) {
                RustPathfinding.printProfilerStats();
            }
        }

    }

    /**
     * Cache reads only for EntityLivingBase entities
     */
    @Inject(
        method = "getEntitiesWithinAABBExcludingEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cacheEntitySearchForMinions(
        Entity entity,
        AxisAlignedBB aabb,
        CallbackInfoReturnable<List> cir
    ) {
        if (!(entity instanceof EntityLivingBase) || (entity instanceof EntityPlayer)) {
            return;
        }

        World world = (World) (Object) this;
        long currentTick = world.getTotalWorldTime();

        if (currentTick - lastCacheCleanup > CLEANUP_INTERVAL) {
            lastCacheCleanup = currentTick;
            entitySearchCache.entrySet().removeIf(
                entry -> (currentTick - entry.getValue().timestamp) > CACHE_DURATION_TICKS * 2
            );
        }

        int cacheKey = generateCacheKey(entity, aabb);
        CachedEntitySearch cached = entitySearchCache.get(cacheKey);

        if (cached != null && (currentTick - cached.timestamp) < CACHE_DURATION_TICKS) {
            cir.setReturnValue(new ArrayList<>(cached.entities));
        }
    }

    /**
     * Cache writes only for EntityLivingBase entities
     */
    @Inject(
        method = "getEntitiesWithinAABBExcludingEntity",
        at = @At("RETURN")
    )
    private void cacheEntitySearchResult(
        Entity entity,
        AxisAlignedBB aabb,
        CallbackInfoReturnable<List> cir
    ) {
        if (!(entity instanceof EntityLivingBase) || (entity instanceof EntityPlayer)) {
            return; // skip caching for players
        }

        World world = (World) (Object) this;
        long currentTick = world.getTotalWorldTime();

        int cacheKey = generateCacheKey(entity, aabb);
        List result = cir.getReturnValue();

        entitySearchCache.put(
            cacheKey,
            new CachedEntitySearch(new ArrayList<>(result), currentTick)
        );
    }

    @Unique
    private static int generateCacheKey(Entity entity, AxisAlignedBB aabb) {
        int hash = entity.getEntityId();
        hash = 31 * hash + (int) aabb.minX;
        hash = 31 * hash + (int) aabb.minY;
        hash = 31 * hash + (int) aabb.minZ;
        hash = 31 * hash + (int) (aabb.maxX - aabb.minX);
        hash = 31 * hash + (int) (aabb.maxY - aabb.minY);
        hash = 31 * hash + (int) (aabb.maxZ - aabb.minZ);
        return hash;
    }
}