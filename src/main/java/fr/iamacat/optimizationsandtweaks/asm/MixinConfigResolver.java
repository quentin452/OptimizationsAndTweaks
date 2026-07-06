package fr.iamacat.optimizationsandtweaks.asm;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.common.config.Configuration;

import com.falsepattern.lib.mixin.IMixin;

/**
 * Generic, name-driven resolver for whether a {@link Mixin} entry should be applied.
 * <p>
 * This replaces the historical one-boolean-per-mixin fields that used to live in
 * {@code OptimizationsandTweaksConfig}: the {@link Mixin} enum is now the single source of
 * truth (entry name + declared mod-presence filter), and this class resolves the
 * enable/disable decision from a small, human-editable surface:
 * <ol>
 * <li>{@code forceEnabledMixins} (exact entry name) always wins and enables the mixin.</li>
 * <li>{@code disabledMixins} (exact entry name) disables the mixin - surgical opt-out /
 * crash bisecting.</li>
 * <li>A per-category toggle: {@code categories.core}, {@code categories.client}, or
 * {@code modcompat.<modid>} (auto-derived from the entry name prefix / mixin package).</li>
 * <li>A small built-in default-disabled list, for the handful of mixins that have always
 * shipped disabled by default (kept identical to the pre-refactor defaults).</li>
 * </ol>
 * <p>
 * IMPORTANT: this class is loaded at {@code IFMLLoadingPlugin} (coremod) stage, before
 * Minecraft itself is on the classpath. It must never reference any {@code net.minecraft.*}
 * class. {@link Configuration} is a plain Forge utility class (no MC classes involved) and is
 * safe to use here.
 */
public final class MixinConfigResolver {

    public static final MixinConfigResolver INSTANCE = new MixinConfigResolver();

    /**
     * Exact {@link Mixin} entry names that default to disabled, matching the pre-refactor
     * {@code @Config.DefaultBoolean(false)} fields they used to be gated by. Anything not
     * listed here defaults to enabled.
     */
    private static final Set<String> DEFAULT_DISABLED = Collections.unmodifiableSet(
        new HashSet<>(
            Arrays.asList(
                "client_core_MixinRenderBlocks",
                "client_essenceofthegod_MixinBarTickHandler",
                "client_essenceofthegod_MixinPlayerStats",
                "client_practicallogistics_MixinEventRegistry",
                "common_core_MixinCompressedStreamTools",
                "common_core_MixinGodZillaFix",
                "common_growthcraft_MixinAppleFuelHandler",
                "common_jewelrycraft2_MixinEntityEventHandler",
                "common_minenautica_MixinAluminumOxideWorldGen",
                "common_minenautica_MixinBiomeGenGrassyPlateaus",
                "common_minenautica_MixinBiomeGenKelpForest",
                "common_minenautica_MixinBloodgrass",
                "common_minenautica_MixinCanBlockStay",
                "common_opis_MixinopisProfilerEvent")));

    private boolean loaded = false;
    private boolean coreEnabled = true;
    private boolean clientEnabled = true;
    private final Map<String, Boolean> modCompatEnabled = new HashMap<>();
    private final Set<String> disabledMixins = new HashSet<>();
    private final Set<String> forceEnabledMixins = new HashSet<>();

    private MixinConfigResolver() {}

    /**
     * Reads (and if needed creates/upgrades) {@code config/optimizationsandtweaks-mixins.cfg},
     * relative to the current working directory (the Minecraft instance root - valid at
     * coremod stage, well before FML resolves its own mod-specific config directory).
     * Safe to call more than once (e.g. also from regular mod code after FML has started):
     * subsequent calls are no-ops.
     */
    public synchronized void load() {
        if (loaded) return;
        File configFile = new File("config", "optimizationsandtweaks-mixins.cfg");
        Configuration cfg = new Configuration(configFile);
        try {
            cfg.load();

            coreEnabled = cfg.getBoolean(
                "core",
                "categories",
                true,
                "Vanilla/Forge core optimization mixins (common + server side).");
            clientEnabled = cfg
                .getBoolean("client", "categories", true, "Vanilla/Forge core client-side mixins (rendering tweaks).");

            Set<String> modCategories = new TreeSet<>();
            for (Mixin mixin : Mixin.values()) {
                String category = category(mixin);
                if (isModCompat(category)) {
                    modCategories.add(modCompatId(category));
                }
            }
            for (String modid : modCategories) {
                modCompatEnabled.put(
                    modid,
                    cfg.getBoolean(
                        "modcompat_" + modid,
                        "modcompat",
                        true,
                        "Mixins targeting/compatible with " + modid + "."));
            }

            disabledMixins.addAll(
                Arrays.asList(
                    cfg.getStringList(
                        "disabledMixins",
                        "advanced",
                        new String[0],
                        "Exact Mixin enum entry names to force-disable, regardless of the category "
                            + "toggles above. Use this for surgical opt-out / crash bisecting.")));
            forceEnabledMixins.addAll(
                Arrays.asList(
                    cfg.getStringList(
                        "forceEnabledMixins",
                        "advanced",
                        new String[0],
                        "Exact Mixin enum entry names to force-enable, regardless of the category "
                            + "toggles above or their built-in default.")));
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
            loaded = true;
        }
    }

    public boolean isEnabled(Mixin mixin) {
        return isEnabled(mixin.name(), category(mixin));
    }

    private boolean isEnabled(String name, String category) {
        if (!loaded) load();
        if (forceEnabledMixins.contains(name)) return true;
        if (disabledMixins.contains(name)) return false;

        boolean categoryEnabled;
        if ("core".equals(category)) {
            categoryEnabled = coreEnabled;
        } else if ("client".equals(category)) {
            categoryEnabled = clientEnabled;
        } else {
            categoryEnabled = modCompatEnabled.getOrDefault(modCompatId(category), true);
        }
        if (!categoryEnabled) return false;

        return !DEFAULT_DISABLED.contains(name);
    }

    private static boolean isModCompat(String category) {
        return category.startsWith("modcompat.");
    }

    private static String modCompatId(String category) {
        return category.substring("modcompat.".length());
    }

    /**
     * Derives the toggle category of a mixin entry from its side and its declared path
     * (the mixin implementation package, e.g. {@code "core.MixinWorld"} -> {@code "core"},
     * {@code "buildcraft.addon.oiltweaks.MixinOilTweakEventHandler"} -> {@code "buildcraft"}).
     * The vanilla/Forge "core" package is split into {@code "core"} (common/server side) and
     * {@code "client"} (client side); everything else is a mod-compat category.
     */
    static String category(Mixin mixin) {
        String path = mixin.getMixin();
        int dot = path.indexOf('.');
        String topPackage = dot >= 0 ? path.substring(0, dot) : path;
        if ("core".equals(topPackage)) {
            return mixin.getSide() == IMixin.Side.CLIENT ? "client" : "core";
        }
        return "modcompat." + topPackage;
    }
}
