package fr.iamacat.catmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CatTorch extends BlockTorch {

    public CatTorch() {
        super();
        this.setHardness(0.0F);
        this.setLightLevel(1.0F);
        this.setStepSound(soundTypeWood);
        this.setTickRandomly(true);
        this.setLightOpacity(0);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        // Set block dimensions based on orientation
        switch (world.getBlockMetadata(x, y, z)) {
            case 1:
                setBlockBounds(0.5F - 0.1875F, 0.2F, 1.0F - 0.1875F, 0.5F + 0.1875F, 0.8F, 1.0F);
                break;
            case 2:
                setBlockBounds(0.0F, 0.2F, 0.5F - 0.1875F, 0.1875F, 0.8F, 0.5F + 0.1875F);
                break;
            case 3:
                setBlockBounds(1.0F - 0.1875F, 0.2F, 0.5F - 0.1875F, 1.0F, 0.8F, 0.5F + 0.1875F);
                break;
            default:
                setBlockBounds(0.5F - 0.1875F, 0.2F, 0.0F, 0.5F + 0.1875F, 0.8F, 0.1875F);
                break;
        }
    }

    private void setDefaultDirection(World world, int x, int y, int z, EntityLivingBase placer) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            Block blockBehind = world.getBlock(x, y, z + 1);
            if (placer != null) {
                int direction = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
                if (direction == 0) {
                    meta = 2;
                }
                if (direction == 1) {
                    meta = 1;
                }
                if (direction == 2) {
                    meta = 3;
                }
                if (direction == 3) {
                    meta = 0;
                }
            } else {
                if (blockBehind.isOpaqueCube()) {
                    meta = 0;
                } else {
                    meta = 1;
                }
            }
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }
    }
}
