package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockLiquid.class)
public class MixinBlockLiquid extends Block {

    protected MixinBlockLiquid(Material materialIn) {
        super(materialIn);
    }

    /**
     * Can add to the passed in vector for a movement vector to be applied to the entity. Args: x, y, z, entity, vec3d
     */
    public void velocityToAddToEntity(World worldIn, int x, int y, int z, Entity entityIn, Vec3 velocity) {
        Vec3 vec31 = this.getFlowVector(worldIn, x, y, z);
        velocity.xCoord += vec31.xCoord;
        velocity.yCoord += vec31.yCoord;
        velocity.zCoord += vec31.zCoord;
    }

    /**
     * Returns a vector indicating the direction and intensity of liquid flow
     */
    @Overwrite
    private Vec3 getFlowVector(IBlockAccess p_149800_1_, int p_149800_2_, int p_149800_3_, int p_149800_4_) {
        Vec3 vec3 = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
        int l = this.getEffectiveFlowDecay(p_149800_1_, p_149800_2_, p_149800_3_, p_149800_4_);
        int i1;

        for (i1 = 0; i1 < 4; ++i1) {
            int j1 = p_149800_2_;
            int k1 = p_149800_4_;

            if (i1 == 0) j1 = p_149800_2_ - 1;
            if (i1 == 1) k1 = p_149800_4_ - 1;
            if (i1 == 2) ++j1;
            if (i1 == 3) ++k1;

            int l1 = this.getEffectiveFlowDecay(p_149800_1_, j1, p_149800_3_, k1);
            int i2;

            if (l1 < 0) {
                if (!p_149800_1_.getBlock(j1, p_149800_3_, k1)
                    .getMaterial()
                    .blocksMovement()) {
                    l1 = this.getEffectiveFlowDecay(p_149800_1_, j1, p_149800_3_ - 1, k1);

                    if (l1 >= 0) {
                        i2 = l1 - (l - 8);
                        vec3 = vec3.addVector((j1 - p_149800_2_) * i2, 0, (k1 - p_149800_4_) * i2);
                    }
                }
            } else {
                i2 = l1 - l;
                vec3 = vec3.addVector((j1 - p_149800_2_) * i2, 0, (k1 - p_149800_4_) * i2);
            }
        }

        if (p_149800_1_.getBlockMetadata(p_149800_2_, p_149800_3_, p_149800_4_) >= 8) {
            boolean flag = false;

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (this.isBlockSolid(p_149800_1_, p_149800_2_ + i, p_149800_3_, p_149800_4_ + j, i1)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) break;
            }

            if (flag) {
                vec3 = vec3.normalize()
                    .addVector(0.0D, -6.0D, 0.0D);
            }
        }

        return vec3.normalize();
    }

    /**
     * Returns true if the given side of this block type should be rendered (if it's solid or not), if the adjacent
     * block is at the given coordinates. Args: blockAccess, x, y, z, side
     */

    public boolean isBlockSolid(IBlockAccess worldIn, int x, int y, int z, int side) {
        Material material = worldIn.getBlock(x, y, z)
            .getMaterial();
        return material != this.blockMaterial
            && (side == 1 || (material != Material.ice && super.isBlockSolid(worldIn, x, y, z, side)));
    }

    /**
     * Returns the flow decay but converts values indicating falling liquid (values >=8) to their effective source block
     * value of zero
     */
    @Overwrite
    protected int getEffectiveFlowDecay(IBlockAccess p_149800_1_, int p_149800_2_, int p_149800_3_, int p_149800_4_) {
        Block block = p_149800_1_.getBlock(p_149800_2_, p_149800_3_, p_149800_4_);

        if (block.getMaterial() != this.blockMaterial) {
            return -1;
        }

        int metadata = p_149800_1_.getBlockMetadata(p_149800_2_, p_149800_3_, p_149800_4_);

        return metadata >= 8 ? 0 : metadata;
    }

}
