package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import thaumcraft.common.items.equipment.ItemElementalAxe;
import thaumcraft.common.lib.utils.Utils;

@Mixin(Utils.class)
public class MixinThaumcraftUtils {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean isWoodLog(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);

        if (block == Blocks.air) {
            return false;
        }

        if (block.canSustainLeaves(world, x, y, z)) {
            return true;
        }

        return optimizationsAndTweaks$isBlockInOreDictLogs(block, metadata);
    }

    @Unique
    private static boolean optimizationsAndTweaks$isBlockInOreDictLogs(Block block, int metadata) {
        return ItemElementalAxe.oreDictLogs.contains(Arrays.asList(block, metadata));
    }
}
