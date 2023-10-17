package fr.iamacat.multithreading.mixins.common.core;

import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityAITasks.class)
public class MixinEntityAITasks {
    @Shadow
    private final Profiler theProfiler;
    @Shadow
    public List<EntityAITasks.EntityAITaskEntry> taskEntries = new ArrayList<EntityAITasks.EntityAITaskEntry>();
    @Shadow
    private List executingTaskEntries = new ArrayList();

    public MixinEntityAITasks(Profiler p_i1628_1_) {
        this.theProfiler = p_i1628_1_;
    }

    /**
     * Determine if a specific AI Task can be executed, which means that all running higher (= lower int value) priority
     * tasks are compatible with it or all lower priority tasks can be interrupted.
     */
    @Overwrite
    private boolean canUse(EntityAITasks.EntityAITaskEntry p_75775_1_) {
        this.theProfiler.startSection("canUse");

        for (EntityAITasks.EntityAITaskEntry entityaitaskentry : this.taskEntries) {
            if (entityaitaskentry != p_75775_1_) {
                if (p_75775_1_.priority >= entityaitaskentry.priority) {
                    boolean isExecuting = this.executingTaskEntries.contains(entityaitaskentry);
                    if (isExecuting && !multithreadingandtweaks$areTasksCompatible(p_75775_1_, entityaitaskentry)) {
                        this.theProfiler.endSection();
                        return false;
                    }
                } else {
                    if (this.executingTaskEntries.contains(entityaitaskentry) && !entityaitaskentry.action.isInterruptible()) {
                        this.theProfiler.endSection();
                        return false;
                    }
                }
            }
        }

        this.theProfiler.endSection();
        return true;
    }

    /**
     * Returns whether two EntityAITaskEntries can be executed concurrently
     */
    @Unique
    private boolean multithreadingandtweaks$areTasksCompatible(EntityAITasks.EntityAITaskEntry p_75777_1_, EntityAITasks.EntityAITaskEntry p_75777_2_) {
        return (p_75777_1_.action.getMutexBits() & p_75777_2_.action.getMutexBits()) == 0;
    }
}
