package fr.iamacat.optimizationsandtweaks.mixins.common.coroutil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import CoroUtil.pathfinding.IPFCallback;
import CoroUtil.pathfinding.PFCallbackItem;
import CoroUtil.pathfinding.PFQueue;
import fr.iamacat.optimizationsandtweaks.mixins.common.accessor.PathNavigateAccessor;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.AsyncPathRequestDispatcher;

@Mixin(PFQueue.class)
public class MixinPFQueueTryPath {

    /**
     * @reason Redirect to async pathfinding. Reached via PFQueue.getPath(...) from mob AI, i.e.
     *         on the server thread, so the snapshot read is safe; the completion callback runs on
     *         the server tick (never off-thread). Returns false to fall back to CoroUtil's own
     *         pathfinding when the request is declined (region too large / native queue full).
     */
    @Overwrite
    public static boolean tryPath(Entity var1, int x, int y, int z, float var2, int priority, IPFCallback parCallback) {
        if (var1 instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) var1;
            long requestId = AsyncPathRequestDispatcher.submit(
                entity,
                entity.worldObj,
                (double) x,
                (double) y,
                (double) z,
                var2,
                true, // isWoodenDoorAllowed
                true, // isMovementBlockAllowed
                ((PathNavigateAccessor) entity.getNavigator()).getCanSwim(),
                !entity.canBreatheUnderwater(),
                (path) -> {
                    if (path != null) {
                        if (parCallback != null) {
                            parCallback.pfComplete(new PFCallbackItem(path, entity, 1.0f));
                        } else {
                            entity.getNavigator()
                                .setPath(path, 1.0);
                        }
                    }
                },
                (error) -> {});
            return requestId != 0;
        }
        return false;
    }
}
