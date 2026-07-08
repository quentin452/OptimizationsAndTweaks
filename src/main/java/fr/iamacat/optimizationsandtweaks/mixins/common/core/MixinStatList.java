package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds a way to debug Registered stat ID in logs.
 */
@Mixin(StatList.class)
public abstract class MixinStatList {

    @Inject(method = "func_151182_a", at = @At("TAIL"), remap = false)
    private static void onRegisterStat(EntityList.EntityEggInfo eggInfo, CallbackInfoReturnable<StatBase> info) {
        StatBase stat = info.getReturnValue();
        if (stat != null) {
            String statId = stat.statId;
            System.out.println("[OptimizationsAndTweaks Registered stat ID] " + statId);
        }
    }
}
