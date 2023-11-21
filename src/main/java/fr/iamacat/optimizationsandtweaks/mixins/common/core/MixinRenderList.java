package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderList;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.IntBuffer;

@Mixin(RenderList.class)
public class MixinRenderList {
    /** The location of the 16x16x16 render chunk rendered by this RenderList. */
    @Shadow
    public int renderChunkX;
    @Shadow
    public int renderChunkY;
    @Shadow
    public int renderChunkZ;
    /** The in-world location of the camera, used to translate the world into the proper position for rendering. */
    @Shadow
    private double cameraX;
    @Shadow
    private double cameraY;
    @Shadow
    private double cameraZ;
    /** A list of OpenGL render list IDs rendered by this RenderList. */
    @Shadow
    private IntBuffer glLists = GLAllocation.createDirectIntBuffer(65536);
    /** Does this RenderList contain properly-initialized and current data for rendering? */
    @Shadow
    private boolean valid;
    /** Has glLists been flipped to make it ready for reading yet? */
    @Shadow
    private boolean bufferFlipped;
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setupRenderList(int p_78422_1_, int p_78422_2_, int p_78422_3_, double p_78422_4_, double p_78422_6_, double p_78422_8_)
    {
        this.valid = true;
        this.glLists.clear();
        this.renderChunkX = p_78422_1_;
        this.renderChunkY = p_78422_2_;
        this.renderChunkZ = p_78422_3_;
        this.cameraX = p_78422_4_;
        this.cameraY = p_78422_6_;
        this.cameraZ = p_78422_8_;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void addGLRenderList(int p_78420_1_)
    {
        this.glLists.put(p_78420_1_);

        if (this.glLists.remaining() == 0)
        {
            this.callLists();
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void callLists()
    {
        if (this.valid)
        {
            if (!this.bufferFlipped)
            {
                this.glLists.flip();
                this.bufferFlipped = true;
            }

            if (this.glLists.remaining() > 0)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(this.renderChunkX - this.cameraX), (float)(this.renderChunkY - this.cameraY), (float)(this.renderChunkZ - this.cameraZ));
                GL11.glCallLists(this.glLists);
                GL11.glPopMatrix();
            }
        }
    }
}
