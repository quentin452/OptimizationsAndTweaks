package fr.iamacat.multithreading.mixins.client.xaerominimap;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.gui.GuiMap;

@Mixin(GuiMap.class)
public class MixinGuiMap {
    @Unique
    private static long multithreadingandtweaks$lastRenderTime = 0L;
    @Unique
    private static final long RENDER_INTERVAL = 1000L / 30L;


    /**
     * @author iamacatfr
     * @reason
     */
    @Inject(method = "renderTexturedModalRectWithLighting2", at = @At("HEAD"), remap = false, cancellable = true)
    private static void multithreadingandtweaksdrawrenderTexturedModalRectWithLighting2(float x, float y, float width, float height, CallbackInfo ci) {
        if(MultithreadingandtweaksConfig.enableXaerosMapOptimizations){
            long currentTime = System.currentTimeMillis();

            if (currentTime - multithreadingandtweaks$lastRenderTime >= RENDER_INTERVAL) {
        GL11.glBegin(7);
        GL13.glMultiTexCoord2d(33984, 0.0, 1.0);
        GL13.glMultiTexCoord2d(33985, 0.0, 1.0);
        GL13.glMultiTexCoord2d(33986, 0.0, 1.0);
        GL13.glMultiTexCoord2d(33987, 0.0, 1.0);
        GL11.glVertex3d((x + 0.0F), (y + height), 0.0);
        GL13.glMultiTexCoord2d(33984, 1.0, 1.0);
        GL13.glMultiTexCoord2d(33985, 1.0, 1.0);
        GL13.glMultiTexCoord2d(33986, 1.0, 1.0);
        GL13.glMultiTexCoord2d(33987, 1.0, 1.0);
        GL11.glVertex3d((x + width), (y + height), 0.0);
        GL13.glMultiTexCoord2d(33984, 1.0, 0.0);
        GL13.glMultiTexCoord2d(33985, 1.0, 0.0);
        GL13.glMultiTexCoord2d(33986, 1.0, 0.0);
        GL13.glMultiTexCoord2d(33987, 1.0, 0.0);
        GL11.glVertex3d((x + width), (y + 0.0F), 0.0);
        GL13.glMultiTexCoord2d(33984, 0.0, 0.0);
        GL13.glMultiTexCoord2d(33985, 0.0, 0.0);
        GL13.glMultiTexCoord2d(33986, 0.0, 0.0);
        GL13.glMultiTexCoord2d(33987, 0.0, 0.0);
        GL11.glVertex3d((x + 0.0F), (y + 0.0F), 0.0);
        GL11.glEnd();
                multithreadingandtweaks$lastRenderTime = currentTime;
                }
        ci.cancel();
    }}
    /**
     * @author iamacatfr
     * @reason
     */
    @Inject(method = "renderTexturedModalSubRectWithLighting", at = @At("HEAD"), remap = false, cancellable = true)
    private static void multithreadingandtweaksdrawrenderTexturedModalSubRectWithLighting(float x, float y, float textureX1, float textureY1, float textureX2, float textureY2, float width, float height, CallbackInfo ci) {
            if(MultithreadingandtweaksConfig.enableXaerosMapOptimizations){
                long currentTime = System.currentTimeMillis();

                if (currentTime - multithreadingandtweaks$lastRenderTime >= RENDER_INTERVAL) {
        GL11.glBegin(7);
        GL13.glMultiTexCoord2d(33984, textureX1, textureY2);
        GL13.glMultiTexCoord2d(33985, textureX1, textureY2);
        GL13.glMultiTexCoord2d(33986, textureX1, textureY2);
        GL13.glMultiTexCoord2d(33987, textureX1, textureY2);
        GL11.glVertex3d((x + 0.0F), (y + height), 0.0);
        GL13.glMultiTexCoord2d(33984, textureX2, textureY2);
        GL13.glMultiTexCoord2d(33985, textureX2, textureY2);
        GL13.glMultiTexCoord2d(33986, textureX2, textureY2);
        GL13.glMultiTexCoord2d(33987, textureX2, textureY2);
        GL11.glVertex3d((x + width), (y + height), 0.0);
        GL13.glMultiTexCoord2d(33984, textureX2, textureY1);
        GL13.glMultiTexCoord2d(33985, textureX2, textureY1);
        GL13.glMultiTexCoord2d(33986, textureX2, textureY1);
        GL13.glMultiTexCoord2d(33987, textureX2, textureY1);
        GL11.glVertex3d((x + width), (y + 0.0F), 0.0);
        GL13.glMultiTexCoord2d(33984, textureX1, textureY1);
        GL13.glMultiTexCoord2d(33985, textureX1, textureY1);
        GL13.glMultiTexCoord2d(33986, textureX1, textureY1);
        GL13.glMultiTexCoord2d(33987, textureX1, textureY1);
        GL11.glVertex3d((x + 0.0F), (y + 0.0F), 0.0);
        GL11.glEnd();
                }
        ci.cancel();
    }}
    /**
     * @author iamacatfr
     * @reason
     */
    @Inject(method = "renderTexturedModalRect", at = @At("HEAD"), remap = false, cancellable = true)
    private static void multithreadingandtweaksdrawrenderTexturedModalRect(float x, float y, float width, float height, CallbackInfo ci) {
                if(MultithreadingandtweaksConfig.enableXaerosMapOptimizations){
                    long currentTime = System.currentTimeMillis();

                    if (currentTime - multithreadingandtweaks$lastRenderTime >= RENDER_INTERVAL) {

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((x + 0.0F), (y + height), 0.0, 0.0, 1.0);
        tessellator.addVertexWithUV((x + width), (y + height), 0.0, 1.0, 1.0);
        tessellator.addVertexWithUV((x + width), (y + 0.0F), 0.0, 1.0, 0.0);
        tessellator.addVertexWithUV((x + 0.0F), (y + 0.0F), 0.0, 0.0, 0.0);
        tessellator.draw();
                    }
        ci.cancel();
    }}
}
