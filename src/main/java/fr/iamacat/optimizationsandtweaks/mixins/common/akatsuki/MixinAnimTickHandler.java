package fr.iamacat.optimizationsandtweaks.mixins.common.akatsuki;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.akazuki.animation.common.MCACommonLibrary.IMCAnimatedEntity;
import com.akazuki.animation.common.MCACommonLibrary.animation.AnimTickHandler;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(AnimTickHandler.class)
public class MixinAnimTickHandler {

    @Unique
    private ConcurrentLinkedQueue<IMCAnimatedEntity> optimizationsAndTweaks$activeEntities = new ConcurrentLinkedQueue<>();

    @Inject(method = "addEntity", at = @At("HEAD"), remap = false, cancellable = true)
    public void addEntity(IMCAnimatedEntity entity, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinAnimTickHandler) {
            this.optimizationsAndTweaks$activeEntities.add(entity);
            ci.cancel();
        }
    }

    @Inject(method = "onServerTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void onServerTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinAnimTickHandler) {
            if (!optimizationsAndTweaks$activeEntities.isEmpty() && event.phase == TickEvent.Phase.START) {
                for (IMCAnimatedEntity entity : optimizationsAndTweaks$activeEntities) {
                    entity.getAnimationHandler()
                        .animationsUpdate();
                    if (((Entity) entity).isDead) {
                        optimizationsAndTweaks$activeEntities.remove(entity);
                    }
                }
            }
        }
        ci.cancel();
    }
}
