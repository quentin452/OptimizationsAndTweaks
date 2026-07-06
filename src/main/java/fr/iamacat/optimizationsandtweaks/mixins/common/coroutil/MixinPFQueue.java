package fr.iamacat.optimizationsandtweaks.mixins.common.coroutil;

import net.minecraft.entity.EntityLiving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import CoroUtil.pathfinding.PFCallbackItem;
import CoroUtil.pathfinding.PFJobData;
import CoroUtil.pathfinding.PFQueue;
import fr.iamacat.optimizationsandtweaks.mixins.common.accessor.PathNavigateAccessor;
import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;

@Mixin(PFQueue.class)
public class MixinPFQueue {

    /**
     * @reason Redirect to async pathfinding
     */
    @Overwrite
    public static boolean tryPath(PFJobData parJob) {
        if (parJob.sourceEntity instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) parJob.sourceEntity;
            AsyncPathfindingExecutor.submitPathfindingWithCallback(
                entity.worldObj,
                entity,
                (double) parJob.dest.posX,
                (double) parJob.dest.posY,
                (double) parJob.dest.posZ,
                parJob.distMax,
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
                (error) -> {},
                true, // isWoodenDoorAllowed - default to true
                true, // isMovementBlockAllowed - default to true
                ((PathNavigateAccessor) entity.getNavigator()).getCanSwim(),
                !entity.canBreatheUnderwater());
            return true;
        }
        return false;
    }
}
