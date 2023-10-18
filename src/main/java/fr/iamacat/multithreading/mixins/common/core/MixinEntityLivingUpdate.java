package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;

import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityLivingBase.class, priority = 1100)
public abstract class MixinEntityLivingUpdate extends Entity {

    @Unique
    private EntityLivingBase entityLivingBase;
    @Shadow
    private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    @Shadow
    private static final AttributeModifier sprintingSpeedBoostModifier = (new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896D, 2)).setSaved(false);
    @Shadow
    private BaseAttributeMap attributeMap;
    @Shadow
    private final HashMap activePotionsMap = new HashMap();
    /** The equipment this mob was previously wearing, used for syncing. */
    @Shadow
    private final ItemStack[] previousEquipment = new ItemStack[5];
    /** Whether an arm swing is currently in progress. */
    @Shadow
    public boolean isSwingInProgress;
    @Shadow
    public int swingProgressInt;
    @Shadow
    public int arrowHitTimer;
    @Shadow
    public float prevHealth;
    /** The amount of time remaining this entity should act 'hurt'. (Visual appearance of red tint) */
    @Shadow
    public int hurtTime;
    /** What the hurt time was max set to last. */
    @Shadow
    public int maxHurtTime;
    /** The yaw at which this entity was last attacked from. */
    @Shadow
    public float attackedAtYaw;
    /** The amount of time remaining this entity should act 'dead', i.e. have a corpse in the world. */
    @Shadow
    public int deathTime;
    @Shadow
    public int attackTime;
    @Shadow
    public float prevSwingProgress;
    @Shadow
    public float swingProgress;
    @Shadow
    public float prevLimbSwingAmount;
    @Shadow
    public float limbSwingAmount;
    /**
     * Only relevant when limbYaw is not 0(the entity is moving). Influences where in its swing legs and arms currently
     * are.
     */
    @Shadow
    public float limbSwing;
    @Shadow
    public int maxHurtResistantTime = 20;
    @Shadow
    public float prevCameraPitch;
    @Shadow
    public float cameraPitch;
    @Shadow
    public float field_70769_ao;
    @Shadow
    public float field_70770_ap;
    @Shadow
    public float renderYawOffset;
    @Shadow
    public float prevRenderYawOffset;
    /** Entity head rotation yaw */
    @Shadow
    public float rotationYawHead;
    /** Entity head rotation yaw at previous tick */
    @Shadow
    public float prevRotationYawHead;
    /** A factor used to determine how far this entity will move each tick if it is jumping or falling. */
    @Shadow
    public float jumpMovementFactor = 0.02F;
    /** The most recent player that has attacked this entity */
    @Shadow
    protected EntityPlayer attackingPlayer;
    /**
     * Set to 60 when hit by the player or the player's wolf, then decrements. Used to determine whether the entity
     * should drop items on death.
     */
    @Shadow
    protected int recentlyHit;
    /** This gets set on entity death, but never used. Looks like a duplicate of isDead */
    @Shadow
    protected boolean dead;
    /** The age of this EntityLiving (used to determine when it dies) */
    @Shadow
    protected int entityAge;
    @Shadow
    protected float field_70768_au;
    @Shadow
    protected float field_110154_aX;
    @Shadow
    protected float field_70764_aw;
    @Shadow
    protected float field_70763_ax;
    @Shadow
    protected float field_70741_aB;
    /** The score value of the Mob, the amount of points the mob is worth. */
    @Shadow
    protected int scoreValue;
    /** Damage taken in the last hit. Mobs are resistant to damage less than this for a short time after taking damage. */
    @Shadow
    protected float lastDamage;
    /** used to check whether entity is jumping. */
    @Shadow
    protected boolean isJumping;
    @Shadow
    public float moveStrafing;
    @Shadow
    public float moveForward;
    @Shadow
    protected float randomYawVelocity;
    /** The number of updates over which the new position and rotation are to be applied to the entity. */
    @Shadow
    protected int newPosRotationIncrements;
    /** The new X position to be applied to the entity. */
    @Shadow
    protected double newPosX;
    /** The new Y position to be applied to the entity. */
    @Shadow
    protected double newPosY;
    @Shadow
    protected double newPosZ;
    /** The new yaw rotation to be applied to the entity. */
    @Shadow
    protected double newRotationYaw;
    /** The new yaw rotation to be applied to the entity. */
    @Shadow
    protected double newRotationPitch;
    /** Whether the DataWatcher needs to be updated with the active potions */
    @Shadow
    private boolean potionsNeedUpdate = true;
    /** is only being set, has no uses as of MC 1.1 */
    @Shadow
    private EntityLivingBase entityLivingToAttack;
    @Shadow
    private int revengeTimer;
    @Shadow
    private EntityLivingBase lastAttacker;
    /** Holds the value of ticksExisted when setLastAttacker was last called. */
    @Shadow
    private int lastAttackerTime;
    /**
     * A factor used to determine how far this entity will move each tick if it is walking on land. Adjusted by speed,
     * and slipperiness of the current block.
     */
    @Shadow
    private float landMovementFactor;
    /** Number of ticks since last jump */
    @Shadow
    private int jumpTicks;
    @Shadow
    private float field_110151_bq;
    @Unique
    private EntityLivingBase entityObject;

    @Unique
    private final int batchSize = MultithreadingandtweaksConfig.batchsize;
    @Unique
    private final List<MixinEntityLivingUpdate> batchedEntities = new ArrayList<>();
    @Unique
    private final List<CompletableFuture<Void>> updateFutures = new ArrayList<>();

    @Unique
    private double strafe;
    @Unique
    private double forward;
    @Unique
    private float friction;

    @Unique
    private final ThreadPoolExecutor multithreadingandtweaks$executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksConfig.numberofcpus,
        MultithreadingandtweaksConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        r -> {
            Runnable wrappedRunnable = () -> {
                try {
                    r.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            return new Thread(wrappedRunnable, "Entity-Living-Update-%d" + MixinEntityLivingUpdate.this.hashCode());
        });

    public MixinEntityLivingUpdate(World worldIn) {
        super(worldIn);
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "onLivingUpdate", at = @At("HEAD"), remap = false, cancellable = true)
    public void modifiedonLivingUpdate(CallbackInfo ci)
    {
        if(MultithreadingandtweaksConfig.enableMixinEntityLivingUpdate){
        if (this.jumpTicks > 0)
        {
            --this.jumpTicks;
        }

        if (this.newPosRotationIncrements > 0)
        {
            double d0 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double d1 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double d2 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else if (!this.isClientWorld())
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D)
        {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.005D)
        {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.005D)
        {
            this.motionZ = 0.0D;
        }

        this.worldObj.theProfiler.startSection("ai");

        if (this.isMovementBlocked())
        {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        }
        else if (this.isClientWorld())
        {
            if (this.isAIEnabled())
            {
                this.worldObj.theProfiler.startSection("newAi");
                multithreadingandtweaks$executorService.submit(this::updateAITasks);
                this.worldObj.theProfiler.endSection();
            }
            else
            {
                this.worldObj.theProfiler.startSection("oldAi");
                this.updateEntityActionState();
                this.worldObj.theProfiler.endSection();
                this.rotationYawHead = this.rotationYaw;
            }
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");

        if (this.isJumping)
        {
            if (!this.isInWater() && !this.handleLavaMovement())
            {
                if (this.onGround && this.jumpTicks == 0)
                {
                    multithreadingandtweaks$executorService.submit(this::jump);
                    this.jumpTicks = 10;
                }
            }
            else
            {
                this.motionY += 0.03999999910593033D;
            }
        }
        else
        {
            this.jumpTicks = 0;
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;
        multithreadingandtweaks$executorService.submit(() -> this.modifiedmoveEntityWithHeading(this.moveStrafing, this.moveForward,ci));
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");

        if (!this.worldObj.isRemote)
        {
            this.collideWithNearbyEntities();
        }

        this.worldObj.theProfiler.endSection();}
        ci.cancel();
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground. Args: distanceFallenThisTick, onGround
     */
    @Inject(method = "updateFallState", at = @At("HEAD"), remap = false, cancellable = true)
    protected void modifiedupdateFallState(double distanceFallenThisTick, boolean isOnGround,CallbackInfo ci) {
        if(MultithreadingandtweaksConfig.enableMixinEntityLivingUpdate){
        if (!this.isInWater()) {
            this.handleWaterMovement();
        }

        if (isOnGround && this.fallDistance > 0.0F) {
            double posX = this.posX;
            double posY = this.posY - 0.20000000298023224D - (double) this.yOffset;
            double posZ = this.posZ;
            int i = MathHelper.floor_double(posX);
            int j = MathHelper.floor_double(posY);
            int k = MathHelper.floor_double(posZ);
            Block block = this.worldObj.getBlock(i, j, k);

            if (block.getMaterial() == Material.air) {
                int l = this.worldObj.getBlock(i, j - 1, k)
                    .getRenderType();

                if (l == 11 || l == 32 || l == 21) {
                    block = this.worldObj.getBlock(i, j - 1, k);
                }
            } else if (!this.worldObj.isRemote && this.fallDistance > 3.0F) {
                this.worldObj.playAuxSFX(2006, i, j, k, MathHelper.ceiling_float_int(this.fallDistance - 3.0F));
            }

            block.onFallenUpon(this.worldObj, i, j, k, this, this.fallDistance);
        }

        super.updateFallState(distanceFallenThisTick, isOnGround);
    }
        ci.cancel();
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    @Unique
    protected void jump()
    {
        this.motionY = 0.41999998688697815D;

        if (this.isPotionActive(Potion.jump))
        {
            this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting())
        {
            float f = this.rotationYaw * 0.017453292F;
            this.motionX -= (double)(MathHelper.sin(f) * 0.2F);
            this.motionZ += (double)(MathHelper.cos(f) * 0.2F);
        }

        this.isAirBorne = true;
        ForgeHooks.onLivingJump(entityLivingBase);
    }

    @Unique
    protected void collideWithNearbyEntities()
    {
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

        if (list != null && !list.isEmpty())
        {
            for (int i = 0; i < list.size(); ++i)
            {
                Entity entity = (Entity)list.get(i);

                if (entity.canBePushed())
                {
                    this.collideWithEntity(entity);
                }
            }
        }
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    @Inject(method = "moveEntityWithHeading", at = @At("HEAD"), remap = false, cancellable = true)
    public void modifiedmoveEntityWithHeading(float p_70612_1_, float p_70612_2_,CallbackInfo ci)
    {
        if(MultithreadingandtweaksConfig.enableMixinEntityLivingUpdate){
        double d0;

        if (this.isInWater() && (!(entityLivingBase instanceof EntityPlayer) || !((EntityPlayer)entityLivingBase).capabilities.isFlying))
        {
            d0 = this.posY;
            this.moveFlying(p_70612_1_, p_70612_2_, this.isAIEnabled() ? 0.04F : 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
            this.motionY -= 0.02D;

            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ))
            {
                this.motionY = 0.30000001192092896D;
            }
        }
        else if (this.handleLavaMovement() && (!(entityLivingBase instanceof EntityPlayer) || !((EntityPlayer)entityLivingBase).capabilities.isFlying))
        {
            d0 = this.posY;
            this.moveFlying(p_70612_1_, p_70612_2_, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
            this.motionY -= 0.02D;

            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ))
            {
                this.motionY = 0.30000001192092896D;
            }
        }
        else
        {
            float f2 = 0.91F;

            if (this.onGround)
            {
                f2 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
            }

            float f3 = 0.16277136F / (f2 * f2 * f2);
            float f4;

            if (this.onGround)
            {
                f4 = this.getAIMoveSpeed() * f3;
            }
            else
            {
                f4 = this.jumpMovementFactor;
            }

            this.moveFlying(p_70612_1_, p_70612_2_, f4);
            f2 = 0.91F;

            if (this.onGround)
            {
                f2 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
            }

            if (this.isOnLadder())
            {
                float f5 = 0.15F;

                if (this.motionX < (double)(-f5))
                {
                    this.motionX = (double)(-f5);
                }

                if (this.motionX > (double)f5)
                {
                    this.motionX = (double)f5;
                }

                if (this.motionZ < (double)(-f5))
                {
                    this.motionZ = (double)(-f5);
                }

                if (this.motionZ > (double)f5)
                {
                    this.motionZ = (double)f5;
                }

                this.fallDistance = 0.0F;

                if (this.motionY < -0.15D)
                {
                    this.motionY = -0.15D;
                }

                boolean flag = this.isSneaking() && entityLivingBase instanceof EntityPlayer;

                if (flag && this.motionY < 0.0D)
                {
                    this.motionY = 0.0D;
                }
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.isCollidedHorizontally && this.isOnLadder())
            {
                this.motionY = 0.2D;
            }

            if (this.worldObj.isRemote && (!this.worldObj.blockExists((int)this.posX, 0, (int)this.posZ) || !this.worldObj.getChunkFromBlockCoords((int)this.posX, (int)this.posZ).isChunkLoaded))
            {
                if (this.posY > 0.0D)
                {
                    this.motionY = -0.1D;
                }
                else
                {
                    this.motionY = 0.0D;
                }
            }
            else
            {
                this.motionY -= 0.08D;
            }

            this.motionY *= 0.9800000190734863D;
            this.motionX *= f2;
            this.motionZ *= (double)f2;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f6 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

        if (f6 > 1.0F)
        {
            f6 = 1.0F;
        }

        this.limbSwingAmount += (f6 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }
        ci.cancel();
    }
    @Unique
    public boolean isPotionActive(Potion p_70644_1_)
    {
        return this.activePotionsMap.containsKey(p_70644_1_.id);
    }

    /**
     * returns the PotionEffect for the supplied Potion if it is active, null otherwise.
     */
    @Unique
    public PotionEffect getActivePotionEffect(Potion p_70660_1_)
    {
        return (PotionEffect)this.activePotionsMap.get(Integer.valueOf(p_70660_1_.id));
    }
    /**
     * Dead and sleeping entities cannot move
     */
    @Unique
    protected boolean isMovementBlocked()
    {
        return this.getHealth() <= 0.0F;
    }

    @Unique
    public final float getHealth()
    {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }
    @Unique
    protected void collideWithEntity(Entity p_82167_1_)
    {
        p_82167_1_.applyEntityCollision(this);
    }
    /**
     * Returns whether the entity is in a local (client) world
     */
    @Unique
    public boolean isClientWorld()
    {
        return !this.worldObj.isRemote;
    }

    @Unique
    protected void updateAITasks() {}
    @Unique
    protected void updateEntityActionState()
    {
        ++this.entityAge;
    }
    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Unique
    protected boolean isAIEnabled()
    {
        return false;
    }
    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    @Unique
    public boolean isOnLadder()
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        Block block = this.worldObj.getBlock(i, j, k);
        return ForgeHooks.isLivingOnLadder(block, worldObj, i, j, k, entityLivingBase);
    }
    /**
     * the movespeed used for the new AI system
     */
    @Unique
    public float getAIMoveSpeed()
    {
        return this.isAIEnabled() ? this.landMovementFactor : 0.1F;
    }

}
