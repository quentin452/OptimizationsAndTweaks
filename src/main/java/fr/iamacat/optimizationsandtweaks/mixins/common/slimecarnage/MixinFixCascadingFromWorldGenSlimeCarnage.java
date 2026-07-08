package fr.iamacat.optimizationsandtweaks.mixins.common.slimecarnage;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraft.world.biome.BiomeGenPlains;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.IWorldGenerator;
import supremopete.SlimeCarnage.worldgen.*;

/**
 * Fixes some cascading worldgen caused by WorldGenSlimeCarnage from Slime Carnage Mod.
 */
@Mixin(WorldGenSlimeCarnage.class)
public class MixinFixCascadingFromWorldGenSlimeCarnage implements IWorldGenerator {

    // generate: kept as @Shadow (stub body) purely so this class still satisfies IWorldGenerator at
    // compile time - the two @Inject points below instrument the ORIGINAL (no longer @Overwrite'n)
    // method body.
    @Shadow
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {}

    // generate: the ORIGINAL SlimeCarnage WorldGenSlimeCarnage#generate (verified against the decompiled
    // 1.0.5d jar) is a `switch` on dimensionId with NO `break` statements, so it falls through: e.g. in
    // the Nether (dim -1) it calls generateNether() [no-op, always empty in the original] AND THEN falls
    // into generateSurface() [real ruins/sewers/madlab/tomb generation] AND generateEnd() [no-op] - i.e.
    // the original mod incorrectly also runs the overworld surface generator in the Nether. The OaT delta
    // is exactly the missing `break` statements (stop after the matching case). No RNG is consumed by the
    // dispatcher itself, so this is converted to two unconditional @Inject(shift=AFTER)+cancel points
    // right after the generateNether()/generateSurface() calls - reproducing "stop after this branch"
    // without touching generateNether/generateEnd (still @Shadow, both always-empty in the real mod) or
    // generateSurface (kept @Overwrite below, unrelated rewrite). The last case (generateEnd) needs no
    // inject since nothing follows it in the switch.
    @Inject(
        method = "generate",
        at = @At(
            value = "INVOKE",
            target = "Lsupremopete/SlimeCarnage/worldgen/WorldGenSlimeCarnage;generateNether(Lnet/minecraft/world/World;Ljava/util/Random;II)V",
            shift = At.Shift.AFTER),
        cancellable = true,
        remap = false)
    private void optimizationsAndTweaks$stopAfterNether(Random random, int chunkX, int chunkZ, World world,
        IChunkProvider chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(
        method = "generate",
        at = @At(
            value = "INVOKE",
            target = "Lsupremopete/SlimeCarnage/worldgen/WorldGenSlimeCarnage;generateSurface(Lnet/minecraft/world/World;Ljava/util/Random;II)V",
            shift = At.Shift.AFTER),
        cancellable = true,
        remap = false)
    private void optimizationsAndTweaks$stopAfterSurface(Random random, int chunkX, int chunkZ, World world,
        IChunkProvider chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void generateSurface(World world, Random random, int chunkX, int chunkZ) {
        if (optimizationsAndTweaks$generateCave(world, random, chunkX, chunkZ)) {
            return;
        }

        int biomeCheck = optimizationsAndTweaks$getBiomeCheck(world, random, chunkX, chunkZ);
        if (biomeCheck == 1) {
            optimizationsAndTweaks$generateDesertRuins(world, random, chunkX, chunkZ);
        } else if (biomeCheck == 2) {
            optimizationsAndTweaks$generateStoneRuins(world, random, chunkX, chunkZ);
        }

        if (optimizationsAndTweaks$generateMadLab(random)) {
            optimizationsAndTweaks$generateMadLab(world, random, chunkX, chunkZ);
        }

        if (biomeCheck == 1 && optimizationsAndTweaks$generateDesertTomb(random)) {
            optimizationsAndTweaks$generateDesertTomb(world, random, chunkX, chunkZ);
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$generateCave(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord2 = chunkX + rand.nextInt(16);
        int Ycoord2 = 64 + rand.nextInt(6);
        int Zcoord2 = chunkZ + rand.nextInt(16);

        return new WorldGenSewers().func_76484_a(world, rand, Xcoord2, Ycoord2, Zcoord2);
    }

    @Unique
    private int optimizationsAndTweaks$getBiomeCheck(World world, Random rand, int chunkX, int chunkZ) {
        BiomeGenBase biomegenbase = world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16);
        if (biomegenbase instanceof BiomeGenDesert) {
            return rand.nextInt(10);
        } else if (biomegenbase instanceof BiomeGenPlains) {
            return rand.nextInt(4);
        }
        return 0;
    }

    @Unique
    private void optimizationsAndTweaks$generateDesertRuins(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord4 = chunkX + rand.nextInt(16);
        int scrub4 = 66 + rand.nextInt(12);
        int Xcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenDesertRuins()).func_76484_a(world, rand, Xcoord4, scrub4, Xcoord5);
    }

    @Unique
    private void optimizationsAndTweaks$generateStoneRuins(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord4 = chunkX + rand.nextInt(16);
        int Xcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenStoneRuins()).func_76484_a(world, rand, Xcoord4, 66 + rand.nextInt(12), Xcoord5);
    }

    @Unique
    private boolean optimizationsAndTweaks$generateMadLab(Random rand) {
        return rand.nextInt(16) == 0;
    }

    @Unique
    private void optimizationsAndTweaks$generateMadLab(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord4 = chunkX + rand.nextInt(16);
        int scrub5 = 66 + rand.nextInt(6);
        int Xcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenMadLab()).func_76484_a(world, rand, Xcoord4, scrub5, Xcoord5);
    }

    @Unique
    private boolean optimizationsAndTweaks$generateDesertTomb(Random rand) {
        return rand.nextInt(10) == 0;
    }

    @Unique
    private void optimizationsAndTweaks$generateDesertTomb(World world, Random rand, int chunkX, int chunkZ) {
        int Xcoord5 = chunkX + rand.nextInt(16);
        int Ycoord5 = 64 + rand.nextInt(8);
        int Zcoord5 = chunkZ + rand.nextInt(16);
        (new WorldGenDesertTomb()).func_76484_a(world, rand, Xcoord5, Ycoord5, Zcoord5);
    }

    @Shadow
    private void generateNether(World world, Random random, int chunkX, int chunkZ) {}

    @Shadow
    private void generateEnd(World world, Random random, int chunkX, int chunkZ) {}
}
