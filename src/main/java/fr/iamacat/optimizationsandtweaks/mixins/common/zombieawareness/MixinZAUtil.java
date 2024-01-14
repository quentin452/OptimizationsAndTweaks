package fr.iamacat.optimizationsandtweaks.mixins.common.zombieawareness;

import ZombieAwareness.EntityScent;
import ZombieAwareness.ZAUtil;
import ZombieAwareness.config.ZAConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(ZAUtil.class)
public class MixinZAUtil {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Entity getScent(Entity var0) {
        AxisAlignedBB searchBox = var0.boundingBox.expand(ZAConfig.maxPFRangeSense, ZAConfig.maxPFRangeSense, ZAConfig.maxPFRangeSense);
        List<Entity> var1 = var0.worldObj.getEntitiesWithinAABBExcludingEntity(var0, searchBox, entity -> entity instanceof EntityScent);
        Entity var3 = null;
        for (Entity var2 : var1) {
            if (var0.getDistanceToEntity(var2) < ((EntityScent) var2).getRange() && var0.getDistanceToEntity(var2) > 5.0F && var0.worldObj.rand.nextInt(20) == 0) {
                var3 = var2;
                break;
            }
        }
        return var3;
    }
}
