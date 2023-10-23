package fr.iamacat.optimizationsandtweaks.config;

import net.minecraft.client.gui.GuiScreen;

import com.falsepattern.lib.config.SimpleGuiFactory;

public class OptimizationsandTweaksGuiConfigFactory implements SimpleGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return OptimizationsandTweaksGuiConfig.class;
    }
}
