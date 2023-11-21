package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderList;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
@SideOnly(Side.CLIENT)
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
    @Unique
    private ConcurrentSkipListSet<Integer> multithreadingandtweaks$glLists = new ConcurrentSkipListSet<>();

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
        this.multithreadingandtweaks$glLists.clear();
        this.renderChunkX = p_78422_1_;
        this.renderChunkY = p_78422_2_;
        this.renderChunkZ = p_78422_3_;
        this.cameraX = p_78422_4_;
        this.cameraY = p_78422_6_;
        this.cameraZ = p_78422_8_;
    }

    /**
     * Add an OpenGL render list ID to the list.
     */
    @Overwrite
    public void addGLRenderList(int listID) {
        this.multithreadingandtweaks$glLists.add(listID);

        if (this.multithreadingandtweaks$glLists.size() >= 65536) {
            this.callLists();
        }
    }

    /**
     * Render all OpenGL lists in the list.
     */
    @Overwrite
    public void callLists()
    {
        if (this.valid && !this.multithreadingandtweaks$glLists.isEmpty())
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(this.renderChunkX - this.cameraX), (float)(this.renderChunkY - this.cameraY), (float)(this.renderChunkZ - this.cameraZ));

            for (int listID : this.multithreadingandtweaks$glLists) {
                GL11.glCallList(listID);
            }

            GL11.glPopMatrix();
            this.multithreadingandtweaks$glLists.clear();
        }
    }
}
