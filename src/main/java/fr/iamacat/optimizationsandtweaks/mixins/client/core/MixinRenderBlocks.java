package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {
    /** The IBlockAccess used by this instance of RenderBlocks */
    @Shadow
    public IBlockAccess blockAccess;
    /** If set to >=0, all block faces will be rendered using this texture index */
    @Shadow
    public IIcon overrideBlockTexture;
    /** Set to true if the texture should be flipped horizontally during render*Face */
    @Shadow
    public boolean flipTexture;
    @Shadow
    public boolean field_152631_f;

    /** If true, renders all faces on all blocks rather than using the logic in Block.shouldSideBeRendered. */
    @Shadow
    public boolean renderAllFaces;
    /** Fancy grass side matching biome */
    @Shadow
    public static boolean fancyGrass = true;
    @Shadow
    public boolean useInventoryTint = true;
    @Shadow
    public boolean renderFromInside = false;
    /** The minimum X value for rendering (default 0.0). */
    @Shadow
    public double renderMinX;
    /** The maximum X value for rendering (default 1.0). */
    @Shadow
    public double renderMaxX;
    /** The minimum Y value for rendering (default 0.0). */
    @Shadow
    public double renderMinY;
    /** The maximum Y value for rendering (default 1.0). */
    @Shadow
    public double renderMaxY;
    /** The minimum Z value for rendering (default 0.0). */
    @Shadow
    public double renderMinZ;
    /** The maximum Z value for rendering (default 1.0). */
    @Shadow
    public double renderMaxZ;
    @Shadow
    public boolean lockBlockBounds;
    @Shadow
    public boolean partialRenderBounds;
    @Shadow
    public final Minecraft minecraftRB;
    @Shadow
    public int uvRotateEast;
    @Shadow
    public int uvRotateWest;
    @Shadow
    public int uvRotateSouth;
    @Shadow
    public int uvRotateNorth;
    @Shadow
    public int uvRotateTop;
    @Shadow
    public int uvRotateBottom;
    /** Whether ambient occlusion is enabled or not */
    @Shadow

    public boolean enableAO;
    /** Used as a scratch variable for ambient occlusion on the north/bottom/east corner. */
    @Shadow
    public float aoLightValueScratchXYZNNN;
    /** Used as a scratch variable for ambient occlusion between the bottom face and the north face. */
    @Shadow
    public float aoLightValueScratchXYNN;
    /** Used as a scratch variable for ambient occlusion on the north/bottom/west corner. */
    @Shadow
    public float aoLightValueScratchXYZNNP;
    /** Used as a scratch variable for ambient occlusion between the bottom face and the east face. */
    @Shadow
    public float aoLightValueScratchYZNN;
    /** Used as a scratch variable for ambient occlusion between the bottom face and the west face. */
    @Shadow
    public float aoLightValueScratchYZNP;
    /** Used as a scratch variable for ambient occlusion on the south/bottom/east corner. */
    @Shadow
    public float aoLightValueScratchXYZPNN;
    /** Used as a scratch variable for ambient occlusion between the bottom face and the south face. */
    @Shadow
    public float aoLightValueScratchXYPN;
    /** Used as a scratch variable for ambient occlusion on the south/bottom/west corner. */
    @Shadow
    public float aoLightValueScratchXYZPNP;
    /** Used as a scratch variable for ambient occlusion on the north/top/east corner. */
    @Shadow
    public float aoLightValueScratchXYZNPN;
    /** Used as a scratch variable for ambient occlusion between the top face and the north face. */
    @Shadow
    public float aoLightValueScratchXYNP;
    /** Used as a scratch variable for ambient occlusion on the north/top/west corner. */
    @Shadow
    public float aoLightValueScratchXYZNPP;
    /** Used as a scratch variable for ambient occlusion between the top face and the east face. */
    @Shadow
    public float aoLightValueScratchYZPN;
    /** Used as a scratch variable for ambient occlusion on the south/top/east corner. */
    @Shadow
    public float aoLightValueScratchXYZPPN;
    /** Used as a scratch variable for ambient occlusion between the top face and the south face. */
    @Shadow
    public float aoLightValueScratchXYPP;
    /** Used as a scratch variable for ambient occlusion between the top face and the west face. */
    @Shadow
    public float aoLightValueScratchYZPP;

    /** Used as a scratch variable for ambient occlusion on the south/top/west corner. */
    @Shadow
    public float aoLightValueScratchXYZPPP;
    /** Used as a scratch variable for ambient occlusion between the north face and the east face. */
    @Shadow
    public float aoLightValueScratchXZNN;
    /** Used as a scratch variable for ambient occlusion between the south face and the east face. */
    @Shadow
    public float aoLightValueScratchXZPN;
    /** Used as a scratch variable for ambient occlusion between the north face and the west face. */
    @Shadow
    public float aoLightValueScratchXZNP;
    /** Used as a scratch variable for ambient occlusion between the south face and the west face. */
    @Shadow
    public float aoLightValueScratchXZPP;
    /** Ambient occlusion brightness XYZNNN */
    @Shadow
    public int aoBrightnessXYZNNN;
    /** Ambient occlusion brightness XYNN */
    @Shadow
    public int aoBrightnessXYNN;
    /** Ambient occlusion brightness XYZNNP */
    @Shadow
    public int aoBrightnessXYZNNP;

    /** Ambient occlusion brightness YZNN */
    @Shadow
    public int aoBrightnessYZNN;
    /** Ambient occlusion brightness YZNP */
    @Shadow
    public int aoBrightnessYZNP;
    /** Ambient occlusion brightness XYZPNN */
    @Shadow
    public int aoBrightnessXYZPNN;
    /** Ambient occlusion brightness XYPN */
    @Shadow
    public int aoBrightnessXYPN;
    /** Ambient occlusion brightness XYZPNP */
    @Shadow
    public int aoBrightnessXYZPNP;
    /** Ambient occlusion brightness XYZNPN */
    @Shadow
    public int aoBrightnessXYZNPN;
    /** Ambient occlusion brightness XYNP */
    @Shadow
    public int aoBrightnessXYNP;
    /** Ambient occlusion brightness XYZNPP */
    @Shadow
    public int aoBrightnessXYZNPP;
    /** Ambient occlusion brightness YZPN */
    @Shadow
    public int aoBrightnessYZPN;
    /** Ambient occlusion brightness XYZPPN */
    @Shadow
    public int aoBrightnessXYZPPN;
    /** Ambient occlusion brightness XYPP */
    @Shadow
    public int aoBrightnessXYPP;

    /** Ambient occlusion brightness YZPP */
    @Shadow
    public int aoBrightnessYZPP;
    /** Ambient occlusion brightness XYZPPP */
    @Shadow
    public int aoBrightnessXYZPPP;
    /** Ambient occlusion brightness XZNN */
    @Shadow
    public int aoBrightnessXZNN;
    /** Ambient occlusion brightness XZPN */
    @Shadow
    public int aoBrightnessXZPN;
    /** Ambient occlusion brightness XZNP */
    @Shadow
    public int aoBrightnessXZNP;
    /** Ambient occlusion brightness XZPP */
    @Shadow
    public int aoBrightnessXZPP;
    /** Brightness top left */
    @Shadow
    public int brightnessTopLeft;

    /** Brightness bottom left */
    @Shadow
    public int brightnessBottomLeft;
    /** Brightness bottom right */
    @Shadow
    public int brightnessBottomRight;
    /** Brightness top right */
    @Shadow
    public int brightnessTopRight;
    /** Red color value for the top left corner */
    @Shadow
    public float colorRedTopLeft;
    /** Red color value for the bottom left corner */
    @Shadow
    public float colorRedBottomLeft;
    /** Red color value for the bottom right corner */
    @Shadow
    public float colorRedBottomRight;
    /** Red color value for the top right corner */
    @Shadow
    public float colorRedTopRight;
    /** Green color value for the top left corner */
    @Shadow
    public float colorGreenTopLeft;
    /** Green color value for the bottom left corner */
    @Shadow
    public float colorGreenBottomLeft;
    /** Green color value for the bottom right corner */
    @Shadow
    public float colorGreenBottomRight;
    /** Green color value for the top right corner */
    @Shadow
    public float colorGreenTopRight;
    /** Blue color value for the top left corner */
    @Shadow
    public float colorBlueTopLeft;
    /** Blue color value for the bottom left corner */
    @Shadow
    public float colorBlueBottomLeft;
    /** Blue color value for the bottom right corner */
    @Shadow
    public float colorBlueBottomRight;
    /** Blue color value for the top right corner */
    @Shadow
    public float colorBlueTopRight;

    public MixinRenderBlocks(Minecraft minecraftRB) {
        this.minecraftRB = Minecraft.getMinecraft();
    }
    // todo refactor
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean renderStandardBlockWithAmbientOcclusionPartial(Block p_147808_1_, int p_147808_2_, int p_147808_3_, int p_147808_4_, float p_147808_5_, float p_147808_6_, float p_147808_7_)
    {
        this.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (this.getBlockIcon(p_147808_1_).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (this.hasOverrideBlockTexture())
        {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        int i1;
        float f7;

        if (this.renderAllFaces || p_147808_1_.shouldSideBeRendered(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_, 0))
        {
            if (this.renderMinY <= 0.0D)
            {
                --p_147808_3_;
            }

            this.aoBrightnessXYNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            this.aoBrightnessYZNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            this.aoBrightnessYZNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            this.aoBrightnessXYPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            this.aoLightValueScratchXYNN = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ + 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYPN = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            flag2 = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1).getCanBlockGrass();

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1);
            }

            if (this.renderMinY <= 0.0D)
            {
                ++p_147808_3_;
            }

            i1 = l;

            if (this.renderMinY <= 0.0D || !this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_).isOpaqueCube())
            {
                i1 = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            }

            f7 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_).getAmbientOcclusionLightValue();
            f3 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + f7 + this.aoLightValueScratchYZNN) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.5F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYNeg(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, this.getBlockIcon(p_147808_1_, this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_, 0));
            flag = true;
        }

        if (this.renderAllFaces || p_147808_1_.shouldSideBeRendered(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_, 1))
        {
            if (this.renderMaxY >= 1.0D)
            {
                ++p_147808_3_;
            }

            this.aoBrightnessXYNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            this.aoBrightnessXYPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            this.aoBrightnessYZPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            this.aoBrightnessYZPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            this.aoLightValueScratchXYNP = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYPP = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ + 1).getAmbientOcclusionLightValue();
            flag2 = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1).getCanBlockGrass();

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1);
            }

            if (this.renderMaxY >= 1.0D)
            {
                --p_147808_3_;
            }

            i1 = l;

            if (this.renderMaxY >= 1.0D || !this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_).isOpaqueCube())
            {
                i1 = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            }

            f7 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_).getAmbientOcclusionLightValue();
            f6 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (this.aoLightValueScratchYZPP + f7 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, i1);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_;
            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYPos(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, this.getBlockIcon(p_147808_1_, this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_, 1));
            flag = true;
        }

        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;
        IIcon iicon;

        if (this.renderAllFaces || p_147808_1_.shouldSideBeRendered(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ - 1, 2))
        {
            if (this.renderMinZ <= 0.0D)
            {
                --p_147808_4_;
            }

            this.aoLightValueScratchXZNN = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPN = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoBrightnessXZNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            this.aoBrightnessYZNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            this.aoBrightnessYZPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            this.aoBrightnessXZPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            flag2 = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1).getCanBlockGrass();

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_);
            }

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_);
            }

            if (this.renderMinZ <= 0.0D)
            {
                ++p_147808_4_;
            }

            i1 = l;

            if (this.renderMinZ <= 0.0D || !this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ - 1).isOpaqueCube())
            {
                i1 = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            }

            f7 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ - 1).getAmbientOcclusionLightValue();
            f8 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            f9 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
            f10 = (this.aoLightValueScratchYZNN + f7 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
            f11 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + f7) / 4.0F;
            f3 = (float)((double)f8 * this.renderMaxY * (1.0D - this.renderMinX) + (double)f9 * this.renderMaxY * this.renderMinX + (double)f10 * (1.0D - this.renderMaxY) * this.renderMinX + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinX));
            f4 = (float)((double)f8 * this.renderMaxY * (1.0D - this.renderMaxX) + (double)f9 * this.renderMaxY * this.renderMaxX + (double)f10 * (1.0D - this.renderMaxY) * this.renderMaxX + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX));
            f5 = (float)((double)f8 * this.renderMinY * (1.0D - this.renderMaxX) + (double)f9 * this.renderMinY * this.renderMaxX + (double)f10 * (1.0D - this.renderMinY) * this.renderMaxX + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxX));
            f6 = (float)((double)f8 * this.renderMinY * (1.0D - this.renderMinX) + (double)f9 * this.renderMinY * this.renderMinX + (double)f10 * (1.0D - this.renderMinY) * this.renderMinX + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMinX));
            j1 = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            k1 = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, i1);
            l1 = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, i1);
            this.brightnessTopLeft = this.mixAoBrightness(j1, k1, l1, i2, this.renderMaxY * (1.0D - this.renderMinX), this.renderMaxY * this.renderMinX, (1.0D - this.renderMaxY) * this.renderMinX, (1.0D - this.renderMaxY) * (1.0D - this.renderMinX));
            this.brightnessBottomLeft = this.mixAoBrightness(j1, k1, l1, i2, this.renderMaxY * (1.0D - this.renderMaxX), this.renderMaxY * this.renderMaxX, (1.0D - this.renderMaxY) * this.renderMaxX, (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX));
            this.brightnessBottomRight = this.mixAoBrightness(j1, k1, l1, i2, this.renderMinY * (1.0D - this.renderMaxX), this.renderMinY * this.renderMaxX, (1.0D - this.renderMinY) * this.renderMaxX, (1.0D - this.renderMinY) * (1.0D - this.renderMaxX));
            this.brightnessTopRight = this.mixAoBrightness(j1, k1, l1, i2, this.renderMinY * (1.0D - this.renderMinX), this.renderMinY * this.renderMinX, (1.0D - this.renderMinY) * this.renderMinX, (1.0D - this.renderMinY) * (1.0D - this.renderMinX));

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(p_147808_1_, this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_, 2);
            this.renderFaceZNeg(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147808_5_;
                this.colorRedBottomLeft *= p_147808_5_;
                this.colorRedBottomRight *= p_147808_5_;
                this.colorRedTopRight *= p_147808_5_;
                this.colorGreenTopLeft *= p_147808_6_;
                this.colorGreenBottomLeft *= p_147808_6_;
                this.colorGreenBottomRight *= p_147808_6_;
                this.colorGreenTopRight *= p_147808_6_;
                this.colorBlueTopLeft *= p_147808_7_;
                this.colorBlueBottomLeft *= p_147808_7_;
                this.colorBlueBottomRight *= p_147808_7_;
                this.colorBlueTopRight *= p_147808_7_;
                this.renderFaceZNeg(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || p_147808_1_.shouldSideBeRendered(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ + 1, 3))
        {
            if (this.renderMaxZ >= 1.0D)
            {
                ++p_147808_4_;
            }

            this.aoLightValueScratchXZNP = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPP = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoBrightnessXZNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            this.aoBrightnessXZPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            this.aoBrightnessYZNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            this.aoBrightnessYZPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            flag2 = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1).getCanBlockGrass();

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_);
            }

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_);
            }

            if (this.renderMaxZ >= 1.0D)
            {
                --p_147808_4_;
            }

            i1 = l;

            if (this.renderMaxZ >= 1.0D || !this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ + 1).isOpaqueCube())
            {
                i1 = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            }

            f7 = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ + 1).getAmbientOcclusionLightValue();
            f8 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + f7 + this.aoLightValueScratchYZPP) / 4.0F;
            f9 = (f7 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f10 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
            f11 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + f7) / 4.0F;
            f3 = (float)((double)f8 * this.renderMaxY * (1.0D - this.renderMinX) + (double)f9 * this.renderMaxY * this.renderMinX + (double)f10 * (1.0D - this.renderMaxY) * this.renderMinX + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinX));
            f4 = (float)((double)f8 * this.renderMinY * (1.0D - this.renderMinX) + (double)f9 * this.renderMinY * this.renderMinX + (double)f10 * (1.0D - this.renderMinY) * this.renderMinX + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMinX));
            f5 = (float)((double)f8 * this.renderMinY * (1.0D - this.renderMaxX) + (double)f9 * this.renderMinY * this.renderMaxX + (double)f10 * (1.0D - this.renderMinY) * this.renderMaxX + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxX));
            f6 = (float)((double)f8 * this.renderMaxY * (1.0D - this.renderMaxX) + (double)f9 * this.renderMaxY * this.renderMaxX + (double)f10 * (1.0D - this.renderMaxY) * this.renderMaxX + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX));
            j1 = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, i1);
            k1 = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, i1);
            l1 = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, i1);
            this.brightnessTopLeft = this.mixAoBrightness(j1, i2, l1, k1, this.renderMaxY * (1.0D - this.renderMinX), (1.0D - this.renderMaxY) * (1.0D - this.renderMinX), (1.0D - this.renderMaxY) * this.renderMinX, this.renderMaxY * this.renderMinX);
            this.brightnessBottomLeft = this.mixAoBrightness(j1, i2, l1, k1, this.renderMinY * (1.0D - this.renderMinX), (1.0D - this.renderMinY) * (1.0D - this.renderMinX), (1.0D - this.renderMinY) * this.renderMinX, this.renderMinY * this.renderMinX);
            this.brightnessBottomRight = this.mixAoBrightness(j1, i2, l1, k1, this.renderMinY * (1.0D - this.renderMaxX), (1.0D - this.renderMinY) * (1.0D - this.renderMaxX), (1.0D - this.renderMinY) * this.renderMaxX, this.renderMinY * this.renderMaxX);
            this.brightnessTopRight = this.mixAoBrightness(j1, i2, l1, k1, this.renderMaxY * (1.0D - this.renderMaxX), (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX), (1.0D - this.renderMaxY) * this.renderMaxX, this.renderMaxY * this.renderMaxX);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(p_147808_1_, this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_, 3);
            this.renderFaceZPos(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147808_5_;
                this.colorRedBottomLeft *= p_147808_5_;
                this.colorRedBottomRight *= p_147808_5_;
                this.colorRedTopRight *= p_147808_5_;
                this.colorGreenTopLeft *= p_147808_6_;
                this.colorGreenBottomLeft *= p_147808_6_;
                this.colorGreenBottomRight *= p_147808_6_;
                this.colorGreenTopRight *= p_147808_6_;
                this.colorBlueTopLeft *= p_147808_7_;
                this.colorBlueBottomLeft *= p_147808_7_;
                this.colorBlueBottomRight *= p_147808_7_;
                this.colorBlueTopRight *= p_147808_7_;
                this.renderFaceZPos(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || p_147808_1_.shouldSideBeRendered(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_, 4))
        {
            if (this.renderMinX <= 0.0D)
            {
                --p_147808_2_;
            }

            this.aoLightValueScratchXYNN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZNN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZNP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ + 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYNP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoBrightnessXYNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            this.aoBrightnessXZNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            this.aoBrightnessXZNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            this.aoBrightnessXYNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            flag2 = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_ + 1, p_147808_4_).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_ - 1, p_147808_4_).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_ - 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_ + 1).getCanBlockGrass();

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1);
            }

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1);
            }

            if (this.renderMinX <= 0.0D)
            {
                ++p_147808_2_;
            }

            i1 = l;

            if (this.renderMinX <= 0.0D || !this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_).isOpaqueCube())
            {
                i1 = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ - 1, p_147808_3_, p_147808_4_);
            }

            f7 = this.blockAccess.getBlock(p_147808_2_ - 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            f8 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + f7 + this.aoLightValueScratchXZNP) / 4.0F;
            f9 = (f7 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
            f10 = (this.aoLightValueScratchXZNN + f7 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
            f11 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + f7) / 4.0F;
            f3 = (float)((double)f9 * this.renderMaxY * this.renderMaxZ + (double)f10 * this.renderMaxY * (1.0D - this.renderMaxZ) + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ) + (double)f8 * (1.0D - this.renderMaxY) * this.renderMaxZ);
            f4 = (float)((double)f9 * this.renderMaxY * this.renderMinZ + (double)f10 * this.renderMaxY * (1.0D - this.renderMinZ) + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ) + (double)f8 * (1.0D - this.renderMaxY) * this.renderMinZ);
            f5 = (float)((double)f9 * this.renderMinY * this.renderMinZ + (double)f10 * this.renderMinY * (1.0D - this.renderMinZ) + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMinZ) + (double)f8 * (1.0D - this.renderMinY) * this.renderMinZ);
            f6 = (float)((double)f9 * this.renderMinY * this.renderMaxZ + (double)f10 * this.renderMinY * (1.0D - this.renderMaxZ) + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ) + (double)f8 * (1.0D - this.renderMinY) * this.renderMaxZ);
            j1 = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, i1);
            k1 = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, i1);
            l1 = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, i1);
            this.brightnessTopLeft = this.mixAoBrightness(k1, l1, i2, j1, this.renderMaxY * this.renderMaxZ, this.renderMaxY * (1.0D - this.renderMaxZ), (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ), (1.0D - this.renderMaxY) * this.renderMaxZ);
            this.brightnessBottomLeft = this.mixAoBrightness(k1, l1, i2, j1, this.renderMaxY * this.renderMinZ, this.renderMaxY * (1.0D - this.renderMinZ), (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ), (1.0D - this.renderMaxY) * this.renderMinZ);
            this.brightnessBottomRight = this.mixAoBrightness(k1, l1, i2, j1, this.renderMinY * this.renderMinZ, this.renderMinY * (1.0D - this.renderMinZ), (1.0D - this.renderMinY) * (1.0D - this.renderMinZ), (1.0D - this.renderMinY) * this.renderMinZ);
            this.brightnessTopRight = this.mixAoBrightness(k1, l1, i2, j1, this.renderMinY * this.renderMaxZ, this.renderMinY * (1.0D - this.renderMaxZ), (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ), (1.0D - this.renderMinY) * this.renderMaxZ);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(p_147808_1_, this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_, 4);
            this.renderFaceXNeg(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147808_5_;
                this.colorRedBottomLeft *= p_147808_5_;
                this.colorRedBottomRight *= p_147808_5_;
                this.colorRedTopRight *= p_147808_5_;
                this.colorGreenTopLeft *= p_147808_6_;
                this.colorGreenBottomLeft *= p_147808_6_;
                this.colorGreenBottomRight *= p_147808_6_;
                this.colorGreenTopRight *= p_147808_6_;
                this.colorBlueTopLeft *= p_147808_7_;
                this.colorBlueBottomLeft *= p_147808_7_;
                this.colorBlueBottomRight *= p_147808_7_;
                this.colorBlueTopRight *= p_147808_7_;
                this.renderFaceXNeg(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || p_147808_1_.shouldSideBeRendered(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_, 5))
        {
            if (this.renderMaxX >= 1.0D)
            {
                ++p_147808_2_;
            }

            this.aoLightValueScratchXYPN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_, p_147808_4_ + 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYPP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_).getAmbientOcclusionLightValue();
            this.aoBrightnessXYPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_);
            this.aoBrightnessXZPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ - 1);
            this.aoBrightnessXZPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_ + 1);
            this.aoBrightnessXYPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_);
            flag2 = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_ + 1, p_147808_4_).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_ - 1, p_147808_4_).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_ + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_ - 1).getCanBlockGrass();

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_ - 1);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ - 1, p_147808_4_ + 1);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPN = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_ - 1);
            }

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPP = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_, p_147808_3_ + 1, p_147808_4_ + 1);
            }

            if (this.renderMaxX >= 1.0D)
            {
                --p_147808_2_;
            }

            i1 = l;

            if (this.renderMaxX >= 1.0D || !this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_).isOpaqueCube())
            {
                i1 = p_147808_1_.getMixedBrightnessForBlock(this.blockAccess, p_147808_2_ + 1, p_147808_3_, p_147808_4_);
            }

            f7 = this.blockAccess.getBlock(p_147808_2_ + 1, p_147808_3_, p_147808_4_).getAmbientOcclusionLightValue();
            f8 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + f7 + this.aoLightValueScratchXZPP) / 4.0F;
            f9 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + f7) / 4.0F;
            f10 = (this.aoLightValueScratchXZPN + f7 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
            f11 = (f7 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f3 = (float)((double)f8 * (1.0D - this.renderMinY) * this.renderMaxZ + (double)f9 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ) + (double)f10 * this.renderMinY * (1.0D - this.renderMaxZ) + (double)f11 * this.renderMinY * this.renderMaxZ);
            f4 = (float)((double)f8 * (1.0D - this.renderMinY) * this.renderMinZ + (double)f9 * (1.0D - this.renderMinY) * (1.0D - this.renderMinZ) + (double)f10 * this.renderMinY * (1.0D - this.renderMinZ) + (double)f11 * this.renderMinY * this.renderMinZ);
            f5 = (float)((double)f8 * (1.0D - this.renderMaxY) * this.renderMinZ + (double)f9 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ) + (double)f10 * this.renderMaxY * (1.0D - this.renderMinZ) + (double)f11 * this.renderMaxY * this.renderMinZ);
            f6 = (float)((double)f8 * (1.0D - this.renderMaxY) * this.renderMaxZ + (double)f9 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ) + (double)f10 * this.renderMaxY * (1.0D - this.renderMaxZ) + (double)f11 * this.renderMaxY * this.renderMaxZ);
            j1 = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            k1 = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, i1);
            l1 = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, i1);
            this.brightnessTopLeft = this.mixAoBrightness(j1, i2, l1, k1, (1.0D - this.renderMinY) * this.renderMaxZ, (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ), this.renderMinY * (1.0D - this.renderMaxZ), this.renderMinY * this.renderMaxZ);
            this.brightnessBottomLeft = this.mixAoBrightness(j1, i2, l1, k1, (1.0D - this.renderMinY) * this.renderMinZ, (1.0D - this.renderMinY) * (1.0D - this.renderMinZ), this.renderMinY * (1.0D - this.renderMinZ), this.renderMinY * this.renderMinZ);
            this.brightnessBottomRight = this.mixAoBrightness(j1, i2, l1, k1, (1.0D - this.renderMaxY) * this.renderMinZ, (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ), this.renderMaxY * (1.0D - this.renderMinZ), this.renderMaxY * this.renderMinZ);
            this.brightnessTopRight = this.mixAoBrightness(j1, i2, l1, k1, (1.0D - this.renderMaxY) * this.renderMaxZ, (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ), this.renderMaxY * (1.0D - this.renderMaxZ), this.renderMaxY * this.renderMaxZ);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(p_147808_1_, this.blockAccess, p_147808_2_, p_147808_3_, p_147808_4_, 5);
            this.renderFaceXPos(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147808_5_;
                this.colorRedBottomLeft *= p_147808_5_;
                this.colorRedBottomRight *= p_147808_5_;
                this.colorRedTopRight *= p_147808_5_;
                this.colorGreenTopLeft *= p_147808_6_;
                this.colorGreenBottomLeft *= p_147808_6_;
                this.colorGreenBottomRight *= p_147808_6_;
                this.colorGreenTopRight *= p_147808_6_;
                this.colorBlueTopLeft *= p_147808_7_;
                this.colorBlueBottomLeft *= p_147808_7_;
                this.colorBlueBottomRight *= p_147808_7_;
                this.colorBlueTopRight *= p_147808_7_;
                this.renderFaceXPos(p_147808_1_, (double)p_147808_2_, (double)p_147808_3_, (double)p_147808_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        this.enableAO = false;
        return flag;
    }
    // Todo refactor
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean renderStandardBlockWithAmbientOcclusion(Block p_147751_1_, int p_147751_2_, int p_147751_3_, int p_147751_4_, float p_147751_5_, float p_147751_6_, float p_147751_7_)
    {
        this.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (this.getBlockIcon(p_147751_1_).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (this.hasOverrideBlockTexture())
        {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        int i1;
        float f7;

        if (this.renderAllFaces || p_147751_1_.shouldSideBeRendered(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_, 0))
        {
            if (this.renderMinY <= 0.0D)
            {
                --p_147751_3_;
            }

            this.aoBrightnessXYNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            this.aoBrightnessYZNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            this.aoBrightnessYZNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            this.aoBrightnessXYPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            this.aoLightValueScratchXYNN = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ + 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYPN = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            flag2 = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1).getCanBlockGrass();

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1);
            }

            if (this.renderMinY <= 0.0D)
            {
                ++p_147751_3_;
            }

            i1 = l;

            if (this.renderMinY <= 0.0D || !this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_).isOpaqueCube())
            {
                i1 = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            }

            f7 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_).getAmbientOcclusionLightValue();
            f3 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + f7 + this.aoLightValueScratchYZNN) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147751_5_ * 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147751_6_ * 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147751_7_ * 0.5F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYNeg(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, this.getBlockIcon(p_147751_1_, this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_, 0));
            flag = true;
        }

        if (this.renderAllFaces || p_147751_1_.shouldSideBeRendered(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_, 1))
        {
            if (this.renderMaxY >= 1.0D)
            {
                ++p_147751_3_;
            }

            this.aoBrightnessXYNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            this.aoBrightnessXYPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            this.aoBrightnessYZPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            this.aoBrightnessYZPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            this.aoLightValueScratchXYNP = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYPP = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ + 1).getAmbientOcclusionLightValue();
            flag2 = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1).getCanBlockGrass();

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1);
            }

            if (this.renderMaxY >= 1.0D)
            {
                --p_147751_3_;
            }

            i1 = l;

            if (this.renderMaxY >= 1.0D || !this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_).isOpaqueCube())
            {
                i1 = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            }

            f7 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_).getAmbientOcclusionLightValue();
            f6 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (this.aoLightValueScratchYZPP + f7 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, i1);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147751_5_;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147751_6_;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147751_7_;
            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYPos(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, this.getBlockIcon(p_147751_1_, this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_, 1));
            flag = true;
        }

        IIcon iicon;

        if (this.renderAllFaces || p_147751_1_.shouldSideBeRendered(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ - 1, 2))
        {
            if (this.renderMinZ <= 0.0D)
            {
                --p_147751_4_;
            }

            this.aoLightValueScratchXZNN = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPN = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoBrightnessXZNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            this.aoBrightnessYZNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            this.aoBrightnessYZPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            this.aoBrightnessXZPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            flag2 = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1).getCanBlockGrass();

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_);
            }

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_);
            }

            if (this.renderMinZ <= 0.0D)
            {
                ++p_147751_4_;
            }

            i1 = l;

            if (this.renderMinZ <= 0.0D || !this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ - 1).isOpaqueCube())
            {
                i1 = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            }

            f7 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ - 1).getAmbientOcclusionLightValue();
            f3 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (this.aoLightValueScratchYZNN + f7 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
            f6 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + f7) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147751_5_ * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147751_6_ * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147751_7_ * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(p_147751_1_, this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_, 2);
            this.renderFaceZNeg(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147751_5_;
                this.colorRedBottomLeft *= p_147751_5_;
                this.colorRedBottomRight *= p_147751_5_;
                this.colorRedTopRight *= p_147751_5_;
                this.colorGreenTopLeft *= p_147751_6_;
                this.colorGreenBottomLeft *= p_147751_6_;
                this.colorGreenBottomRight *= p_147751_6_;
                this.colorGreenTopRight *= p_147751_6_;
                this.colorBlueTopLeft *= p_147751_7_;
                this.colorBlueBottomLeft *= p_147751_7_;
                this.colorBlueBottomRight *= p_147751_7_;
                this.colorBlueTopRight *= p_147751_7_;
                this.renderFaceZNeg(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || p_147751_1_.shouldSideBeRendered(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ + 1, 3))
        {
            if (this.renderMaxZ >= 1.0D)
            {
                ++p_147751_4_;
            }

            this.aoLightValueScratchXZNP = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPP = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoBrightnessXZNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            this.aoBrightnessXZPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            this.aoBrightnessYZNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            this.aoBrightnessYZPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            flag2 = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1).getCanBlockGrass();

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_);
            }

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_);
            }

            if (this.renderMaxZ >= 1.0D)
            {
                --p_147751_4_;
            }

            i1 = l;

            if (this.renderMaxZ >= 1.0D || !this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ + 1).isOpaqueCube())
            {
                i1 = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            }

            f7 = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ + 1).getAmbientOcclusionLightValue();
            f3 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + f7 + this.aoLightValueScratchYZPP) / 4.0F;
            f6 = (f7 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f5 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
            f4 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + f7) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147751_5_ * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147751_6_ * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147751_7_ * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(p_147751_1_, this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_, 3);
            this.renderFaceZPos(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, this.getBlockIcon(p_147751_1_, this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_, 3));

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147751_5_;
                this.colorRedBottomLeft *= p_147751_5_;
                this.colorRedBottomRight *= p_147751_5_;
                this.colorRedTopRight *= p_147751_5_;
                this.colorGreenTopLeft *= p_147751_6_;
                this.colorGreenBottomLeft *= p_147751_6_;
                this.colorGreenBottomRight *= p_147751_6_;
                this.colorGreenTopRight *= p_147751_6_;
                this.colorBlueTopLeft *= p_147751_7_;
                this.colorBlueBottomLeft *= p_147751_7_;
                this.colorBlueBottomRight *= p_147751_7_;
                this.colorBlueTopRight *= p_147751_7_;
                this.renderFaceZPos(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || p_147751_1_.shouldSideBeRendered(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_, 4))
        {
            if (this.renderMinX <= 0.0D)
            {
                --p_147751_2_;
            }

            this.aoLightValueScratchXYNN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZNN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZNP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ + 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYNP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoBrightnessXYNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            this.aoBrightnessXZNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            this.aoBrightnessXZNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            this.aoBrightnessXYNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            flag2 = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_ + 1, p_147751_4_).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_ - 1, p_147751_4_).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_ - 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_ + 1).getCanBlockGrass();

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1);
            }

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1);
            }

            if (this.renderMinX <= 0.0D)
            {
                ++p_147751_2_;
            }

            i1 = l;

            if (this.renderMinX <= 0.0D || !this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_).isOpaqueCube())
            {
                i1 = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ - 1, p_147751_3_, p_147751_4_);
            }

            f7 = this.blockAccess.getBlock(p_147751_2_ - 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            f6 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + f7 + this.aoLightValueScratchXZNP) / 4.0F;
            f3 = (f7 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (this.aoLightValueScratchXZNN + f7 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
            f5 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + f7) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, i1);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147751_5_ * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147751_6_ * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147751_7_ * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(p_147751_1_, this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_, 4);
            this.renderFaceXNeg(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147751_5_;
                this.colorRedBottomLeft *= p_147751_5_;
                this.colorRedBottomRight *= p_147751_5_;
                this.colorRedTopRight *= p_147751_5_;
                this.colorGreenTopLeft *= p_147751_6_;
                this.colorGreenBottomLeft *= p_147751_6_;
                this.colorGreenBottomRight *= p_147751_6_;
                this.colorGreenTopRight *= p_147751_6_;
                this.colorBlueTopLeft *= p_147751_7_;
                this.colorBlueBottomLeft *= p_147751_7_;
                this.colorBlueBottomRight *= p_147751_7_;
                this.colorBlueTopRight *= p_147751_7_;
                this.renderFaceXNeg(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || p_147751_1_.shouldSideBeRendered(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_, 5))
        {
            if (this.renderMaxX >= 1.0D)
            {
                ++p_147751_2_;
            }

            this.aoLightValueScratchXYPN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_, p_147751_4_ + 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYPP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_).getAmbientOcclusionLightValue();
            this.aoBrightnessXYPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_);
            this.aoBrightnessXZPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ - 1);
            this.aoBrightnessXZPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_ + 1);
            this.aoBrightnessXYPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_);
            flag2 = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_ + 1, p_147751_4_).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_ - 1, p_147751_4_).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_ + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_ - 1).getCanBlockGrass();

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_ - 1);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ - 1, p_147751_4_ + 1);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPN = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_ - 1);
            }

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPP = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_, p_147751_3_ + 1, p_147751_4_ + 1);
            }

            if (this.renderMaxX >= 1.0D)
            {
                --p_147751_2_;
            }

            i1 = l;

            if (this.renderMaxX >= 1.0D || !this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_).isOpaqueCube())
            {
                i1 = p_147751_1_.getMixedBrightnessForBlock(this.blockAccess, p_147751_2_ + 1, p_147751_3_, p_147751_4_);
            }

            f7 = this.blockAccess.getBlock(p_147751_2_ + 1, p_147751_3_, p_147751_4_).getAmbientOcclusionLightValue();
            f3 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + f7 + this.aoLightValueScratchXZPP) / 4.0F;
            f4 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (this.aoLightValueScratchXZPN + f7 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
            f6 = (f7 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147751_5_ * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147751_6_ * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147751_7_ * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(p_147751_1_, this.blockAccess, p_147751_2_, p_147751_3_, p_147751_4_, 5);
            this.renderFaceXPos(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147751_5_;
                this.colorRedBottomLeft *= p_147751_5_;
                this.colorRedBottomRight *= p_147751_5_;
                this.colorRedTopRight *= p_147751_5_;
                this.colorGreenTopLeft *= p_147751_6_;
                this.colorGreenBottomLeft *= p_147751_6_;
                this.colorGreenBottomRight *= p_147751_6_;
                this.colorGreenTopRight *= p_147751_6_;
                this.colorBlueTopLeft *= p_147751_7_;
                this.colorBlueBottomLeft *= p_147751_7_;
                this.colorBlueBottomRight *= p_147751_7_;
                this.colorBlueTopRight *= p_147751_7_;
                this.renderFaceXPos(p_147751_1_, (double)p_147751_2_, (double)p_147751_3_, (double)p_147751_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        this.enableAO = false;
        return flag;
    }
    // todo refactor
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean renderStandardBlock(Block blockType, int blockX, int blockY, int blockZ)
    {
        int l = blockType.colorMultiplier(this.blockAccess, blockX, blockY, blockZ);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        return Minecraft.isAmbientOcclusionEnabled() && blockType.getLightValue() == 0 ? (this.partialRenderBounds ? this.renderStandardBlockWithAmbientOcclusionPartial(blockType, blockX, blockY, blockZ, f, f1, f2) : this.renderStandardBlockWithAmbientOcclusion(blockType, blockX, blockY, blockZ, f, f1, f2)) : this.renderStandardBlockWithColorMultiplier(blockType, blockX, blockY, blockZ, f, f1, f2);
    }
    // todo refactor
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderFaceXPos(Block p_147764_1_, double p_147764_2_, double p_147764_4_, double p_147764_6_, IIcon p_147764_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            p_147764_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147764_8_.getInterpolatedU(this.renderMinZ * 16.0D);
        double d4 = (double)p_147764_8_.getInterpolatedU(this.renderMaxZ * 16.0D);

        if (this.field_152631_f)
        {
            d4 = (double)p_147764_8_.getInterpolatedU((1.0D - this.renderMinZ) * 16.0D);
            d3 = (double)p_147764_8_.getInterpolatedU((1.0D - this.renderMaxZ) * 16.0D);
        }

        double d5 = (double)p_147764_8_.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double)p_147764_8_.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
        {
            d3 = (double)p_147764_8_.getMinU();
            d4 = (double)p_147764_8_.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
        {
            d5 = (double)p_147764_8_.getMinV();
            d6 = (double)p_147764_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateSouth == 2)
        {
            d3 = (double)p_147764_8_.getInterpolatedU(this.renderMinY * 16.0D);
            d5 = (double)p_147764_8_.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d4 = (double)p_147764_8_.getInterpolatedU(this.renderMaxY * 16.0D);
            d6 = (double)p_147764_8_.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateSouth == 1)
        {
            d3 = (double)p_147764_8_.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double)p_147764_8_.getInterpolatedV(this.renderMaxZ * 16.0D);
            d4 = (double)p_147764_8_.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double)p_147764_8_.getInterpolatedV(this.renderMinZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateSouth == 3)
        {
            d3 = (double)p_147764_8_.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d4 = (double)p_147764_8_.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double)p_147764_8_.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double)p_147764_8_.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147764_2_ + this.renderMaxX;
        double d12 = p_147764_4_ + this.renderMinY;
        double d13 = p_147764_4_ + this.renderMaxY;
        double d14 = p_147764_6_ + this.renderMinZ;
        double d15 = p_147764_6_ + this.renderMaxZ;

        if (this.renderFromInside)
        {
            d14 = p_147764_6_ + this.renderMaxZ;
            d15 = p_147764_6_ + this.renderMinZ;
        }

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
            tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
        }
    }


    @Shadow
    public IIcon getBlockIcon(Block p_147793_1_, IBlockAccess p_147793_2_, int p_147793_3_, int p_147793_4_, int p_147793_5_, int p_147793_6_)
    {
        return this.getIconSafe(p_147793_1_.getIcon(p_147793_2_, p_147793_3_, p_147793_4_, p_147793_5_, p_147793_6_));
    }
    @Shadow
    public IIcon getBlockIcon(Block p_147745_1_)
    {
        return this.getIconSafe(p_147745_1_.getBlockTextureFromSide(1));
    }

    @Shadow
    public IIcon getIconSafe(IIcon p_147758_1_)
    {
        if (p_147758_1_ == null)
        {
            p_147758_1_ = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
        }

        return (IIcon)p_147758_1_;
    }

    @Shadow
    public boolean hasOverrideBlockTexture()
    {
        return this.overrideBlockTexture != null;
    }

    @Shadow
    public int getAoBrightness(int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_)
    {
        if (p_147778_1_ == 0)
        {
            p_147778_1_ = p_147778_4_;
        }

        if (p_147778_2_ == 0)
        {
            p_147778_2_ = p_147778_4_;
        }

        if (p_147778_3_ == 0)
        {
            p_147778_3_ = p_147778_4_;
        }

        return p_147778_1_ + p_147778_2_ + p_147778_3_ + p_147778_4_ >> 2 & 16711935;
    }
    @Shadow
    public int mixAoBrightness(int p_147727_1_, int p_147727_2_, int p_147727_3_, int p_147727_4_, double p_147727_5_, double p_147727_7_, double p_147727_9_, double p_147727_11_)
    {
        int i1 = (int)((double)(p_147727_1_ >> 16 & 255) * p_147727_5_ + (double)(p_147727_2_ >> 16 & 255) * p_147727_7_ + (double)(p_147727_3_ >> 16 & 255) * p_147727_9_ + (double)(p_147727_4_ >> 16 & 255) * p_147727_11_) & 255;
        int j1 = (int)((double)(p_147727_1_ & 255) * p_147727_5_ + (double)(p_147727_2_ & 255) * p_147727_7_ + (double)(p_147727_3_ & 255) * p_147727_9_ + (double)(p_147727_4_ & 255) * p_147727_11_) & 255;
        return i1 << 16 | j1;
    }

    @Shadow
    public void renderFaceZNeg(Block p_147761_1_, double p_147761_2_, double p_147761_4_, double p_147761_6_, IIcon p_147761_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            p_147761_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147761_8_.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double)p_147761_8_.getInterpolatedU(this.renderMaxX * 16.0D);

        if (this.field_152631_f)
        {
            d4 = (double)p_147761_8_.getInterpolatedU((1.0D - this.renderMinX) * 16.0D);
            d3 = (double)p_147761_8_.getInterpolatedU((1.0D - this.renderMaxX) * 16.0D);
        }

        double d5 = (double)p_147761_8_.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double)p_147761_8_.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D)
        {
            d3 = (double)p_147761_8_.getMinU();
            d4 = (double)p_147761_8_.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
        {
            d5 = (double)p_147761_8_.getMinV();
            d6 = (double)p_147761_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateEast == 2)
        {
            d3 = (double)p_147761_8_.getInterpolatedU(this.renderMinY * 16.0D);
            d4 = (double)p_147761_8_.getInterpolatedU(this.renderMaxY * 16.0D);
            d5 = (double)p_147761_8_.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d6 = (double)p_147761_8_.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateEast == 1)
        {
            d3 = (double)p_147761_8_.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d4 = (double)p_147761_8_.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d5 = (double)p_147761_8_.getInterpolatedV(this.renderMaxX * 16.0D);
            d6 = (double)p_147761_8_.getInterpolatedV(this.renderMinX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateEast == 3)
        {
            d3 = (double)p_147761_8_.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147761_8_.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double)p_147761_8_.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double)p_147761_8_.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147761_2_ + this.renderMinX;
        double d12 = p_147761_2_ + this.renderMaxX;
        double d13 = p_147761_4_ + this.renderMinY;
        double d14 = p_147761_4_ + this.renderMaxY;
        double d15 = p_147761_6_ + this.renderMinZ;

        if (this.renderFromInside)
        {
            d11 = p_147761_2_ + this.renderMaxX;
            d12 = p_147761_2_ + this.renderMinX;
        }

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        }
    }

    @Shadow
    public void renderFaceXNeg(Block p_147798_1_, double p_147798_2_, double p_147798_4_, double p_147798_6_, IIcon p_147798_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            p_147798_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147798_8_.getInterpolatedU(this.renderMinZ * 16.0D);
        double d4 = (double)p_147798_8_.getInterpolatedU(this.renderMaxZ * 16.0D);
        double d5 = (double)p_147798_8_.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double)p_147798_8_.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
        {
            d3 = (double)p_147798_8_.getMinU();
            d4 = (double)p_147798_8_.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
        {
            d5 = (double)p_147798_8_.getMinV();
            d6 = (double)p_147798_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateNorth == 1)
        {
            d3 = (double)p_147798_8_.getInterpolatedU(this.renderMinY * 16.0D);
            d5 = (double)p_147798_8_.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d4 = (double)p_147798_8_.getInterpolatedU(this.renderMaxY * 16.0D);
            d6 = (double)p_147798_8_.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateNorth == 2)
        {
            d3 = (double)p_147798_8_.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double)p_147798_8_.getInterpolatedV(this.renderMinZ * 16.0D);
            d4 = (double)p_147798_8_.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double)p_147798_8_.getInterpolatedV(this.renderMaxZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateNorth == 3)
        {
            d3 = (double)p_147798_8_.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d4 = (double)p_147798_8_.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double)p_147798_8_.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double)p_147798_8_.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147798_2_ + this.renderMinX;
        double d12 = p_147798_4_ + this.renderMinY;
        double d13 = p_147798_4_ + this.renderMaxY;
        double d14 = p_147798_6_ + this.renderMinZ;
        double d15 = p_147798_6_ + this.renderMaxZ;

        if (this.renderFromInside)
        {
            d14 = p_147798_6_ + this.renderMaxZ;
            d15 = p_147798_6_ + this.renderMinZ;
        }

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d11, d12, d14, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d12, d15, d4, d6);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d13, d15, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d11, d12, d14, d8, d10);
            tessellator.addVertexWithUV(d11, d12, d15, d4, d6);
        }
    }
    /**
     * Renders the given texture to the south (z-positive) face of the block.  Args: block, x, y, z, texture
     */
    @Shadow
    public void renderFaceZPos(Block p_147734_1_, double p_147734_2_, double p_147734_4_, double p_147734_6_, IIcon p_147734_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            p_147734_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147734_8_.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double)p_147734_8_.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double)p_147734_8_.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double)p_147734_8_.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D)
        {
            d3 = (double)p_147734_8_.getMinU();
            d4 = (double)p_147734_8_.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
        {
            d5 = (double)p_147734_8_.getMinV();
            d6 = (double)p_147734_8_.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateWest == 1)
        {
            d3 = (double)p_147734_8_.getInterpolatedU(this.renderMinY * 16.0D);
            d6 = (double)p_147734_8_.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147734_8_.getInterpolatedU(this.renderMaxY * 16.0D);
            d5 = (double)p_147734_8_.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateWest == 2)
        {
            d3 = (double)p_147734_8_.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double)p_147734_8_.getInterpolatedV(this.renderMinX * 16.0D);
            d4 = (double)p_147734_8_.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double)p_147734_8_.getInterpolatedV(this.renderMaxX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateWest == 3)
        {
            d3 = (double)p_147734_8_.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147734_8_.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double)p_147734_8_.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double)p_147734_8_.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147734_2_ + this.renderMinX;
        double d12 = p_147734_2_ + this.renderMaxX;
        double d13 = p_147734_4_ + this.renderMinY;
        double d14 = p_147734_4_ + this.renderMaxY;
        double d15 = p_147734_6_ + this.renderMaxZ;

        if (this.renderFromInside)
        {
            d11 = p_147734_2_ + this.renderMaxX;
            d12 = p_147734_2_ + this.renderMinX;
        }

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d14, d15, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d12, d14, d15, d7, d9);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d14, d15, d3, d5);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d14, d15, d7, d9);
        }
    }

    @Shadow
    public void renderFaceYNeg(Block p_147768_1_, double p_147768_2_, double p_147768_4_, double p_147768_6_, IIcon p_147768_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            p_147768_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147768_8_.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double)p_147768_8_.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double)p_147768_8_.getInterpolatedV(this.renderMinZ * 16.0D);
        double d6 = (double)p_147768_8_.getInterpolatedV(this.renderMaxZ * 16.0D);

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D)
        {
            d3 = (double)p_147768_8_.getMinU();
            d4 = (double)p_147768_8_.getMaxU();
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
        {
            d5 = (double)p_147768_8_.getMinV();
            d6 = (double)p_147768_8_.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateBottom == 2)
        {
            d3 = (double)p_147768_8_.getInterpolatedU(this.renderMinZ * 16.0D);
            d5 = (double)p_147768_8_.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d4 = (double)p_147768_8_.getInterpolatedU(this.renderMaxZ * 16.0D);
            d6 = (double)p_147768_8_.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateBottom == 1)
        {
            d3 = (double)p_147768_8_.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double)p_147768_8_.getInterpolatedV(this.renderMinX * 16.0D);
            d4 = (double)p_147768_8_.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d6 = (double)p_147768_8_.getInterpolatedV(this.renderMaxX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateBottom == 3)
        {
            d3 = (double)p_147768_8_.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147768_8_.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double)p_147768_8_.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d6 = (double)p_147768_8_.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147768_2_ + this.renderMinX;
        double d12 = p_147768_2_ + this.renderMaxX;
        double d13 = p_147768_4_ + this.renderMinY;
        double d14 = p_147768_6_ + this.renderMinZ;
        double d15 = p_147768_6_ + this.renderMaxZ;

        if (this.renderFromInside)
        {
            d11 = p_147768_2_ + this.renderMaxX;
            d12 = p_147768_2_ + this.renderMinX;
        }

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        }
    }

    @Shadow
    public void renderFaceYPos(Block p_147806_1_, double p_147806_2_, double p_147806_4_, double p_147806_6_, IIcon p_147806_8_)
    {
        Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            p_147806_8_ = this.overrideBlockTexture;
        }

        double d3 = (double)p_147806_8_.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double)p_147806_8_.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double)p_147806_8_.getInterpolatedV(this.renderMinZ * 16.0D);
        double d6 = (double)p_147806_8_.getInterpolatedV(this.renderMaxZ * 16.0D);

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D)
        {
            d3 = (double)p_147806_8_.getMinU();
            d4 = (double)p_147806_8_.getMaxU();
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
        {
            d5 = (double)p_147806_8_.getMinV();
            d6 = (double)p_147806_8_.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateTop == 1)
        {
            d3 = (double)p_147806_8_.getInterpolatedU(this.renderMinZ * 16.0D);
            d5 = (double)p_147806_8_.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d4 = (double)p_147806_8_.getInterpolatedU(this.renderMaxZ * 16.0D);
            d6 = (double)p_147806_8_.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateTop == 2)
        {
            d3 = (double)p_147806_8_.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double)p_147806_8_.getInterpolatedV(this.renderMinX * 16.0D);
            d4 = (double)p_147806_8_.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d6 = (double)p_147806_8_.getInterpolatedV(this.renderMaxX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateTop == 3)
        {
            d3 = (double)p_147806_8_.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double)p_147806_8_.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double)p_147806_8_.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d6 = (double)p_147806_8_.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = p_147806_2_ + this.renderMinX;
        double d12 = p_147806_2_ + this.renderMaxX;
        double d13 = p_147806_4_ + this.renderMaxY;
        double d14 = p_147806_6_ + this.renderMinZ;
        double d15 = p_147806_6_ + this.renderMaxZ;

        if (this.renderFromInside)
        {
            d11 = p_147806_2_ + this.renderMaxX;
            d12 = p_147806_2_ + this.renderMinX;
        }

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        }
        else
        {
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        }
    }

    @Shadow
    public boolean renderStandardBlockWithColorMultiplier(Block p_147736_1_, int p_147736_2_, int p_147736_3_, int p_147736_4_, float p_147736_5_, float p_147736_6_, float p_147736_7_)
    {
        this.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * p_147736_5_;
        float f8 = f4 * p_147736_6_;
        float f9 = f4 * p_147736_7_;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (p_147736_1_ != Blocks.grass)
        {
            f10 = f3 * p_147736_5_;
            f11 = f5 * p_147736_5_;
            f12 = f6 * p_147736_5_;
            f13 = f3 * p_147736_6_;
            f14 = f5 * p_147736_6_;
            f15 = f6 * p_147736_6_;
            f16 = f3 * p_147736_7_;
            f17 = f5 * p_147736_7_;
            f18 = f6 * p_147736_7_;
        }

        int l = p_147736_1_.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_);

        if (this.renderAllFaces || p_147736_1_.shouldSideBeRendered(this.blockAccess, p_147736_2_, p_147736_3_ - 1, p_147736_4_, 0))
        {
            tessellator.setBrightness(this.renderMinY > 0.0D ? l : p_147736_1_.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_ - 1, p_147736_4_));
            tessellator.setColorOpaque_F(f10, f13, f16);
            this.renderFaceYNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, this.getBlockIcon(p_147736_1_, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 0));
            flag = true;
        }

        if (this.renderAllFaces || p_147736_1_.shouldSideBeRendered(this.blockAccess, p_147736_2_, p_147736_3_ + 1, p_147736_4_, 1))
        {
            tessellator.setBrightness(this.renderMaxY < 1.0D ? l : p_147736_1_.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_ + 1, p_147736_4_));
            tessellator.setColorOpaque_F(f7, f8, f9);
            this.renderFaceYPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, this.getBlockIcon(p_147736_1_, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 1));
            flag = true;
        }

        IIcon iicon;

        if (this.renderAllFaces || p_147736_1_.shouldSideBeRendered(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ - 1, 2))
        {
            tessellator.setBrightness(this.renderMinZ > 0.0D ? l : p_147736_1_.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            iicon = this.getBlockIcon(p_147736_1_, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 2);
            this.renderFaceZNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_);
                this.renderFaceZNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || p_147736_1_.shouldSideBeRendered(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ + 1, 3))
        {
            tessellator.setBrightness(this.renderMaxZ < 1.0D ? l : p_147736_1_.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            iicon = this.getBlockIcon(p_147736_1_, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 3);
            this.renderFaceZPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_);
                this.renderFaceZPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || p_147736_1_.shouldSideBeRendered(this.blockAccess, p_147736_2_ - 1, p_147736_3_, p_147736_4_, 4))
        {
            tessellator.setBrightness(this.renderMinX > 0.0D ? l : p_147736_1_.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_ - 1, p_147736_3_, p_147736_4_));
            tessellator.setColorOpaque_F(f12, f15, f18);
            iicon = this.getBlockIcon(p_147736_1_, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 4);
            this.renderFaceXNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_);
                this.renderFaceXNeg(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || p_147736_1_.shouldSideBeRendered(this.blockAccess, p_147736_2_ + 1, p_147736_3_, p_147736_4_, 5))
        {
            tessellator.setBrightness(this.renderMaxX < 1.0D ? l : p_147736_1_.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_ + 1, p_147736_3_, p_147736_4_));
            tessellator.setColorOpaque_F(f12, f15, f18);
            iicon = this.getBlockIcon(p_147736_1_, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 5);
            this.renderFaceXPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_);
                this.renderFaceXPos(p_147736_1_, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        return flag;
    }
}
