package fr.iamacat.optimizationsandtweaks.mixins.common.goblins;

import java.util.Random;

import fr.iamacat.optimizationsandtweaks.utilsformods.goblins.GOBLINWorldGenGVillagetwo;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import goblin.*;

@Mixin(GOBLINWorldGenGVillage1.class)
public class MixinGOBLINWorldGenGVillage1 extends GOBLINWorldGen {

    @Shadow
    int houseLoc1;
    @Shadow
    int houseLoc2;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random rand, int i, int j, int k) {
        if (GOBLINWorldGenGVillagetwo.canGenerateVillage(world, i, j, k)) {
            GOBLINWorldGenGVillagetwo.func_76484_a(world, rand, i, j, k);
        }
        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean canGenerate(World world, Random rand, int i, int j, int k) {
        int countGrass = 0;
        for (int i1 = 0; i1 <= 20; ++i1) {
            for (int k1 = 0; k1 <= 30; ++k1) {
                for (int j1 = -1; j1 <= 1; ++j1) {
                    if (world.getBlock(i + i1, j + j1, k + k1) == Blocks.grass) {
                        if (j1 == 1) {
                            ++countGrass;
                        } else {
                            countGrass += 2;
                        }
                    }
                }
            }
        }
        return countGrass > 1100;
    }
}
