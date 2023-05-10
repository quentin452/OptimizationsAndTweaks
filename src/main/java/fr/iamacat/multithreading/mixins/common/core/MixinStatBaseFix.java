package fr.iamacat.multithreading.mixins.common.core;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.stats.StatBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(StatBase.class)
public abstract class MixinStatBaseFix {

    private static Map<String, StatBase> statMap = new HashMap<>();

    @Inject(method = "registerStat", at = @At("HEAD"), cancellable = true)
    private void onRegisterStat(CallbackInfoReturnable<StatBase> callbackInfo) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinStatBaseFix) {
            StatBase stat = callbackInfo.getReturnValue();

            if (stat != null && stat.statId != null) {
                String statId = stat.statId;

                if (statMap.containsKey(statId)) {
                    StatBase existingStat = statMap.get(statId);
                    existingStat.registerStat();
                    callbackInfo.setReturnValue(existingStat);
                } else {
                    statMap.put(statId, stat);
                    callbackInfo.cancel(); // Cancel the original method to prevent registration
                }
            }
        }
    }
}

