package fr.iamacat.catmod.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class CatCoin extends Item {
        public CatCoin() {
           this.setFull3D();
           this.setMaxDamage(1);
           this.setPotionEffect(PotionHelper.blazePowderEffect);
        }
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.catCoin.item"));
    }
}

