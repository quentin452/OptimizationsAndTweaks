package fr.iamacat.optimizationsandtweaks.utilsformods.minenautica;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fr.iamacat.optimizationsandtweaks.Tags;

public class MinenauticaBiomeIDConfig {

    public static Configuration config;
    private static final List<ConfigCategory> allCategories = new ArrayList();
    private static String CATEOGY_BIOMES;
    public static int kelpForest;
    public static int grassyPlateaus;
    public static int safeShallows;

    private static void setupCategories() {
        CATEOGY_BIOMES = makeCategory("biome ids");
    }

    private static String makeCategory(String name) {
        ConfigCategory category = config.getCategory(name);
        category.setLanguageKey(Tags.MINENAUTICAMODID + ".config." + name);
        allCategories.add(category);
        return name;
    }

    public static void setupAndLoad(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "minenauticabiomes.cfg"));
        setupCategories();
        load();
    }

    public static void load() {
        kelpForest = config.get(CATEOGY_BIOMES, "KelpForest Biome ID", 180, "Biome id config")
            .getInt();
        grassyPlateaus = config.get(CATEOGY_BIOMES, "Grassy Plateaus Biome ID", 181, "Biome id config")
            .getInt();
        safeShallows = config.get(CATEOGY_BIOMES, "Safe Shallows Biome ID", 182, "Biome id config")
            .getInt();
        if (config.hasChanged()) {
            config.save();
        }
    }
}
