package fr.iamacat.optimizationsandtweaks.mixins.common.lotofthings;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.superdextor.LOT.LOTGeneration;
import com.superdextor.LOT.init.LOTBlocks;
import com.superdextor.LOT.worldgen.GenRainbowBox;
import com.superdextor.LOT.worldgen.GenRainbowFlowers;

@Mixin(LOTGeneration.class)
public class MixinLOTGeneration {

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGenerate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider, CallbackInfo ci) {
        if (world.isRemote || !world.getChunkProvider()
            .chunkExists(chunkX, chunkZ)) {
            ci.cancel();
            return;
        }
    }

    @Overwrite(remap = false)
    public void generateOverworld(World world, Random rand, int x, int z) {
        int xCh = x + rand.nextInt(16);
        int yCh = rand.nextInt(100);
        int zCh = z + rand.nextInt(16);
        GenRainbowFlowers rainbowflower = new GenRainbowFlowers(1);
        GenRainbowBox rainbowbox = new GenRainbowBox(Blocks.grass);
        rainbowflower.func_76484_a(world, rand, xCh, yCh, zCh);
        rainbowbox.func_76484_a(world, rand, xCh, yCh, zCh);
        this.generateOre(LOTBlocks.copper_ore, world, rand, x, z, 4, 12, 24, 0, 89, Blocks.stone);
        this.generateOre(LOTBlocks.ruby_ore, world, rand, x, z, 3, 8, 1, 0, 14, Blocks.stone);
    }

    @Shadow
    public void generateOre(Block block, World world, Random random, int chunkX, int chunkZ, int minVienSize,
        int maxVienSize, int chance, int minY, int maxY, Block generateIn) {

    }
}
