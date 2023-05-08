package fr.iamacat.multithreading.config;

import com.falsepattern.lib.config.Config;

import fr.iamacat.multithreading.Tags;

@Config(modid = Tags.MODID)
public class MultithreadingandtweaksMultithreadingConfig {

    // Make inconfig ingame
    // make categories

    @Config.Comment("Enable multithreaded for leaf decay.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinLeafDecay;
    @Config.Comment("Enable multithreaded for AI Task.)")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAITask;
    @Config.Comment("Enable multithreaded for Entity Update.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityUpdate;
    @Config.Comment("Enable multithreaded for Fire Tick.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFireTick;
    @Config.Comment("Enable multithreaded for Growth Spreading.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinGrowthSpreading;
    @Config.Comment("Enable multithreaded for Liquid Tick.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinliquidTick;
    @Config.Comment("Enable multithreaded for Entity Spawning.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntitySpawning;
    @Config.Comment("Enable multithreaded for Chunk Populating such as structure ,dungeons ets....")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinChunkPopulating;
    @Config.Comment("Enable multithreaded GUI/HUD/TEXT rendering")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinGUIHUD;
    @Config.Comment("Enable multithreaded Particle rendering")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinParticle;
    @Config.Comment("Enable multithreaded TileEntities rendering")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinTileEntities;
    @Config.Comment("Enable multithreaded Worldgen)")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinWorldgen;
    @Config.Comment("Enable multithreaded Worldgen Tick")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinWorldTick;
    @Config.Comment("Enable multithreaded Entities Tick")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntitiesTick;
    @Config.Comment("Enable multithreaded Entities Rendering")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntitiesRendering;
    @Config.Comment("Enable multithreaded Tile Entities Tick")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinTileEntitiesTick;
    @Config.Comment("Enable multithreaded Explosions")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinExplosions;
    @Config.Comment("Enable Sky Lightning Multithreaded update when break or place a block for better fps")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinMultithreadedSkyLightning;
    @Config.Comment("Enable Multithreaded BlockChunk Updating")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinBlockCHunkUpdate;
    @Config.Comment("Enable Multithreaded Fall Blocks Tick")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFallBlocksTick;
    @Config.Comment("Enable Multithreaded Liquid Rendering")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinLiquidRendering;
    @Config.Comment("Enable Multithreaded NetWork Manager")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinNetworkManager;
    @Config.Comment("Enable Multithreaded Block Updates")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinWorldUpdateBlocks;
    @Config.Comment("Enable Multithreaded Grass Block Spreading")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinGrassSpread;
    @Config.Comment("Choose the number of processor/CPU of your computer to fix potential issues.")
    @Config.DefaultInt(6)
    @Config.RangeInt(min = 1, max = 64)
    @Config.RequiresWorldRestart
    public static int numberofcpus;
    @Config.Comment("Batch size ,if you have tps issues try lowering or highering the batch size.")
    @Config.DefaultInt(150)
    @Config.RangeInt(min = 1, max = 1000)
    @Config.RequiresWorldRestart
    public static int batchsize;
    /*
     * @Config.Comment("List of entities to ignore for entity ticking optimization.")
     * @Config.DefaultStringList({ "Wither", "EnderDragon" })
     * public static String[] optimizeEntityTickingIgnoreList;
     */
}
