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

import fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfinding;

/**
 * Mixin for PathFinder to use Rust pathfinding implementation
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
    private static boolean optimizationsAndTweaks$rustPathfindingEnabled = false;

    @Unique
    private static boolean optimizationsAndTweaks$rustPathfindingChecked = false;

    @Unique
    private RustPathfinding.PathFinderHandle optimizationsAndTweaks$rustPathFinderHandle = null;

    /**
     * Check if Rust pathfinding is available (only once)
     */
    @Unique
    private static void optimizationsAndTweaks$checkRustPathfinding() {
        if (!optimizationsAndTweaks$rustPathfindingChecked) {
            optimizationsAndTweaks$rustPathfindingChecked = true;
            optimizationsAndTweaks$rustPathfindingEnabled = RustPathfinding.isAvailable();
        }
    }

    /**
     * Intercept createEntityPathTo(Entity, Entity, float) to use Rust implementation
     */
    @Inject(
        method = "createEntityPathTo(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/pathfinding/PathEntity;",
        at = @At("HEAD"),
        cancellable = true)
    private void optimizationsAndTweaks$createEntityPathToEntity(Entity entity, Entity target, float maxDistance,
        CallbackInfoReturnable<PathEntity> cir) {
        optimizationsAndTweaks$checkRustPathfinding();

        if (!optimizationsAndTweaks$rustPathfindingEnabled) {
            return; // Use vanilla implementation
        }

        try {
            // Get or create PathFinder handle for this instance
            long pathfinderHandle = optimizationsAndTweaks$getOrCreatePathFinderHandle();

            // Use Rust pathfinding
            PathEntity path = fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfindingBridge.findPathDirect(
                this.worldMap,
                entity,
                target.posX,
                target.boundingBox.minY,
                target.posZ,
                maxDistance,
                this.isWoddenDoorAllowed,
                this.isMovementBlockAllowed,
                this.isPathingInWater,
                this.canEntityDrown);

            cir.setReturnValue(path);
        } catch (Exception e) {
            // Fall back to vanilla on error
            cpw.mods.fml.common.FMLLog.warning(
                "[OptimizationsAndTweaks] Rust pathfinding failed, falling back to vanilla: %s",
                e.getMessage());
        }
    }

    /**
     * Intercept createEntityPathTo(Entity, int, int, int, float) to use Rust implementation
     */
    @Inject(
        method = "createEntityPathTo(Lnet/minecraft/entity/Entity;IIIF)Lnet/minecraft/pathfinding/PathEntity;",
        at = @At("HEAD"),
        cancellable = true)
    private void optimizationsAndTweaks$createEntityPathToCoords(Entity entity, int x, int y, int z, float maxDistance,
        CallbackInfoReturnable<PathEntity> cir) {
        optimizationsAndTweaks$checkRustPathfinding();

        if (!optimizationsAndTweaks$rustPathfindingEnabled) {
            return; // Use vanilla implementation
        }

        try {
            // Get or create PathFinder handle for this instance
            long pathfinderHandle = optimizationsAndTweaks$getOrCreatePathFinderHandle();

            // Use Rust pathfinding
            PathEntity path = fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfindingBridge.findPathDirect(
                this.worldMap,
                entity,
                (double) x + 0.5,
                (double) y + 0.5,
                (double) z + 0.5,
                maxDistance,
                this.isWoddenDoorAllowed,
                this.isMovementBlockAllowed,
                this.isPathingInWater,
                this.canEntityDrown);

            cir.setReturnValue(path);
        } catch (Exception e) {
            // Fall back to vanilla on error
            cpw.mods.fml.common.FMLLog.warning(
                "[OptimizationsAndTweaks] Rust pathfinding failed, falling back to vanilla: %s",
                e.getMessage());
        }
    }

    /**
     * Get or create the Rust PathFinder handle for this instance
     */
    @Unique
    private long optimizationsAndTweaks$getOrCreatePathFinderHandle() {
        if (optimizationsAndTweaks$rustPathFinderHandle == null) {
            optimizationsAndTweaks$rustPathFinderHandle = new RustPathfinding.PathFinderHandle(
                this.isWoddenDoorAllowed,
                this.isMovementBlockAllowed,
                this.isPathingInWater,
                this.canEntityDrown);
        }
        return optimizationsAndTweaks$rustPathFinderHandle.getHandle();
    }
}
