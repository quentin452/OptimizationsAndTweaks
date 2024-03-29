package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(value = Tessellator.class, priority = 999)
public class MixinTesselator {

    @Shadow
    private static int nativeBufferSize = 0x200000;
    @Shadow
    private static int trivertsInBuffer = (nativeBufferSize / 48) * 6;
    @Shadow
    public static boolean renderingWorldRenderer = false;
    @Shadow
    public boolean defaultTexture = false;
    @Shadow
    private int rawBufferSize = 0;
    @Shadow
    public int textureID = 0;

    /** The byte buffer used for GL allocation. */
    @Shadow
    private static ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(nativeBufferSize * 4);
    /** The same memory as byteBuffer, but referenced as an integer buffer. */
    @Shadow
    private static IntBuffer intBuffer = byteBuffer.asIntBuffer();
    /** The same memory as byteBuffer, but referenced as an float buffer. */
    @Shadow
    private static FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    /** The same memory as byteBuffer, but referenced as an short buffer. */
    @Shadow
    private static ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
    /** Raw integer array. */
    @Shadow
    private int[] rawBuffer;

    /** The number of vertices to be drawn in the next draw call. Reset to 0 between draw calls. */
    @Shadow
    private int vertexCount;
    /** The first coordinate to be used for the texture. */
    @Shadow
    private double textureU;
    /** The second coordinate to be used for the texture. */
    @Shadow
    private double textureV;
    @Shadow
    private int brightness;
    /** The color (RGBA) value to be used for the following draw call. */
    @Shadow
    private int color;
    /** Whether the current draw object for this tessellator has color values. */
    @Shadow
    private boolean hasColor;
    /** Whether the current draw object for this tessellator has texture coordinates. */
    @Shadow
    private boolean hasTexture;
    @Shadow
    private boolean hasBrightness;
    /** Whether the current draw object for this tessellator has normal values. */
    @Shadow
    private boolean hasNormals;
    /** The index into the raw buffer to be used for the next data. */
    @Shadow
    private int rawBufferIndex;
    /**
     * The number of vertices manually added to the given draw call. This differs from vertexCount because it adds extra
     * vertices when converting quads to triangles.
     */
    @Shadow
    private int addedVertices;
    /** Disables all color information for the following draw call. */
    @Shadow
    private boolean isColorDisabled;
    /** The draw mode currently being used by the tessellator. */
    @Shadow
    private int drawMode;
    /** An offset to be applied along the x-axis for all vertices in this draw call. */
    @Shadow
    private double xOffset;
    /** An offset to be applied along the y-axis for all vertices in this draw call. */
    @Shadow
    private double yOffset;
    /** An offset to be applied along the z-axis for all vertices in this draw call. */
    @Shadow
    private double zOffset;
    /** The normal to be applied to the face being drawn. */
    @Shadow
    private int normal;

    /** Whether this tessellator is currently in draw mode. */
    @Shadow
    private boolean isDrawing;
    /** The size of the buffers used (in integers). */
    @Shadow
    private int bufferSize;

    /**
     * Draws the data set up in this tessellator and resets the state to prepare for new drawing.
     */
    @Overwrite
    public int draw() {
        if (OptimizationsandTweaksConfig.enableMixinTesselator) {
            if (!this.isDrawing) {
                throw new IllegalStateException("Not tesselating!");
            } else {
                this.isDrawing = false;

                // Move GL enable/disable calls outside the loop
                if (this.hasTexture) {
                    floatBuffer.position(3);
                    GL11.glTexCoordPointer(2, 32, MixinTesselator.floatBuffer);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness) {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    shortBuffer.position(14);
                    GL11.glTexCoordPointer(2, 32, shortBuffer);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor) {
                    byteBuffer.position(20);
                    GL11.glColorPointer(4, true, 32, byteBuffer);
                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals) {
                    byteBuffer.position(24);
                    GL11.glNormalPointer(32, MixinTesselator.byteBuffer);
                    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }

                int offs = 0;
                while (offs < vertexCount) {
                    int vtc = Math.min(vertexCount - offs, nativeBufferSize >> 5);
                    intBuffer.clear();
                    intBuffer.put(this.rawBuffer, offs * 8, vtc * 8);
                    byteBuffer.position(0);
                    byteBuffer.limit(vtc * 32);
                    offs += vtc;

                    floatBuffer.position(0);
                    GL11.glVertexPointer(3, 32, floatBuffer);
                    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                    GL11.glDrawArrays(this.drawMode, 0, vtc);
                    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                }

                // Move GL enable/disable calls outside the loop
                if (this.hasTexture) {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness) {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor) {
                    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals) {
                    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }

                if (rawBufferSize > 0x20000 && rawBufferIndex < (rawBufferSize << 3)) {
                    rawBufferSize = 0x10000;
                    rawBuffer = new int[rawBufferSize];
                }

                int i = this.rawBufferIndex * 4;
                this.optimizationsAndTweaks$reset();
                return i;
            }
        }
        return 0;
    }

    /**
     * Clears the tessellator state in preparation for new drawing.
     */
    @Unique
    private void optimizationsAndTweaks$reset() {
        this.vertexCount = 0;
        byteBuffer.clear();
        this.rawBufferIndex = 0;
        this.addedVertices = 0;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public TesselatorVertexState getVertexState(float p_147564_1_, float p_147564_2_, float p_147564_3_) {
        return new TesselatorVertexState(
            Arrays.copyOf(this.rawBuffer, this.rawBufferIndex),
            this.rawBufferIndex,
            this.vertexCount,
            this.hasTexture,
            this.hasBrightness,
            this.hasNormals,
            this.hasColor);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setVertexState(TesselatorVertexState p_147565_1_) {
        int newBufferSize = Math.max(rawBufferSize, p_147565_1_.getRawBuffer().length);
        if (newBufferSize > rawBuffer.length) {
            rawBuffer = Arrays.copyOf(rawBuffer, newBufferSize);
        }
        System.arraycopy(p_147565_1_.getRawBuffer(), 0, this.rawBuffer, 0, p_147565_1_.getRawBuffer().length);
        this.rawBufferIndex = p_147565_1_.getRawBufferIndex();
        this.vertexCount = p_147565_1_.getVertexCount();
        this.hasTexture = p_147565_1_.getHasTexture();
        this.hasBrightness = p_147565_1_.getHasBrightness();
        this.hasColor = p_147565_1_.getHasColor();
        this.hasNormals = p_147565_1_.getHasNormals();
    }
}
