package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;
import fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfindingBridge;

import fr.iamacat.optimizationsandtweaks.utils.pathfinding.PendingPathRequest;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.CachedPath;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mixin for PathFinder to use async Rust pathfinding implementation
 * 
 * This mixin intercepts pathfinding requests and submits them to the async executor.
 * Results are cached per entity and reused when available.
 */
@Mixin(PathFinder.class)
public abstract class MixinPathFinder {

    @Shadow
    private IBlockAccess worldMap;

    @Shadow
    private boolean isWoddenDoorAllowed;

    @Shadow
    private boolean isMovementBlockAllowed;

    @Shadow
    private boolean isPathingInWater;

    @Shadow
    private boolean canEntityDrown;

    @Unique
    private static boolean optimizationsAndTweaks$asyncPathfindingEnabled = false;

    @Unique
    private static boolean optimizationsAndTweaks$asyncPathfindingChecked = false;

    // Cache for pending async pathfinding requests per entity
    @Unique
    private static final Map<Integer, PendingPathRequest> optimizationsAndTweaks$pendingPaths = new ConcurrentHashMap<>();

    // Cache for completed paths per entity
    @Unique
    private static final Map<Integer, CachedPath> optimizationsAndTweaks$cachedPaths = new ConcurrentHashMap<>();

    /**
     * Check if async pathfinding is available (only once)
     */
    @Unique
    private static void optimizationsAndTweaks$checkAsyncPathfinding() {
        // Always re-check and (re)initialize if necessary, to handle world restarts
        optimizationsAndTweaks$asyncPathfindingEnabled = AsyncPathfindingExecutor.isInitialized();
        if (!optimizationsAndTweaks$asyncPathfindingEnabled) {
            AsyncPathfindingExecutor.initializeAuto();
            optimizationsAndTweaks$asyncPathfindingEnabled = AsyncPathfindingExecutor.isInitialized();
        }
    }

    /**
     * Intercept createEntityPathTo(Entity, Entity, float) to use async Rust implementation
     */
    @Inject(
        method = "createEntityPathTo(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/pathfinding/PathEntity;",
        at = @At("HEAD"),
        cancellable = true)
    private void optimizationsAndTweaks$createEntityPathToEntity(Entity entity, Entity target, float maxDistance,
        CallbackInfoReturnable<PathEntity> cir) {
        optimizationsAndTweaks$checkAsyncPathfinding();

        if (!optimizationsAndTweaks$asyncPathfindingEnabled) {
            return; // Use vanilla implementation
        }

        try {
            PathEntity path = optimizationsAndTweaks$getOrRequestPath(
                entity,
                target.posX,
                target.boundingBox.minY,
                target.posZ,
                maxDistance
            );

            if (path != null) {
                cir.setReturnValue(path);
            } else {
                // Prevent running the heavy vanilla pathfinder while async result is pending
                cir.setReturnValue(null);
            }
        } catch (Exception e) {
            // Fall back to vanilla on error
            cpw.mods.fml.common.FMLLog.warning(
                "[OptimizationsAndTweaks] Async pathfinding failed, falling back to vanilla: %s",
                e.getMessage());
        }
    }

    /**
     * Intercept createEntityPathTo(Entity, int, int, int, float) to use async Rust implementation
     */
    @Inject(
        method = "createEntityPathTo(Lnet/minecraft/entity/Entity;IIIF)Lnet/minecraft/pathfinding/PathEntity;",
        at = @At("HEAD"),
        cancellable = true)
    private void optimizationsAndTweaks$createEntityPathToCoords(Entity entity, int x, int y, int z, float maxDistance,
        CallbackInfoReturnable<PathEntity> cir) {
        optimizationsAndTweaks$checkAsyncPathfinding();

        if (!optimizationsAndTweaks$asyncPathfindingEnabled) {
            return; // Use vanilla implementation
        }

        try {
            PathEntity path = optimizationsAndTweaks$getOrRequestPath(
                entity,
                (double) x + 0.5,
                (double) y + 0.5,
                (double) z + 0.5,
                maxDistance
            );

            if (path != null) {
                cir.setReturnValue(path);
            } else {
                // Prevent running the heavy vanilla pathfinder while async result is pending
                cir.setReturnValue(null);
            }
        } catch (Exception e) {
            // Fall back to vanilla on error
            cpw.mods.fml.common.FMLLog.warning(
                "[OptimizationsAndTweaks] Async pathfinding failed, falling back to vanilla: %s",
                e.getMessage());
        }
    }

    /**
     * Get cached path or submit async request
     * Returns cached path if available, null if request is pending or needs to be submitted
     */
    @Unique
    private PathEntity optimizationsAndTweaks$getOrRequestPath(
            Entity entity,
            double targetX,
            double targetY,
            double targetZ,
            float maxDistance) {
        
        int entityId = entity.getEntityId();
        long currentTime = System.currentTimeMillis();

        // Check if we have a cached path that's still valid
        CachedPath cached = optimizationsAndTweaks$cachedPaths.get(entityId);
        if (cached != null && cached.isValid(entity.posX, entity.posY, entity.posZ, targetX, targetY, targetZ, currentTime)) {
            return cached.getPath();
        }

        // Check if we have a pending request
        PendingPathRequest pending = optimizationsAndTweaks$pendingPaths.get(entityId);
        if (pending != null && pending.isStillValid(targetX, targetY, targetZ, currentTime)) {
            // Request is still pending and target hasn't changed significantly
            // Return null to fall through to vanilla (entity will retry next tick)
            return null;
        }

        // Submit new async pathfinding request
        long requestId = AsyncPathfindingExecutor.submitPathfindingWithCallback(
                worldMap,
                entity,
                targetX,
                targetY,
                targetZ,
                maxDistance,
                path -> {
                    optimizationsAndTweaks$cachedPaths.put(entityId, new CachedPath(path, entity.posX, entity.posY, entity.posZ, targetX, targetY, targetZ, System.currentTimeMillis()));
                    optimizationsAndTweaks$pendingPaths.remove(entityId);
                    // Immediately apply the path so early-priority AIs (e.g., AttackOnCollide at 2) don't stall
                    try {
                        if (entity instanceof net.minecraft.entity.EntityLiving && path != null) {
                            ((net.minecraft.entity.EntityLiving) entity).getNavigator().setPath(path, 1.0D);
                        }
                    } catch (Throwable ignored) {}
                },
                error -> optimizationsAndTweaks$pendingPaths.remove(entityId),
                isWoddenDoorAllowed,
                isMovementBlockAllowed,
                isPathingInWater,
                canEntityDrown
        );

        if (requestId != 0) {
            // Request submitted successfully
            optimizationsAndTweaks$pendingPaths.put(
                entityId,
                new PendingPathRequest(requestId, targetX, targetY, targetZ, currentTime)
            );
        }

        // Return null to fall through to vanilla for this tick
        // The async result will be available on subsequent ticks
        return null;
    }
}
