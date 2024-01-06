package fr.iamacat.optimizationsandtweaks.mixins.common.goblins;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.goblins.GOBLINWorldGenFireplaceTwo;
import goblin.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(GOBLINWorldGenFireplace.class)
public class MixinGOBLINWorldGenFireplace {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random rand, int i, int j, int k) {
        if (GOBLINWorldGenFireplaceTwo.canGenerateFireplace(world, i, j, k)) {
            GOBLINWorldGenFireplaceTwo.func_76484_a(world, rand, i, j, k);
            return true;
        }
        return false;
    }
}
