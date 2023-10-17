package fr.iamacat.multithreading.mixins.client.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SideOnly(Side.CLIENT)
@Mixin(EntityFX.class)
public class MixinEntityFX extends Entity
{
    @Shadow
    protected int particleTextureIndexX;
    @Shadow
    protected int particleTextureIndexY;
    @Shadow
    protected float particleTextureJitterX;
    @Shadow
    protected float particleTextureJitterY;
    @Shadow
    protected int particleAge;
    @Shadow
    protected int particleMaxAge;
    @Shadow
    protected float particleScale;
    @Shadow
    protected float particleGravity;
    /** The red amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
    @Shadow
    protected float particleRed;
    /** The green amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
    @Shadow
    protected float particleGreen;
    /** The blue amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
    @Shadow
    protected float particleBlue;
    /** Particle alpha */
    @Shadow
    protected float particleAlpha;
    /** The icon field from which the given particle pulls its texture. */
    @Shadow
    protected IIcon particleIcon;
    @Shadow
    public static double interpPosX;
    @Shadow
    public static double interpPosY;
    @Shadow
    public static double interpPosZ;

    protected MixinEntityFX(World p_i1218_1_, double p_i1218_2_, double p_i1218_4_, double p_i1218_6_)
    {
        super(p_i1218_1_);
        this.particleAlpha = 1.0F;
        this.setSize(0.2F, 0.2F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(p_i1218_2_, p_i1218_4_, p_i1218_6_);
        this.lastTickPosX = p_i1218_2_;
        this.lastTickPosY = p_i1218_4_;
        this.lastTickPosZ = p_i1218_6_;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
        this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
        this.particleAge = 0;
    }

    public MixinEntityFX(World p_i1219_1_, double p_i1219_2_, double p_i1219_4_, double p_i1219_6_, double p_i1219_8_, double p_i1219_10_, double p_i1219_12_)
    {
        this(p_i1219_1_, p_i1219_2_, p_i1219_4_, p_i1219_6_);
        this.motionX = p_i1219_8_ + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        this.motionY = p_i1219_10_ + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        this.motionZ = p_i1219_12_ + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
        float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
        float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double)f1 * (double)f * 0.4000000059604645D;
        this.motionY = this.motionY / (double)f1 * (double)f * 0.4000000059604645D + 0.10000000149011612D;
        this.motionZ = this.motionZ / (double)f1 * (double)f * 0.4000000059604645D;
    }
    @Unique
    public MixinEntityFX multiplyVelocity(float p_70543_1_)
    {
        this.motionX *= p_70543_1_;
        this.motionY = (this.motionY - 0.10000000149011612D) * (double)p_70543_1_ + 0.10000000149011612D;
        this.motionZ *= p_70543_1_;
        return this;
    }
    @Unique
    public void setRBGColorF(float p_70538_1_, float p_70538_2_, float p_70538_3_)
    {
        this.particleRed = p_70538_1_;
        this.particleGreen = p_70538_2_;
        this.particleBlue = p_70538_3_;
    }

    /**
     * Sets the particle alpha (float)
     */
    @Unique
    public void setAlphaF(float p_82338_1_)
    {
        this.particleAlpha = p_82338_1_;
    }
    @Unique
    public float getRedColorF()
    {
        return this.particleRed;
    }

    @Unique
    public float getGreenColorF()
    {
        return this.particleGreen;
    }

    @Unique
    public float getBlueColorF()
    {
        return this.particleBlue;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit() {}

    /**
     * Called to update the entity's position/logic.
     */
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.motionY -= 0.04D * (double)this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
    /**
     * @author
     * @reason
     */

    @Overwrite
    public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_)
    {
        float uMin = this.particleIcon != null ? this.particleIcon.getMinU() : this.particleTextureIndexX / 16.0F;
        float uMax = this.particleIcon != null ? this.particleIcon.getMaxU() : uMin + 0.0624375F;
        float vMin = this.particleIcon != null ? this.particleIcon.getMinV() : this.particleTextureIndexY / 16.0F;
        float vMax = this.particleIcon != null ? this.particleIcon.getMaxV() : vMin + 0.0624375F;

        float scale = 0.1F * this.particleScale;

        float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)p_70539_2_ - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)p_70539_2_ - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_70539_2_ - interpPosZ);

        p_70539_1_.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);

        float x1 = x - p_70539_3_ * scale - p_70539_6_ * scale;
        float x2 = x - p_70539_3_ * scale + p_70539_6_ * scale;
        float x3 = x + p_70539_3_ * scale + p_70539_6_ * scale;
        float x4 = x + p_70539_3_ * scale - p_70539_6_ * scale;

        float y1 = y - p_70539_4_ * scale;
        float y2 = y + p_70539_4_ * scale;

        float z1 = z - p_70539_5_ * scale - p_70539_7_ * scale;
        float z2 = z - p_70539_5_ * scale + p_70539_7_ * scale;
        float z3 = z + p_70539_5_ * scale + p_70539_7_ * scale;
        float z4 = z + p_70539_5_ * scale - p_70539_7_ * scale;

        p_70539_1_.addVertexWithUV(x1, y1, z1, uMax, vMax);
        p_70539_1_.addVertexWithUV(x2, y2, z2, uMax, vMin);
        p_70539_1_.addVertexWithUV(x3, y2, z3, uMin, vMin);
        p_70539_1_.addVertexWithUV(x4, y1, z4, uMin, vMax);
    }

    public int getFXLayer()
    {
        return 0;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound) {}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund) {}

    @Unique
    public void setParticleIcon(IIcon p_110125_1_)
    {
        if (this.getFXLayer() == 1)
        {
            this.particleIcon = p_110125_1_;
        }
        else
        {
            if (this.getFXLayer() != 2)
            {
                throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
            }

            this.particleIcon = p_110125_1_;
        }
    }

    /**
     * Public method to set private field particleTextureIndex.
     */
    @Unique
    public void setParticleTextureIndex(int p_70536_1_)
    {
        if (this.getFXLayer() != 0)
        {
            throw new RuntimeException("Invalid call to Particle.setMiscTex");
        }
        else
        {
            this.particleTextureIndexX = p_70536_1_ % 16;
            this.particleTextureIndexY = p_70536_1_ / 16;
        }
    }

    @Unique
    public void nextTextureIndexX()
    {
        ++this.particleTextureIndexX;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }

    public String toString()
    {
        return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.particleAge;
    }
}
