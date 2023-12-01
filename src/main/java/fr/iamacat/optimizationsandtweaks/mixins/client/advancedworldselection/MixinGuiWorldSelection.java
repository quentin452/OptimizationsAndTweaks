package fr.iamacat.optimizationsandtweaks.mixins.client.advancedworldselection;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNoCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import sedridor.worldselectionadvanced.GuiWorldSelection;
import sedridor.worldselectionadvanced.WorldSelectionAdvanced;

@SideOnly(Side.CLIENT)
@Mixin(GuiWorldSelection.class)
public class MixinGuiWorldSelection extends GuiSelectWorld implements GuiYesNoCallback {

    @Shadow
    private boolean unicodeEverywhere;

    public MixinGuiWorldSelection(GuiScreen p_i1054_1_) {
        super(p_i1054_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void drawString(FontRenderer par1FontRenderer, String text, int left, int top, int color,
        boolean unicodeFlag) {
        int scaleFactor = WorldSelectionAdvanced.getScaledResolution()
            .getScaleFactor();
        if (scaleFactor % 2 == 0) {
            // par1FontRenderer.setUnicodeFlag(true); cause conflicts with my MixinFontRenderer
            this.drawString(par1FontRenderer, text, left, top, color);
            // par1FontRenderer.setUnicodeFlag(this.unicodeEverywhere); cause conflicts with my MixinFontRenderer
        } else {
            this.drawString(par1FontRenderer, text, left, top, color);
        }
    }
}
