package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Vec3;

@Mixin(PathEntity.class)
public abstract class MixinPathEntity {

    @Shadow
    private PathPoint[] points;

    @Shadow
    private int currentPathIndex;

    @Shadow
    public abstract Vec3 getVectorFromIndex(Entity entityIn, int index);

    @Inject(method = "getPosition(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Vec3;", at = @At("HEAD"), cancellable = true)
    public void getPosition(Entity entityIn, CallbackInfoReturnable<Vec3> cir) {
        if (this.currentPathIndex >= this.points.length) {
            if (this.points.length > 0) {
                cir.setReturnValue(this.getVectorFromIndex(entityIn, this.points.length - 1));
            } else {
                cir.setReturnValue(null);
            }
        }
    }
}
