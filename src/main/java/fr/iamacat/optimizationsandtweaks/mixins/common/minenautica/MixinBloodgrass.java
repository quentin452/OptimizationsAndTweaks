package fr.iamacat.optimizationsandtweaks.mixins.common.minenautica;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.minenautica.Minenautica.Blocks.Bloodgrass;
import com.minenautica.Minenautica.Blocks.GroundCoral;

@Mixin(Bloodgrass.class)
public abstract class MixinBloodgrass extends Block {

    private World world;

    protected MixinBloodgrass(Material materialIn, World world) {
        super(materialIn);
        this.world = world;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_149718_j(World world, int x, int y, int z) {
        Block belowBlock = world.getBlock(x, y - 1, z);
        Block currentBlock = world.getBlock(x, y, z);
        Block aboveBlock = world.getBlock(x, y + 1, z);
        Block aboveBlock2 = world.getBlock(x, y + 2, z);

        int currentMeta = getCoralBlockMetadata(world, x, y, z);
        int belowMeta = getCoralBlockMetadata(world, x, y - 1, z);
        int aboveMeta = getCoralBlockMetadata(world, x, y + 1, z);

        if ((belowBlock instanceof GroundCoral || belowBlock instanceof BlockSand) && checkWater(aboveBlock, true)) {
            return true;
        }

        if (currentMeta != 1 && currentMeta != 4) {
            return false;
        }

        if (belowBlock != currentBlock || belowMeta != currentMeta) {
            return belowBlock instanceof GroundCoral || belowBlock instanceof BlockSand;
        }

        if (checkWater(aboveBlock, true)) {
            return true;
        }

        return aboveBlock == currentBlock && aboveMeta == currentMeta
            || (aboveBlock == Blocks.air && checkWater(aboveBlock2, true));
    }

    @Shadow
    public static int getCoralBlockMetadata(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        return metadata < 8 ? metadata : metadata - 8;
    }

    @Shadow
    public static boolean checkWater(Block block, boolean stationary) {
        return checkWater(block) && block.func_149698_L() == stationary;
    }

    @Shadow
    public static boolean checkWater(Block block) {
        return !(block instanceof Bloodgrass) && block.getMaterial() == Material.water;
    }

}
