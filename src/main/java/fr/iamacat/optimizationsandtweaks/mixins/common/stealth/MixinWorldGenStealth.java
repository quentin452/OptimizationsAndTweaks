package fr.iamacat.optimizationsandtweaks.mixins.common.stealth;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Kills StealthMod's cascading worldgen — the ACTUAL cost behind its 44-52 ms/chunk (82% of all
 * IWorldGenerator time), measured after the triple-registration fix proved the duplicates were cheap
 * (0.07 ms) and the FIRST pass paid everything.
 * <p>
 * {@code addOre} places veins at {@code posX + rand(16)} with NO +8 offset, so {@code WorldGenMinable}
 * (spread up to ~8 blocks) writes into the -x/-z NEIGHBOR chunks and forces their generation inside
 * this generator's call — the classic missing-offset cascade (docs/08 pattern). The measured cost
 * profile proves it: pass 1 = 52 ms (pays neighbor terrain gen), passes 2-3 = 0.07 ms (neighbors
 * already exist).
 * <p>
 * This replaces the loop with the vanilla-standard {@code +8} window offset: veins start inside
 * [x+8, x+24) and even at max spread stay within the 2x2 already-generated populate window — zero
 * forced neighbor generation. Same vein counts/sizes/levels and same RNG draw pattern; ore positions
 * shift by 8 blocks (statistically identical density).
 * <p>
 * The mod compiles against SRG at runtime, so the injected method is targeted by its plain (mod-own)
 * name with {@code remap = false}; the body is normal OaT code, reobfuscated with the mod.
 */
@Mixin(targets = "com.stealth.mod.init.objects.world.WorldGenStealth", remap = false)
public class MixinWorldGenStealth {

    @Inject(method = "addOre", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$addOreInPopulateWindow(Block block, Block blockSpawn, Random random,
        World world, int posX, int posZ, int minY, int maxY, int minV, int maxV, int spawnChance, CallbackInfo ci) {
        for (int i = 0; i < spawnChance; ++i) {
            int x = posX + 8 + random.nextInt(16);
            int y = minY + random.nextInt(maxY - minY);
            int z = posZ + 8 + random.nextInt(16);
            new WorldGenMinable(block, maxV).generate(world, random, x, y, z);
        }
        ci.cancel();
    }
}
