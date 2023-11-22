package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SideOnly(Side.CLIENT)
@Mixin(TextureUtil.class)
public class MixinTextureUtil {
    @Shadow
    private static int field_147958_e = -1;
    @Shadow
    private static int field_147956_f = -1;
    @Shadow
    private static float field_152779_g = -1.0F;
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void func_152777_a(boolean p_152777_0_, boolean p_152777_1_, float p_152777_2_)
    {
        field_147958_e = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
        field_147956_f = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);
        field_152779_g = GL11.glGetTexParameterf(GL11.GL_TEXTURE_2D, 34046);
        func_147954_b(p_152777_0_, p_152777_1_);
        func_152778_a(p_152777_2_);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void func_147954_b(boolean p_147954_0_, boolean p_147954_1_)
    {
        if (p_147954_0_)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, p_147954_1_ ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }
        else
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, p_147954_1_ ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void func_152778_a(float p_152778_0_)
    {
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 34046, p_152778_0_);
    }
}
