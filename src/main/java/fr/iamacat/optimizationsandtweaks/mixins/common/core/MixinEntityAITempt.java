package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityAITempt.class)
public class MixinEntityAITempt {
    @Shadow
    private EntityCreature temptedEntity;
    @Shadow
    private double field_75282_b;
    @Shadow
    private double targetX;
    @Shadow
    private double targetY;
    @Shadow
    private double targetZ;
    @Shadow
    private double field_75278_f;
    @Shadow
    private double field_75279_g;
    @Shadow
    private EntityPlayer temptingPlayer;
    @Shadow
    private int delayTemptCounter;
    @Shadow
    private boolean isRunning;
    @Shadow
    private Item field_151484_k;
    @Shadow
    private boolean scaredByPlayerMovement;
    @Shadow
    private boolean field_75286_m;

    @Unique
    private static int activeFollowers = 0;
    public MixinEntityAITempt(EntityCreature p_i45316_1_, double p_i45316_2_, Item p_i45316_4_, boolean p_i45316_5_)
    {
        this.temptedEntity = p_i45316_1_;
        this.field_75282_b = p_i45316_2_;
        this.field_151484_k = p_i45316_4_;
        this.scaredByPlayerMovement = p_i45316_5_;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Overwrite
    public boolean shouldExecute() {
        // max 30 entities can follow the player at the same time
        if (activeFollowers >= 30 || this.delayTemptCounter > 0) {
            return false;
        } else {
            this.temptingPlayer = this.temptedEntity.worldObj.getClosestPlayerToEntity(this.temptedEntity, 10.0D);

            if (this.temptingPlayer == null || this.temptedEntity == null) {
                return false;
            } else {
                ItemStack itemstack = this.temptingPlayer.getCurrentEquippedItem();
                return itemstack != null && itemstack.getItem() == this.field_151484_k;
            }
        }
    }
    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Overwrite
    public boolean continueExecuting()
    {
        if (this.scaredByPlayerMovement)
        {
            if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 36.0D)
            {
                if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D)
                {
                    return false;
                }

                if (Math.abs((double)this.temptingPlayer.rotationPitch - this.field_75278_f) > 5.0D || Math.abs((double)this.temptingPlayer.rotationYaw - this.field_75279_g) > 5.0D)
                {
                    return false;
                }
            }
            else
            {
                this.targetX = this.temptingPlayer.posX;
                this.targetY = this.temptingPlayer.posY;
                this.targetZ = this.temptingPlayer.posZ;
            }

            this.field_75278_f = this.temptingPlayer.rotationPitch;
            this.field_75279_g = this.temptingPlayer.rotationYaw;
        }

        return this.shouldExecute();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Overwrite
    public void startExecuting() {
        if (this.temptedEntity.canEntityBeSeen(this.temptingPlayer)) {
            this.targetX = this.temptingPlayer.posX;
            this.targetY = this.temptingPlayer.posY;
            this.targetZ = this.temptingPlayer.posZ;
            this.isRunning = true;
            this.field_75286_m = this.temptedEntity.getNavigator().getAvoidsWater();
            this.temptedEntity.getNavigator().setAvoidsWater(false);
            activeFollowers++;
        }
    }


    /**
     * Resets the task
     */
    @Overwrite
    public void resetTask()
    {
        this.temptingPlayer = null;
        this.temptedEntity.getNavigator().clearPathEntity();
        this.delayTemptCounter = 100;
        this.isRunning = false;
        this.temptedEntity.getNavigator().setAvoidsWater(this.field_75286_m);
        activeFollowers--;
    }

    /**
     * Updates the task
     */
    @Overwrite
    public void updateTask() {
        if (activeFollowers >= 30) {
            return;
        }

        EntityLookHelper lookHelper = this.temptedEntity.getLookHelper();
        lookHelper.setLookPositionWithEntity(this.temptingPlayer, 30.0F, (float) this.temptedEntity.getVerticalFaceSpeed());

        double distanceSq = this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer);
        EntityCreature entity = this.temptedEntity;
        EntityPlayer player = this.temptingPlayer;
        double speed = this.field_75282_b;

        if (distanceSq < 6.25D) {
            entity.getNavigator().clearPathEntity();
        } else {
            if (activeFollowers <= 30) {
                entity.getNavigator().tryMoveToEntityLiving(player, speed);
            }
        }
    }
}
