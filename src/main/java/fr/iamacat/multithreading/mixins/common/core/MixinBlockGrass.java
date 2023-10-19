package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(BlockGrass.class)
public abstract class MixinBlockGrass extends Block implements IGrowable {
    protected MixinBlockGrass(Material materialIn) {
        super(materialIn);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!MultithreadingandtweaksConfig.enableMixinBlockGrass || worldIn.isRemote) {
            return;
        }

        int lightValue = worldIn.getBlockLightValue(x, y + 1, z);
        int lightOpacity = worldIn.getBlockLightOpacity(x, y + 1, z);

        if (lightValue < 4 && lightOpacity > 2) {
            worldIn.setBlock(x, y, z, Blocks.dirt);
        } else if (lightValue >= 9) {
            for (int i = 0; i < 4; i++) {
                int offsetX = x + random.nextInt(3) - 1;
                int offsetY = y + random.nextInt(5) - 3;
                int offsetZ = z + random.nextInt(3) - 1;

                Block blockBelow = worldIn.getBlock(offsetX, offsetY + 1, offsetZ);
                Block blockCurrent = worldIn.getBlock(offsetX, offsetY, offsetZ);
                int metadata = worldIn.getBlockMetadata(offsetX, offsetY, offsetZ);

                if (blockCurrent == Blocks.dirt && metadata == 0 && lightValue >= 4 && lightOpacity <= 2) {
                    worldIn.setBlock(offsetX, offsetY, offsetZ, Blocks.grass);
                }
            }
        }
    }
}
