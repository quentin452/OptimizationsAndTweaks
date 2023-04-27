package fr.iamacat.catmod.items;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

public class CatIngot extends ItemBlock {
    public CatIngot(Block block) {
        super(block);
        setUnlocalizedName("CatIngot");
        setCreativeTab(CreativeTabs.tabMaterials);
    }
}