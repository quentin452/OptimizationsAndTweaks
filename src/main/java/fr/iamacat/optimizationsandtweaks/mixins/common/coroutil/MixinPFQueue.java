package fr.iamacat.optimizationsandtweaks.mixins.common.coroutil;

import net.minecraft.entity.EntityLiving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import CoroUtil.pathfinding.PFCallbackItem;
import CoroUtil.pathfinding.PFJobData;
import CoroUtil.pathfinding.PFQueue;
import fr.iamacat.optimizationsandtweaks.mixins.common.accessor.PathNavigateAccessor;
import fr.iamacat.optimizationsandtweaks.utils.pathfinding.AsyncPathRequestDispatcher;

@Mixin(PFQueue.class)
public class MixinPFQueue {

    /**
     * @reason Redirect to async pathfinding. Reached via PFQueue.getPath(...) from mob AI, i.e.
     *         on the server thread, so the snapshot read is safe; the completion callback runs on
     *         the server tick (never off-thread). Returns false to fall back to CoroUtil's own
     *         pathfinding when the request is declined (region too large / native queue full).
     */
    @Overwrite
    public static boolean tryPath(PFJobData parJob) {
        if (parJob.sourceEntity instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) parJob.sourceEntity;
            long requestId = AsyncPathRequestDispatcher.submit(
                entity,
                entity.worldObj,
                (double) parJob.dest.posX,
                (double) parJob.dest.posY,
                (double) parJob.dest.posZ,
                parJob.distMax,
                true, // isWoodenDoorAllowed - default to true
                true, // isMovementBlockAllowed - default to true
                ((PathNavigateAccessor) entity.getNavigator()).getCanSwim(),
                !entity.canBreatheUnderwater(),
                (path) -> {
                    if (path != null) {
                        if (parJob.callback != null) {
                            parJob.callback.pfComplete(new PFCallbackItem(path, entity, 1.0f));
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
