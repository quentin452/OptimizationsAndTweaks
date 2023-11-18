package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer {
    @Unique
    private ModelRenderer multithreadingandtweaks$modelRenderer;
    /** The size of the texture file's width in pixels. */
    @Shadow
    public float textureWidth;
    /** The size of the texture file's height in pixels. */
    @Shadow
    public float textureHeight;
    /** The X offset into the texture used for displaying this model */
    @Shadow
    private int textureOffsetX;
    /** The Y offset into the texture used for displaying this model */
    @Shadow
    private int textureOffsetY;
    @Shadow
    public float rotationPointX;
    @Shadow
    public float rotationPointY;
    @Shadow
    public float rotationPointZ;
    @Shadow
    public float rotateAngleX;
    @Shadow
    public float rotateAngleY;
    @Shadow
    public float rotateAngleZ;
    @Shadow
    private boolean compiled;
    /** The GL display list rendered by the Tessellator for this model */
    @Shadow
    private int displayList;
    @Shadow
    public boolean showModel;

    /** Hides the model. */
    @Shadow
    public boolean isHidden;
    @Shadow
    public List cubeList;
    @Shadow
    public List childModels;
    @Shadow
    public final String boxName;
    @Shadow
    private ModelBase baseModel;
    @Shadow
    public float offsetX;
    @Shadow
    public float offsetY;
    @Shadow
    public float offsetZ;

    public MixinModelRenderer(ModelBase p_i1172_1_, String p_i1172_2_) {
        this.textureWidth = 64.0F;
        this.textureHeight = 32.0F;
        this.showModel = true;
        this.cubeList = new ArrayList();
        this.baseModel = p_i1172_1_;
        p_i1172_1_.boxList.add(this);
        this.boxName = p_i1172_2_;
        this.setTextureSize(p_i1172_1_.textureWidth, p_i1172_1_.textureHeight);
    }

    public MixinModelRenderer(ModelBase p_i1173_1_) {
        this(p_i1173_1_, null);
    }

    public MixinModelRenderer(ModelBase p_i1174_1_, int p_i1174_2_, int p_i1174_3_) {
        this(p_i1174_1_);
        this.setTextureOffset(p_i1174_2_, p_i1174_3_);
    }

    /**
     * Sets the current box's rotation points and rotation angles to another box.
     */
    public void addChild(ModelRenderer p_78792_1_) {
        if (this.childModels == null) {
            this.childModels = new ArrayList();
        }

        this.childModels.add(p_78792_1_);
    }

    @Unique
    public ModelRenderer setTextureOffset(int p_78784_1_, int p_78784_2_) {
        this.textureOffsetX = p_78784_1_;
        this.textureOffsetY = p_78784_2_;
        return multithreadingandtweaks$modelRenderer;
    }

    @Unique
    public ModelRenderer addBox(String p_78786_1_, float p_78786_2_, float p_78786_3_, float p_78786_4_, int p_78786_5_,
        int p_78786_6_, int p_78786_7_) {
        p_78786_1_ = this.boxName + "." + p_78786_1_;
        TextureOffset textureoffset = this.baseModel.getTextureOffset(p_78786_1_);
        this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
        this.cubeList.add(
            (new ModelBox(
                multithreadingandtweaks$modelRenderer,
                this.textureOffsetX,
                this.textureOffsetY,
                p_78786_2_,
                p_78786_3_,
                p_78786_4_,
                p_78786_5_,
                p_78786_6_,
                p_78786_7_,
                0.0F)).func_78244_a(p_78786_1_));
        return multithreadingandtweaks$modelRenderer;
    }

    @Unique
    public ModelRenderer addBox(float p_78789_1_, float p_78789_2_, float p_78789_3_, int p_78789_4_, int p_78789_5_,
        int p_78789_6_) {
        this.cubeList.add(
            new ModelBox(
                multithreadingandtweaks$modelRenderer,
                this.textureOffsetX,
                this.textureOffsetY,
                p_78789_1_,
                p_78789_2_,
                p_78789_3_,
                p_78789_4_,
                p_78789_5_,
                p_78789_6_,
                0.0F));
        return multithreadingandtweaks$modelRenderer;
    }

    /**
     * Creates a textured box. Args: originX, originY, originZ, width, height, depth, scaleFactor.
     */
    @Unique
    public void addBox(float p_78790_1_, float p_78790_2_, float p_78790_3_, int p_78790_4_, int p_78790_5_,
        int p_78790_6_, float p_78790_7_) {
        this.cubeList.add(
            new ModelBox(
                multithreadingandtweaks$modelRenderer,
                this.textureOffsetX,
                this.textureOffsetY,
                p_78790_1_,
                p_78790_2_,
                p_78790_3_,
                p_78790_4_,
                p_78790_5_,
                p_78790_6_,
                p_78790_7_));
    }

    @Unique
    public void setRotationPoint(float p_78793_1_, float p_78793_2_, float p_78793_3_) {
        this.rotationPointX = p_78793_1_;
        this.rotationPointY = p_78793_2_;
        this.rotationPointZ = p_78793_3_;
    }

    /**
     * @author
     * @reason
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public void render(float partialTicks) {
        if (this.isHidden || !this.showModel) {
            return;
        }

        if (!this.compiled) {
            this.compileDisplayList(partialTicks);
        }

        GL11.glPushMatrix();

        GL11.glTranslatef(
            (this.rotationPointX - this.offsetX) * partialTicks,
            (this.rotationPointY - this.offsetY) * partialTicks,
            (this.rotationPointZ - this.offsetZ) * partialTicks);

        float angleX = this.rotateAngleX + MathHelper.sin(System.currentTimeMillis() * 0.1F) * 0.1F;
        float angleY = this.rotateAngleY + MathHelper.cos(System.currentTimeMillis() * 0.15F) * 0.1F;
        float angleZ = this.rotateAngleZ;

        if (angleX != 0.0F) {
            GL11.glRotatef(angleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
        }
        if (angleY != 0.0F) {
            GL11.glRotatef(angleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        }
        if (angleZ != 0.0F) {
            GL11.glRotatef(angleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
        }
        if (this.displayList == 0) {
            this.displayList = GL11.glGenLists(1);
            GL11.glNewList(this.displayList, GL11.GL_COMPILE);
            GL11.glEndList();
        }

        GL11.glCallList(this.displayList);
        optimizationsAndTweaks$renderChildModels(partialTicks);

        GL11.glPopMatrix();
    }

    @Unique
    private void optimizationsAndTweaks$renderChildModels(float p_78785_1_) {
        if (this.childModels != null) {
            for (ModelRenderer childModel : (List<ModelRenderer>) this.childModels) {
                childModel.render(p_78785_1_);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public void renderWithRotation(float p_78791_1_) {
            if (!this.isHidden && (this.showModel)) {
                    if (!this.compiled) {
                        this.compileDisplayList(p_78791_1_);
                    }

                    GL11.glPushMatrix();
                    GL11.glTranslatef(
                        this.rotationPointX * p_78791_1_,
                        this.rotationPointY * p_78791_1_,
                        this.rotationPointZ * p_78791_1_);

                    if (this.rotateAngleY != 0.0F) {
                        GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F) {
                        GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                    }

                    if (this.rotateAngleZ != 0.0F) {
                        GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    GL11.glCallList(this.displayList);
                    GL11.glPopMatrix();
        }
    }

    /**
     * Allows the changing of Angles after a box has been rendered
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public void postRender(float p_78794_1_) {
            if (!this.isHidden && (this.showModel)) {
                    if (!this.compiled) {
                        this.compileDisplayList(p_78794_1_);
                    }

                    if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                        if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                            GL11.glTranslatef(
                                this.rotationPointX * p_78794_1_,
                                this.rotationPointY * p_78794_1_,
                                this.rotationPointZ * p_78794_1_);
                        }
                    } else {
                        GL11.glTranslatef(
                            this.rotationPointX * p_78794_1_,
                            this.rotationPointY * p_78794_1_,
                            this.rotationPointZ * p_78794_1_);

                        if (this.rotateAngleZ != 0.0F) {
                            GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                        }

                        if (this.rotateAngleY != 0.0F) {
                            GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                        }

                        if (this.rotateAngleX != 0.0F) {
                            GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                        }
                    }
        }
    }

    /**
     * Compiles a GL display list for this model
     */
    @Unique
    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float p_78788_1_) {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        for (Object o : this.cubeList) {
            ((ModelBox) o).render(tessellator, p_78788_1_);
        }

        GL11.glEndList();
        this.compiled = true;
    }

    /**
     * Returns the model renderer with the new texture parameters.
     */
    @Unique
    public ModelRenderer setTextureSize(int p_78787_1_, int p_78787_2_) {
        this.textureWidth = p_78787_1_;
        this.textureHeight = p_78787_2_;
        return multithreadingandtweaks$modelRenderer;
    }
}
