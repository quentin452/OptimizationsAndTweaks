package fr.iamacat.optimizationsandtweaks.config;

import net.minecraft.client.gui.GuiScreen;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.SimpleGuiConfig;

import fr.iamacat.optimizationsandtweaks.Tags;

public class OptimizationsandTweaksGuiConfig extends SimpleGuiConfig {

    public OptimizationsandTweaksGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, OptimizationsandTweaksConfig.class, Tags.MODID, Tags.MODNAME);
    }
}
