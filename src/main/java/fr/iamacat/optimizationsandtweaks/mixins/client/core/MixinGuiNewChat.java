package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
@SideOnly(Side.CLIENT)
@Mixin(value = GuiNewChat.class,priority = 999)
public class MixinGuiNewChat  extends Gui {
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    @Shadow
    private final Minecraft mc;
    /** A list of messages previously sent through the chat GUI */
    @Shadow
    private final List sentMessages = new ArrayList();
    /** Chat lines to be displayed in the chat box */
    @Shadow
    private final List chatLines = new ArrayList();
    @Shadow
    private final List field_146253_i = new ArrayList();
    @Shadow
    private int field_146250_j;
    @Shadow
    private boolean field_146251_k;

    public MixinGuiNewChat(Minecraft mc) {
        this.mc = mc;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void drawChat(int p_146230_1_) {
        if (this.mc.gameSettings.chatVisibility == EntityPlayer.EnumChatVisibility.HIDDEN) {
            return;
        }

        int j = this.func_146232_i();
        boolean flag = this.getChatOpen();
        int l = this.field_146253_i.size();

        if (l > 0 && flag) {
            float f1 = this.func_146244_h();
            int i1 = MathHelper.ceiling_float_int(this.func_146228_f() / f1);

            GL11.glPushMatrix();
            GL11.glTranslatef(2.0F, 20.0F, 0.0F);
            GL11.glScalef(f1, f1, 1.0F);

            for (int j1 = 0; j1 + this.field_146250_j < this.field_146253_i.size() && j1 < j; ++j1) {
                ChatLine chatline = (ChatLine)this.field_146253_i.get(j1 + this.field_146250_j);

                if (chatline != null) {

                    int i2;
                    i2 = 255;

                    int j2 = -j1 * 9;
                    drawRect(0, j2 - 9, i1 + 4, j2, i2 / 2 << 24);
                    GL11.glEnable(GL11.GL_BLEND);
                    String s = chatline.func_151461_a().getFormattedText();
                    this.mc.fontRenderer.drawStringWithShadow(s, 0, j2 - 8, 16777215 + (i2 << 24));
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                }
            }

            int j1 = this.mc.fontRenderer.FONT_HEIGHT;
            GL11.glTranslatef(-3.0F, 0.0F, 0.0F);
            int k2 = l * j1 + l;
            int l1 = j * j1 + j;
            int i3 = this.field_146250_j * l1 / l;
            int l2 = l1 * l1 / k2;

            if (k2 != l1) {
                int i2 = i3 > 0 ? 170 : 96;
                int i4 = this.field_146251_k ? 13382451 : 3355562;
                drawRect(0, -i3, 2, -i3 - l2, i4 + (i2 << 24));
                drawRect(2, -i3, 1, -i3 - l2, 13421772 + (i2 << 24));
            }

            GL11.glPopMatrix();
        }
    }

    @Shadow
    public int func_146232_i()
    {
        return this.func_146246_g() / 9;
    }
    @Shadow
    public int func_146246_g()
    {
        return func_146243_b(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }
    @Shadow
    public static int func_146243_b(float p_146243_0_)
    {
        short short1 = 180;
        byte b0 = 20;
        return MathHelper.floor_float(p_146243_0_ * (short1 - b0) + b0);
    }
    @Shadow
    public boolean getChatOpen()
    {
        return this.mc.currentScreen instanceof GuiChat;
    }
    @Shadow
    public float func_146244_h()
    {
        return this.mc.gameSettings.chatScale;
    }
    @Shadow
    public int func_146228_f()
    {
        return func_146233_a(this.mc.gameSettings.chatWidth);
    }
    @Shadow
    public static int func_146233_a(float p_146233_0_)
    {
        short short1 = 320;
        byte b0 = 40;
        return MathHelper.floor_float(p_146233_0_ * (short1 - b0) + b0);
    }
}
