package fr.iamacat.catmod.init;

import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RegisterFuel implements IFuelHandler {
    @Override
    public int getBurnTime(ItemStack fuel) {
        if(fuel.getItem()== RegisterItems.catCoin){
            return 1000;// can burn 5 items
        }
        else if(fuel.getItem()== Item.getItemFromBlock(RegisterBlocks.catBlock)){
            return 100;//
        }

        return 0;
    }
}
