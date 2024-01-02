package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minecraft;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

public class BlockGrass {

    public static void updateTick(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
            int lightValue = worldIn.getBlockLightValue(x, y + 1, z);
            int lightOpacity = worldIn.getBlockLightOpacity(x, y + 1, z);

            if (lightValue < 4 && lightOpacity > 2) {
                worldIn.setBlock(x, y, z, Blocks.dirt);
                ci.cancel();
            } else if (lightValue >= 9) {

                for (int i = 0; i < 4; i++) {
                    int i1 = x + random.nextInt(3) - 1;
                    int j1 = y + random.nextInt(5) - 3;
                    int k1 = z + random.nextInt(3) - 1;

                    Block block = worldIn.getBlock(i1, j1 + 1, k1);
                    int blockLightValue = worldIn.getBlockLightValue(i1, j1 + 1, k1);
                    int blockLightOpacity = worldIn.getBlockLightOpacity(i1, j1 + 1, k1);

                    if (block == Blocks.dirt && worldIn.getBlockMetadata(i1, j1, k1) == 0
                        && blockLightValue >= 4
                        && blockLightOpacity <= 2) {
                        worldIn.setBlock(i1, j1, k1, Blocks.grass);
                    }
                }
            }
    }
}
