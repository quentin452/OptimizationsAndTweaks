package fr.iamacat.optimizationsandtweaks.mixins.common.core.entity;

import java.util.Random;

import net.minecraft.entity.passive.EntityAnimal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Optimizes EntityAnimal. The only real delta vs vanilla {@code onLivingUpdate} is that the "in love"
 * particle-spawn cosmetics draw from ONE shared {@link Random} instead of each animal's own {@code rand}
 * field; redirecting every {@code this.rand} read in that method to the shared instance lets the rest of
 * the original (untouched) vanilla body run unmodified. Purely cosmetic particle RNG (position jitter),
 * no gameplay/determinism impact.
 */
@Mixin(EntityAnimal.class)
public class MixinEntityAnimal {

    @Unique
    private static final Random optimizationsAndTweaks$random = new Random();

    @Redirect(
        method = "onLivingUpdate",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rand:Ljava/util/Random;"),
        remap = false)
    private Random optimizationsAndTweaks$sharedRandom(EntityAnimal instance) {
        return optimizationsAndTweaks$random;
    }
}
