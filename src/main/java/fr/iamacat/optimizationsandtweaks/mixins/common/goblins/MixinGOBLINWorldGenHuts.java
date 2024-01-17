package fr.iamacat.optimizationsandtweaks.mixins.common.goblins;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import fr.iamacat.optimizationsandtweaks.utilsformods.goblins.GOBLINWorldGenHutsTwo;
import goblin.GOBLINWorldGen;
import goblin.GOBLINWorldGenHuts;

@Mixin(GOBLINWorldGenHuts.class)
public class MixinGOBLINWorldGenHuts extends GOBLINWorldGen {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random rand, int i, int j, int k) {
        if (GOBLINWorldGenHutsTwo.canGenerateHuts(world, i, j, k)) {
            GOBLINWorldGenHutsTwo.func_76484_a(world, rand, i, j, k);
            return true;
        }
        return false;
    }
}
