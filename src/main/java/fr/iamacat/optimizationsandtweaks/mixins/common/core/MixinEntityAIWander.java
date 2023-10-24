package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityAIWander.class)
public class MixinEntityAIWander extends EntityAIBase
{  //WIP
    @Shadow
    private EntityCreature entity;
    @Shadow
    private double xPosition;
    @Shadow
    private double yPosition;
    @Shadow
    private double zPosition;
    @Shadow
    private double speed;

    public MixinEntityAIWander(EntityCreature p_i1648_1_, double p_i1648_2_)
    {
        this.entity = p_i1648_1_;
        this.speed = p_i1648_2_;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Overwrite
    public boolean shouldExecute()
    {
        if (this.entity.getAge() >= 100)
        {
            return false;
        }
        else if (this.entity.getRNG().nextInt(120) != 0)
        {
            return false;
        }
        else
        {
            Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);

            if (vec3 == null)
            {
                return false;
            }
            else
            {
                this.xPosition = vec3.xCoord;
                this.yPosition = vec3.yCoord;
                this.zPosition = vec3.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }
}
