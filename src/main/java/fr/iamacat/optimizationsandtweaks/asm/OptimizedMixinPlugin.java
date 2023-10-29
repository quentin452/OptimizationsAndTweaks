package fr.iamacat.optimizationsandtweaks.asm;

import org.apache.logging.log4j.Logger;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.ConfigurationManager;
import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.IMixinPlugin;
import com.falsepattern.lib.mixin.ITargetedMod;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import lombok.Getter;

public class OptimizedMixinPlugin implements IMixinPlugin {

    @Getter
    private final Logger logger = IMixinPlugin.createLogger("OptimizationsAndTweaks");

    public OptimizedMixinPlugin() {
        try {
            ConfigurationManager.initialize(OptimizationsandTweaksConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ITargetedMod[] getTargetedModEnumValues() {
        return TargetedMod.values();
    }

    @Override
    public IMixin[] getMixinEnumValues() {
        return Mixin.values();
    }
}
