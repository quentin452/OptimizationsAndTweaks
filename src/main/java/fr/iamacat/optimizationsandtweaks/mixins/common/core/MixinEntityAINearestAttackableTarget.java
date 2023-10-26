package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.utils.multithreadingandtweaks.util.Sorter2;

@Mixin(EntityAINearestAttackableTarget.class)
public class MixinEntityAINearestAttackableTarget extends EntityAITarget {

    @Shadow
    private final Class<? extends EntityLivingBase> targetClass;
    @Shadow
    private final int targetChance;
    @Unique
    private final Sorter2 theNearestAttackableTargetSorter;
    @Shadow
    private final IEntitySelector targetEntitySelector;
    @Shadow
    private EntityLivingBase targetEntity;

    public MixinEntityAINearestAttackableTarget(EntityCreature p_i1663_1_, Class<? extends EntityLivingBase> p_i1663_2_,
        int p_i1663_3_, boolean p_i1663_4_) {
        this(p_i1663_1_, p_i1663_2_, p_i1663_3_, p_i1663_4_, false);
    }

    public MixinEntityAINearestAttackableTarget(EntityCreature p_i1664_1_, Class<? extends EntityLivingBase> p_i1664_2_,
        int p_i1664_3_, boolean p_i1664_4_, boolean p_i1664_5_) {
        this(p_i1664_1_, p_i1664_2_, p_i1664_3_, p_i1664_4_, p_i1664_5_, null);
    }

    public MixinEntityAINearestAttackableTarget(EntityCreature p_i1665_1_, Class<? extends EntityLivingBase> p_i1665_2_,
        int p_i1665_3, boolean p_i1665_4, boolean p_i1665_5, final IEntitySelector p_i1665_6) {
        super(p_i1665_1_, p_i1665_4, p_i1665_5);
        this.targetClass = p_i1665_2_;
        this.targetChance = p_i1665_3;
        this.theNearestAttackableTargetSorter = new Sorter2(p_i1665_1_);
        this.setMutexBits(1);
        this.targetEntitySelector = p_i1665_1_1 -> p_i1665_6 == null || (p_i1665_6.isEntityApplicable(p_i1665_1_)
            && this.isSuitableTarget((EntityLivingBase) p_i1665_1_, false));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG()
            .nextInt(this.targetChance) != 0) {
            return false;
        } else {
            double d0 = this.getTargetDistance();
            List<? extends EntityLivingBase> list = this.taskOwner.worldObj.selectEntitiesWithinAABB(
                targetClass,
                this.taskOwner.boundingBox.expand(d0, 4.0D, d0),
                targetEntitySelector);
            list.sort(this.theNearestAttackableTargetSorter);

            if (list.isEmpty()) {
                return false;
            } else {
                this.targetEntity = list.get(0);
                return true;
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}
