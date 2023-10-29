package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.profiler.Profiler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(EntityAITasks.class)
public class MixinEntityAITasks {

    @Shadow
    private int tickCount;
    @Shadow
    private int tickRate = 3;
    @Shadow
    private final Profiler theProfiler;
    @Shadow
    public List taskEntries = new ArrayList();
    @Shadow
    private List executingTaskEntries = new ArrayList();

    public MixinEntityAITasks(Profiler p_i1628_1_) {
        this.theProfiler = p_i1628_1_;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private boolean canContinue(EntityAITasks.EntityAITaskEntry p_75773_1_)
    {
        this.theProfiler.startSection("canContinue");
        boolean flag = p_75773_1_.action.continueExecuting();
        this.theProfiler.endSection();
        return flag;
    }
    
    /**
     * Returns whether two EntityAITaskEntries can be executed concurrently
     */
    @Unique
    private boolean multithreadingandtweaks$areTasksCompatible(EntityAITasks.EntityAITaskEntry p_75777_1_,
        EntityAITasks.EntityAITaskEntry p_75777_2_) {
        return (p_75777_1_.action.getMutexBits() & p_75777_2_.action.getMutexBits()) == 0;
    }
}
