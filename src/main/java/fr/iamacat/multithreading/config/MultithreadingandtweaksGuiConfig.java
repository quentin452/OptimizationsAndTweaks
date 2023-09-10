package fr.iamacat.multithreading.config;

import net.minecraft.client.gui.GuiScreen;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.SimpleGuiConfig;

import fr.iamacat.multithreading.Tags;

public class MultithreadingandtweaksGuiConfig extends SimpleGuiConfig {

    public MultithreadingandtweaksGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, MultithreadingandtweaksConfig.class, Tags.MODID, Tags.MODNAME);
    }
}
