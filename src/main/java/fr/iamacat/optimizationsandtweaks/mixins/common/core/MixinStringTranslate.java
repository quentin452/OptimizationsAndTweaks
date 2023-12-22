package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.IllegalFormatException;
import java.util.Map;

import net.minecraft.util.StringTranslate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.Maps;

@Mixin(StringTranslate.class)
public class MixinStringTranslate {

    @Shadow
    private final Map<String, String> languageList = Maps.newHashMap();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public synchronized String translateKeyFormat(String key, Object... format) {
        String translatedKey = tryTranslateKey(key);
        try {
            return String.format(translatedKey, format);
        } catch (IllegalFormatException e) {
            return "Format error: " + translatedKey;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private String tryTranslateKey(String key) {
        return languageList.getOrDefault(key, key);
    }
}
