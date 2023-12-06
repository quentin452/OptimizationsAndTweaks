package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {

    @Shadow
    public int FONT_HEIGHT = 9;
    @Shadow
    public Random fontRandom = new Random();

    @Shadow
    private int textColor;

    /** If true, strings should be rendered with Unicode fonts instead of the default.png font */
    @Shadow
    private boolean unicodeFlag;
    /** If true, the Unicode Bidirectional Algorithm should be run before rendering any string. */
    @Shadow
    private boolean bidiFlag;
    /** Used to specify new red value for the current color. */
    @Shadow
    private float red;
    /** Used to specify new blue value for the current color. */
    @Shadow
    private float blue;
    /** Used to specify new green value for the current color. */
    @Shadow
    private float green;
    /** Used to speify new alpha value for the current color. */
    @Shadow
    private float alpha;

    /** Set if the "k" style (random) is active in currently rendering string */
    @Shadow
    private boolean randomStyle;
    /** Set if the "l" style (bold) is active in currently rendering string */
    @Shadow
    private boolean boldStyle;
    /** Set if the "o" style (italic) is active in currently rendering string */
    @Shadow
    private boolean italicStyle;
    /** Set if the "n" style (underlined) is active in currently rendering string */
    @Shadow
    private boolean underlineStyle;
    /** Set if the "m" style (strikethrough) is active in currently rendering string */
    @Shadow
    private boolean strikethroughStyle;
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

    @Unique
    public float renderCharAtPos(int p_78278_1_, char p_78278_2_, boolean p_78278_3_) {
        if (p_78278_2_ == 32) {
            return 4.0F;
        } else {
            boolean isSpecialCharacter = SPECIAL_CHARACTERS_SET.contains(p_78278_2_) && !this.unicodeFlag;
            return isSpecialCharacter ? this.renderDefaultChar(p_78278_1_, p_78278_3_)
                : this.renderUnicodeChar(p_78278_2_, p_78278_3_);
        }
    }

    /**
     * Render a single character with the default.png font at current (posX,posY) location...
     */
    @Unique
    public float renderDefaultChar(int p_78266_1_, boolean p_78266_2_) {
        float f = (float) (p_78266_1_ % 16 * 8);
        float f1 = (float) (p_78266_1_ / 16 * 8);
        float f2 = p_78266_2_ ? 1.0F : 0.0F;
        float f3 = (float) this.charWidth[p_78266_1_] - 0.01F;

        // Precompute texture coordinates
        float[] texCoords = { f / 128.0F, f1 / 128.0F, f / 128.0F, (f1 + 7.99F) / 128.0F, (f + f3 - 1.0F) / 128.0F,
            f1 / 128.0F, (f + f3 - 1.0F) / 128.0F, (f1 + 7.99F) / 128.0F };

        // Precompute vertices
        float[] vertices = { this.posX + f2, this.posY, 0.0F, this.posX - f2, this.posY + 7.99F, 0.0F,
            this.posX + f3 - 1.0F + f2, this.posY, 0.0F, this.posX + f3 - 1.0F - f2, this.posY + 7.99F, 0.0F };

        bindTexture(this.locationFontTexture);

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        for (int i = 0; i < 4; i++) {
            GL11.glTexCoord2f(texCoords[i * 2], texCoords[i * 2 + 1]);
            GL11.glVertex3f(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]);
        }

        GL11.glEnd();

        return (float) this.charWidth[p_78266_1_];
    }

    @Unique
    public float renderUnicodeChar(char p_78277_1_, boolean p_78277_2_) {
        if (this.glyphWidth[p_78277_1_] == 0) {
            return 0.0F;
        } else {
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
    protected void bindTexture(ResourceLocation location) {
        renderEngine.bindTexture(location);
    }

    @Shadow
    private void loadGlyphTexture(int p_78257_1_) {
        bindTexture(this.getUnicodePageLocation(p_78257_1_));
    }

    @Unique
    public ResourceLocation getUnicodePageLocation(int p_111271_1_) {
        if (unicodePageLocations[p_111271_1_] == null) {
            unicodePageLocations[p_111271_1_] = new ResourceLocation(
                String.format("textures/font/unicode_page_%02x.png", p_111271_1_));
        }

        return unicodePageLocations[p_111271_1_];
    }

    /**
     * Draws the specified string with a shadow.
     */
    public int drawStringWithShadow(String text, int x, int y, int color)
    {
        return this.drawString(text, x, y, color, true);
    }

    /**
     * Draws the specified string.
     */
    public int drawString(String text, int x, int y, int color)
    {
        return this.drawString(text, x, y, color, false);
    }

    /**
     * Draws the specified string. Args: string, x, y, color, dropShadow
     */
    public int drawString(String text, int x, int y, int color, boolean dropShadow)
    {
        enableAlpha();
        this.resetStyles();
        int l;

        if (dropShadow)
        {
            l = this.renderString(text, x + 1, y + 1, color, true);
            l = Math.max(l, this.renderString(text, x, y, color, false));
        }
        else
        {
            l = this.renderString(text, x, y, color, false);
        }

        return l;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getCharWidth(char p_78263_1_)
    {
        if (p_78263_1_ == 167)
        {
            return -1;
        }
        else if (p_78263_1_ == 32)
        {
            return 4;
        }
        else
        {
            int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(p_78263_1_);

            if (p_78263_1_ > 0 && i != -1 && !this.unicodeFlag)
            {
                return this.charWidth[i];
            }
            else if (this.glyphWidth[p_78263_1_] != 0)
            {
                int j = this.glyphWidth[p_78263_1_] >>> 4;
                int k = this.glyphWidth[p_78263_1_] & 15;

                if (k > 7)
                {
                    k = 15;
                    j = 0;
                }

                ++k;
                return (k - j) / 2 + 1;
            }
            else
            {
                return 0;
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getStringWidth(String p_78256_1_)
    {
        if (p_78256_1_ == null)
        {
            return 0;
        }
        else
        {
            int i = 0;
            boolean flag = false;

            for (int j = 0; j < p_78256_1_.length(); ++j)
            {
                char c0 = p_78256_1_.charAt(j);
                int k = this.getCharWidth(c0);

                if (k < 0 && j < p_78256_1_.length() - 1)
                {
                    ++j;
                    c0 = p_78256_1_.charAt(j);

                    if (c0 != 108 && c0 != 76)
                    {
                        if (c0 == 114 || c0 == 82)
                        {
                            flag = false;
                        }
                    }
                    else
                    {
                        flag = true;
                    }

                    k = 0;
                }

                i += k;

                if (flag && k > 0)
                {
                    ++i;
                }
            }

            return i;
        }
    }
    @Unique
    private int renderStringAligned(String p_78274_1_, int p_78274_2_, int p_78274_3_, int p_78274_4_, int p_78274_5_, boolean p_78274_6_)
    {
        if (this.bidiFlag)
        {
            int i1 = this.getStringWidth(this.bidiReorder(p_78274_1_));
            p_78274_2_ = p_78274_2_ + p_78274_4_ - i1;
        }

        return this.renderString(p_78274_1_, p_78274_2_, p_78274_3_, p_78274_5_, p_78274_6_);
    }

    @Unique
    public int renderString(String p_78258_1_, int p_78258_2_, int p_78258_3_, int p_78258_4_, boolean p_78258_5_) {
        if (p_78258_1_ == null) {
            return 0;
        } else {
            if (this.bidiFlag) {
                p_78258_1_ = this.bidiReorder(p_78258_1_);
            }

            if ((p_78258_4_ & -67108864) == 0) {
                p_78258_4_ |= -16777216;
            }

            if (p_78258_5_) {
                p_78258_4_ = (p_78258_4_ & 16579836) >> 2 | p_78258_4_ & -16777216;
            }

            this.red = (p_78258_4_ >> 16 & 255) / 255.0F;
            this.blue = (p_78258_4_ >> 8 & 255) / 255.0F;
            this.green = (p_78258_4_ & 255) / 255.0F;
            this.alpha = (p_78258_4_ >> 24 & 255) / 255.0F;
            setColor(this.red, this.blue, this.green, this.alpha);
            this.posX = p_78258_2_;
            this.posY = p_78258_3_;
            this.renderStringAtPos(p_78258_1_, p_78258_5_);
            return (int) this.posX;
        }
   }

    /**
     * Render a single line string at the current (posX,posY) and update posX
     */

    @Unique
    public void renderStringAtPos(String p_78255_1_, boolean p_78255_2_) {
        for (int i = 0; i < p_78255_1_.length(); ++i) {
            char c0 = p_78255_1_.charAt(i);
            int j;
            int k;

            if (c0 == 167 && i + 1 < p_78255_1_.length()) {
                j = "0123456789abcdefklmnor".indexOf(
                    p_78255_1_.toLowerCase()
                        .charAt(i + 1));

                if (j < 16) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    if (j < 0) {
                        j = 15;
                    }

                    if (p_78255_2_) {
                        j += 16;
                    }

                    k = this.colorCode[j];
                    this.textColor = k;
                    setColor((k >> 16) / 255.0F, (k >> 8 & 255) / 255.0F, (k & 255) / 255.0F, this.alpha);
                } else if (j == 16) {
                    this.randomStyle = true;
                } else if (j == 17) {
                    this.boldStyle = true;
                } else if (j == 18) {
                    this.strikethroughStyle = true;
                } else if (j == 19) {
                    this.underlineStyle = true;
                } else if (j == 20) {
                    this.italicStyle = true;
                } else {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    setColor(this.red, this.blue, this.green, this.alpha);
                }

                ++i;
            } else {
                j = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
                    .indexOf(c0);

                if (this.randomStyle && j != -1) {
                    do {
                        k = this.fontRandom.nextInt(this.charWidth.length);
                    } while (this.charWidth[j] != this.charWidth[k]);

                    j = k;
                }

                float f1 = this.unicodeFlag ? 0.5F : 1.0F;
                boolean flag1 = (c0 == 0 || j == -1 || this.unicodeFlag) && p_78255_2_;

                if (flag1) {
                    this.posX -= f1;
                    this.posY -= f1;
                }

                float f = this.renderCharAtPos(j, c0, this.italicStyle);

                if (flag1) {
                    this.posX += f1;
                    this.posY += f1;
                }

                if (this.boldStyle) {
                    this.posX += f1;

                    if (flag1) {
                        this.posX -= f1;
                        this.posY -= f1;
                    }

                    this.renderCharAtPos(j, c0, this.italicStyle);
                    this.posX -= f1;

                    if (flag1) {
                        this.posX += f1;
                        this.posY += f1;
                    }

                    ++f;
                }

                doDraw(f);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void doDraw(float f) {
        {
            {
                Tessellator tessellator;

                if (this.strikethroughStyle) {
                    tessellator = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    tessellator.addVertex(this.posX, this.posY + ((double) this.FONT_HEIGHT / 2), 0.0D);
                    tessellator.addVertex(this.posX + f, this.posY + ((double) this.FONT_HEIGHT / 2), 0.0D);
                    tessellator.addVertex(this.posX + f, this.posY + ((double) this.FONT_HEIGHT / 2) - 1.0F, 0.0D);
                    tessellator.addVertex(this.posX, this.posY + ((double) this.FONT_HEIGHT / 2) - 1.0F, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                if (this.underlineStyle) {
                    tessellator = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    int l = this.underlineStyle ? -1 : 0;
                    tessellator.addVertex(this.posX + l, this.posY + this.FONT_HEIGHT, 0.0D);
                    tessellator.addVertex(this.posX + f, this.posY + this.FONT_HEIGHT, 0.0D);
                    tessellator.addVertex(this.posX + f, this.posY + this.FONT_HEIGHT - 1.0F, 0.0D);
                    tessellator.addVertex(this.posX + l, this.posY + this.FONT_HEIGHT - 1.0F, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                this.posX += (float) ((int) f);
            }
        }
    }

    /**
     * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
     */
    @Overwrite
    public String bidiReorder(String p_147647_1_) {
        try {
            Bidi bidi = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException arabicshapingexception) {
            return p_147647_1_;
        }
    }

    @Shadow
    protected void enableAlpha() {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    @Shadow
    private void resetStyles() {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    @Shadow
    protected void setColor(float r, float g, float b, float a) {
        GL11.glColor4f(r, g, b, a);
    }

}
