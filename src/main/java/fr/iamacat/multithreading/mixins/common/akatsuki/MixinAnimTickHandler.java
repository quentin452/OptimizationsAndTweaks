package fr.iamacat.multithreading.mixins.common.akatsuki;

import com.akazuki.animation.common.MCACommonLibrary.IMCAnimatedEntity;
import com.akazuki.animation.common.MCACommonLibrary.animation.AnimTickHandler;
import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(AnimTickHandler.class)
public class MixinAnimTickHandler {
    @Unique
    private ConcurrentLinkedQueue<IMCAnimatedEntity> multithreadingandtweaks$activeEntities = new ConcurrentLinkedQueue<>();
    @Inject(method = "addEntity", at = @At("HEAD"), remap = false, cancellable = true)
    public void addEntity(IMCAnimatedEntity entity, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinAnimTickHandler) {
        this.multithreadingandtweaks$activeEntities.add(entity);
        ci.cancel();
        }
    }
    @Inject(method = "onServerTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void onServerTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinAnimTickHandler) {
            if (!multithreadingandtweaks$activeEntities.isEmpty() && event.phase == TickEvent.Phase.START) {
                for (IMCAnimatedEntity entity : multithreadingandtweaks$activeEntities) {
                    entity.getAnimationHandler().animationsUpdate();
                    if (((Entity) entity).isDead) {
                        multithreadingandtweaks$activeEntities.remove(entity);
                    }
                }
            }
        }
        ci.cancel();
    }
}
