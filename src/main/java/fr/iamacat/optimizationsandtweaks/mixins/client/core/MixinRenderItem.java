package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import java.util.Objects;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mixin(RenderItem.class)
public abstract class MixinRenderItem extends Render {

    @Shadow
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation(
        "textures/misc/enchanted_item_glint.png");
    @Shadow
    private RenderBlocks renderBlocksRi = new RenderBlocks();
    /** The RNG used in RenderItem (for bobbing itemstacks on the ground) */
    @Shadow
    private Random random = new Random();
    @Shadow
    public boolean renderWithColor = true;
    /** Defines the zLevel of rendering of item on GUI. */
    @Shadow
    public float zLevel;
    @Shadow
    public static boolean renderInFrame;

    /**
     * Renders the item's icon or block into the UI at the specified position.
     */
    @Overwrite
    public void renderItemIntoGUI(FontRenderer p_77015_1_, TextureManager p_77015_2_, ItemStack p_77015_3_,
        int p_77015_4_, int p_77015_5_) {
        this.renderItemIntoGUI(p_77015_1_, p_77015_2_, p_77015_3_, p_77015_4_, p_77015_5_, false);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void renderItemIntoGUI(FontRenderer p_77015_1_, TextureManager p_77015_2_, ItemStack p_77015_3_,
        int p_77015_4_, int p_77015_5_, boolean renderEffect) {
        int k = p_77015_3_.getItemDamage();
        IIcon object = p_77015_3_.getIconIndex();
        int l;
        float f;
        float f3;
        float f4;

        if (p_77015_3_.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(
            Block.getBlockFromItem(p_77015_3_.getItem())
                .getRenderType())) {
            p_77015_2_.bindTexture(TextureMap.locationBlocksTexture);
            Block block = Block.getBlockFromItem(p_77015_3_.getItem());
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            if (block.getRenderBlockPass() != 0) {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            } else {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
                GL11.glDisable(GL11.GL_BLEND);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef((p_77015_4_ - 2), (p_77015_5_ + 3), -3.0F + this.zLevel);
            GL11.glScalef(10.0F, 10.0F, 10.0F);
            GL11.glTranslatef(1.0F, 0.5F, 1.0F);
            GL11.glScalef(1.0F, 1.0F, -1.0F);
            GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            l = Objects.requireNonNull(p_77015_3_.getItem())
                .getColorFromItemStack(p_77015_3_, 0);
            f3 = (l >> 16 & 255) / 255.0F;
            f4 = (l >> 8 & 255) / 255.0F;
            f = (l & 255) / 255.0F;

            if (this.renderWithColor) {
                GL11.glColor4f(f3, f4, f, 1.0F);
            }

            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            this.renderBlocksRi.useInventoryTint = this.renderWithColor;
            this.renderBlocksRi.renderBlockAsItem(block, k, 1.0F);
            this.renderBlocksRi.useInventoryTint = true;

            if (block.getRenderBlockPass() == 0) {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            }

            GL11.glPopMatrix();
        } else if (Objects.requireNonNull(p_77015_3_.getItem())
            .requiresMultipleRenderPasses()) {
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                p_77015_2_.bindTexture(TextureMap.locationItemsTexture);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(0, 0, 0, 0);
                GL11.glColorMask(false, false, false, true);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                tessellator.setColorOpaque_I(-1);
                tessellator.addVertex(p_77015_4_ - 2, p_77015_5_ + 18, this.zLevel);
                tessellator.addVertex(p_77015_4_ + 18, p_77015_5_ + 18, this.zLevel);
                tessellator.addVertex(p_77015_4_ + 18, p_77015_5_ - 2, this.zLevel);
                tessellator.addVertex(p_77015_4_ - 2, p_77015_5_ - 2, this.zLevel);
                tessellator.draw();
                GL11.glColorMask(true, true, true, true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                Item item = p_77015_3_.getItem();
                for (l = 0; l < item.getRenderPasses(k); ++l) {
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    p_77015_2_.bindTexture(
                        item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture
                            : TextureMap.locationItemsTexture);
                    IIcon iicon = item.getIcon(p_77015_3_, l);
                    int i1 = p_77015_3_.getItem()
                        .getColorFromItemStack(p_77015_3_, l);
                    f = (i1 >> 16 & 255) / 255.0F;
                    float f1 = (i1 >> 8 & 255) / 255.0F;
                    float f2 = (i1 & 255) / 255.0F;

                    if (this.renderWithColor) {
                        GL11.glColor4f(f, f1, f2, 1.0F);
                    }

                    GL11.glDisable(GL11.GL_LIGHTING); // Forge: Make sure that render states are reset, ad renderEffect
                                                      // can derp them up.
                    GL11.glEnable(GL11.GL_ALPHA_TEST);

                    this.renderIcon(p_77015_4_, p_77015_5_, iicon, 16, 16);

                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_LIGHTING);

                    if (renderEffect && p_77015_3_.hasEffect(l)) {
                        renderEffect(p_77015_2_, p_77015_4_, p_77015_5_);
                    }
                }

                GL11.glEnable(GL11.GL_LIGHTING);
            } else {
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                ResourceLocation resourcelocation = p_77015_2_.getResourceLocation(p_77015_3_.getItemSpriteNumber());
                p_77015_2_.bindTexture(resourcelocation);

                if (object == null) {
                    object = ((TextureMap) Minecraft.getMinecraft()
                        .getTextureManager()
                        .getTexture(resourcelocation)).getAtlasSprite("missingno");
                }

                l = p_77015_3_.getItem()
                    .getColorFromItemStack(p_77015_3_, 0);
                f3 = (l >> 16 & 255) / 255.0F;
                f4 = (l >> 8 & 255) / 255.0F;
                f = (l & 255) / 255.0F;

                if (this.renderWithColor) {
                    GL11.glColor4f(f3, f4, f, 1.0F);
                }

                GL11.glDisable(GL11.GL_LIGHTING); // Forge: Make sure that render states are reset, a renderEffect can
                                                  // derp them up.
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);

                this.renderIcon(p_77015_4_, p_77015_5_, object, 16, 16);

                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_BLEND);

                if (renderEffect && p_77015_3_.hasEffect(0)) {
                    renderEffect(p_77015_2_, p_77015_4_, p_77015_5_);
                }
                GL11.glEnable(GL11.GL_LIGHTING);
            }

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    /**
     * @author
     * @reason
     */
    @SuppressWarnings("unused")
    @Overwrite
    public void renderItemAndEffectIntoGUI(FontRenderer p_82406_1_, TextureManager p_82406_2_,
        final ItemStack p_82406_3_, int p_82406_4_, int p_82406_5_) {
        if (p_82406_3_ != null) {
            this.zLevel += 50.0F;

            try {
                if (!ForgeHooksClient.renderInventoryItem(
                    this.field_147909_c,
                    p_82406_2_,
                    p_82406_3_,
                    renderWithColor,
                    zLevel,
                    (float) p_82406_4_,
                    (float) p_82406_5_)) {
                    this.renderItemIntoGUI(p_82406_1_, p_82406_2_, p_82406_3_, p_82406_4_, p_82406_5_, true);
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
                crashreportcategory.addCrashSectionCallable("Item Type", () -> String.valueOf(p_82406_3_.getItem()));
                crashreportcategory
                    .addCrashSectionCallable("Item Aux", () -> String.valueOf(p_82406_3_.getItemDamage()));
                crashreportcategory
                    .addCrashSectionCallable("Item NBT", () -> String.valueOf(p_82406_3_.getTagCompound()));
                crashreportcategory.addCrashSectionCallable("Item Foil", () -> String.valueOf(p_82406_3_.hasEffect()));
                throw new ReportedException(crashreport);
            }

            // Forge: Bugfix, Move this to a per-render pass, modders must handle themselves
            if (false && p_82406_3_.hasEffect()) {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDepthMask(false);
                p_82406_2_.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
                this.renderGlint(
                    p_82406_4_ * 431278612 + p_82406_5_ * 32178161,
                    p_82406_4_ - 2,
                    p_82406_5_ - 2,
                    20,
                    20);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }

            this.zLevel -= 50.0F;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderIcon(int p_94149_1_, int p_94149_2_, IIcon p_94149_3_, int p_94149_4_, int p_94149_5_) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(
            (p_94149_1_),
            (p_94149_2_ + p_94149_5_),
            this.zLevel,
            p_94149_3_.getMinU(),
            p_94149_3_.getMaxV());
        tessellator.addVertexWithUV(
            (p_94149_1_ + p_94149_4_),
            (p_94149_2_ + p_94149_5_),
            this.zLevel,
            p_94149_3_.getMaxU(),
            p_94149_3_.getMaxV());
        tessellator.addVertexWithUV(
            (p_94149_1_ + p_94149_4_),
            (p_94149_2_),
            this.zLevel,
            p_94149_3_.getMaxU(),
            p_94149_3_.getMinV());
        tessellator
            .addVertexWithUV((p_94149_1_), (p_94149_2_), this.zLevel, p_94149_3_.getMinU(), p_94149_3_.getMinV());
        tessellator.draw();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void renderGlint(int p_77018_1_, int p_77018_2_, int p_77018_3_, int p_77018_4_, int p_77018_5_) {
        OpenGlHelper.glBlendFunc(772, 1, 0, 0);
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        long currentTime = Minecraft.getSystemTime();
        float f4 = 4.0F;

        for (int j1 = 0; j1 < 2; ++j1) {
            float f3 = 0.0F;
            float f2 = (currentTime % (3000 + j1 * 1873)) / (3000.0F + (j1 * 1873)) * 256.0F;

            if (j1 == 1) {
                f4 = -1.0F;
            }

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(
                (p_77018_2_),
                (p_77018_3_ + p_77018_5_),
                this.zLevel,
                ((f2 + p_77018_5_ * f) * f),
                ((f3 + p_77018_5_) * f1));
            tessellator.addVertexWithUV(
                (p_77018_2_ + p_77018_4_),
                (p_77018_3_ + p_77018_5_),
                this.zLevel,
                ((f2 + p_77018_4_ + p_77018_5_ * f4) * f),
                ((f3 + p_77018_5_) * f1));
            tessellator.addVertexWithUV(
                (p_77018_2_ + p_77018_4_),
                (p_77018_3_),
                this.zLevel,
                ((f2 + p_77018_4_) * f),
                ((f3 + 0.0F) * f1));
            tessellator.addVertexWithUV((p_77018_2_), (p_77018_3_), this.zLevel, ((f2 + 0.0F) * f), ((f3 + 0.0F) * f1));
            tessellator.draw();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void renderEffect(TextureManager manager, int x, int y) {
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        manager.bindTexture(RES_ITEM_GLINT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
        this.renderGlint(x * 431278612 + y * 32178161, x - 2, y - 2, 20, 20);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }
}
