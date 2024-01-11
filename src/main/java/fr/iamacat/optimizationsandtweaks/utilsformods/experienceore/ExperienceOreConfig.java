package fr.iamacat.optimizationsandtweaks.utilsformods.experienceore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.shad0wb1ade.experienceore.Main;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ExperienceOreConfig {

    public static Configuration config;
    private static final List<ConfigCategory> allCategories = new ArrayList();
    private static String CATEOGY_BIOMES;
    public static int experienceorefrequency;

    private static void setupCategories() {
        CATEOGY_BIOMES = makeCategory("whole");
    }

    private static String makeCategory(String name) {
        ConfigCategory category = config.getCategory(name);
        category.setLanguageKey(Main.MOD_ID + ".config." + name);
        allCategories.add(category);
        return name;
    }

    public static void setupAndLoad(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "experienceore.cfg"));
        setupCategories();
        load();
    }

    public static void load() {
        experienceorefrequency = config.get(CATEOGY_BIOMES, "Experience Ore Frequency", 32)
            .getInt();
        if (config.hasChanged()) {
            config.save();
        }
    }
}
