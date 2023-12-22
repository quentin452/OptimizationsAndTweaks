package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.profiler.Profiler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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
    public boolean canContinue(EntityAITasks.EntityAITaskEntry p_75773_1_) {
        this.theProfiler.startSection("canContinue");
        boolean flag = p_75773_1_.action.continueExecuting();
        this.theProfiler.endSection();
        return flag;
    }

    /**
     * Returns whether two EntityAITaskEntries can be executed concurrently
     */
    @Shadow
    private boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry p_75777_1_,
        EntityAITasks.EntityAITaskEntry p_75777_2_) {
        return (p_75777_1_.action.getMutexBits() & p_75777_2_.action.getMutexBits()) == 0;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onUpdateTasks() {
        ArrayList arraylist = new ArrayList();
        Iterator iterator;
        EntityAITasks.EntityAITaskEntry entityaitaskentry;

        if (this.tickCount++ % this.tickRate == 0) {
            iterator = this.taskEntries.iterator();

            while (iterator.hasNext()) {
                entityaitaskentry = (EntityAITasks.EntityAITaskEntry) iterator.next();
                boolean flag = this.executingTaskEntries.contains(entityaitaskentry);

                if (flag) {
                    if (this.canUse(entityaitaskentry) && this.canContinue(entityaitaskentry)) {
                        continue;
                    }

                    entityaitaskentry.action.resetTask();
                    this.executingTaskEntries.remove(entityaitaskentry);
                }

                if (this.canUse(entityaitaskentry) && entityaitaskentry.action.shouldExecute()) {
                    arraylist.add(entityaitaskentry);
                    this.executingTaskEntries.add(entityaitaskentry);
                }
            }
        } else {
            iterator = this.executingTaskEntries.iterator();

            while (iterator.hasNext()) {
                entityaitaskentry = (EntityAITasks.EntityAITaskEntry) iterator.next();

                if (!entityaitaskentry.action.continueExecuting()) {
                    entityaitaskentry.action.resetTask();
                    iterator.remove();
                }
            }
        }

        this.theProfiler.startSection("goalStart");
        iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            entityaitaskentry = (EntityAITasks.EntityAITaskEntry) iterator.next();
            this.theProfiler.startSection(
                entityaitaskentry.action.getClass()
                    .getSimpleName());
            entityaitaskentry.action.startExecuting();
            this.theProfiler.endSection();
        }

        this.theProfiler.endSection();
        this.theProfiler.startSection("goalTick");
        iterator = this.executingTaskEntries.iterator();

        while (iterator.hasNext()) {
            entityaitaskentry = (EntityAITasks.EntityAITaskEntry) iterator.next();
            entityaitaskentry.action.updateTask();
        }

        this.theProfiler.endSection();
    }

    @Shadow
    private boolean canUse(EntityAITasks.EntityAITaskEntry p_75775_1_) {
        this.theProfiler.startSection("canUse");
        Iterator iterator = this.taskEntries.iterator();

        while (iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityaitaskentry = (EntityAITasks.EntityAITaskEntry) iterator.next();

            if (entityaitaskentry != p_75775_1_) {
                if (p_75775_1_.priority >= entityaitaskentry.priority) {
                    if (this.executingTaskEntries.contains(entityaitaskentry)
                        && !this.areTasksCompatible(p_75775_1_, entityaitaskentry)) {
                        this.theProfiler.endSection();
                        return false;
                    }
                } else if (this.executingTaskEntries.contains(entityaitaskentry)
                    && !entityaitaskentry.action.isInterruptible()) {
                        this.theProfiler.endSection();
                        return false;
                    }
            }
        }

        this.theProfiler.endSection();
        return true;
    }

}
