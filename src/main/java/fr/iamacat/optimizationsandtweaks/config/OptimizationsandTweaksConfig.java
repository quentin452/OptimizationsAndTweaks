package fr.iamacat.optimizationsandtweaks.config;

import com.falsepattern.lib.config.Config;

import fr.iamacat.optimizationsandtweaks.Tags;

/**
 * Genuinely standalone, non-mixin options. Per-mixin enable/disable toggles used to live here
 * too (one boolean field per {@code Mixin} enum entry), but that was pure double-bookkeeping:
 * the {@code Mixin} enum is now the single source of truth for that, resolved generically by
 * {@code fr.iamacat.optimizationsandtweaks.asm.MixinConfigResolver} via
 * {@code config/optimizationsandtweaks-mixins.cfg} (category toggles + disabledMixins /
 * forceEnabledMixins). See docs/11-audit-oat-config.md in the Mod-Sandbox hub repo.
 * <p>
 * Note on {@code @Config.RequiresMcRestart}: every option below (like every mixin) is read once
 * at coremod/mod-init time and never re-read afterwards, so a value change only ever takes
 * effect on the next full Minecraft restart - {@code RequiresWorldRestart} was never the
 * correct semantics here.
 */
@Config(modid = Tags.MODID)
public class OptimizationsandTweaksConfig {

    @Config.Comment("Enabling Block Name Debugger for getPendingBlockUpdates " + "from WorldServer Class?.")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean enablegetPendingBlockUpdatesDebugger;

    @Config.Comment("Cache Thaumcraft inferred aspects to disk (config/optimizationsandtweaks/"
        + "thaumcraft-aspect-cache.txt). First boot generates it (~unchanged), later boots skip the "
        + "~40s recipe-graph inference. Auto-invalidated when the modlist or MineTweaker scripts change.")
    @Config.DefaultBoolean(true)
    public static boolean enableThaumcraftAspectCache;

    @Config.Comment("Choose the number of processor/CPU of your computer to " + "fix potential issues.")
    @Config.DefaultInt(6)
    @Config.RangeInt(min = 1, max = 64)
    @Config.RequiresMcRestart
    public static int numberofcpus;

    @Config.Comment("Enable Rust-powered pathfinding optimization for PathFinder (significant performance improvement) (async)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enablePathFindingOptimizations;

    @Config.Comment("Enable optimizationsandtweaks Rust profiler")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean enableRustProfiler;

    @Config.Comment("Enable optimizationsandtweaks Rust panic guards")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableRustPanicGuard;

    @Config.Comment("Auto-confirm future FML queries after first confirmation")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableFMLAutoConfirmAfterFirstConfirmation;

    @Config.Comment("Tidy Chunk Backport feature(EntityItem remover at first "
        + "chunk generation to reduce tps lags)(require MixinWorld)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableTidyChunkBackport;

    @Config.Comment("Tidy Chunk Backport Number of ticks post chunk generation "
        + "to check for EntityItems , 20 tick = 1 seconde")
    @Config.DefaultInt(50)
    @Config.RangeInt(min = 0, max = 1000)
    @Config.RequiresMcRestart
    public static int TidyChunkBackportPostTick;

    @Config.Comment("Tidy Chunk Backport debugger")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean enableTidyChunkBackportDebugger;

    @Config.Comment("EntityItem Spawning debugger")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean enableEntityItemSpawningDebugger;

    @Config.Comment("UnunquadiumLand mod Dimensionid(Require " + "enableMixinmcreator_ununquadiumLand)")
    @Config.DefaultInt(3)
    @Config.RangeInt(min = 0, max = 65536)
    @Config.RequiresMcRestart
    public static int LandDimensionID;
}
