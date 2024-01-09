package fr.iamacat.optimizationsandtweaks.mixins.common.minefactoryreloaded;

import java.util.Random;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minefactoryreloaded.WorldGenLakesMeta2;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import powercrystals.minefactoryreloaded.world.WorldGenLakesMeta;

@Mixin(WorldGenLakesMeta.class)
public class MixinFixWorldGenLakesMetaCascadingWorldgenLag extends WorldGenerator {
    @Shadow
    private Block _block;
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        WorldGenLakesMeta2 worldGenLakesMeta2 = new WorldGenLakesMeta2(_block);
        worldGenLakesMeta2.generate(world,random,x,y,z);
        return true;
    }
}
