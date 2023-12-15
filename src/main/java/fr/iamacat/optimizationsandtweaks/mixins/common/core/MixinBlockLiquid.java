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
    public Vec3 getFlowVector(IBlockAccess blockAccess, int x, int y, int z) {
        Vec3 vec3 = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
        int decay = getEffectiveFlowDecay(blockAccess, x, y, z);

        for (int i = 0; i < 4; ++i) {
            int xOffset = x;
            int zOffset = z;

            if (i == 0) xOffset = x - 1;
            if (i == 1) zOffset = z - 1;
            if (i == 2) ++xOffset;
            if (i == 3) ++zOffset;

            int neighborDecay = getEffectiveFlowDecay(blockAccess, xOffset, y, zOffset);
            int diff;

            if (neighborDecay < 0) {
                if (!blockAccess.getBlock(xOffset, y, zOffset).getMaterial().blocksMovement()) {
                    neighborDecay = getEffectiveFlowDecay(blockAccess, xOffset, y - 1, zOffset);

                    if (neighborDecay >= 0) {
                        diff = neighborDecay - (decay - 8);
                        vec3 = vec3.addVector((xOffset - x) * diff, 0, (zOffset - z) * diff);
                    }
                }
            } else {
                diff = neighborDecay - decay;
                vec3 = vec3.addVector((xOffset - x) * diff, 0, (zOffset - z) * diff);
            }
        }

        if (blockAccess.getBlockMetadata(x, y, z) >= 8) {
            boolean solidBlock = false;

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (isBlockSolid(blockAccess, x + i, y, z + j, i)) {
                        solidBlock = true;
                        break;
                    }
                }
                if (solidBlock) break;
            }

            if (solidBlock) {
                vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
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
    protected int getEffectiveFlowDecay(IBlockAccess blockAccess, int x, int y, int z) {
        Block block = blockAccess.getBlock(x, y, z);
        int metadata = blockAccess.getBlockMetadata(x, y, z);

        if (block.getMaterial() == this.blockMaterial && metadata >= 8) {
            return metadata;
        }

        return -1;
    }

}
