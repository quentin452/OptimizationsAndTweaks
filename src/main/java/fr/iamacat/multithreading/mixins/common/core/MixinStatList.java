package fr.iamacat.multithreading.mixins.common.core;

import net.minecraft.entity.EntityList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(StatList.class)
public abstract class MixinStatList {

    @Inject(method = "func_151182_a", at = @At("TAIL"), remap = false)
    private static void onRegisterStat(EntityList.EntityEggInfo eggInfo, CallbackInfoReturnable<StatBase> info) {
        if (MultithreadingandtweaksConfig.enableMixinStatList) {
            StatBase stat = info.getReturnValue();
            String statId = stat.statId;
            System.out.println("[Registered stat ID] " + statId);
        }
    }
}
