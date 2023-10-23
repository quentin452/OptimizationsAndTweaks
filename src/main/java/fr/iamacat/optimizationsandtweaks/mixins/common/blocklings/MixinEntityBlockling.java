package fr.iamacat.optimizationsandtweaks.mixins.common.blocklings;

import java.util.Random;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blocklings.entity.EntityBlockling;
import com.blocklings.gui.InventoryBlockling;
import com.blocklings.main.Blocklings;
import com.blocklings.network.CreatePacketServerSide;

import fr.iamacat.optimizationsandtweaks.config.MultithreadingandtweaksConfig;

@Mixin(EntityBlockling.class)
public abstract class MixinEntityBlockling extends EntityTameable implements IInvBasic, IRangedAttackMob {

    @Unique
    private EntityBlockling entityBlockling;
    @Shadow
    Random random = new Random();
    @Shadow
    public int currentXP = 0;
    @Shadow
    public int currentLevel = 1;
    @Shadow
    public int currentUpgradeTier = 1;
    @Shadow
    public int currentSpecialTier = 1;
    @Shadow
    public int requiredXP = 200;
    @Shadow
    public double maxHealth = 2.0;
    @Shadow
    public double attackDamage = 1.0;
    @Shadow
    public double moveSpeed = 0.2;
    @Shadow
    public float moveTimer = 0.0F;
    @Shadow
    public boolean moveTimerSwitch = true;
    @Shadow
    public float attackTimer = 0.0F;
    @Shadow
    public boolean attackTimerSwitch = true;
    @Shadow
    public boolean isAttacking = false;
    @Shadow
    public int swordID = 0;
    @Shadow
    public int regenTimer = 0;
    @Shadow
    public int image = 0;
    @Shadow
    public float field_70130_N = 0.5F;
    @Shadow
    public float field_70131_O = 0.55F;
    @Shadow
    public InventoryBlockling gear;
    @Shadow
    private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.25, 20, 10.0F);
    @Shadow
    private EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, 1.0, false);
    @Shadow
    private EntityAIPanic aiPanic = new EntityAIPanic(this, 1.0);

    public MixinEntityBlockling(World p_i1604_1_, EntityBlockling entityBlockling) {
        super(p_i1604_1_);
        this.entityBlockling = entityBlockling;
    }

    @Shadow
    public void setXP(int currentXP) {
        this.currentXP = currentXP;
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "func_70636_d", at = @At("HEAD"), remap = false, cancellable = true)
    public void func_70636_d(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntityBlockling) {
            super.onLivingUpdate();

            World world = this.worldObj;
            boolean isRemote = world.isRemote;

            if (attackTimer > 0.0F) {
                attackTimer--;
            }

            if (attackTimer == 7.0F && !isRemote) {
                setXP((int) (attackDamage + random.nextInt((int) (attackDamage * Blocklings.xp)) + currentXP));
            }

            if (currentSpecialTier >= 8 && !isRemote) {
                regenTimer++;
            }

            if (regenTimer == 150 && !isRemote) {
                heal(1.0F);
                regenTimer = 0;
            }

            if (isDead && !isRemote && isTamed()) {
                image = 0;
                Blocklings.itemBlockling.setBlockling(entityBlockling);
                world.spawnEntityInWorld(
                    new EntityItem(world, posX, posY, posZ, new ItemStack(Blocklings.itemBlockling)));
            }

            isImmuneToFire = currentUpgradeTier >= 10;

            setEntitySize();
            setBlocklingLevel();
            calculateRequiredXP();
            checkUpgrades();
            checkSpecials();
            setMaxHealth();
            setAttackDamage();
            setMoveSpeed();

            if (!isRemote) {
                CreatePacketServerSide.sendS2CEntitySync(this);
            }
            ci.cancel();
        }
    }

    @Shadow
    public void setEntitySize() {
        if (this.currentLevel <= 10) {
            this.field_70130_N = (float) this.currentLevel * 0.14F + 0.35F;
            this.field_70131_O = (float) this.currentLevel * 0.14F + 0.4F;
        } else if (this.currentLevel > 10) {
            this.field_70130_N = 1.75F;
            this.field_70131_O = 1.8F;
        }

        this.setSize(this.field_70130_N, this.field_70131_O);
    }

    @Shadow
    public void setCombatTask() {
        this.tasks.removeTask(this.aiAttackOnCollide);
        this.tasks.removeTask(this.aiArrowAttack);
        if (this.swordID == 6 && this.currentLevel >= 10) {
            this.tasks.addTask(3, this.aiArrowAttack);
        } else {
            this.tasks.addTask(3, this.aiAttackOnCollide);
        }

    }

    @Shadow
    public void calculateRequiredXP() {
        this.requiredXP = 50 * this.currentLevel * this.currentLevel + 150 * this.currentLevel;
    }

    @Shadow
    public void setBlocklingLevel() {
        if (this.currentXP >= this.requiredXP) {
            ++this.currentLevel;
            this.calculateRequiredXP();
            this.worldObj.playSoundAtEntity(
                this,
                "random.levelup",
                0.5F,
                0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
            this.currentXP = 0;
        }

    }

    @Unique
    private void checkUpgrades() {
        ItemStack itemstack1 = this.gear.getStackInSlot(0);
        ItemStack itemstack2 = this.gear.getStackInSlot(1);
        ItemStack itemstack3 = this.gear.getStackInSlot(2);
        ItemStack itemstack4 = this.gear.getStackInSlot(3);
        ItemStack itemstack5 = this.gear.getStackInSlot(4);
        ItemStack itemstack6 = this.gear.getStackInSlot(5);
        if (itemstack3 != null && itemstack3.getItem() == Blocklings.itemUpgradeWood
            && itemstack5 == null
            && this.currentLevel >= 2
            && !this.worldObj.isRemote) {
            this.currentUpgradeTier = 2;
            this.worldObj.playSoundAtEntity(
                this,
                "random.anvil_use",
                0.5F,
                0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
            this.gear.setInventorySlotContents(4, new ItemStack(itemstack3.getItem()));
            this.gear.setInventorySlotContents(2, (ItemStack) null);
        } else if (itemstack3 != null && itemstack3.getItem() == Blocklings.itemUpgradeCobblestone
            && itemstack5 != null
            && itemstack5.getItem() == Blocklings.itemUpgradeWood
            && this.currentLevel >= 3
            && !this.worldObj.isRemote) {
                this.currentUpgradeTier = 3;
                this.worldObj.playSoundAtEntity(
                    this,
                    "random.anvil_use",
                    0.5F,
                    0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                this.gear.setInventorySlotContents(4, new ItemStack(itemstack3.getItem()));
                this.gear.setInventorySlotContents(2, (ItemStack) null);
            } else if (itemstack3 != null && itemstack3.getItem() == Blocklings.itemUpgradeStone
                && itemstack5 != null
                && itemstack5.getItem() == Blocklings.itemUpgradeCobblestone
                && this.currentLevel >= 4
                && !this.worldObj.isRemote) {
                    this.currentUpgradeTier = 4;
                    this.worldObj.playSoundAtEntity(
                        this,
                        "random.anvil_use",
                        0.5F,
                        0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                    this.gear.setInventorySlotContents(4, new ItemStack(itemstack3.getItem()));
                    this.gear.setInventorySlotContents(2, (ItemStack) null);
                } else if (itemstack3 != null && itemstack3.getItem() == Blocklings.itemUpgradeIron
                    && itemstack5 != null
                    && itemstack5.getItem() == Blocklings.itemUpgradeStone
                    && this.currentLevel >= 5
                    && !this.worldObj.isRemote) {
                        this.currentUpgradeTier = 5;
                        this.worldObj.playSoundAtEntity(
                            this,
                            "random.anvil_use",
                            0.5F,
                            0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                        this.gear.setInventorySlotContents(4, new ItemStack(itemstack3.getItem()));
                        this.gear.setInventorySlotContents(2, (ItemStack) null);
                    } else if (itemstack3 != null && itemstack3.getItem() == Blocklings.itemUpgradeLapis
                        && itemstack5 != null
                        && itemstack5.getItem() == Blocklings.itemUpgradeIron
                        && this.currentLevel >= 6
                        && !this.worldObj.isRemote) {
                            this.currentUpgradeTier = 6;
                            this.worldObj.playSoundAtEntity(
                                this,
                                "random.anvil_use",
                                0.5F,
                                0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                            this.gear.setInventorySlotContents(4, new ItemStack(itemstack3.getItem()));
                            this.gear.setInventorySlotContents(2, (ItemStack) null);
                        } else if (itemstack3 != null && itemstack3.getItem() == Blocklings.itemUpgradeGold
                            && itemstack5 != null
                            && itemstack5.getItem() == Blocklings.itemUpgradeLapis
                            && this.currentLevel >= 7
                            && !this.worldObj.isRemote) {
                                this.currentUpgradeTier = 7;
                                this.worldObj.playSoundAtEntity(
                                    this,
                                    "random.anvil_use",
                                    0.5F,
                                    0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                                this.gear.setInventorySlotContents(4, new ItemStack(itemstack3.getItem()));
                                this.gear.setInventorySlotContents(2, (ItemStack) null);
                            } else if (itemstack3 != null && itemstack3.getItem() == Blocklings.itemUpgradeDiamond
                                && itemstack5 != null
                                && itemstack5.getItem() == Blocklings.itemUpgradeGold
                                && this.currentLevel >= 8
                                && !this.worldObj.isRemote) {
                                    this.currentUpgradeTier = 8;
                                    this.worldObj.playSoundAtEntity(
                                        this,
                                        "random.anvil_use",
                                        0.5F,
                                        0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                                    this.gear.setInventorySlotContents(4, new ItemStack(itemstack3.getItem()));
                                    this.gear.setInventorySlotContents(2, (ItemStack) null);
                                } else if (itemstack3 != null && itemstack3.getItem() == Blocklings.itemUpgradeEmerald
                                    && itemstack5 != null
                                    && itemstack5.getItem() == Blocklings.itemUpgradeDiamond
                                    && this.currentLevel >= 9
                                    && !this.worldObj.isRemote) {
                                        this.currentUpgradeTier = 9;
                                        this.worldObj.playSoundAtEntity(
                                            this,
                                            "random.anvil_use",
                                            0.5F,
                                            0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                                        this.gear.setInventorySlotContents(4, new ItemStack(itemstack3.getItem()));
                                        this.gear.setInventorySlotContents(2, (ItemStack) null);
                                    } else
                                    if (itemstack3 != null && itemstack3.getItem() == Blocklings.itemUpgradeObsidian
                                        && itemstack5 != null
                                        && itemstack5.getItem() == Blocklings.itemUpgradeEmerald
                                        && this.currentLevel >= 10
                                        && !this.worldObj.isRemote) {
                                            this.currentUpgradeTier = 10;
                                            this.worldObj.playSoundAtEntity(
                                                this,
                                                "random.anvil_use",
                                                0.5F,
                                                0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                                            this.gear.setInventorySlotContents(4, new ItemStack(itemstack3.getItem()));
                                            this.gear.setInventorySlotContents(2, (ItemStack) null);
                                        } else if (itemstack5 == null && this.currentUpgradeTier <= 2
                                            && !this.worldObj.isRemote) {
                                                this.currentUpgradeTier = 1;
                                            } else if (itemstack5 == null && this.currentUpgradeTier == 3
                                                && !this.worldObj.isRemote) {
                                                    this.gear.setInventorySlotContents(
                                                        4,
                                                        new ItemStack(Blocklings.itemUpgradeWood));
                                                    this.currentUpgradeTier = 2;
                                                } else if (itemstack5 == null && this.currentUpgradeTier == 4
                                                    && !this.worldObj.isRemote) {
                                                        this.gear.setInventorySlotContents(
                                                            4,
                                                            new ItemStack(Blocklings.itemUpgradeCobblestone));
                                                        this.currentUpgradeTier = 3;
                                                    } else if (itemstack5 == null && this.currentUpgradeTier == 5
                                                        && !this.worldObj.isRemote) {
                                                            this.gear.setInventorySlotContents(
                                                                4,
                                                                new ItemStack(Blocklings.itemUpgradeStone));
                                                            this.currentUpgradeTier = 4;
                                                        } else if (itemstack5 == null && this.currentUpgradeTier == 6
                                                            && !this.worldObj.isRemote) {
                                                                this.gear.setInventorySlotContents(
                                                                    4,
                                                                    new ItemStack(Blocklings.itemUpgradeIron));
                                                                this.currentUpgradeTier = 5;
                                                            } else
                                                            if (itemstack5 == null && this.currentUpgradeTier == 7
                                                                && !this.worldObj.isRemote) {
                                                                    this.gear.setInventorySlotContents(
                                                                        4,
                                                                        new ItemStack(Blocklings.itemUpgradeLapis));
                                                                    this.currentUpgradeTier = 6;
                                                                } else
                                                                if (itemstack5 == null && this.currentUpgradeTier == 8
                                                                    && !this.worldObj.isRemote) {
                                                                        this.gear.setInventorySlotContents(
                                                                            4,
                                                                            new ItemStack(Blocklings.itemUpgradeGold));
                                                                        this.currentUpgradeTier = 7;
                                                                    } else if (itemstack5 == null
                                                                        && this.currentUpgradeTier == 9
                                                                        && !this.worldObj.isRemote) {
                                                                            this.gear.setInventorySlotContents(
                                                                                4,
                                                                                new ItemStack(
                                                                                    Blocklings.itemUpgradeDiamond));
                                                                            this.currentUpgradeTier = 8;
                                                                        } else if (itemstack5 == null
                                                                            && this.currentUpgradeTier == 10
                                                                            && !this.worldObj.isRemote) {
                                                                                this.gear.setInventorySlotContents(
                                                                                    4,
                                                                                    new ItemStack(
                                                                                        Blocklings.itemUpgradeEmerald));
                                                                                this.currentUpgradeTier = 9;
                                                                            }

    }

    @Unique
    private void checkSpecials() {
        ItemStack itemstack1 = this.gear.getStackInSlot(0);
        ItemStack itemstack2 = this.gear.getStackInSlot(1);
        ItemStack itemstack3 = this.gear.getStackInSlot(2);
        ItemStack itemstack4 = this.gear.getStackInSlot(3);
        ItemStack itemstack5 = this.gear.getStackInSlot(4);
        ItemStack itemstack6 = this.gear.getStackInSlot(5);
        if (itemstack4 != null && itemstack4.getItem() == Blocklings.itemUpgradeHealth1
            && itemstack6 == null
            && !this.worldObj.isRemote) {
            this.currentSpecialTier = 2;
            this.worldObj.playSoundAtEntity(
                this,
                "random.anvil_use",
                0.5F,
                0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
            this.gear.setInventorySlotContents(5, new ItemStack(itemstack4.getItem()));
            this.gear.setInventorySlotContents(3, (ItemStack) null);
        } else if (itemstack4 != null && itemstack4.getItem() == Blocklings.itemUpgradeDamage1
            && itemstack6 != null
            && itemstack6.getItem() == Blocklings.itemUpgradeHealth1
            && !this.worldObj.isRemote) {
                this.currentSpecialTier = 3;
                this.worldObj.playSoundAtEntity(
                    this,
                    "random.anvil_use",
                    0.5F,
                    0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                this.gear.setInventorySlotContents(5, new ItemStack(itemstack4.getItem()));
                this.gear.setInventorySlotContents(3, (ItemStack) null);
            } else if (itemstack4 != null && itemstack4.getItem() == Blocklings.itemUpgradeSpeed1
                && itemstack6 != null
                && itemstack6.getItem() == Blocklings.itemUpgradeDamage1
                && !this.worldObj.isRemote) {
                    this.currentSpecialTier = 4;
                    this.worldObj.playSoundAtEntity(
                        this,
                        "random.anvil_use",
                        0.5F,
                        0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                    this.gear.setInventorySlotContents(5, new ItemStack(itemstack4.getItem()));
                    this.gear.setInventorySlotContents(3, (ItemStack) null);
                } else if (itemstack4 != null && itemstack4.getItem() == Blocklings.itemUpgradeHealth2
                    && itemstack6 != null
                    && itemstack6.getItem() == Blocklings.itemUpgradeSpeed1
                    && !this.worldObj.isRemote) {
                        this.currentSpecialTier = 5;
                        this.worldObj.playSoundAtEntity(
                            this,
                            "random.anvil_use",
                            0.5F,
                            0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                        this.gear.setInventorySlotContents(5, new ItemStack(itemstack4.getItem()));
                        this.gear.setInventorySlotContents(3, (ItemStack) null);
                    } else if (itemstack4 != null && itemstack4.getItem() == Blocklings.itemUpgradeDamage2
                        && itemstack6 != null
                        && itemstack6.getItem() == Blocklings.itemUpgradeHealth2
                        && !this.worldObj.isRemote) {
                            this.currentSpecialTier = 6;
                            this.worldObj.playSoundAtEntity(
                                this,
                                "random.anvil_use",
                                0.5F,
                                0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                            this.gear.setInventorySlotContents(5, new ItemStack(itemstack4.getItem()));
                            this.gear.setInventorySlotContents(3, (ItemStack) null);
                        } else if (itemstack4 != null && itemstack4.getItem() == Blocklings.itemUpgradeSpeed2
                            && itemstack6 != null
                            && itemstack6.getItem() == Blocklings.itemUpgradeDamage2
                            && !this.worldObj.isRemote) {
                                this.currentSpecialTier = 7;
                                this.worldObj.playSoundAtEntity(
                                    this,
                                    "random.anvil_use",
                                    0.5F,
                                    0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                                this.gear.setInventorySlotContents(5, new ItemStack(itemstack4.getItem()));
                                this.gear.setInventorySlotContents(3, (ItemStack) null);
                            } else if (itemstack4 != null && itemstack4.getItem() == Blocklings.itemUpgradeHealth3
                                && itemstack6 != null
                                && itemstack6.getItem() == Blocklings.itemUpgradeSpeed2
                                && !this.worldObj.isRemote) {
                                    this.currentSpecialTier = 8;
                                    this.worldObj.playSoundAtEntity(
                                        this,
                                        "random.anvil_use",
                                        0.5F,
                                        0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                                    this.gear.setInventorySlotContents(5, new ItemStack(itemstack4.getItem()));
                                    this.gear.setInventorySlotContents(3, (ItemStack) null);
                                } else if (itemstack4 != null && itemstack4.getItem() == Blocklings.itemUpgradeDamage3
                                    && itemstack6 != null
                                    && itemstack6.getItem() == Blocklings.itemUpgradeHealth3
                                    && !this.worldObj.isRemote) {
                                        this.currentSpecialTier = 9;
                                        this.worldObj.playSoundAtEntity(
                                            this,
                                            "random.anvil_use",
                                            0.5F,
                                            0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                                        this.gear.setInventorySlotContents(5, new ItemStack(itemstack4.getItem()));
                                        this.gear.setInventorySlotContents(3, (ItemStack) null);
                                    } else
                                    if (itemstack4 != null && itemstack4.getItem() == Blocklings.itemUpgradeSpeed3
                                        && itemstack6 != null
                                        && itemstack6.getItem() == Blocklings.itemUpgradeDamage3
                                        && !this.worldObj.isRemote) {
                                            this.currentSpecialTier = 10;
                                            this.worldObj.playSoundAtEntity(
                                                this,
                                                "random.anvil_use",
                                                0.5F,
                                                0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                                            this.gear.setInventorySlotContents(5, new ItemStack(itemstack4.getItem()));
                                            this.gear.setInventorySlotContents(3, (ItemStack) null);
                                        } else if (itemstack6 == null && this.currentSpecialTier <= 2
                                            && !this.worldObj.isRemote) {
                                                this.currentSpecialTier = 1;
                                            } else if (itemstack6 == null && this.currentSpecialTier == 3
                                                && !this.worldObj.isRemote) {
                                                    this.gear.setInventorySlotContents(
                                                        5,
                                                        new ItemStack(Blocklings.itemUpgradeHealth1));
                                                    this.currentSpecialTier = 2;
                                                } else if (itemstack6 == null && this.currentSpecialTier == 4
                                                    && !this.worldObj.isRemote) {
                                                        this.gear.setInventorySlotContents(
                                                            5,
                                                            new ItemStack(Blocklings.itemUpgradeDamage1));
                                                        this.currentSpecialTier = 3;
                                                    } else if (itemstack6 == null && this.currentSpecialTier == 5
                                                        && !this.worldObj.isRemote) {
                                                            this.gear.setInventorySlotContents(
                                                                5,
                                                                new ItemStack(Blocklings.itemUpgradeSpeed1));
                                                            this.currentSpecialTier = 4;
                                                        } else if (itemstack6 == null && this.currentSpecialTier == 6
                                                            && !this.worldObj.isRemote) {
                                                                this.gear.setInventorySlotContents(
                                                                    5,
                                                                    new ItemStack(Blocklings.itemUpgradeHealth2));
                                                                this.currentSpecialTier = 5;
                                                            } else
                                                            if (itemstack6 == null && this.currentSpecialTier == 7
                                                                && !this.worldObj.isRemote) {
                                                                    this.gear.setInventorySlotContents(
                                                                        5,
                                                                        new ItemStack(Blocklings.itemUpgradeDamage2));
                                                                    this.currentSpecialTier = 6;
                                                                } else
                                                                if (itemstack6 == null && this.currentSpecialTier == 8
                                                                    && !this.worldObj.isRemote) {
                                                                        this.gear.setInventorySlotContents(
                                                                            5,
                                                                            new ItemStack(
                                                                                Blocklings.itemUpgradeSpeed2));
                                                                        this.currentSpecialTier = 7;
                                                                    } else if (itemstack6 == null
                                                                        && this.currentSpecialTier == 9
                                                                        && !this.worldObj.isRemote) {
                                                                            this.gear.setInventorySlotContents(
                                                                                5,
                                                                                new ItemStack(
                                                                                    Blocklings.itemUpgradeHealth3));
                                                                            this.currentSpecialTier = 8;
                                                                        } else if (itemstack6 == null
                                                                            && this.currentSpecialTier == 10
                                                                            && !this.worldObj.isRemote) {
                                                                                this.gear.setInventorySlotContents(
                                                                                    5,
                                                                                    new ItemStack(
                                                                                        Blocklings.itemUpgradeDamage3));
                                                                                this.currentSpecialTier = 9;
                                                                            }

    }

    @Unique
    private void checkWeapon() {
        ItemStack itemstack1 = this.gear.getStackInSlot(0);
        ItemStack itemstack2 = this.gear.getStackInSlot(1);
        ItemStack itemstack3 = this.gear.getStackInSlot(2);
        ItemStack itemstack4 = this.gear.getStackInSlot(3);
        ItemStack itemstack5 = this.gear.getStackInSlot(4);
        ItemStack itemstack6 = this.gear.getStackInSlot(5);
        if (itemstack1 != null && this.currentLevel >= 10 && itemstack2 == null && !this.worldObj.isRemote) {
            if (itemstack1.getItem() == Items.wooden_sword) {
                this.swordID = 1;
            } else if (itemstack1.getItem() == Items.stone_sword) {
                this.swordID = 2;
            } else if (itemstack1.getItem() == Items.iron_sword) {
                this.swordID = 3;
            } else if (itemstack1.getItem() == Items.golden_sword) {
                this.swordID = 4;
            } else if (itemstack1.getItem() == Items.diamond_sword) {
                this.swordID = 5;
            }
        } else if (itemstack1 == null && itemstack2 == null && !this.worldObj.isRemote) {
            this.swordID = 0;
        }

        if (itemstack2 != null && this.currentLevel >= 10 && !this.worldObj.isRemote) {
            if (itemstack2.getItem() == Blocklings.itemShuriken) {
                this.swordID = 6;
            }
        } else if (itemstack1 == null && itemstack2 == null && !this.worldObj.isRemote) {
            this.swordID = 0;
        }

        this.setCombatTask();
    }

    @Unique
    private void setupGear() {
        InventoryBlockling gear1 = this.gear;
        this.gear = new InventoryBlockling("Inventory", this.howManySlots());
        this.gear.func_110133_a(this.getCommandSenderName());
        if (gear1 != null) {
            gear1.func_110132_b(this);
            int i = Math.min(gear1.getSizeInventory(), this.gear.getSizeInventory());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = gear1.getStackInSlot(j);
                if (itemstack != null) {
                    this.gear.setInventorySlotContents(j, itemstack.copy());
                }
            }

            gear1 = null;
        }

        this.gear.func_110134_a(this);
    }

    @Unique
    public int howManySlots() {
        return 6;
    }

    @Unique
    public void setMaxHealth() {
        double bonusHealth = this.currentUpgradeTier * 2;
        if (this.currentSpecialTier >= 2 && this.currentSpecialTier < 5) {
            bonusHealth = this.currentUpgradeTier * 2 + 5;
        }

        if (this.currentSpecialTier >= 5 && this.currentSpecialTier < 8) {
            bonusHealth = this.currentUpgradeTier * 2 + 11;
        }

        if (this.currentSpecialTier >= 8) {
            bonusHealth = this.currentUpgradeTier * 2 + 20;
        }

        if (this.currentLevel <= 10) {
            this.maxHealth = (double) (this.currentLevel * 2) + bonusHealth;
        } else if (this.currentLevel > 10) {
            this.maxHealth = 20.0 + bonusHealth;
        }

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(this.maxHealth);
        if ((double) this.getHealth() > this.maxHealth) {
            this.heal(0.0F);
        }

    }

    @Unique
    public void setAttackDamage() {
        double bonusDamage = this.currentUpgradeTier;
        if (this.swordID > 0 && this.swordID < 6) {
            bonusDamage = this.currentUpgradeTier + this.swordID * 2;
        }

        if (this.currentSpecialTier >= 3 && this.currentSpecialTier < 6) {
            bonusDamage = this.currentUpgradeTier + 3;
        }

        if (this.currentSpecialTier >= 6 && this.currentSpecialTier < 9) {
            bonusDamage = this.currentUpgradeTier + 6;
        }

        if (this.currentSpecialTier >= 9) {
            bonusDamage = this.currentUpgradeTier + 10;
        }

        if (this.currentSpecialTier >= 3 && this.currentSpecialTier < 6 && this.swordID > 0 && this.swordID < 6) {
            bonusDamage = this.currentUpgradeTier + this.swordID * 2 + 3;
        }

        if (this.currentSpecialTier >= 6 && this.currentSpecialTier < 9 && this.swordID > 0 && this.swordID < 6) {
            bonusDamage = this.currentUpgradeTier + this.swordID * 2 + 6;
        }

        if (this.currentSpecialTier >= 9 && this.swordID > 0 && this.swordID < 6) {
            bonusDamage = this.currentUpgradeTier + this.swordID * 2 + 10;
        }

        if (this.swordID == 6 && this.currentLevel >= 10) {
            this.attackDamage = 5.0;
        } else if (this.currentLevel <= 10) {
            this.attackDamage = (double) this.currentLevel + bonusDamage;
        } else if (this.currentLevel > 10) {
            this.attackDamage = 10.0 + bonusDamage;
        }

        this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
            .setBaseValue(this.attackDamage);
    }

    @Unique
    public void setMoveSpeed() {
        double bonusSpeed = 0.0;
        if (this.currentSpecialTier >= 4 && this.currentSpecialTier < 7) {
            bonusSpeed = 0.05;
        }

        if (this.currentSpecialTier >= 7 && this.currentSpecialTier < 10) {
            bonusSpeed = 0.11;
        }

        if (this.currentSpecialTier >= 10) {
            bonusSpeed = 0.15;
        }

        if (this.currentLevel <= 10) {
            this.moveSpeed = 0.25 + (double) this.currentLevel / 200.0 + bonusSpeed;
        } else if (this.currentLevel > 10) {
            this.moveSpeed = 0.35 + bonusSpeed;
        }

        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(this.moveSpeed);
    }
}
