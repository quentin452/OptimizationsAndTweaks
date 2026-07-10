package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.fml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.functions.ArtifactVersionNameFunction;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

/**
 * Cross-mixin holder for the aggregated missing-dependency information collected by
 * {@code MixinLoaderMissingDeps} and rendered by {@code MixinGuiModsMissing}.
 * <p>
 * Vanilla FML aborts {@code Loader.sortModList()} on the FIRST mod whose requirements are not
 * satisfied, so the missing-mods screen only ever lists a single mod. The Loader mixin scans
 * every mod, records what each one is missing here (grouped by mod), and the GUI mixin reads
 * this map to render all of them (paginated so a long list never draws off-screen).
 * <p>
 * The map is insertion-ordered (mod display label -&gt; ordered set of missing/incompatible
 * requirements). It is populated once, just before the aggregated {@code MissingModsException}
 * is thrown, and read on the client render thread; access is single-threaded in practice
 * (startup), so no synchronization is required.
 */
public final class MissingModsAggregate {

    /** Mod display label ("Name (modid)") -&gt; ordered set of its missing / incompatible requirements. */
    public static final Map<String, Set<ArtifactVersion>> GROUPS = new LinkedHashMap<>();

    private MissingModsAggregate() {}

    /** Replaces the stored groups with the given snapshot (defensive copy). */
    public static void set(Map<String, Set<ArtifactVersion>> groups) {
        GROUPS.clear();
        for (Map.Entry<String, Set<ArtifactVersion>> e : groups.entrySet()) {
            GROUPS.put(e.getKey(), new LinkedHashSet<>(e.getValue()));
        }
    }

    /** Total number of missing/incompatible requirement lines across all mods. */
    public static int totalMissing() {
        int n = 0;
        for (Set<ArtifactVersion> s : GROUPS.values()) {
            n += s.size();
        }
        return n;
    }

    public static Map<String, Set<ArtifactVersion>> groups() {
        return Collections.unmodifiableMap(GROUPS);
    }

    /**
     * Scans the full active mod list and builds an aggregated {@link MissingModsException} listing
     * EVERY mod with an unsatisfied requirement (grouped per mod in {@link #GROUPS}), instead of the
     * single mod vanilla FML aborts on. The scan mirrors the read-only verification loop of
     * {@code Loader.sortModList()} (hard-missing requirements + present-but-wrong-version deps).
     *
     * @param activeMods {@code Loader.instance().getActiveModList()}
     * @return the aggregated exception whose {@code missingMods} set is the union of every mod's
     *         missing requirements, or {@code null} if nothing is actually missing.
     */
    public static MissingModsException buildFrom(List<ModContainer> activeMods) {
        // modId -> processed version, for every active mod plus registered APIs (mirrors vanilla).
        Map<String, ArtifactVersion> modVersions = new HashMap<>();
        for (ModContainer mod : Iterables.concat(activeMods, ModAPIManager.INSTANCE.getAPIList())) {
            modVersions.put(mod.getModId(), mod.getProcessedVersion());
        }

        Map<String, Set<ArtifactVersion>> groups = new LinkedHashMap<>();
        Set<ArtifactVersion> union = new LinkedHashSet<>();

        for (ModContainer mod : activeMods) {
            Set<ArtifactVersion> missingForMod = new LinkedHashSet<>();

            // Hard-missing requirements (dependency mod not present at all).
            Map<String, ArtifactVersion> names = Maps
                .uniqueIndex(mod.getRequirements(), new ArtifactVersionNameFunction());
            for (String modid : Sets.difference(names.keySet(), modVersions.keySet())) {
                missingForMod.add(names.get(modid));
            }

            // Present-but-wrong-version requirements.
            ImmutableList<ArtifactVersion> allDeps = ImmutableList.<ArtifactVersion>builder()
                .addAll(mod.getDependants())
                .addAll(mod.getDependencies())
                .build();
            for (ArtifactVersion v : allDeps) {
                ArtifactVersion present = modVersions.get(v.getLabel());
                if (present != null && !v.containsVersion(present)) {
                    missingForMod.add(v);
                }
            }

            if (!missingForMod.isEmpty()) {
                groups.put(mod.getName() + " (" + mod.getModId() + ")", missingForMod);
                union.addAll(missingForMod);
            }
        }

        if (union.isEmpty()) {
            return null;
        }

        set(groups);
        FMLLog.severe(
            "[OptimizationsAndTweaks] %d mod(s) have unsatisfied dependencies (listing all, not just the first):",
            groups.size());
        for (Map.Entry<String, Set<ArtifactVersion>> e : groups.entrySet()) {
            FMLLog.severe("  - %s requires %s", e.getKey(), e.getValue());
        }
        return new MissingModsException(union);
    }

    /**
     * Builds the ordered render model for the missing-mods screen: a per-mod header line followed by
     * one indented line per missing/incompatible requirement. When the aggregate is empty (e.g. the
     * client handler mixin did not run) it falls back to the single {@code fallback} exception so the
     * screen still lists at least what vanilla would have.
     * <p>
     * Lives here (not in the mixin) because Mixin forbids referencing classes declared inside a mixin
     * package directly at runtime.
     */
    public static List<Line> displayLines(MissingModsException fallback) {
        List<Line> lines = new ArrayList<>();
        if (!GROUPS.isEmpty()) {
            for (Map.Entry<String, Set<ArtifactVersion>> e : GROUPS.entrySet()) {
                lines.add(new Line(e.getKey() + " requires:", 0xFFAA00));
                for (ArtifactVersion v : e.getValue()) {
                    lines.add(new Line("    " + describe(v), 0xEEEEEE));
                }
            }
        } else if (fallback != null && fallback.missingMods != null) {
            for (ArtifactVersion v : fallback.missingMods) {
                lines.add(new Line(describe(v), 0xEEEEEE));
            }
        }
        return lines;
    }

    private static String describe(ArtifactVersion v) {
        if (v instanceof DefaultArtifactVersion) {
            DefaultArtifactVersion dav = (DefaultArtifactVersion) v;
            if (dav.getRange() != null && dav.getRange()
                .isUnboundedAbove()) {
                return v.getLabel() + " : minimum version required is "
                    + dav.getRange()
                        .getLowerBoundString();
            }
        }
        return v.getLabel() + " : " + v.getRangeString();
    }

    /** One rendered line of the missing-mods screen (text + ARGB-less RGB color). */
    public static final class Line {

        public final String text;
        public final int color;

        Line(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }
}
