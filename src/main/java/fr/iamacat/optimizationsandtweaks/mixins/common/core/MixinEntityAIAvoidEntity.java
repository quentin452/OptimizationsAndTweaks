package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(EntityAIAvoidEntity.class)
public class MixinEntityAIAvoidEntity {
    @Shadow
    public final IEntitySelector field_98218_a = new IEntitySelector()
    {
        /**
         * Return whether the specified entity is applicable to this filter.
         */

        public boolean isEntityApplicable(Entity p_82704_1_)
        {
            return p_82704_1_.isEntityAlive() && theEntity.getEntitySenses().canSee(p_82704_1_);
        }
    };
    /** The entity we are attached to */
    @Shadow
    private EntityCreature theEntity;
    @Shadow
    private double farSpeed;
    @Shadow
    private double nearSpeed;
    @Shadow
    private Entity closestLivingEntity;
    @Shadow
    private float distanceFromEntity;
    /** The PathEntity of our entity */
    @Shadow
    private PathEntity entityPathEntity;
    /** The PathNavigate of our entity */
    @Shadow
    private PathNavigate entityPathNavigate;
    /** The class of the entity we should avoid */
    @Shadow
    private Class targetEntityClass;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean shouldExecute()
    {
        if (this.targetEntityClass == EntityPlayer.class)
        {
            if (this.theEntity instanceof EntityTameable && ((EntityTameable)this.theEntity).isTamed())
            {
                return false;
            }

            this.closestLivingEntity = this.theEntity.worldObj.getClosestPlayerToEntity(this.theEntity, this.distanceFromEntity);

            if (this.closestLivingEntity == null)
            {
                return false;
            }
        }
        else
        {
            List list = this.theEntity.worldObj.selectEntitiesWithinAABB(this.targetEntityClass, this.theEntity.boundingBox.expand((double)this.distanceFromEntity, 3.0D, (double)this.distanceFromEntity), this.field_98218_a);

            if (list.isEmpty())
            {
                return false;
            }

            this.closestLivingEntity = (Entity)list.get(0);
        }

        Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 16, 7, Vec3.createVectorHelper(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

        if (vec3 == null)
        {
            return false;
        }
        else if (this.closestLivingEntity.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.theEntity))
        {
            return false;
        }
        else
        {
            this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(vec3);
        }
    }

}
