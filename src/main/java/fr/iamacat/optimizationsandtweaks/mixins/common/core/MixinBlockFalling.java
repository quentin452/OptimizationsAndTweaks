package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockFalling.class)
public class MixinBlockFalling extends Block {

    @Shadow
    public static boolean fallInstantly;
    public MixinBlockFalling() {
        super(Material.sand);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public MixinBlockFalling(Material p_i45405_1_) {
        super(p_i45405_1_);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Shadow
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    @Shadow
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
    }

    /**
     * Ticks the block if it's been scheduled
     */
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            this.func_149830_m(worldIn, x, y, z);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void func_149830_m(World world, int x, int y, int z) {
        if (BlockFalling.func_149831_e(world, x, y - 1, z) && y >= 0) {
            int maxDistance = 32;

            if (!fallInstantly && world.checkChunksExist(
                x - maxDistance,
                y - maxDistance,
                z - maxDistance,
                x + maxDistance,
                y + maxDistance,
                z + maxDistance)) {
                if (!world.isRemote) {
                    EntityFallingBlock fallingBlock = new EntityFallingBlock(
                        world,
                        x + 0.5D,
                        y + 0.5D,
                        z + 0.5D,
                        this,
                        world.getBlockMetadata(x, y, z));
                    this.func_149829_a(fallingBlock);
                    world.spawnEntityInWorld(fallingBlock);
                }
            } else {
                world.setBlockToAir(x, y, z);

                while (BlockFalling.func_149831_e(world, x, y - 1, z) && y > 0) {
                    --y;
                }

                if (y > 0) {
                    world.setBlock(x, y, z, this);
                }
            }
        }
    }

    @Shadow
    protected void func_149829_a(EntityFallingBlock p_149829_1_) {}

}
