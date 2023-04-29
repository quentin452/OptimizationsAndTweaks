package fr.iamacat.multithreading.config;

import com.falsepattern.lib.config.Config;

import fr.iamacat.multithreading.Tags;

@Config(modid = Tags.MODID)
public class MultithreadingandtweaksConfig {

    @Config.Comment("Enable multithreaded for leaf decay.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMixinLeafDecay;
    @Config.Comment("Enable multithreaded for AI Task.(disabled because laggy)")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean enableMixinEntityAITask;
    @Config.Comment("Enable multithreaded for Entity Update.(disabled because laggy)")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean enableMixinEntityUpdate;
    @Config.Comment("Enable multithreaded for Fire Tick.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMixinFireTick;
    @Config.Comment("Enable multithreaded for Growth Spreading.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMixinGrowthSpreading;
    @Config.Comment("Enable multithreaded for Liquid Tick.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMixinliquidTick;
    @Config.Comment("Enable multithreaded for Entity Spawning.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMixinEntitySpawning;

    @Config.Comment("Enable multithreaded for Chunk Populating such as structure ,dungeons ets....")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMixinChunkPopulating;
    /*
     * @Config.Comment("What the maximum render distance should be if raiseMaxRenderDistance is enabled.")
     * @Config.DefaultInt(32)
     * @Config.RangeInt(min = 16, max = 128)
     * public static int newMaxRenderDistance;
     */
    /*
     * @Config.Comment("List of entities to ignore for entity ticking optimization.")
     * @Config.DefaultStringList({ "Wither", "EnderDragon" })
     * public static String[] optimizeEntityTickingIgnoreList;
     */
}
