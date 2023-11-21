package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.util.RenderDistanceSorter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.IntBuffer;
import java.util.*;
@SideOnly(Side.CLIENT)
@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    @Shadow
    private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
    @Shadow
    private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
    @Shadow
    private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
    @Shadow
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    @Shadow
    public List tileEntities = new ArrayList();
    @Shadow
    private WorldClient theWorld;
    /** The RenderEngine instance used by RenderGlobal */
    @Shadow
    private List worldRenderersToUpdate = new ArrayList();
    @Shadow
    private WorldRenderer[] sortedWorldRenderers;
    @Shadow
    private WorldRenderer[] worldRenderers;
    @Shadow
    private int renderChunksWide;
    @Shadow
    private int renderChunksTall;
    @Shadow
    private int renderChunksDeep;
    /** OpenGL render lists base */
    @Shadow
    private int glRenderListBase;
    /** A reference to the Minecraft object. */
    @Shadow
    private Minecraft mc;
    @Shadow
    private RenderBlocks renderBlocksRg;
    /** OpenGL occlusion query base */
    @Shadow
    private IntBuffer glOcclusionQueryBase;
    /** Is occlusion testing enabled */
    @Shadow
    private boolean occlusionEnabled;
    /** counts the cloud render updates. Used with mod to stagger some updates */
    @Shadow
    private int cloudTickCounter;
    /** The star GL Call list */
    @Shadow
    private int starGLCallList;
    /** OpenGL sky list */
    @Shadow
    private int glSkyList;
    /** OpenGL sky list 2 */
    @Shadow
    private int glSkyList2;
    /** Minimum block X */
    @Shadow
    private int minBlockX;
    /** Minimum block Y */
    @Shadow
    private int minBlockY;
    /** Minimum block Z */
    @Shadow
    private int minBlockZ;
    /** Maximum block X */
    @Shadow
    private int maxBlockX;
    /** Maximum block Y */
    @Shadow
    private int maxBlockY;
    /** Maximum block Z */
    @Shadow
    private int maxBlockZ;
    /**
     * Stores blocks currently being broken. Key is entity ID of the thing doing the breaking. Value is a
     * DestroyBlockProgress
     */
    @Shadow
    private final Map damagedBlocks = new HashMap();
    /** Currently playing sounds.  Type:  HashMap<ChunkCoordinates, ISound> */
    @Shadow
    private final Map mapSoundPositions = Maps.newHashMap();
    @Shadow
    private IIcon[] destroyBlockIcons;
    @Shadow
    private boolean displayListEntitiesDirty;
    @Shadow
    private int displayListEntities;
    @Shadow
    private int renderDistanceChunks = -1;
    /** Render entities startup counter (init value=2) */
    @Shadow
    private int renderEntitiesStartupCounter = 2;
    /** Count entities total */
    @Shadow
    private int countEntitiesTotal;
    /** Count entities rendered */
    @Shadow
    private int countEntitiesRendered;
    /** Count entities hidden */
    @Shadow
    private int countEntitiesHidden;
    /** Occlusion query result */
    @Shadow
    IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
    /** How many renderers are loaded this frame that try to be rendered */
    @Shadow
    private int renderersLoaded;
    /** How many renderers are being clipped by the frustrum this frame */
    @Shadow
    private int renderersBeingClipped;
    /** How many renderers are being occluded this frame */
    @Shadow
    private int renderersBeingOccluded;
    /** How many renderers are actually being rendered this frame */
    @Shadow
    private int renderersBeingRendered;
    /** How many renderers are skipping rendering due to not having a render pass this frame */
    @Shadow
    private int renderersSkippingRenderPass;
    /** Dummy render int */
    @Shadow
    private int dummyRenderInt;
    /** World renderers check index */
    @Shadow
    private int worldRenderersCheckIndex;
    /** List of OpenGL lists for the current render pass */
    @Shadow
    private List glRenderLists = new ArrayList();
    /** All render lists (fixed length 4) */
    @Shadow
    private RenderList[] allRenderLists = new RenderList[] {new RenderList(), new RenderList(), new RenderList(), new RenderList()};
    /**
     * Previous x position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    @Shadow
    double prevSortX = -9999.0D;
    /**
     * Previous y position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    @Shadow
    double prevSortY = -9999.0D;
    /**
     * Previous Z position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    @Shadow
    double prevSortZ = -9999.0D;
    @Shadow
    double prevRenderSortX = -9999.0D;
    @Shadow
    double prevRenderSortY = -9999.0D;
    @Shadow
    double prevRenderSortZ = -9999.0D;
    @Shadow
    int prevChunkSortX = -999;
    @Shadow
    int prevChunkSortY = -999;
    @Shadow
    int prevChunkSortZ = -999;
    /** The offset used to determine if a renderer is one of the sixteenth that are being updated this frame */
    @Shadow
    int frustumCheckOffset;
    /**
     * @author
     * @reason
     */
    @Overwrite
    private int renderSortedRenderers(int p_72724_1_, int p_72724_2_, int p_72724_3_, double p_72724_4_)
    {
        this.glRenderLists.clear();
        int l = 0;
        int i1 = p_72724_1_;
        int j1 = p_72724_2_;
        byte b0 = 1;

        if (p_72724_3_ == 1)
        {
            i1 = this.sortedWorldRenderers.length - 1 - p_72724_1_;
            j1 = this.sortedWorldRenderers.length - 1 - p_72724_2_;
            b0 = -1;
        }

        for (int k1 = i1; k1 != j1; k1 += b0)
        {
            if (p_72724_3_ == 0)
            {
                ++this.renderersLoaded;

                if (this.sortedWorldRenderers[k1].skipRenderPass[p_72724_3_])
                {
                    ++this.renderersSkippingRenderPass;
                }
                else if (!this.sortedWorldRenderers[k1].isInFrustum)
                {
                    ++this.renderersBeingClipped;
                }
                else if (this.occlusionEnabled && !this.sortedWorldRenderers[k1].isVisible)
                {
                    ++this.renderersBeingOccluded;
                }
                else
                {
                    ++this.renderersBeingRendered;
                }
            }

            if (!this.sortedWorldRenderers[k1].skipRenderPass[p_72724_3_] && this.sortedWorldRenderers[k1].isInFrustum && (!this.occlusionEnabled || this.sortedWorldRenderers[k1].isVisible))
            {
                int l1 = this.sortedWorldRenderers[k1].getGLCallListForPass(p_72724_3_);

                if (l1 >= 0)
                {
                    this.glRenderLists.add(this.sortedWorldRenderers[k1]);
                    ++l;
                }
            }
        }

        EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
        double d3 = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * p_72724_4_;
        double d1 = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * p_72724_4_;
        double d2 = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * p_72724_4_;
        int i2 = 0;
        int j2;

        for (j2 = 0; j2 < this.allRenderLists.length; ++j2)
        {
            this.allRenderLists[j2].resetList();
        }

        int k2;
        int l2;

        for (j2 = 0; j2 < this.glRenderLists.size(); ++j2)
        {
            WorldRenderer worldrenderer = (WorldRenderer)this.glRenderLists.get(j2);
            k2 = -1;

            for (l2 = 0; l2 < i2; ++l2)
            {
                if (this.allRenderLists[l2].rendersChunk(worldrenderer.posXMinus, worldrenderer.posYMinus, worldrenderer.posZMinus))
                {
                    k2 = l2;
                }
            }

            if (k2 < 0)
            {
                k2 = i2++;
                this.allRenderLists[k2].setupRenderList(worldrenderer.posXMinus, worldrenderer.posYMinus, worldrenderer.posZMinus, d3, d1, d2);
            }

            this.allRenderLists[k2].addGLRenderList(worldrenderer.getGLCallListForPass(p_72724_3_));
        }

        j2 = MathHelper.floor_double(d3);
        int i3 = MathHelper.floor_double(d2);
        k2 = j2 - (j2 & 1023);
        l2 = i3 - (i3 & 1023);
        Arrays.sort(this.allRenderLists, new RenderDistanceSorter(k2, l2));
        this.renderAllRenderLists(p_72724_3_, p_72724_4_);
        return l;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderAllRenderLists(int p_72733_1_, double p_72733_2_)
    {
        this.mc.entityRenderer.enableLightmap(p_72733_2_);

        for (int j = 0; j < this.allRenderLists.length; ++j)
        {
            this.allRenderLists[j].callLists();
        }

        this.mc.entityRenderer.disableLightmap(p_72733_2_);
    }
}
