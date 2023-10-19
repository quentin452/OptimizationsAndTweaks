package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.pathfinding.PathEntity2;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.pathfinding.PathFinder2;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.pathfinding.PathPoint2;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathFinder.class)
public class MixinPathFinder {

    /**
     * @author
     * @reason
     */

    @Inject(method = "addToPath", at = @At("HEAD"), remap = false, cancellable = true)
    private PathEntity2 addToPath(Entity p_75861_1, PathPoint2 p_75861_2, PathPoint2 p_75861_3, PathPoint2 p_75861_4, float p_75861_5, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinPathFinder) {
            IBlockAccess world = p_75861_1.worldObj;

            boolean woodenDoor = true;
            boolean movementBlock = true;
            boolean inWater = false;
            boolean canDrown = true;

            PathFinder2 finder = new PathFinder2(
                world,
                woodenDoor,
                movementBlock,
                inWater,
                canDrown
            );

            return finder.addToPath(p_75861_1, p_75861_2, p_75861_3, p_75861_4, p_75861_5);
        }
        ci.cancel();
        return null;
    }
}
