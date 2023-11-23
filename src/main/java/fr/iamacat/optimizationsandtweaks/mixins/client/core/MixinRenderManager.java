package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ReportedException;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    /** A map of entity classes and the associated renderer. */
    @Shadow
    public Map entityRenderMap = new HashMap();
    /** Renders fonts */
    @Shadow
    private FontRenderer fontRenderer;
    @Shadow
    public static double renderPosX;
    @Shadow
    public static double renderPosY;
    @Shadow
    public static double renderPosZ;
    @Shadow
    public TextureManager renderEngine;

    /** whether bounding box should be rendered or not */
    @Shadow
    public static boolean debugBoundingBox;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean renderEntityStatic(Entity entity, float partialTicks, boolean doRender) {
        if (entity.ticksExisted == 0) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
        }

        double interpX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
        double interpY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
        double interpZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
        float interpYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        int brightness = entity.getBrightnessForRender(partialTicks);

        if (entity.isBurning()) {
            brightness = 15728880;
        }

        int lightmapX = brightness % 65536;
        int lightmapY = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) lightmapX, (float) lightmapY);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        return func_147939_a(
            entity,
            interpX - renderPosX,
            interpY - renderPosY,
            interpZ - renderPosZ,
            interpYaw,
            partialTicks,
            doRender);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean func_147939_a(Entity entity, double x, double y, double z, float yaw, float partialTicks,
        boolean doRender) {
        Render render = optimizationsAndTweaks$getEntityRenderObject(entity);

        if (render == null || render.isStaticEntity() && !doRender || renderEngine == null) {
            return doRender;
        }

        try {
            render.doRender(entity, x, y, z, yaw, partialTicks);
            render.doRenderShadowAndFire(entity, x, y, z, yaw, partialTicks);

            if (debugBoundingBox && !entity.isInvisible() && !doRender) {
                optimizationsAndTweaks$renderDebugBoundingBox(entity, x, y, z, yaw, partialTicks);
            }
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Rendering entity in world");
            CrashReportCategory entityCategory = crashReport.makeCategory("Entity being rendered");
            entity.addEntityCrashInfo(entityCategory);
            CrashReportCategory rendererCategory = crashReport.makeCategory("Renderer details");
            rendererCategory.addCrashSection("Assigned renderer", render);
            rendererCategory.addCrashSection("Location", CrashReportCategory.func_85074_a(x, y, z));
            rendererCategory.addCrashSection("Rotation", yaw);
            rendererCategory.addCrashSection("Delta", partialTicks);
            throw new ReportedException(crashReport);
        }
        return true;
    }

    /**
     * Renders the bounding box around an entity when F3+B is pressed
     */
    @Unique
    private void optimizationsAndTweaks$renderDebugBoundingBox(Entity p_85094_1_, double p_85094_2_, double p_85094_4_,
        double p_85094_6_, float p_85094_8_, float p_85094_9_) {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        float f2 = p_85094_1_.width / 2.0F;
        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(
            p_85094_2_ - (double) f2,
            p_85094_4_,
            p_85094_6_ - (double) f2,
            p_85094_2_ + (double) f2,
            p_85094_4_ + (double) p_85094_1_.height,
            p_85094_6_ + (double) f2);
        RenderGlobal.drawOutlinedBoundingBox(axisalignedbb, 16777215);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }

    @Unique
    public Render optimizationsAndTweaks$getEntityRenderObject(Entity p_78713_1_) {
        return this.optimizationsAndTweaks$getEntityClassRenderObject(p_78713_1_.getClass());
    }

    @Unique
    public Render optimizationsAndTweaks$getEntityClassRenderObject(Class p_78715_1_) {
        Render render = (Render) this.entityRenderMap.get(p_78715_1_);

        if (render == null && p_78715_1_ != Entity.class) {
            render = this.optimizationsAndTweaks$getEntityClassRenderObject(p_78715_1_.getSuperclass());
            this.entityRenderMap.put(p_78715_1_, render);
        }

        return render;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateIcons(IIconRegister p_94178_1_)
    {
        for (Object o : this.entityRenderMap.values()) {
            Render render = (Render) o;
            render.updateIcons(p_94178_1_);
        }
    }
}
