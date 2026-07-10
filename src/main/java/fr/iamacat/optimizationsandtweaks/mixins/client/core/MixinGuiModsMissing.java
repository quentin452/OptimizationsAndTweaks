package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import java.util.List;

import net.minecraft.client.gui.GuiErrorScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.client.GuiModsMissing;
import cpw.mods.fml.common.MissingModsException;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.fml.MissingModsAggregate;

/**
 * Renders the missing-dependency screen for ALL conflicting mods (grouped by mod), instead of the
 * single set the vanilla screen shows. Reads the aggregated per-mod breakdown collected by
 * {@code MixinFMLClientHandlerMissingDeps} (via {@link MissingModsAggregate}); when that is empty it
 * falls back to the single {@code MissingModsException} the screen was built with, so behaviour never
 * regresses even if the aggregation mixin did not run.
 * <p>
 * The list is line-budgeted to the window height: whatever does not fit is collapsed into a
 * "... and N more" line so the text never draws off-screen no matter how many mods are missing deps.
 */
@Mixin(GuiModsMissing.class)
public abstract class MixinGuiModsMissing extends GuiErrorScreen {

    @Shadow(remap = false)
    private MissingModsException modsMissing;

    private MixinGuiModsMissing() {
        super(null, null);
    }

    /**
     * @author iamacat
     * @reason List every mod with missing dependencies (grouped + paginated), not just the first.
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        final int lineHeight = 10;
        final int footerY = this.height - 16;
        // Last y a body line may occupy (keep clear of the footer, reserve one slot for "... N more").
        final int maxBodyY = footerY - (lineHeight * 2);

        int y = 20;
        this.drawCenteredString(
            this.fontRendererObj,
            "Forge Mod Loader has found a problem with your minecraft installation",
            this.width / 2,
            y,
            0xFFFFFF);
        y += 12;
        this.drawCenteredString(
            this.fontRendererObj,
            "The mods and versions listed below could not be found",
            this.width / 2,
            y,
            0xFFFFFF);
        y += 14;

        List<MissingModsAggregate.Line> lines = MissingModsAggregate.displayLines(this.modsMissing);
        int hidden = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (y > maxBodyY) {
                hidden = lines.size() - i;
                break;
            }
            MissingModsAggregate.Line line = lines.get(i);
            this.drawCenteredString(this.fontRendererObj, line.text, this.width / 2, y, line.color);
            y += lineHeight;
        }
        if (hidden > 0) {
            this.drawCenteredString(
                this.fontRendererObj,
                "... and " + hidden + " more (see logs/fml-client-latest.log)",
                this.width / 2,
                y,
                0xFF5555);
        }

        this.drawCenteredString(
            this.fontRendererObj,
            "The file 'logs/fml-client-latest.log' contains more information",
            this.width / 2,
            footerY,
            0xFFFFFF);
    }
}
