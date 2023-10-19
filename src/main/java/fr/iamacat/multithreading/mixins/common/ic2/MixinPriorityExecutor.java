package fr.iamacat.multithreading.mixins.common.ic2;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.industrialcraft2.FixedPriorityQueue2;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.industrialcraft2.ThreadFactoryImpl2;
import ic2.core.util.PriorityExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(PriorityExecutor.class)
public class MixinPriorityExecutor extends ThreadPoolExecutor {
    public MixinPriorityExecutor(int threadCount) {
        super(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS, new FixedPriorityQueue2<>(), new ThreadFactoryImpl2());
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "executeAll", at = @At("HEAD"),remap = false, cancellable = true)
    public void executeAll(List<? extends Runnable> tasks, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinPriorityExecutor){
        if (this.isShutdown()) {
            throw new RejectedExecutionException("Tasks " + tasks + " rejected from " + this + ".");
        } else {
            while(this.prestartCoreThread()) {
            }

            this.getQueue().addAll(tasks);
        }
        ci.cancel();
    }
    }
}
