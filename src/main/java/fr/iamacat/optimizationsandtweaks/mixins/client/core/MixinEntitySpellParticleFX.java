package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.config.MultithreadingandtweaksConfig;

@Mixin(EntitySpellParticleFX.class)
public class MixinEntitySpellParticleFX extends EntityFX {

    /** Base spell texture index */
    @Unique
    private int multithreadingandtweaks$baseSpellTextureIndex = 128;

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

    /**
     * @author
     * @reason
     */

    @Overwrite
    public void renderParticle(Tessellator p_70539_1, float p_70539_2, float p_70539_3, float p_70539_4,
        float p_70539_5, float p_70539_6, float p_70539_7) {
        super.renderParticle(p_70539_1, p_70539_2, p_70539_3, p_70539_4, p_70539_5, p_70539_6, p_70539_7);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Overwrite
    public void onUpdate() {
        if (MultithreadingandtweaksConfig.enableMixinRenderManager) {

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (this.particleAge++ >= this.particleMaxAge) {
                this.setDead();
            }

            this.setParticleTextureIndex(
                this.multithreadingandtweaks$baseSpellTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
            this.motionY += 0.004D;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.posY == this.prevPosY) {
                this.motionX *= 1.1D;
                this.motionZ *= 1.1D;
            }

            this.motionX *= 0.9599999785423279D;
            this.motionY *= 0.9599999785423279D;
            this.motionZ *= 0.9599999785423279D;

            if (this.onGround) {
                this.motionX *= 0.699999988079071D;
                this.motionZ *= 0.699999988079071D;
            }
        }
    }

    /**
     * Sets the base spell texture index
     */
    @Unique
    public void multithreadingandtweaks$setBaseSpellTextureIndex(int p_70589_1_) {
        this.multithreadingandtweaks$baseSpellTextureIndex = p_70589_1_;
    }
}
