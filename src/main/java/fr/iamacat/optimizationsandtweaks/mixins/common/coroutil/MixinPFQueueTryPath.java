package fr.iamacat.optimizationsandtweaks.mixins.common.coroutil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import CoroUtil.pathfinding.PFQueue;
import CoroUtil.pathfinding.IPFCallback;
import CoroUtil.pathfinding.PFCallbackItem;
import fr.iamacat.optimizationsandtweaks.mixins.common.accessor.PathNavigateAccessor;
import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

@Mixin(PFQueue.class)
public class MixinPFQueueTryPath {

    /**
     * @reason Redirect to async pathfinding
     */
    @Overwrite
    public static boolean tryPath(Entity var1, int x, int y, int z, float var2, int priority, IPFCallback parCallback) {
        if (var1 instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) var1;
            AsyncPathfindingExecutor.submitPathfindingWithCallback(
                entity.worldObj,
                entity,
                (double)x,
                (double)y,
                (double)z,
                var2,
                (path) -> {
                    if (path != null) {
                        if (parCallback != null) {
                            parCallback.pfComplete(new PFCallbackItem(path, entity, 1.0f));
                        } else {
                            entity.getNavigator().setPath(path, 1.0);
                        }
                    }
                },
                (error) -> {},
                true, // isWoodenDoorAllowed
                true, // isMovementBlockAllowed
                ((PathNavigateAccessor)entity.getNavigator()).getCanSwim(),
                !entity.canBreatheUnderwater()
            );
            return true;
        }
        return false;
    }
}