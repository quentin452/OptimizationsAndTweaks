package fr.iamacat.optimizationsandtweaks.mixins.common.core.entity;

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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Iterator;

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

    @Shadow
    private void searchForOtherItemsNearby()
    {
        Iterator iterator = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.5D, 0.0D, 0.5D)).iterator();

        while (iterator.hasNext())
        {
            EntityItem entityitem = (EntityItem)iterator.next();
            this.combineItems(entityitem);
        }
    }
    @Shadow
    public boolean combineItems(EntityItem p_70289_1_)
    {
        if (p_70289_1_ == entityItem)
        {
            return false;
        }
        else if (p_70289_1_.isEntityAlive() && this.isEntityAlive())
        {
            ItemStack itemstack = this.getEntityItem();
            ItemStack itemstack1 = p_70289_1_.getEntityItem();

            if (itemstack1.getItem() != itemstack.getItem())
            {
                return false;
            }
            else if (itemstack1.hasTagCompound() ^ itemstack.hasTagCompound())
            {
                return false;
            }
            else if (itemstack1.hasTagCompound() && !itemstack1.getTagCompound().equals(itemstack.getTagCompound()))
            {
                return false;
            }
            else if (itemstack1.getItem() == null)
            {
                return false;
            }
            else if (itemstack1.getItem().getHasSubtypes() && itemstack1.getItemDamage() != itemstack.getItemDamage())
            {
                return false;
            }
            else if (itemstack1.stackSize < itemstack.stackSize)
            {
                return p_70289_1_.combineItems(entityItem);
            }
            else if (itemstack1.stackSize + itemstack.stackSize > itemstack1.getMaxStackSize())
            {
                return false;
            }
            else
            {
                itemstack1.stackSize += itemstack.stackSize;
                p_70289_1_.delayBeforeCanPickup = Math.max(p_70289_1_.delayBeforeCanPickup, this.delayBeforeCanPickup);
                p_70289_1_.age = Math.min(p_70289_1_.age, this.age);
                p_70289_1_.setEntityItemStack(itemstack1);
                this.setDead();
                return true;
            }
        }
        else
        {
            return false;
        }
    }
    @Shadow
    public ItemStack getEntityItem()
    {
        ItemStack itemstack = this.getDataWatcher().getWatchableObjectItemStack(10);
        return itemstack == null ? new ItemStack(Blocks.stone) : itemstack;
    }
}
