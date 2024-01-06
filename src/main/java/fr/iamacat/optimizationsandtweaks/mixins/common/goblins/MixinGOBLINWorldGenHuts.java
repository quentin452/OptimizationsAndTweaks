package fr.iamacat.optimizationsandtweaks.mixins.common.goblins;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.goblins.GOBLINWorldGenHutsTwo;
import goblin.GOBLINWorldGen;
import goblin.GOBLINWorldGenHuts;
import goblin.mod_Goblins;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

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
