package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.utils.pathfinding.AsyncPathCaches;

/**
 * Captures the speed an AI asks its navigator to move at, so the async path apply
 * ({@link MixinPathFinder}) can restore it instead of hardcoding 1.0.
 *
 * <p>
 * Vanilla flow is {@code tryMoveToXYZ(x, y, z, speed) -> getPathToXYZ() -> setPath(path, speed)}.
 * Under async pathfinding {@code getPathToXYZ} returns null (path in flight), so vanilla runs
 * {@code setPath(null, speed)} and the real path is applied a few ticks later off this call. We
 * record {@code speed} here — including the null-path call, hence {@code @At("HEAD")} before
 * vanilla's early return — keyed by entity id; {@code MixinPathFinder} reads it on apply. Without
 * this, EntityAIPanic's 2.0 sprint (and any non-1.0 movement) collapses to a normal walk.
 */
@Mixin(PathNavigate.class)
public abstract class MixinPathNavigate {

    @Shadow
    private EntityLiving theEntity;

    @Inject(method = "setPath(Lnet/minecraft/pathfinding/PathEntity;D)Z", at = @At("HEAD"), require = 0)
    private void optimizationsAndTweaks$captureSpeed(PathEntity path, double speed,
        CallbackInfoReturnable<Boolean> cir) {
        if (theEntity != null) {
            AsyncPathCaches.requestedSpeed.put(theEntity.getEntityId(), speed);
        }
    }
}
