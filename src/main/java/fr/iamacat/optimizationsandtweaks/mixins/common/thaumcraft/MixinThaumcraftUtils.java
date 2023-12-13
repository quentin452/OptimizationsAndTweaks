package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.common.items.equipment.ItemElementalAxe;
import thaumcraft.common.lib.utils.Utils;

import java.util.Arrays;

@Mixin(Utils.class)
public class MixinThaumcraftUtils {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean isWoodLog(IBlockAccess world, int x, int y, int z) {
        Block bi = world.getBlock(x, y, z);
        int md = world.getBlockMetadata(x, y, z);
        if (bi == Blocks.air) {
            return false;
        } else if (bi.canSustainLeaves(world, x, y, z)) {
            return true;
        } else {
            return ItemElementalAxe.oreDictLogs.contains(Arrays.asList(bi, md));
        }
    }
}
