//needed to make a custom tnt(the block)
package fr.iamacat.catmod.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.catmod.entities.tnt.EntityCatTnt;
import fr.iamacat.catmod.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class CatTnt extends BlockTNT {


    public CatTnt() {
        super();
        this.setHardness(0.0F);
        this.setResistance(0.0F);
    }
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;
    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.topIcon = iconRegister.registerIcon(Reference.MOD_ID + ":cat_tnt_top");
        this.bottomIcon = iconRegister.registerIcon(Reference.MOD_ID + ":cat_tnt_bottom");
        this.sideIcon = iconRegister.registerIcon(Reference.MOD_ID + ":cat_tnt_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0) {
            return bottomIcon;
        } else if (side == 1) {
            return topIcon;
        } else {
            return sideIcon;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (world.getBlock(x, y, z) == CatTnt.CAT_TNT_BLOCK) {
            createCatTntEntity(world, x + 0.5, y + 0.5, z + 0.5, stack);
        }
    }

    public static final Block CAT_TNT_BLOCK = new CatTnt();
    public static final String CAT_TNT_BLOCK_NAME = "cat_tnt_block";

    public static void registerBlocks() {
        GameRegistry.registerBlock(CAT_TNT_BLOCK, CAT_TNT_BLOCK_NAME);
    }

    public static String getBlockName(Block block) {
        return Block.blockRegistry.getNameForObject(block).toString();
    }



    private EntityCatTnt createCatTntEntity(World world, double x, double y, double z, ItemStack stack) {
        EntityCatTnt entity = new EntityCatTnt(world);
        entity.setPosition(x, y, z);
        entity.fuse = 80;
        entity.setCustomName(stack.getDisplayName());
        world.spawnEntityInWorld(entity);
        return entity;
    }
    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        if (!world.isRemote) {
            float size = 4.0F; // the explosion size, in blocks

            // create an explosion at the TNT's location, with the specified size
            world.createExplosion(null, x, y, z, size, true);

            // spawn explosion particles
            world.spawnParticle("hugeexplosion", x, y, z, 0.0D,  0.0D, 1.0D);

            // remove the TNT block
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
        this.explode(world, x, y, z, null);
    }

    private void explode(World world, int x, int y, int z, EntityLivingBase igniter) {
        if (!world.isRemote) {
            float size = 4.0F; // the explosion size, in blocks

            // create an explosion context
            Explosion explosion = new Explosion(world, igniter, x, y, z, size);

            // create an explosion at the TNT's location, with the specified size and explosion context
            world.createExplosion(null, x, y, z, size, false);

            // spawn explosion particles
            world.spawnParticle("hugeexplosion", x, y, z, 0.0D, 0.0D, 0.0D);

            // remove the TNT block
            world.setBlockToAir(x, y, z);
        }
    }

}