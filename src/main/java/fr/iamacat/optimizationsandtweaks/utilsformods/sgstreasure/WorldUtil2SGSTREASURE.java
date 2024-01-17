package fr.iamacat.optimizationsandtweaks.utilsformods.sgstreasure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldUtil2SGSTREASURE {

    public static boolean isSolidBase(World world, int x, int y, int z, int width, int depth, int percentRequired) {
        int platformSize = 0;
        for (int i = 0; i < depth; ++i) {
            for (int k = 0; k < width; ++k) {
                Block block = world.getBlock(x + k, y - 1, z + i);
                if (block != null && block.isBlockSolid(world, x + k, y - 1, z + i, 0)
                    && !block.getMaterial()
                        .isReplaceable()) {
                    ++platformSize;
                }
            }
        }
        float base = (depth * width);
        float percent = platformSize / base * 100.0F;
        return !(percent < percentRequired);
    }
}
