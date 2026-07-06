package fr.iamacat.optimizationsandtweaks.mixins.common.thetitan;

import net.minecraft.entity.EntityLiving;
import net.minecraft.theTitans.events.WorldHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldHandler.class)
public class MixinWorldHandlerTitan {

    @Redirect(
        method = "createEvent",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;func_70106_y()V"),
        remap = false)
    private void redirectMobKill(EntityLiving mob) {
        // System.out.println("[OptimizationsAndTweaks] Prevented mob from being killed: " +
        // mob.getClass().getCanonicalName());
    }

}
