package fr.iamacat.multithreading.config;

import com.falsepattern.lib.config.SimpleGuiFactory;

import net.minecraft.client.gui.GuiScreen;

public class MultithreadingandtweaksGuiConfigFactory implements SimpleGuiFactory {
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return MultithreadingandtweaksGuiConfig.class;
    }
}
