package fr.iamacat.multithreading;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fr.iamacat.multithreading.proxy.CommonProxy;
import fr.iamacat.multithreading.utils.Reference;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = Reference.MC_VERSION)

public class Multithreaded {

    private static final String VERSION = "2.0"; // Change this to the desired version

    public static boolean MixinEntitySpawning;
    public static boolean MixinGrowthSpreading;
    public static boolean MixinLeafDecay;
    public static boolean MixinFireTick;

    public static boolean MixinLiquidTick;
    private static Configuration config;

    @Mod.Instance(Reference.MOD_ID)
    public static Multithreaded instance;
    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "Multithreadedandtweaks.cfg");
        Configuration config = new Configuration(configFile);

        // Create categories in the configuration file
        String categoryMixins = "Mixins";
        String categoryOptimizations = "Optimizations";
        String categoryTweaks = "Tweaks";

        // Get the loaded config version from the configuration file
        String loadedModVersion = config.getLoadedConfigVersion();

        // Check if the loaded config version matches the current mod version
        if (loadedModVersion == null || !loadedModVersion.equals(VERSION)) {
            // Delete the old config
            configFile.delete();

            // Get the config version from the configuration file or use the default value if it doesn't exist
            String configVersion = config.getString(
                "config_version",
                Configuration.CATEGORY_GENERAL,
                VERSION,
                "The version of the configuration file. Change this to reset the configuration file.");

            // Create a new config with the specified config version
            config = new Configuration(configFile, configVersion);
        }

        // Load the configuration file
        config.load();

        // Read the values from the configuration file
        boolean MixinEntitySpawning = config
            .getBoolean("MixinEntitySpawning", categoryMixins, true, "Enable multithreaded for mob spawning");
        boolean MixinGrowthSpreading = config
            .getBoolean("MixinGrowthSpreading", categoryMixins, true, "Enable multithreaded for block growth like suggar cane");
        boolean MixinLeafDecay = config
            .getBoolean("MixinLeafDecay", categoryMixins, true, "Enable multithreaded for leaf decay");
        boolean MixinFireTick = config
            .getBoolean("MixinFireTick", categoryMixins, true, "Enable multithreaded for fire tick");
        boolean MixinLiquidTick = config
            .getBoolean("MixinLiquidTick", categoryMixins, true, "Enable multithreaded for liquid tick");

        /*
         * int someIntValue = config.getInt(
         * "SomeIntValue",
         * categoryOptimizations,
         * 42,
         * 0,
         * 100,
         * "This is a comment for the integer value");
         * String someStringValue = config.getString(
         * "SomeStringValue",
         * categoryTweaks,
         * "default value",
         * "This is a comment for the string value");
         */

        // Save the updated configuration file
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static class WorldLoadHandler {

    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register the proxy as an event handler
        MinecraftForge.EVENT_BUS.register(proxy);
    }
}
