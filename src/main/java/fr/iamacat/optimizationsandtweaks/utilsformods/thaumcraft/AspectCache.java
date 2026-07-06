package fr.iamacat.optimizationsandtweaks.utilsformods.thaumcraft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.item.Item;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * DISK cache for inferred Thaumcraft aspects (generateTagsFromRecipes).
 * Inference brute-forces recipe matching across the whole pack: ~39s of
 * postInit at 929 mods (JFR: 24% of samples in checkMatch/matches).
 * Results only depend on the recipe graph, so they are stable for a given
 * modlist. Invalidation: modlist (id+version) + MineTweaker scripts.
 * NULL results are cached too (no-aspect items each cost a full scan).
 */
public final class AspectCache {

    private static final String NULL_MARKER = "-";
    private static volatile boolean loaded = false;
    private static boolean dirty = false;
    private static String hash;
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    private AspectCache() {}

    private static boolean enabled() {
        return OptimizationsandTweaksConfig.enableThaumcraftAspectCache;
    }

    /** null = not cached; empty AspectList (size 0) = cached "no aspects" result. */
    public static AspectList get(Item item, int meta) {
        if (!enabled()) {
            return null;
        }
        ensureLoaded();
        String value = CACHE.get(key(item, meta));
        if (value == null) {
            return null;
        }
        AspectList tags = new AspectList();
        if (NULL_MARKER.equals(value)) {
            return tags; // marker: computed, no aspects
        }
        for (String pair : value.split(",")) {
            int eq = pair.indexOf('=');
            if (eq <= 0) continue;
            Aspect aspect = Aspect.aspects.get(pair.substring(0, eq));
            if (aspect == null) {
                return null; // aspect gone (aspect-adding mod removed): recompute
            }
            tags.add(aspect, Integer.parseInt(pair.substring(eq + 1)));
        }
        return tags;
    }

    public static void put(Item item, int meta, AspectList tags) {
        if (!enabled()) {
            return;
        }
        ensureLoaded();
        String value;
        if (tags == null || tags.size() == 0) {
            value = NULL_MARKER;
        } else {
            StringBuilder sb = new StringBuilder();
            for (Aspect aspect : tags.getAspects()) {
                if (aspect == null) continue;
                if (sb.length() > 0) sb.append(',');
                sb.append(aspect.getTag())
                    .append('=')
                    .append(tags.getAmount(aspect));
            }
            value = sb.length() == 0 ? NULL_MARKER : sb.toString();
        }
        String k = key(item, meta);
        if (!value.equals(CACHE.put(k, value))) {
            dirty = true;
        }
    }

    /** Call at FMLLoadCompleteEvent (the game may exit via halt(): never rely on a shutdown hook). */
    public static void saveIfDirty() {
        if (!enabled() || !loaded || !dirty) {
            return;
        }
        try {
            File file = cacheFile();
            file.getParentFile()
                .mkdirs();
            try (BufferedWriter w = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                w.write(currentHash());
                w.newLine();
                for (Map.Entry<String, String> e : CACHE.entrySet()) {
                    w.write(e.getKey() + "\t" + e.getValue());
                    w.newLine();
                }
            }
            dirty = false;
            FMLLog.info("[OptimizationsAndTweaks] Aspect cache saved: %d entries", CACHE.size());
        } catch (Exception e) {
            FMLLog.warning("[OptimizationsAndTweaks] Could not save aspect cache: %s", e);
        }
    }

    private static String key(Item item, int meta) {
        Object name = Item.itemRegistry.getNameForObject(item);
        return name + "@" + meta;
    }

    private static synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            hash = currentHash();
            File file = cacheFile();
            if (!file.isFile()) {
                FMLLog.info("[OptimizationsAndTweaks] No aspect cache (cold boot): it will be generated this run");
                return;
            }
            try (BufferedReader r = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                String fileHash = r.readLine();
                if (!hash.equals(fileHash)) {
                    FMLLog.info("[OptimizationsAndTweaks] Aspect cache invalidated (modlist/scripts changed)");
                    return;
                }
                String line;
                while ((line = r.readLine()) != null) {
                    int tab = line.indexOf('\t');
                    if (tab > 0) {
                        CACHE.put(line.substring(0, tab), line.substring(tab + 1));
                    }
                }
            }
            FMLLog.info("[OptimizationsAndTweaks] Aspect cache loaded: %d entries", CACHE.size());
        } catch (Exception e) {
            CACHE.clear();
            FMLLog.warning("[OptimizationsAndTweaks] Unreadable aspect cache (ignored): %s", e);
        }
    }

    private static File cacheFile() {
        return new File(
            Loader.instance()
                .getConfigDir(),
            "optimizationsandtweaks/thaumcraft-aspect-cache.txt");
    }

    /** Invalidation hash: modlist (id+version) + MineTweaker scripts (name+size+mtime). */
    private static String currentHash() throws Exception {
        List<String> parts = new ArrayList<>();
        for (ModContainer mod : Loader.instance()
            .getActiveModList()) {
            parts.add(mod.getModId() + ":" + mod.getVersion());
        }
        File scripts = new File(
            Loader.instance()
                .getConfigDir()
                .getParentFile(),
            "scripts");
        File[] zs = scripts.listFiles();
        if (zs != null) {
            for (File f : zs) {
                parts.add(f.getName() + ":" + f.length() + ":" + f.lastModified());
            }
        }
        Collections.sort(parts);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (String p : parts) {
            md.update(p.getBytes(StandardCharsets.UTF_8));
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : md.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
