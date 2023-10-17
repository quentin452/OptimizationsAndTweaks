package fr.iamacat.multithreading.mixins.client.core;

import java.util.List;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EffectRenderer.class)
public class MixinEffectRenderer {

    @Shadow
    private List[] fxLayers = new List[4];
    @Shadow
    private TextureManager renderer;
    @Shadow

    private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");

    /**
     * Renders all current particles. Args player, partialTickTime
     */
    @Overwrite
    public void renderParticles(Entity entity, float partialTicks) {
        float rotationX = ActiveRenderInfo.rotationX;
        float rotationZ = ActiveRenderInfo.rotationZ;
        float rotationYZ = ActiveRenderInfo.rotationYZ;
        float rotationXY = ActiveRenderInfo.rotationXY;
        float rotationXZ = ActiveRenderInfo.rotationXZ;

        EntityFX.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
        EntityFX.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
        EntityFX.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;

        for (int layer = 0; layer < 3; ++layer) {

            if (!this.fxLayers[layer].isEmpty()) {
                switch (layer) {
                    case 0:
                    default:
                        this.renderer.bindTexture(particleTextures);
                        break;
                    case 1:
                        this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                        break;
                    case 2:
                        this.renderer.bindTexture(TextureMap.locationItemsTexture);
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDepthMask(false);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();

                List<EntityFX> particles = this.fxLayers[layer];

                for (EntityFX particle : particles) {
                    if (particle == null) continue;

                    tessellator.setBrightness(particle.getBrightnessForRender(partialTicks));

                    try {
                        particle.renderParticle(
                            tessellator,
                            partialTicks,
                            rotationX,
                            rotationXZ,
                            rotationZ,
                            rotationYZ,
                            rotationXY);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
                        crashreportcategory.addCrashSection("Particle", particle.toString());
                        crashreportcategory
                            .addCrashSection("Particle Type", multithreadingandtweaks$getParticleTypeString(layer));
                        throw new ReportedException(crashreport);
                    }
                }

                tessellator.draw();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDepthMask(true);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            }
        }
    }

    @Unique
    private String multithreadingandtweaks$getParticleTypeString(int layer) {
        switch (layer) {
            case 0:
                return "MISC_TEXTURE";
            case 1:
                return "TERRAIN_TEXTURE";
            case 2:
                return "ITEM_TEXTURE";
            default:
                return "Unknown - " + layer;
        }
    }
}
