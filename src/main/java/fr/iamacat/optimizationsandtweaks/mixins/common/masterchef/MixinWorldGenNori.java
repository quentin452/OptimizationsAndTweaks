package fr.iamacat.optimizationsandtweaks.mixins.common.masterchef;

import com.chef.mod.generate.features.WorldGenNori;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(WorldGenNori.class)
public class MixinWorldGenNori {
    @Shadow
    Block block;
    @Shadow
    int minHeight;
    @Shadow
    int maxHeight;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random random, int x, int y, int z) {
        if (!world.getChunkProvider().chunkExists(x, z)) {
            return false;
        }

        for (int l = 0; l < 20; ++l) {
            int i1 = x + random.nextInt(4) - random.nextInt(4);
            int k1 = z + random.nextInt(4) - random.nextInt(4);
            if (world.getBlock(i1, y, k1) == Blocks.water) {
                int j = this.minHeight + random.nextInt(random.nextInt(this.maxHeight) + 1);

                for (int k = 0; k < j; ++k) {
                    if (this.block.canPlaceBlockAt(world, i1, y, k1)) {
                        world.setBlock(i1, y + k, k1, this.block, 0, 2);
                    }
                }
            }
        }

        return true;
    }
}
