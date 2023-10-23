package fr.iamacat.optimizationsandtweaks.mixins.common.ic2;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import ic2.core.util.Config;

@Mixin(Config.class)
public class MixinConfig {

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static List<String> split(String str, char splitChar) {
        if (OptimizationsandTweaksConfig.enableMixinConfig) {
            List<String> ret = new ArrayList();
            StringBuilder current = new StringBuilder();
            boolean empty = true;
            boolean passNext = false;
            boolean quoted = false;

            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                if (passNext) {
                    current.append(c);
                    empty = false;
                    passNext = false;
                } else if (c == '\\') {
                    current.append(c);
                    empty = false;
                    passNext = true;
                } else if (c == '"') {
                    current.append(c);
                    empty = false;
                    quoted = !quoted;
                } else if (!quoted && c == splitChar) {
                    ret.add(
                        current.toString()
                            .trim());
                    current = new StringBuilder();
                    empty = true;
                } else if (!Character.isWhitespace(c) || !empty) {
                    current.append(c);
                    empty = false;
                }
            }

            ret.add(
                current.toString()
                    .trim());
            return ret;
        }
        return null;
    }
}
