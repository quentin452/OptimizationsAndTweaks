package fr.iamacat.multithreading;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fr.iamacat.multithreading.proxy.CommonProxy;
import fr.iamacat.multithreading.utils.Reference;

import java.io.File;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = Reference.MC_VERSION)

public class Multithreaded {

    private static final String VERSION = "1.5"; // Change this to the desired version

    public static boolean MixinEntitySpawning;
    private static Configuration config;
    @Mod.Instance(Reference.MOD_ID)
    public static Multithreaded instance;
    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "Multithreadedandtweaks.cfg");
        Configuration config = new Configuration(configFile);

        // Get the loaded config version from the configuration file
        String loadedModVersion = config.getLoadedConfigVersion();

        // Check if the loaded config version matches the current mod version
        if (loadedModVersion == null || !loadedModVersion.equals(VERSION)) {
            // Delete the old config
            event.getSuggestedConfigurationFile()
                .delete();

            // Get the config version from the configuration file or use the default value if it doesn't exist
            String configVersion = config.getString(
                "config_version",
                Configuration.CATEGORY_GENERAL,
                VERSION,
                "The version of the configuration file. Change this to reset the configuration file.");

            // Create a new config with the specified config version
            config = new Configuration(event.getSuggestedConfigurationFile(), configVersion);
        }

        // Load the configuration file
        config.load();

        // Read the values from the configuration file
        boolean MixinEntitySpawning = config.getBoolean(
            "MixinEntitySpawning",
            Configuration.CATEGORY_GENERAL,
            true,
            "Enable multithreaded mob spawning");
        // int myIntValue = config.getInt("myIntValue", "general", 42, 0, 100, "This is another comment");

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
