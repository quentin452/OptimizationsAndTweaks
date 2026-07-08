package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Optimizes EntitySpellParticleFX.
 * <p>
 * {@code renderParticle} was dropped 2026-07-08: vanilla computes a local clamp variable that is never
 * actually passed to the {@code super.renderParticle} call it makes (dead code in the vanilla method
 * itself) -- the mixin's copy already just called super directly, so deleting it is a no-op (vanilla
 * body now applies, same dead computation, same visible result).
 * <p>
 * {@code onUpdate} keeps its own {@code optimizationsAndTweaks$baseSpellTextureIndex} field (instead of
 * vanilla's {@code baseSpellTextureIndex}, which {@link #optimizationsAndTweaks$setBaseSpellTextureIndex}
 * writes to) as the ONLY delta vs vanilla -- redirecting that single field read preserves the existing
 * behavior while letting the rest of the original (untouched) method run.
 */
@Mixin(EntitySpellParticleFX.class)
public class MixinEntitySpellParticleFX extends EntityFX {

    /** Base spell texture index */
    @Unique
    private int optimizationsAndTweaks$baseSpellTextureIndex = 128;

    public MixinEntitySpellParticleFX(World p_i1229_1_, double p_i1229_2_, double p_i1229_4_, double p_i1229_6_,
        double p_i1229_8_, double p_i1229_10_, double p_i1229_12_) {
        super(p_i1229_1_, p_i1229_2_, p_i1229_4_, p_i1229_6_, p_i1229_8_, p_i1229_10_, p_i1229_12_);
        this.motionY *= 0.20000000298023224D;

        if (p_i1229_8_ == 0.0D && p_i1229_12_ == 0.0D) {
            this.motionX *= 0.10000000149011612D;
            this.motionZ *= 0.10000000149011612D;
        }

        this.particleScale *= 0.75F;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.noClip = false;
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/particle/EntitySpellParticleFX;baseSpellTextureIndex:I"),
        remap = false)
    private int optimizationsAndTweaks$readBaseSpellTextureIndex(EntitySpellParticleFX instance) {
        return this.optimizationsAndTweaks$baseSpellTextureIndex;
    }

    /**
     * Sets the base spell texture index
     */
    @Unique
    public void optimizationsAndTweaks$setBaseSpellTextureIndex(int p_70589_1_) {
        this.optimizationsAndTweaks$baseSpellTextureIndex = p_70589_1_;
    }
}
