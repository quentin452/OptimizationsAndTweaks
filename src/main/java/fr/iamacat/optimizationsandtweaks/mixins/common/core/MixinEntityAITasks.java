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
    public List<EntityAITasks.EntityAITaskEntry> taskEntries = new ArrayList<EntityAITasks.EntityAITaskEntry>();
    @Shadow
    private List<EntityAITasks.EntityAITaskEntry> executingTaskEntries = new ArrayList<>();

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
     * @author
     * @reason
     */
    @Overwrite
    public void onUpdateTasks() {
        Object2ObjectHashMap<EntityAITasks.EntityAITaskEntry, Boolean> toAdd = new Object2ObjectHashMap<>();

        if (this.tickCount++ % this.tickRate == 0) {
            for (EntityAITasks.EntityAITaskEntry entry : this.taskEntries) {
                if (this.executingTaskEntries.contains(entry)) {
                    if (!this.canUse(entry) || !this.canContinue(entry)) {
                        entry.action.resetTask();
                        this.executingTaskEntries.remove(entry);
                    }
                } else if (this.canUse(entry) && entry.action.shouldExecute()) {
                    toAdd.put(entry, true);
                    this.executingTaskEntries.add(entry);
                }
            }
        } else {
            Iterator<EntityAITasks.EntityAITaskEntry> iterator = this.executingTaskEntries.iterator();
            while (iterator.hasNext()) {
                EntityAITasks.EntityAITaskEntry entry = iterator.next();
                if (!entry.action.continueExecuting()) {
                    entry.action.resetTask();
                    iterator.remove();
                }
            }
        }

        this.theProfiler.startSection("goalStart");
        for (EntityAITasks.EntityAITaskEntry entry : toAdd.keySet()) {
            this.theProfiler.startSection(entry.action.getClass().getSimpleName());
            entry.action.startExecuting();
            this.theProfiler.endSection();
        }
        this.theProfiler.endSection();

        this.theProfiler.startSection("goalTick");
        for (EntityAITasks.EntityAITaskEntry entry : this.executingTaskEntries) {
            entry.action.updateTask();
        }
        this.theProfiler.endSection();
    }

    /**
     * Determine if a specific AI Task can be executed, which means that all running higher (= lower int value) priority
     * tasks are compatible with it or all lower priority tasks can be interrupted.
     */
    @Overwrite
    private boolean canUse(EntityAITasks.EntityAITaskEntry p_75775_1_) {
        if (OptimizationsandTweaksConfig.enableMixinEntityAITasks) {
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
                        if (this.executingTaskEntries.contains(entityaitaskentry)
                            && !entityaitaskentry.action.isInterruptible()) {
                            this.theProfiler.endSection();
                            return false;
                        }
                    }
                }
            }

            this.theProfiler.endSection();
            return true;
        }
        return false;
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
