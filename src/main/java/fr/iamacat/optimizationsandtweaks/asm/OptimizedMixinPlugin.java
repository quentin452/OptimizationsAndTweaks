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
        // Resolve mixins FIRST: its one-shot migrator reads the old
        // optimizationsandtweaks.cfg while it still holds the pre-refactor
        // per-mixin booleans. ConfigurationManager.initialize() rewrites that
        // file down to the surviving fields, so it must run AFTER the migrator.
        MixinConfigResolver.INSTANCE.load();
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
