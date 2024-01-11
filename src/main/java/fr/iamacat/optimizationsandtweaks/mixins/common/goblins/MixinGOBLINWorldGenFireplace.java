package fr.iamacat.optimizationsandtweaks.mixins.common.goblins;

import fr.iamacat.optimizationsandtweaks.utilsformods.goblins.GOBLINWorldGenFireplaceTwo;
import goblin.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

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
