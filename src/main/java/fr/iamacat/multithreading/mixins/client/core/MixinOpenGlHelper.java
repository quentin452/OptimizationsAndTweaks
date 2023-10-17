package fr.iamacat.multithreading.mixins.client.core;

import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(OpenGlHelper.class)
public class MixinOpenGlHelper {

    @Shadow
    private static boolean field_153215_z;
    @Shadow
    public static int lightmapTexUnit;
    /* Stores the last values sent into setLightmapTextureCoords */
    @Shadow
    public static float lastBrightnessX = 0.0f;
    @Shadow
    public static float lastBrightnessY = 0.0f;

    private static int cachedLightmapTexUnit = -1;

    /**
     * Sets the current coordinates of the given lightmap texture
     */
    @Overwrite
    public static void setLightmapTextureCoords(int p_77475_0_, float p_77475_1_, float p_77475_2_) {
        if (MultithreadingandtweaksConfig.enableMixinOpenGlHelper) {
            if (p_77475_0_ == cachedLightmapTexUnit) {
                if (field_153215_z) {
                    ARBMultitexture.glMultiTexCoord2fARB(p_77475_0_, p_77475_1_, p_77475_2_);
                } else {
                    GL13.glMultiTexCoord2f(p_77475_0_, p_77475_1_, p_77475_2_);
                }

                if (p_77475_0_ == lightmapTexUnit) {
                    lastBrightnessX = p_77475_1_;
                    lastBrightnessY = p_77475_2_;
                }
            } else {
                cachedLightmapTexUnit = p_77475_0_;
                if (field_153215_z) {
                    ARBMultitexture.glMultiTexCoord2fARB(p_77475_0_, p_77475_1_, p_77475_2_);
                } else {
                    GL13.glMultiTexCoord2f(p_77475_0_, p_77475_1_, p_77475_2_);
                }
                if (p_77475_0_ == lightmapTexUnit) {
                    lastBrightnessX = p_77475_1_;
                    lastBrightnessY = p_77475_2_;
                }
            }
        }
    }
}
