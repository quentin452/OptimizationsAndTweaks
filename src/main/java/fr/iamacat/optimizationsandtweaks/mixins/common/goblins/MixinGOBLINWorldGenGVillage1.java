package fr.iamacat.optimizationsandtweaks.mixins.common.goblins;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import fr.iamacat.optimizationsandtweaks.utilsformods.goblins.GOBLINWorldGenGVillagetwo;
import goblin.*;

/**
 * Fixes cascading worldgens caused by GOBLINWorldGenGVillage1 class from Goblin mod.
 */
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

    // canGenerate: no longer @Overwrite'n here - the OaT body was byte-for-byte identical to the
    // original GOBLINWorldGenGVillage1#canGenerate (pure dead dupe, verified against decompiled
    // goblins_mod_6.0), so it was deleted; the vanilla bytecode (unchanged) now runs directly.
}
