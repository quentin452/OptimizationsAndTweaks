package fr.iamacat.optimizationsandtweaks.mixins.common.core.entity;

import java.util.Objects;

import net.minecraft.block.Block;
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
        ItemStack item = this.optimizationsAndTweaks$getEntityItem();
        if (item == null) {
            this.setDead();
            ci.cancel();
            return;
        }

        if (Objects.requireNonNull(item.getItem())
            .onEntityItemUpdate(entityItem)) {
            return;
        }

        super.onUpdate();

        if (this.delayBeforeCanPickup > 0) {
            --this.delayBeforeCanPickup;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.noClip = this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        boolean positionChanged = (int) this.prevPosX != (int) this.posX || (int) this.prevPosY != (int) this.posY
            || (int) this.prevPosZ != (int) this.posZ;

        if (positionChanged || this.ticksExisted % 25 == 0) {
            Block block = this.worldObj.getBlock(
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posY),
                MathHelper.floor_double(this.posZ));
            Material material = block.getMaterial();

            if (material == Material.lava) {
                this.motionY = 0.2D;
                float randomMotion = this.rand.nextFloat() - this.rand.nextFloat();
                this.motionX = randomMotion * 0.2F;
                this.motionZ = randomMotion * 0.2F;
                this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
            }

            if (!this.worldObj.isRemote) {
                this.optimizationsAndTweaks$searchForOtherItemsNearby(ci);
            }
        }

        float slipperiness = 0.98F;
        if (this.onGround) {
            Block blockUnder = this.worldObj.getBlock(
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.boundingBox.minY) - 1,
                MathHelper.floor_double(this.posZ));
            slipperiness = blockUnder.slipperiness * 0.98F;
        }

        this.motionX *= slipperiness;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= slipperiness;

        if (this.onGround) {
            this.motionY *= -0.5D;
        }

        ++this.age;

        if (!this.worldObj.isRemote) {
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

        if (item.stackSize <= 0) {
            this.setDead();
        }
        ci.cancel();
    }

    /**
     * Returns the ItemStack corresponding to the Entity (Note: if no item exists, will log an error but still return an
     * ItemStack containing Block.stone)
     */
    @Unique
    public ItemStack optimizationsAndTweaks$getEntityItem() {
        ItemStack itemstack = this.getDataWatcher()
            .getWatchableObjectItemStack(10);
        return itemstack == null ? new ItemStack(Blocks.stone) : itemstack;
    }

    /**
     * Looks for other itemstacks nearby and tries to stack them together
     */
    @Unique
    private void optimizationsAndTweaks$searchForOtherItemsNearby(CallbackInfo ci) {
        for (Object o : this.worldObj
            .getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.5D, 0.0D, 0.5D))) {
            EntityItem entityitem = (EntityItem) o;
            this.combineItems(entityitem, ci);
        }
    }

    /**
     * Tries to merge this item with the item passed as the parameter. Returns true if successful. Either this item or
     * the other item will be removed from the world.
     */
    @Inject(method = "combineItems", at = @At("HEAD"), remap = false, cancellable = true)
    public boolean combineItems(EntityItem p_70289_1_, CallbackInfo ci) {
        if (p_70289_1_ == entityItem || !p_70289_1_.isEntityAlive() || !this.isEntityAlive()) {
            return false;
        }

        ItemStack itemstack = this.optimizationsAndTweaks$getEntityItem();
        ItemStack itemstack1 = p_70289_1_.getEntityItem();

        if (itemstack1.getItem() != itemstack.getItem() || (itemstack1.hasTagCompound() ^ itemstack.hasTagCompound())
            || (itemstack1.hasTagCompound() && !itemstack1.getTagCompound()
                .equals(itemstack.getTagCompound()))
            || itemstack1.getItem() == null
            || (itemstack1.getItem()
                .getHasSubtypes() && itemstack1.getItemDamage() != itemstack.getItemDamage())
            || itemstack1.stackSize < itemstack.stackSize
            || itemstack1.stackSize + itemstack.stackSize > itemstack1.getMaxStackSize()) {
            return false;
        }

        itemstack1.stackSize += itemstack.stackSize;
        p_70289_1_.delayBeforeCanPickup = Math.max(p_70289_1_.delayBeforeCanPickup, this.delayBeforeCanPickup);
        p_70289_1_.age = Math.min(p_70289_1_.age, this.age);
        p_70289_1_.setEntityItemStack(itemstack1);
        this.setDead();
        ci.cancel();
        return true;
    }
}
