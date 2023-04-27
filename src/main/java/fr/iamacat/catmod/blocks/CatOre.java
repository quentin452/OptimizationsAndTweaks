package fr.iamacat.catmod.blocks;

import fr.iamacat.catmod.init.RegisterItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class CatOre extends Block {
    public CatOre() {
        super(Material.rock);
        this.setHardness(2.0F);//resistance to pickaxe
        this.setResistance(15.0F);//resistance to tnt
        this.setHarvestLevel("pickaxe",3);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
        Random random = world instanceof World ? ((World)world).rand : new Random();
        return MathHelper.getRandomIntegerInRange(random, 1, 3);
    }

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        return RegisterItems.catIngot;
    }
}