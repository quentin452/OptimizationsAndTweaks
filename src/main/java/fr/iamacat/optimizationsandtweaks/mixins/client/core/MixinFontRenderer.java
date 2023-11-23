package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {
    @Shadow
    private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];

    @Shadow
    protected int[] charWidth = new int[256];
    @Shadow
    private final TextureManager renderEngine;
    @Shadow
    private int[] colorCode = new int[32];
    @Shadow
    protected final ResourceLocation locationFontTexture;

    /** Current X coordinate at which to draw the next character. */
    @Shadow
    protected float posX;
    /** Current Y coordinate at which to draw the next character. */
    @Shadow
    protected float posY;

    @Shadow
    private boolean unicodeFlag;

    @Shadow
    protected byte[] glyphWidth = new byte[65536];

    public MixinFontRenderer(TextureManager p_i1035_3_, ResourceLocation p_i1035_2_) {
        this.locationFontTexture = p_i1035_2_;
        this.renderEngine = p_i1035_3_;
    }

    /**
     * Pick how to render a single character and return the width used.
     */
    @Unique
    private static final String SPECIAL_CHARACTERS = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    @Unique
    private static final Set<Character> SPECIAL_CHARACTERS_SET = new HashSet<>();

    static {
        for (char c : SPECIAL_CHARACTERS.toCharArray()) {
            SPECIAL_CHARACTERS_SET.add(c);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public float renderCharAtPos(int p_78278_1_, char p_78278_2_, boolean p_78278_3_) {
        if (p_78278_2_ == 32) {
            return 4.0F;
        } else {
            boolean isSpecialCharacter = SPECIAL_CHARACTERS_SET.contains(p_78278_2_) && !this.unicodeFlag;
            return isSpecialCharacter ? this.renderDefaultChar(p_78278_1_, p_78278_3_) : this.renderUnicodeChar(p_78278_2_, p_78278_3_);
        }
    }


    /**
     * Render a single character with the default.png font at current (posX,posY) location...
     */
    @Overwrite
    protected float renderDefaultChar(int p_78266_1_, boolean p_78266_2_) {
        float f = (float)(p_78266_1_ % 16 * 8);
        float f1 = (float)(p_78266_1_ / 16 * 8);
        float f2 = p_78266_2_ ? 1.0F : 0.0F;
        float f3 = (float)this.charWidth[p_78266_1_] - 0.01F;

        // Precompute texture coordinates
        float[] texCoords = {
            f / 128.0F, f1 / 128.0F,
            f / 128.0F, (f1 + 7.99F) / 128.0F,
            (f + f3 - 1.0F) / 128.0F, f1 / 128.0F,
            (f + f3 - 1.0F) / 128.0F, (f1 + 7.99F) / 128.0F
        };

        // Precompute vertices
        float[] vertices = {
            this.posX + f2, this.posY, 0.0F,
            this.posX - f2, this.posY + 7.99F, 0.0F,
            this.posX + f3 - 1.0F + f2, this.posY, 0.0F,
            this.posX + f3 - 1.0F - f2, this.posY + 7.99F, 0.0F
        };

        bindTexture(this.locationFontTexture);

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        for (int i = 0; i < 4; i++) {
            GL11.glTexCoord2f(texCoords[i * 2], texCoords[i * 2 + 1]);
            GL11.glVertex3f(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]);
        }

        GL11.glEnd();

        return (float)this.charWidth[p_78266_1_];
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected float renderUnicodeChar(char p_78277_1_, boolean p_78277_2_)
    {
        if (this.glyphWidth[p_78277_1_] == 0)
        {
            return 0.0F;
        }
        else
        {
            int i = p_78277_1_ / 256;
            this.loadGlyphTexture(i);
            int j = this.glyphWidth[p_78277_1_] >>> 4;
            int k = this.glyphWidth[p_78277_1_] & 15;
            float f1 = (k + 1);
            float f2 = (p_78277_1_ % 16 * 16) + (float) j;
            float f3 = ((float) (p_78277_1_ & 255) / 16 * 16);
            float f4 = f1 - j - 0.02F;
            float f5 = p_78277_2_ ? 1.0F : 0.0F;
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
            GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
            GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
            GL11.glVertex3f(this.posX + f4 / 2.0F + f5, this.posY, 0.0F);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F);
            GL11.glEnd();
            return (f1 - j) / 2.0F + 1.0F;
        }
    }

    @Shadow
    protected void bindTexture(ResourceLocation location)
    {
        renderEngine.bindTexture(location);
    }
    @Shadow
    private void loadGlyphTexture(int p_78257_1_)
    {
        bindTexture(this.getUnicodePageLocation(p_78257_1_));
    }

   /**
    * @author
    * @reason
    */
   @Overwrite
   private ResourceLocation getUnicodePageLocation(int p_111271_1_)
   {
       if (unicodePageLocations[p_111271_1_] == null)
       {
           unicodePageLocations[p_111271_1_] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", p_111271_1_));
       }

       return unicodePageLocations[p_111271_1_];
   }
}
