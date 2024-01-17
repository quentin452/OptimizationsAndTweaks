package fr.iamacat.optimizationsandtweaks.utils.toomuchTNT;

import java.util.Random;

import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.toomuchtnt.TooMuchTNT;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockNuclearWaste2 extends BlockSnow {

    public Material blockMaterial;

    public BlockNuclearWaste2(int par1) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.setTickRandomly(true);
        this.setCreativeTab(TooMuchTNT.tabTooMuchTNT);
        this.func_150154_b(0);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("toomuchtnt:NuclearWaste");
    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Items.slime_ball;
    }

    public int quantityDroppedWithBonus(int par1, Random par2Random) {
        return par2Random.nextInt(par1 + 2);
    }

    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
        par5Entity.attackEntityFrom(DamageSource.cactus, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        super.randomDisplayTick(par1World, par2, par3, par4, par5Random);
        if (par5Random.nextInt(10) == 0) {
            par1World.spawnParticle(
                "reddust",
                (double) ((float) par2 + par5Random.nextFloat()),
                (double) ((float) par3 + 0.3F),
                (double) ((float) par4 + par5Random.nextFloat()),
                1.0,
                5.0,
                0.0);
        }

    }

    public MapColor getMapColor(int p_149728_1_) {
        return MapColor.limeColor;
    }
}
