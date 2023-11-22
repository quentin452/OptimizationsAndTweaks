package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.Random;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;

@SideOnly(Side.CLIENT)
@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Shadow
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    @Shadow
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    @Shadow
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    /** A reference to the Minecraft object. */
    @Shadow
    private Minecraft mc;
    @Shadow
    private ItemStack itemToRender;
    /** How far the current item has been equipped (0 disequipped and 1 fully up) */
    @Shadow
    private float equippedProgress;
    @Shadow
    private float prevEquippedProgress;
    @Shadow
    private RenderBlocks renderBlocksIr = new RenderBlocks();
    /** The index of the currently held item (0-8, or -1 if not yet updated) */
    @Shadow
    private int equippedItemSlot = -1;

    /**
     * Renders the item stack for being in an entity's hand Args: itemStack
     */
    @Overwrite
    public void renderItem(EntityLivingBase p_78443_1_, ItemStack p_78443_2_, int p_78443_3_)
    {
        this.renderItem(p_78443_1_, p_78443_2_, p_78443_3_, EQUIPPED);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void renderItem(EntityLivingBase p_78443_1_, ItemStack p_78443_2_, int p_78443_3_, IItemRenderer.ItemRenderType type)
    {
        GL11.glPushMatrix();
        TextureManager texturemanager = this.mc.getTextureManager();
        Item item = p_78443_2_.getItem();
        Block block = Block.getBlockFromItem(item);

        if (block != null && block.getRenderBlockPass() != 0)
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(p_78443_2_, type);
        if (customRenderer != null)
        {
            texturemanager.bindTexture(texturemanager.getResourceLocation(p_78443_2_.getItemSpriteNumber()));
            ForgeHooksClient.renderEquippedItem(type, customRenderer, renderBlocksIr, p_78443_1_, p_78443_2_);
        }
        else
        if (p_78443_2_.getItemSpriteNumber() == 0 && item instanceof ItemBlock && RenderBlocks.renderItemIn3d(Objects.requireNonNull(block).getRenderType()))
        {
            texturemanager.bindTexture(texturemanager.getResourceLocation(0));

            if (block.getRenderBlockPass() != 0)
            {
                GL11.glDepthMask(false);
                this.renderBlocksIr.renderBlockAsItem(block, p_78443_2_.getItemDamage(), 1.0F);
                GL11.glDepthMask(true);
            }
            else
            {
                this.renderBlocksIr.renderBlockAsItem(block, p_78443_2_.getItemDamage(), 1.0F);
            }
        }
        else
        {
            IIcon iicon = p_78443_1_.getItemIcon(p_78443_2_, p_78443_3_);

            if (iicon == null)
            {
                GL11.glPopMatrix();
                return;
            }

            texturemanager.bindTexture(texturemanager.getResourceLocation(p_78443_2_.getItemSpriteNumber()));
            TextureUtil.func_152777_a(false, false, 1.0F);
            Tessellator tessellator = Tessellator.instance;
            float f = iicon.getMinU();
            float f1 = iicon.getMaxU();
            float f2 = iicon.getMinV();
            float f3 = iicon.getMaxV();
            float f4 = 0.0F;
            float f5 = 0.3F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(-f4, -f5, 0.0F);
            float f6 = 1.5F;
            GL11.glScalef(f6, f6, f6);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            renderItemIn2D(tessellator, f1, f2, f, f3, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);

            if (p_78443_2_.hasEffect(p_78443_3_))
            {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                texturemanager.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(768, 1, 1, 0);
                float f7 = 0.76F;
                GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            texturemanager.bindTexture(texturemanager.getResourceLocation(p_78443_2_.getItemSpriteNumber()));
            TextureUtil.func_147945_b();
        }

        if (p_78443_2_ != null && block != null && block.getRenderBlockPass() != 0)
        {
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glPopMatrix();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void renderItemIn2D(Tessellator p_78439_0_, float p_78439_1_, float p_78439_2_, float p_78439_3_, float p_78439_4_, int p_78439_5_, int p_78439_6_, float p_78439_7_)
    {
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(0.0F, 0.0F, 1.0F);
        p_78439_0_.addVertexWithUV(0.0D, 0.0D, 0.0D, p_78439_1_, p_78439_4_);
        p_78439_0_.addVertexWithUV(1.0D, 0.0D, 0.0D, p_78439_3_, p_78439_4_);
        p_78439_0_.addVertexWithUV(1.0D, 1.0D, 0.0D, p_78439_3_, p_78439_2_);
        p_78439_0_.addVertexWithUV(0.0D, 1.0D, 0.0D, p_78439_1_, p_78439_2_);
        p_78439_0_.draw();
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(0.0F, 0.0F, -1.0F);
        p_78439_0_.addVertexWithUV(0.0D, 1.0D, 0.0F - p_78439_7_, p_78439_1_, p_78439_2_);
        p_78439_0_.addVertexWithUV(1.0D, 1.0D, 0.0F - p_78439_7_, p_78439_3_, p_78439_2_);
        p_78439_0_.addVertexWithUV(1.0D, 0.0D, 0.0F - p_78439_7_, p_78439_3_, p_78439_4_);
        p_78439_0_.addVertexWithUV(0.0D, 0.0D, 0.0F - p_78439_7_, p_78439_1_, p_78439_4_);
        p_78439_0_.draw();
        float f5 = 0.5F * (p_78439_1_ - p_78439_3_) / p_78439_5_;
        float f6 = 0.5F * (p_78439_4_ - p_78439_2_) / p_78439_6_;
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(-1.0F, 0.0F, 0.0F);
        int k;
        float f7;
        float f8;

        for (k = 0; k < p_78439_5_; ++k)
        {
            f7 = (float)k / (float)p_78439_5_;
            f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
            p_78439_0_.addVertexWithUV(f7, 0.0D, 0.0F - p_78439_7_, f8, p_78439_4_);
            p_78439_0_.addVertexWithUV(f7, 0.0D, 0.0D, f8, p_78439_4_);
            p_78439_0_.addVertexWithUV(f7, 1.0D, 0.0D, f8, p_78439_2_);
            p_78439_0_.addVertexWithUV(f7, 1.0D, 0.0F - p_78439_7_, f8, p_78439_2_);
        }

        p_78439_0_.draw();
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(1.0F, 0.0F, 0.0F);
        float f9;

        for (k = 0; k < p_78439_5_; ++k)
        {
            f7 = (float)k / (float)p_78439_5_;
            f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
            f9 = f7 + 1.0F / p_78439_5_;
            p_78439_0_.addVertexWithUV(f9, 1.0D, 0.0F - p_78439_7_, f8, p_78439_2_);
            p_78439_0_.addVertexWithUV(f9, 1.0D, 0.0D, f8, p_78439_2_);
            p_78439_0_.addVertexWithUV(f9, 0.0D, 0.0D, f8, p_78439_4_);
            p_78439_0_.addVertexWithUV(f9, 0.0D, 0.0F - p_78439_7_, f8, p_78439_4_);
        }

        p_78439_0_.draw();
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(0.0F, 1.0F, 0.0F);

        for (k = 0; k < p_78439_6_; ++k)
        {
            f7 = (float)k / (float)p_78439_6_;
            f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
            f9 = f7 + 1.0F / p_78439_6_;
            p_78439_0_.addVertexWithUV(0.0D, f9, 0.0D, p_78439_1_, f8);
            p_78439_0_.addVertexWithUV(1.0D, f9, 0.0D, p_78439_3_, f8);
            p_78439_0_.addVertexWithUV(1.0D, f9, 0.0F - p_78439_7_, p_78439_3_, f8);
            p_78439_0_.addVertexWithUV(0.0D, f9, 0.0F - p_78439_7_, p_78439_1_, f8);
        }

        p_78439_0_.draw();
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(0.0F, -1.0F, 0.0F);

        for (k = 0; k < p_78439_6_; ++k)
        {
            f7 = (float)k / (float)p_78439_6_;
            f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
            p_78439_0_.addVertexWithUV(1.0D, f7, 0.0D, p_78439_3_, f8);
            p_78439_0_.addVertexWithUV(0.0D, f7, 0.0D, p_78439_1_, f8);
            p_78439_0_.addVertexWithUV(0.0D, f7, 0.0F - p_78439_7_, p_78439_1_, f8);
            p_78439_0_.addVertexWithUV(1.0D, f7, 0.0F - p_78439_7_, p_78439_3_, f8);
        }

        p_78439_0_.draw();
    }

}
