package fr.iamacat.optimizationsandtweaks.mixins.common.ic2;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.utilsformods.industrialcraft2.FixedPriorityQueue2;
import fr.iamacat.optimizationsandtweaks.utilsformods.industrialcraft2.ThreadFactoryImpl2;
import ic2.core.util.PriorityExecutor;

@Mixin(PriorityExecutor.class)
public class MixinPriorityExecutor extends ThreadPoolExecutor {

    public MixinPriorityExecutor(int threadCount) {
        super(
            threadCount,
            threadCount,
            0L,
            TimeUnit.MILLISECONDS,
            new FixedPriorityQueue2<>(),
            new ThreadFactoryImpl2());
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "executeAll", at = @At("HEAD"), remap = false, cancellable = true)
    public void executeAll(List<? extends Runnable> tasks, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinPriorityExecutor) {
            if (!this.isShutdown()) {
                this.getQueue()
                    .addAll(tasks);
            }
            ci.cancel();
        }
    }
}
