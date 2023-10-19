package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.pathfinding.PathEntity2;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.pathfinding.PathPoint2;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathFinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathFinder.class)
public class MixinPathFinder {

    @Inject(method = "addToPath", at = @At("HEAD"), remap = false, cancellable = true)
    private PathEntity2 addToPath(Entity p_75861_1_, PathPoint2 p_75861_2_, PathPoint2 p_75861_3_, PathPoint2 p_75861_4_, float p_75861_5_, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinPathFinder) {
            return addToPath(p_75861_1_, p_75861_2_, p_75861_3_, p_75861_4_, p_75861_5_,ci);
        }
        ci.cancel();
        return null;
    }
}
