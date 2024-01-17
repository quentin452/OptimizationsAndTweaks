package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer {

    @Shadow
    /** The size of the texture file's width in pixels. */
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
    public boolean mirror;
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
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void render(float partialTicks) {
        if (this.isHidden || !this.showModel) {
            return;
        }

        if (!this.compiled) {
            this.compileDisplayList(partialTicks);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);

        if (this.rotateAngleX != 0.0F || this.rotateAngleY != 0.0F || this.rotateAngleZ != 0.0F) {
            GL11.glPushMatrix();
            GL11.glTranslatef(
                this.rotationPointX * partialTicks,
                this.rotationPointY * partialTicks,
                this.rotationPointZ * partialTicks);

            if (this.rotateAngleZ != 0.0F) {
                GL11.glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
                GL11.glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
                GL11.glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            }

            GL11.glCallList(this.displayList);

            if (this.childModels != null) {
                for (Object childModel : this.childModels) {
                    ((ModelRenderer) childModel).render(partialTicks);
                }
            }

            GL11.glPopMatrix();

        } else {
            if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                GL11.glTranslatef(
                    this.rotationPointX * partialTicks,
                    this.rotationPointY * partialTicks,
                    this.rotationPointZ * partialTicks);
            }

            GL11.glCallList(this.displayList);

            if (this.childModels != null) {
                for (Object childModel : this.childModels) {
                    ((ModelRenderer) childModel).render(partialTicks);
                }
            }

            if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                GL11.glTranslatef(
                    -this.rotationPointX * partialTicks,
                    -this.rotationPointY * partialTicks,
                    -this.rotationPointZ * partialTicks);
            }

        }

        GL11.glPopMatrix();
        GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);

    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void renderWithRotation(float p_78791_1_) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(p_78791_1_);
            }

            if (this.rotateAngleY != 0.0F || this.rotateAngleX != 0.0F || this.rotateAngleZ != 0.0F) {
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
            } else {
                GL11.glPushMatrix();
                GL11.glTranslatef(
                    this.rotationPointX * p_78791_1_,
                    this.rotationPointY * p_78791_1_,
                    this.rotationPointZ * p_78791_1_);
                GL11.glCallList(this.displayList);
                GL11.glPopMatrix();
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void compileDisplayList(float p_78788_1_) {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        for (Object o : this.cubeList) {
            ((ModelBox) o).render(tessellator, p_78788_1_);
        }

        GL11.glEndList();
        this.compiled = true;
    }
}
