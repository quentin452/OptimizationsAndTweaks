package fr.iamacat.optimizationsandtweaks.utilsformods.recurrentcomplextrewrite;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModConfig {

    private static Configuration config;

    public static boolean loggingdisabler = true;

    public ModConfig(File configFile, FMLPreInitializationEvent event) {
        config = new Configuration(configFile);
    }

    public static void initializeConfig(FMLPreInitializationEvent event) {
        config = new Configuration(
            new File(event.getModConfigurationDirectory(), "MYTH_AND_MONSTER_structureconfig.cfg"));
        config.load();
        FileInjector.preinit(event);
        config.save();
    }

    public Configuration getConfig() {
        return config;
    }

    public boolean isMYTH_AND_MONSTERS_atlantisisenabled() {
        return config.get("Dungeon generation", "Enable MYTH_AND_MONSTERS_atlantisV1.0", true)
            .getBoolean();
    }
}
