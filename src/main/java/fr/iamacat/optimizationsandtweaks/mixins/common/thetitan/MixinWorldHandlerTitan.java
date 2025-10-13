package fr.iamacat.optimizationsandtweaks.mixins.common.thetitan;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.theTitans.events.WorldHandler;

import net.minecraft.entity.EntityLiving;

@Mixin(WorldHandler.class)
public class MixinWorldHandlerTitan {

    @Redirect(
        method = "createEvent",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;func_70106_y()V"),
        remap = false
    )
    private void redirectMobKill(EntityLiving mob) {
        //System.out.println("[OptimizationsAndTweaks] Prevented mob from being killed: " + mob.getClass().getCanonicalName());
    }


}