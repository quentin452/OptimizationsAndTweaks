package fr.iamacat.optimizationsandtweaks;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import fr.iamacat.optimizationsandtweaks.asm.Mixin;
import fr.iamacat.optimizationsandtweaks.asm.MixinConfigResolver;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.eventshandler.AsyncPathfindingTickHandler;
import fr.iamacat.optimizationsandtweaks.eventshandler.EntityItemSpawningEventHandler;
import fr.iamacat.optimizationsandtweaks.eventshandler.TidyChunkBackportEventHandler;
import fr.iamacat.optimizationsandtweaks.eventshandler.WorldUnloadEventHandler;
import fr.iamacat.optimizationsandtweaks.proxy.CommonProxy;
import fr.iamacat.optimizationsandtweaks.utils.natives.AsyncPathfindingExecutor;
import fr.iamacat.optimizationsandtweaks.utils.natives.RustFFI;
import fr.iamacat.optimizationsandtweaks.utils.natives.RustPathfinding;
import fr.iamacat.optimizationsandtweaks.utilsformods.experienceore.ExperienceOreConfig;
import fr.iamacat.optimizationsandtweaks.utilsformods.mythandmonsters.recurrentcomplextrewrite.FileInjector;
import fr.iamacat.optimizationsandtweaks.utilsformods.mythandmonsters.recurrentcomplextrewrite.ModConfig;

@Mod(
    modid = Tags.MODID,
    version = Tags.VERSION,
    name = Tags.MODNAME,
    acceptedMinecraftVersions = Tags.MCVERSION,
    dependencies = "required-after:falsepatternlib;required-after:unimixins")
public class OptimizationsAndTweaks {

    @Mod.Instance(Tags.MODID)
    public static OptimizationsAndTweaks instance;
    @SidedProxy(clientSide = Tags.CLIENTPROXY, serverSide = Tags.SERVERPROXY)
    public static CommonProxy proxy;
    public static Configuration config;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        // Initialize Rust FFI
        try {
            File minecraftDir = event.getModConfigurationDirectory()
                .getParentFile();
            if (RustFFI.initialize(minecraftDir)) {
                // Test the Rust FFI
                RustFFI.printHelloWorld();
                String helloMsg = RustFFI.getHelloString();
                if (helloMsg != null) {
                    FMLLog.info("[OptimizationsAndTweaks] Rust says: %s", helloMsg);
                }
                RustFFI.printMessage("Hello from OptimizationsAndTweaks mod!");

                // Initialize Rust pathfinding
                RustPathfinding.initialize();

                try {
                    RustPathfinding.class.getDeclaredMethod("setProfilerEnabled", boolean.class)
                        .invoke(null, OptimizationsandTweaksConfig.enableRustProfiler);
                    FMLLog.info(
                        "[OptimizationsAndTweaks] Rust profiler %s",
                        OptimizationsandTweaksConfig.enableRustProfiler ? "enabled" : "disabled");
                } catch (Throwable t) {}
                try {
                    RustFFI.class.getDeclaredMethod("setPanicGuardEnabled", boolean.class)
                        .invoke(null, OptimizationsandTweaksConfig.enableRustPanicGuard);
                    FMLLog.info(
                        "[OptimizationsAndTweaks] Rust panic guard %s",
                        OptimizationsandTweaksConfig.enableRustProfiler ? "enabled" : "disabled");
                } catch (Throwable t) {}

                // Initialize the async pathfinding executor only when the feature is enabled.
                // Otherwise the native worker pool spun up here is pure waste, and getStatistics()
                // would report the executor as "available" while pathfinding is meant to be off.
                if (OptimizationsandTweaksConfig.enablePathFindingOptimizations) {
                    AsyncPathfindingExecutor.initializeAuto();
                    FMLLog.info("[OptimizationsAndTweaks] Async pathfinding executor initialized");
                }
            }
        } catch (Throwable t) {
            FMLLog.info(
                "[OptimizationsAndTweaks] Rust FFI initialization skipped (optional feature): %s",
                t.getMessage());
        }

        if (FMLCommonHandler.instance()
            .findContainerFor("mam") != null
            && MixinConfigResolver.INSTANCE.isEnabled(Mixin.common_mythandmonsters_MixinMAMWorldGenerator)) {
            File configFile = new File(event.getModConfigurationDirectory(), "MYTH_AND_MONSTER_structureconfig.cfg");
            ModConfig modConfig = new ModConfig(configFile, event);
            FileInjector.setModConfig(modConfig);
            ModConfig.initializeConfig(event);
        }
        if (FMLCommonHandler.instance()
            .findContainerFor("ExpOre") != null
            && MixinConfigResolver.INSTANCE.isEnabled(Mixin.common_experienceore_MixinWorldGenHandlerExperienceOre)) {
            ExperienceOreConfig.setupAndLoad(event);
        }
    }

    @Mod.EventHandler
    public void loadComplete(cpw.mods.fml.common.event.FMLLoadCompleteEvent event) {
        fr.iamacat.optimizationsandtweaks.utilsformods.thaumcraft.AspectCache.saveIfDirty();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register async pathfinding tick handler
        if (OptimizationsandTweaksConfig.enablePathFindingOptimizations && AsyncPathfindingExecutor.isInitialized()) {
            AsyncPathfindingTickHandler asyncTickHandler = new AsyncPathfindingTickHandler();
            FMLCommonHandler.instance()
                .bus()
                .register(asyncTickHandler);
            // LivingDeathEvent lives on the Forge bus, not the FML bus.
            MinecraftForge.EVENT_BUS
                .register(new fr.iamacat.optimizationsandtweaks.eventshandler.AsyncPathCleanupHandler());
            FMLLog.info("[OptimizationsAndTweaks] Async pathfinding tick handler registered");
        }

        if (OptimizationsandTweaksConfig.enableTidyChunkBackport) {
            TidyChunkBackportEventHandler eventHandler = new TidyChunkBackportEventHandler();
            MinecraftForge.EVENT_BUS.register(eventHandler);
        }
        if (OptimizationsandTweaksConfig.enableEntityItemSpawningDebugger) {
            EntityItemSpawningEventHandler eventHandler = new EntityItemSpawningEventHandler();
            MinecraftForge.EVENT_BUS.register(eventHandler);
        }
        if (OptimizationsandTweaksConfig.enableFMLAutoConfirmAfterFirstConfirmation) {
            WorldUnloadEventHandler worldUnloadHandler = new WorldUnloadEventHandler();
            MinecraftForge.EVENT_BUS.register(worldUnloadHandler);
        }
        MinecraftForge.EVENT_BUS.register(proxy);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        // Shutdown async pathfinding executor
        if (AsyncPathfindingExecutor.isInitialized()) {
            FMLLog.info("[OptimizationsAndTweaks] Shutting down async pathfinding executor");
            AsyncPathfindingExecutor.shutdown();
        }
        // Aspects are inferred lazily IN-GAME (after LoadComplete): the world
        // exit is the only moment the cache actually has something to persist.
        fr.iamacat.optimizationsandtweaks.utilsformods.thaumcraft.AspectCache.saveIfDirty();
    }
}
