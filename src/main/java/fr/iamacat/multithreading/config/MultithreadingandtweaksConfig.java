package fr.iamacat.multithreading.config;

import com.falsepattern.lib.config.Config;

import fr.iamacat.multithreading.Tags;

@Config(modid = Tags.MODID)
public class MultithreadingandtweaksConfig {

    // Make inconfig ingame Fixme todo
    // make categories Fixme todo
    // Multithreading/Batch
    @Config.Comment("Enable multithreaded for leaf decay.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinLeafDecay;
    @Config.Comment("Enable multithreaded for EntityLiving Update.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityLivingUpdate;
    @Config.Comment("Enable multithreaded for Fire Tick.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFireTick;

    @Config.Comment("Enable multithreaded for Growth Spreading.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinGrowthSpreading;
    @Config.Comment("(WIP)Enable multithreaded for Liquid Tick.")
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
    @Config.Comment("Enable Multithreaded Liquid Rendering")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinLiquidRendering;
    @Config.Comment("Enable Multithreaded Block Updates")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinUpdateBlocks;
    @Config.Comment("Enable Multithreaded Grass Block Spreading")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinGrassSpread;
    @Config.Comment("Enable Multithreaded Entities Collision")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntitiesCollision;
    @Config.Comment("Enable Multithreaded Update Time and Light")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntitiesUpdateTimeandLight;

    @Config.Comment("Enable Multithreaded Chunk Provider Server")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinChunkProviderServer;
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
    // BugFix Mixins
    @Config.Comment("Optimize EntityLookHelper performances")
    @Config.DefaultBoolean(false)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityLookHelper;
    @Config.Comment("Fix Godzilla Spam Log from orespawn")
    @Config.DefaultBoolean(false)
    @Config.RequiresWorldRestart
    public static boolean enableMixinGodZillaFix;
    @Config.Comment("Optimize Xaero's Minimap Renderer")
    @Config.DefaultBoolean(false)
    @Config.RequiresWorldRestart
    public static boolean enableXaerosMapOptimizations;
    @Config.Comment("Optimize EntityMoveHelper Performances")
    @Config.DefaultBoolean(false)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityMoveHelper;

    @Config.Comment("Optimize selectEntitiesWithinAABB")
    @Config.DefaultBoolean(false)
    @Config.RequiresWorldRestart
    public static boolean enableMixinWorld;
    @Config.Comment("Print stats ids to help to fix duplicated stats i" +
        "ds crash")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinStatList;
    @Config.Comment("Fix Spam logs when minefactory reloaded is installed with several mod(see the mixin to find which")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixNoSuchMethodException;

    @Config.Comment("Fix Some Cascading Worldgen caused by Rubber Trees from minefactory reloaded Mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix;
    @Config.Comment("Fix Some Cascading Worldgen caused by MineFactoryReloadedWorldGen from minefactory reloaded Mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixCascadingforMineFactoryReloadedWorldGen;
    @Config.Comment("Fix Some Cascading Worldgen caused by Lakes from minefactory reloaded Mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixWorldGenLakesMetaMinefactoryReloadedCascadingWorldgenFix;
    @Config.Comment("Fix All Cascading Worldgen caused by Poly Gravel from Shinkeiseikan Collection Mod (ATTENTION this mixin disabling Poly Gravel generation)")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixCascadingFromWorldGenPolyGravel;

    @Config.Comment("Fix Some Cascading Worldgen caused by Shincolle World Gen from Shinkeiseikan Collection Mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixCascadingFromShinColleWorldGen;

    @Config.Comment("Fix Completly Cascading Worldgen caused by Shipwreck Gen from Shipwreck Mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixCascadingFromShipwreckGen;

    @Config.Comment("Fix Some Cascading Worldgen caused by Trees from pam's harvestcraft mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixPamsTreesCascadingWorldgenLag;
    @Config.Comment("Fix Some Cascading Worldgen caused by WorldGenSlimeCarnage from Slime Carnage mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixCascadingFromWorldGenSlimeCarnage;

    @Config.Comment("Fix Some Cascading Worldgen caused by WorldGenPamFruitTree from Pam's Harvestcraft mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixWorldGenPamFruitTree;

    @Config.Comment("Fix Some Cascading Worldgen caused by WorldGenBrassTree from Steamcraft 2 mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixCascadingFromWorldGenBrassTree;
    @Config.Comment("Fix Random Crash caused by oredict from Cofh Core mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinOreDictCofhFix;
    @Config.Comment("Fix tps lags + reduce fps stutters caused by leaves from Thaumcraft  + add Leaf culling")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinPatchBlockMagicalLeavesPerformances;
    @Config.Comment("Reduce tps lags caused by SpawnerAnimals on VoidWorld")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinPatchSpawnerAnimals;

    @Config.Comment("Reduce tps lags caused by BlockLeaves especially on Modpacks")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinLeaves;
    @Config.Comment("Reduce tps lags caused by BiomeGenMagicalForest from Thaumcraft")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinPatchBiomeGenMagicalForest;

    @Config.Comment("Reduce tps lags caused by WorldGenCloudNine from Kingdom of the Overworld")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinPatchWorldGenCloudNine;

    @Config.Comment("Disable Intial World Chunk Load to make world load Faster,but player will see void area for some sec")
    @Config.DefaultBoolean(false)
    @Config.RequiresWorldRestart
    public static boolean enableMixinDisableinitialWorldChunkLoad;
    /*
     * @Config.Comment("List of entities to ignore for entity ticking optimization.")
     * @Config.DefaultStringList({ "Wither", "EnderDragon" })
     * public static String[] optimizeEntityTickingIgnoreList;
     */
}
