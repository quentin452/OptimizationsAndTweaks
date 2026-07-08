package fr.iamacat.optimizationsandtweaks.mixins.common.minestones;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.sinkillerj.minestones.MSEvents;

import fr.iamacat.optimizationsandtweaks.utilsformods.minestones.Patcher;

/**
 * Minestones' {@code MSEvents#onEntityDrop} rolls its stone-drop chance as an integer "1 in X"
 * ({@code MSUtils.randomInt(1, MSConfig.stoneDropRate) == 1}). OaT's config rewrite
 * ({@link fr.iamacat.optimizationsandtweaks.mixins.common.minestones.MixinMSConfig}) replaced that
 * integer rate with a decimal probability stored in {@link Patcher#stoneDropRate}, so the roll itself
 * has to change from an integer draw to a direct probability comparison.
 * <p>
 * This redirects the single {@code MSUtils.randomInt(int,int)} call to that comparison, discarding its
 * original (now-unused) arguments; the rest of the method (mob type, damage-source, {@code FakePlayer}
 * checks and the drop itself) is untouched original bytecode.
 *
 * @author OptimizationsAndTweaks
 * @reason Convert the whole-method @Overwrite (decimal stone-drop-rate feature) to a surgical @Redirect
 *         on the one RNG call it actually changes.
 */
@Mixin(MSEvents.class)
public class MixinMSEvents {

    @Unique
    private static final Random optimizationsandtweaks$rand = new Random();

    @Redirect(
        method = "onEntityDrop",
        at = @At(value = "INVOKE", target = "Lcom/sinkillerj/minestones/MSUtils;randomInt(II)I"),
        remap = false)
    private static int optimizationsandtweaks$rollDecimalStoneDropRate(int min, int max) {
        return optimizationsandtweaks$rand.nextDouble() <= Patcher.stoneDropRate ? 1 : 0;
    }
}
