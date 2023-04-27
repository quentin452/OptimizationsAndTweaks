package fr.iamacat.catmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class CatBlock extends Block {

    public CatBlock(Material rock) {
        super(rock);

        this.setHardness(2.0F);//resistance to pickaxe
        this.setResistance(15.0F);//resistance to tnt
        this.setHarvestLevel("pickaxe",3);
        // this.setLightLevel(14); how much the block will make light
        // this.setBlockUnbreakable();//make the block unbreakable
    }
}