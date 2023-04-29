package fr.iamacat.multithreading.config;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.SimpleGuiConfig;
import fr.iamacat.multithreading.Tags;

import net.minecraft.client.gui.GuiScreen;

public class MultithreadingandtweaksGuiConfig extends SimpleGuiConfig {
    public MultithreadingandtweaksGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, MultithreadingandtweaksConfig.class, Tags.MODID, Tags.MODNAME);
    }
}
