package fr.iamacat.optimizationsandtweaks.mixins.common.core.entity;

import java.util.Iterator;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.config.MultithreadingandtweaksConfig;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity {

    @Shadow
    public int lifespan = 6000;
    @Unique
    private EntityItem entityItem;
    @Shadow
    public int age;

    @Shadow
    public int delayBeforeCanPickup;

    public MixinEntityItem(World worldIn, EntityItem entityItem) {
        super(worldIn);
        this.entityItem = entityItem;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Inject(method = "onUpdate", at = @At("HEAD"), remap = false, cancellable = true)
    public void onUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntityItem) {
            ItemStack stack = this.getDataWatcher()
                .getWatchableObjectItemStack(10);
            if (stack != null && stack.getItem() != null) {
                if (stack.getItem()
                    .onEntityItemUpdate(entityItem)) {
                    return;
                }
            }

            if (this.multithreadingandtweaks$getEntityItem() == null) {
                this.setDead();
            } else {
                super.onUpdate();

                if (this.delayBeforeCanPickup > 0) {
                    --this.delayBeforeCanPickup;
                }

                this.prevPosX = this.posX;
                this.prevPosY = this.posY;
                this.prevPosZ = this.posZ;
                this.motionY -= 0.03999999910593033D;
                this.noClip = this
                    .func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                boolean flag = (int) this.prevPosX != (int) this.posX || (int) this.prevPosY != (int) this.posY
                    || (int) this.prevPosZ != (int) this.posZ;

                if (flag || this.ticksExisted % 25 == 0) {
                    if (this.worldObj
                        .getBlock(
                            MathHelper.floor_double(this.posX),
                            MathHelper.floor_double(this.posY),
                            MathHelper.floor_double(this.posZ))
                        .getMaterial() == Material.lava) {
                        this.motionY = 0.20000000298023224D;
                        this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
                        this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
                        this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                    }

                    if (!this.worldObj.isRemote) {
                        this.multithreadingandtweaks$searchForOtherItemsNearby(ci);
                    }
                }

                float f = 0.98F;

                if (this.onGround) {
                    f = this.worldObj.getBlock(
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.boundingBox.minY) - 1,
                        MathHelper.floor_double(this.posZ)).slipperiness * 0.98F;
                }

                this.motionX *= f;
                this.motionY *= 0.9800000190734863D;
                this.motionZ *= f;

                if (this.onGround) {
                    this.motionY *= -0.5D;
                }

                ++this.age;

                ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

                if (item != null && !this.worldObj.isRemote) {
                    if (this.age >= lifespan) {
                        ItemExpireEvent event = new ItemExpireEvent(
                            entityItem,
                            (item.getItem() == null ? 6000
                                : item.getItem()
                                    .getEntityLifespan(item, worldObj)));
                        if (MinecraftForge.EVENT_BUS.post(event)) {
                            lifespan += event.extraLife;
                        } else {
                            this.setDead();
                        }
                    }

                    if (item.stackSize <= 0) {
                        this.setDead();
                    }
                }

                if (item != null && item.stackSize <= 0) {
                    this.setDead();
                }
            }
            ci.cancel();
        }
    }

    /**
     * Returns the ItemStack corresponding to the Entity (Note: if no item exists, will log an error but still return an
     * ItemStack containing Block.stone)
     */
    @Unique
    public ItemStack multithreadingandtweaks$getEntityItem() {
        ItemStack itemstack = this.getDataWatcher()
            .getWatchableObjectItemStack(10);
        return itemstack == null ? new ItemStack(Blocks.stone) : itemstack;
    }

    /**
     * Looks for other itemstacks nearby and tries to stack them together
     */
    @Unique
    private void multithreadingandtweaks$searchForOtherItemsNearby(CallbackInfo ci) {
        Iterator iterator = this.worldObj
            .getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.5D, 0.0D, 0.5D))
            .iterator();

        while (iterator.hasNext()) {
            EntityItem entityitem = (EntityItem) iterator.next();
            this.combineItems(entityitem, ci);
        }
    }

    /**
     * Tries to merge this item with the item passed as the parameter. Returns true if successful. Either this item or
     * the other item will be removed from the world.
     */
    @Inject(method = "combineItems", at = @At("HEAD"), remap = false, cancellable = true)
    public boolean combineItems(EntityItem p_70289_1_, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntityItem) {
            if (p_70289_1_ == entityItem) {
                return false;
            } else if (p_70289_1_.isEntityAlive() && this.isEntityAlive()) {
                ItemStack itemstack = this.multithreadingandtweaks$getEntityItem();
                ItemStack itemstack1 = p_70289_1_.getEntityItem();

                if (itemstack1.getItem() != itemstack.getItem()) {
                    return false;
                } else if (itemstack1.hasTagCompound() ^ itemstack.hasTagCompound()) {
                    return false;
                } else if (itemstack1.hasTagCompound() && !itemstack1.getTagCompound()
                    .equals(itemstack.getTagCompound())) {
                        return false;
                    } else if (itemstack1.getItem() == null) {
                        return false;
                    } else if (itemstack1.getItem()
                        .getHasSubtypes() && itemstack1.getItemDamage() != itemstack.getItemDamage()) {
                            return false;
                        } else if (itemstack1.stackSize < itemstack.stackSize) {
                            return p_70289_1_.combineItems(entityItem);
                        } else if (itemstack1.stackSize + itemstack.stackSize > itemstack1.getMaxStackSize()) {
                            return false;
                        } else {
                            itemstack1.stackSize += itemstack.stackSize;
                            p_70289_1_.delayBeforeCanPickup = Math
                                .max(p_70289_1_.delayBeforeCanPickup, this.delayBeforeCanPickup);
                            p_70289_1_.age = Math.min(p_70289_1_.age, this.age);
                            p_70289_1_.setEntityItemStack(itemstack1);
                            this.setDead();
                            return true;
                        }
            } else {
                return false;
            }
        }
        ci.cancel();
        return false;
    }
}
