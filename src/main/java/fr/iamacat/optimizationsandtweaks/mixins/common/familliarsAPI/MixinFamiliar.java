package fr.iamacat.optimizationsandtweaks.mixins.common.familliarsAPI;

import de.pitman87.FamiliarsAPI.common.ConfigHandler;
import de.pitman87.FamiliarsAPI.common.FamiliarsAPI;
import de.pitman87.FamiliarsAPI.common.entity.Familiar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(Familiar.class)
public class MixinFamiliar extends EntityLiving implements IAnimals {
    @Unique
    private static final int MAX_AGE = 628;
    @Unique
    private Familiar familiar;
    @Shadow
    public EntityPlayer player;
    @Shadow
    public int maxDistanceToPlayer;
    @Shadow
    protected Entity entityToAttack;
    @Shadow
    protected boolean hasAttacked;
    @Shadow
    public int attackDelay;
    @Shadow
    public int attackRangeX;
    @Shadow
    public int attackRangeY;
    @Shadow
    public float posYOffset;
    @Shadow
    public boolean usesSaveFile;
    @Shadow
    public int age;

    public MixinFamiliar(World world, Familiar familiar) {
        super(world);
        this.familiar = familiar;
        this.age = 0;
        this.isImmuneToFire = true;
        this.setSize(0.1F, 0.1F);
        this.maxDistanceToPlayer = 40;
        this.attackRangeX = 15;
        this.attackRangeY = this.attackRangeX / 2;
        this.attackDelay = 60;
        this.posYOffset = 0.0F;
        this.usesSaveFile = false;
        this.renderDistanceWeight = 10.0;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void func_70636_d() {
        ++this.age;
        if (this.age >= MAX_AGE) {
            this.age = 0;
        }

        if (this.worldObj.isRemote) {
            this.multithreadingandtweaks$checkAndUpdatePlayerFamiliar();
        } else {
            this.updateHeadRotation();
        }

        if (this.player != null) {
            this.multithreadingandtweaks$handlePlayer();
        }
    }
    @Unique
    private void multithreadingandtweaks$checkAndUpdatePlayerFamiliar() {
        if (this.player == null && this.getOwner() != null) {
            this.player = this.getOwner();
            FamiliarsAPI.proxy.checkPlayersFamiliar(familiar, this.getOwnerName());
        }
    }

    @Unique
    private void multithreadingandtweaks$handlePlayer() {
        this.fallDistance = 0.0F;
        if (this.player.isDead && !this.worldObj.isRemote) {
            this.setDead();
        } else {
            if (this.attackTime > 0) {
                --this.attackTime;
            }

            this.followPlayer();
            this.func_70626_be();
            if (ConfigHandler.abilitiesEnabledGlobal && FamiliarsAPI.proxy.useAbility(this.getOwner())) {
                this.updateAbility();
            }
        }
    }

    @Shadow
    public void updateAbility() {
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void func_70612_e(float par1, float par2) {
        float var3 = 0.91F;
        float var8 = 0.16277136F / (var3 * var3 * var3);
        float var5;
        if (this.isAIEnabled()) {
            var5 = this.getAIMoveSpeed();
        } else {
            var5 = this.jumpMovementFactor;
        }

        var5 *= var8;
        this.moveFlying(par1, par2, var5);
        var3 = 0.91F;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)var3;
        this.motionZ *= (double)var3;
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double var9 = this.posX - this.prevPosX;
        double var12 = this.posZ - this.prevPosZ;
        float var11 = MathHelper.sqrt_double(var9 * var9 + var12 * var12) * 4.0F;
        if (var11 > 1.0F) {
            var11 = 1.0F;
        }

        this.limbSwingAmount += (var11 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void updateHeadRotation() {
        if (this.newPosRotationIncrements > 0) {
            double deltaRotation = this.newRotationYaw - (double)this.rotationYaw;
            deltaRotation = (deltaRotation + 180.0) % 360.0 - 180.0;
            this.rotationYaw = (float)((double)this.rotationYaw + deltaRotation / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        this.rotationYawHead = this.rotationYaw;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void spawn(EntityPlayer p) {
        this.player = p;
        this.worldObj = p.worldObj;
        this.setLocationAndAngles(this.player.posX, this.player.posY - 1.0, this.player.posZ, 0.0F, 0.0F);
        this.worldObj.spawnEntityInWorld(this);
        this.setOwner(this.player.getDisplayName());
        this.dimension = p.dimension;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void followPlayer() {
        if (this.getDistanceSqToEntity(this.player) > (double)this.maxDistanceToPlayer) {
            this.setPositionAndUpdate(this.player.posX + 0.5, this.player.posY - (double)this.posYOffset, this.player.posZ + 0.5);
        } else {
            float f2 = MathHelper.sin(this.age / 10.0F) * 0.3F + 0.1F;
            float f = this.player.renderYawOffset * 3.141593F / 180.0F - 0.15F;
            double f1 = MathHelper.cos(f);
            double f3 = MathHelper.sin(f);
            double d = this.player.posX;
            double d1 = this.player.posY - (double)this.posYOffset;
            double d2 = this.player.posZ;
            double d4 = (d - this.posX - f1) / 5.0;
            double d5 = d1 - this.posY + (double)this.player.getEyeHeight() + f2 / 5.0;
            double d6 = (d2 - this.posZ - f3) / 5.0;
            this.moveEntity(d4, d5, d6);
        }
    }
    @Shadow
    public void attackEntity(Entity entity, float f) {
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public Entity findEntityToAttack() {
        Entity target = null;
        List<Entity> entities = this.multithreadingandtweaks$getEntitiesWithinAttackRange();

        for (Entity entity : entities) {
            if (this.canSetAsTarget(entity) && (target == null || (this.canEntityBeSeen(entity) && this.getDistanceSqToEntity(entity) < this.getDistanceSqToEntity(target)))) {
                    target = entity;

            }
        }

        return target;
    }

    @Unique
    private List multithreadingandtweaks$getEntitiesWithinAttackRange() {
        return this.worldObj.getEntitiesWithinAABB(EntityLiving.class, this.multithreadingandtweaks$getBoundingBoxForAttackRange());
    }

    @Unique
    private AxisAlignedBB multithreadingandtweaks$getBoundingBoxForAttackRange() {
        return AxisAlignedBB.getBoundingBox(
            this.posX - (double)this.attackRangeX,
            this.posY - (double)this.attackRangeY,
            this.posZ - (double)this.attackRangeX,
            this.posX + (double)this.attackRangeX,
            this.posY + (double)this.attackRangeY,
            this.posZ + (double)this.attackRangeX
        );
    }

    @Shadow
    public boolean canSetAsTarget(Entity entity) {
        return entity instanceof EntityLiving && !(entity instanceof Familiar);
    }
    @Shadow
    public void func_70626_be() {
        super.updateEntityActionState();
        this.entityToAttack = this.findEntityToAttack();
        if (this.attackTime <= 0) {
            if (this.entityToAttack != null) {
                if (!this.entityToAttack.isEntityAlive()) {
                    this.entityToAttack = null;
                } else if (ConfigHandler.abilitiesEnabledGlobal && FamiliarsAPI.proxy.useAbility(this.getOwner())) {
                    float f1 = this.entityToAttack.getDistanceToEntity(this);
                    if (this.canEntityBeSeen(this.entityToAttack)) {
                        this.attackEntity(this.entityToAttack, f1);
                    } else {
                        this.attackBlockedEntity(this.entityToAttack, f1);
                    }
                }
            }

            if (this.entityToAttack != null) {
                this.faceEntity(this.entityToAttack, 30.0F, 30.0F);
            }

            this.attackTime = this.attackDelay;
            this.hasAttacked = true;
        }

    }
    @Shadow
    public void attackBlockedEntity(Entity entityToAttack2, float f1) {
    }
    @Shadow
    public String getOwnerName() {
        return this.dataWatcher.getWatchableObjectString(17);
    }
    @Shadow
    public void setOwner(String par1Str) {
        this.dataWatcher.updateObject(17, par1Str);
    }
    @Shadow
    public EntityPlayer getOwner() {
        return this.worldObj.getPlayerEntityByName(this.getOwnerName());
    }
}
