package fr.iamacat.optimizationsandtweaks.config;

import com.falsepattern.lib.config.Config;

import fr.iamacat.optimizationsandtweaks.Tags;

@Config(modid = Tags.MODID)
public class OptimizationsandTweaksConfig {

    @Config.Comment("Optimize RandomPositionGenerator Class.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinRandomPositionGenerator;
    @Config.Comment("Optimize EntityAIWander Class.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAIWander;
    @Config.Comment("Optimize EntityAIPlay Class.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAIPlay;
    @Config.Comment("Optimize EntityLiving Update.")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityLivingUpdate;
    @Config.Comment("Optimize Chunk Provider Server")
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
    @Config.Comment("Optimize MixinEntitySquid class")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntitySquid;
    @Config.Comment("Optimize EntityZombie class")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityZombie;
    @Config.Comment("Optimize EntityLookHelper performances")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityLookHelper;
    @Config.Comment("Optimize BaseAttributeMap performances")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinServersideAttributeMap;
    @Config.Comment("Optimize EntityAIAttackOnCollide performances")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAIAttackOnCollide;

    @Config.Comment("Make Minestones item with 64 item stacksize instead of 1")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinItemMinestone;
    @Config.Comment("Fix a large bottleneck in EntityVillageGuard class from Witchery")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityVillageGuard;
    @Config.Comment("Optimize LowerStringMap performances")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinLowerStringMap;
    @Config.Comment("Optimize ModelRenderer")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinModelRenderer;
    @Config.Comment("Optimize RenderManager")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinRenderManager;
    @Config.Comment("Optimize Entity")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntity;
    @Config.Comment("Optimize EntitySpellParticleFX")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntitySpellParticleFX;
    @Config.Comment("Optimize Tesselator")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinTesselator;
    @Config.Comment("Optimize BlockLiquid Tick")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinBlockLiquid;

    @Config.Comment("Optimize EntityItem")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityItem;
    @Config.Comment("Optimize OilTweakEventHandler from Buildcraft oil Tweak addon")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinOilTweakEventHandler;

    @Config.Comment("Optimize SteamcraftEventHandler from Flaxbeard's Steam Power")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinSteamcraftEventHandler;
    @Config.Comment("Optimize EntityBlockLing from Blocklings mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityBlockling;

    @Config.Comment("Optimize CommonProxy from Catwalks 2")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinCommonProxyForCatWalks2;
    @Config.Comment("Optimize EntityEagle from Adventurers Amulet")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityEagle;
    @Config.Comment("Optimize MinecraftServer")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinMinecraftServer;

    @Config.Comment("Optimize DataWatcher(Avoid usage of locks and use ConcurrentHashMap)")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinDataWatcher;
    @Config.Comment("Optimize TickHandler from IC2")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinTickHandler;
    @Config.Comment("Optimize NEIServerUtils from NotEnoughItems")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinNEIServerUtils;
    @Config.Comment("Optimize Config from IC2")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinConfig;
    @Config.Comment("Optimize EventRegistry from Practical Logistics(Disabled due to crash on certain servers)")
    @Config.DefaultBoolean(false)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEventRegistry;
    @Config.Comment("Optimize NibbleArray")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinNibbleArray;
    @Config.Comment("Optimize EntityLiving")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityLiving;
    @Config.Comment("Optimize EntityAIEatDroppedFood from Easy Breeding mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAIEatDroppedFood;

    @Config.Comment("Optimize EntityAITempt + add a follower limit(30 max) at the same time")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAITempt;
    @Config.Comment("Optimize EventHandlerNEP from notenoughpets")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEventHandlerNEP;
    @Config.Comment("Optimize HackTickHandler from Pneumaticraft")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinHackTickHandler;
    @Config.Comment("Optimize EntityAeable")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAgeable;
    @Config.Comment("Optimize PriorityExecutor from Industrialcraft2")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinPriorityExecutor;
    @Config.Comment("Optimize PlayerAether from Aether")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinPlayerAether;
    @Config.Comment("Optimize AnimTickHandler from Akatsuki mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinAnimTickHandler;
    @Config.Comment("Optimize AnimationHandler from Akatsuki mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinAnimationHandler;
    @Config.Comment("Optimize EntitySasosri from Akatsuki mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntitySasosri;
    @Config.Comment("Optimize EntitySasosri2 from Akatsuki mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntitySasosri2;
    @Config.Comment("Optimize PuppetKadz from Akatsuki mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinPuppetKadz;
    @Config.Comment("Optimize Chunk ticking")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinChunk;
    @Config.Comment("Optimize MapStorage")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinMapStorage;
    @Config.Comment("Optimize BlockGrass Ticking")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinBlockGrass;
    @Config.Comment("Optimize OpenGlHelper")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinOpenGlHelper;
    @Config.Comment("Fix Godzilla Spam Log from orespawn")
    @Config.DefaultBoolean(false)
    @Config.RequiresWorldRestart
    public static boolean enableMixinGodZillaFix;
    @Config.Comment("Optimize EntityMoveHelper Performances")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityMoveHelper;
    @Config.Comment("Optimize EntityAnimal")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAnimal;

    @Config.Comment("Optimize EntityAIFollowParent")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAIFollowParent;
    @Config.Comment("Optimize EntityAITasks")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinEntityAITasks;

    @Config.Comment("Optimize PathFinding")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinPathFinding;
    @Config.Comment("Optimize selectEntitiesWithinAABB")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinWorld;
    @Config.Comment("Print stats ids to help to fix duplicated stats i" + "ds crash")
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
    @Config.Comment("Fix Some Cascading Worldgen caused by ThaumcraftWorldGenerator from Thaumcraft4 mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator;
    @Config.Comment("Fix Some Cascading Worldgen caused by WorldGenPamFruitTree from Pam's Harvestcraft mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixWorldGenPamFruitTree;
    @Config.Comment("Fix Unable to play unknown soundEvent: minecraft: for Entities from Farlanders mod")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod;
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

    /*
     * @Config.Comment("List of entities to ignore for entity ticking optimization.")
     * @Config.DefaultStringList({ "Wither", "EnderDragon" })
     * public static String[] optimizeEntityTickingIgnoreList;
     */
}
