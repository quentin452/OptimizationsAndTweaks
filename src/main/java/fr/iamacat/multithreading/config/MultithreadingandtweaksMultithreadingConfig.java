package fr.iamacat.multithreading.config;

import com.falsepattern.lib.config.Config;

import fr.iamacat.multithreading.Tags;

@Config(modid = Tags.MODID)
public class MultithreadingandtweaksMultithreadingConfig {

    @Config.Comment("Enable multithreaded for leaf decay.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMixinLeafDecay;
    @Config.Comment("Enable multithreaded for AI Task.)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMixinEntityAITask;
    @Config.Comment("Enable multithreaded for Entity Update.")
    @Config.DefaultBoolean(true)
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
    @Config.Comment("Enable multithreaded for lightning engine")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMixinLightningBolt;

    @Config.Comment("Choose the number of processor/CPU of your computer to fix potential issues.")
    @Config.DefaultInt(6)
    @Config.RangeInt(min = 1, max = 64)
    public static int numberofcpus;

    @Config.Comment("Batch size ,if you have tps issues try lowering or highering the batch size.")
    @Config.DefaultInt(500)
    @Config.RangeInt(min = 1, max = 10000)
    public static int batchsize;
    /*
     * @Config.Comment("List of entities to ignore for entity ticking optimization.")
     * @Config.DefaultStringList({ "Wither", "EnderDragon" })
     * public static String[] optimizeEntityTickingIgnoreList;
     */
}
