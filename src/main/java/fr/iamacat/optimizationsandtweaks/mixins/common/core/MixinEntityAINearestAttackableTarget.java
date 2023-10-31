package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mixin(EntityAINearestAttackableTarget.class)
public class MixinEntityAINearestAttackableTarget extends EntityAITarget
{
    @Shadow
    private final Class targetClass;
    @Shadow
    private final int targetChance;
    /** Instance of EntityAINearestAttackableTargetSorter. */
    @Shadow
    private final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    /**
     * This filter is applied to the Entity search.  Only matching entities will be targetted.  (null -> no
     * restrictions)
     */
    @Shadow
    private final IEntitySelector targetEntitySelector;
    @Shadow
    private EntityLivingBase targetEntity;
    public MixinEntityAINearestAttackableTarget(EntityCreature p_i1663_1_, Class p_i1663_2_, int p_i1663_3_, boolean p_i1663_4_)
    {
        this(p_i1663_1_, p_i1663_2_, p_i1663_3_, p_i1663_4_, false);
    }

    public MixinEntityAINearestAttackableTarget(EntityCreature p_i1664_1_, Class p_i1664_2_, int p_i1664_3_, boolean p_i1664_4_, boolean p_i1664_5_)
    {
        this(p_i1664_1_, p_i1664_2_, p_i1664_3_, p_i1664_4_, p_i1664_5_, (IEntitySelector)null);
    }

    public MixinEntityAINearestAttackableTarget(EntityCreature p_i1665_1_, Class p_i1665_2_, int p_i1665_3_, boolean p_i1665_4_, boolean p_i1665_5_, final IEntitySelector p_i1665_6_)
    {
        super(p_i1665_1_, p_i1665_4_, p_i1665_5_);
        this.targetClass = p_i1665_2_;
        this.targetChance = p_i1665_3_;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(p_i1665_1_);
        this.setMutexBits(1);
        this.targetEntitySelector = new IEntitySelector()
        {
            /**
             * Return whether the specified entity is applicable to this filter.
             */
            public boolean isEntityApplicable(Entity p_82704_1_)
            {
                return !(p_82704_1_ instanceof EntityLivingBase) ? false : (p_i1665_6_ != null && !p_i1665_6_.isEntityApplicable(p_82704_1_) ? false : isSuitableTarget((EntityLivingBase)p_82704_1_, false));
            }
        };
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Overwrite
    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        }

        double d0 = this.getTargetDistance();
        List<EntityLivingBase> list = this.taskOwner.worldObj.getEntitiesWithinAABB(
            this.targetClass, this.taskOwner.boundingBox.expand(d0, 4.0D, d0));

        list.removeIf(entity -> !this.targetEntitySelector.isEntityApplicable(entity));

        if (list.isEmpty()) {
            return false;
        }

        list.sort(this.theNearestAttackableTargetSorter);
        this.targetEntity = list.get(0);
        return true;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Overwrite
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}
