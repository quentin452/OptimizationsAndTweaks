package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.disastercraft;

import com.williameze.minegicka3.ModBase;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fr.emerald.disaster.MainDisastercraft;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DisastercraftConfigBiomeID {

    public static Configuration config;
    private static final List<ConfigCategory> allCategories = new ArrayList();
    private static String CATEOGY_BIOMES;
    public static int devastedDesertId;
    public static int gravelId;
    public static int devastedId;
    public static int mountainId;

    private static void setupCategories() {
        CATEOGY_BIOMES = makeCategory("biome ids");
    }

    private static String makeCategory(String name) {
        ConfigCategory category = config.getCategory(name);
        category.setLanguageKey(
            MainDisastercraft.instance + ".config."
                + name);
        allCategories.add(category);
        return name;
    }

    public static void setupAndLoad(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "disastercraftbiomeidconfs.cfg"));
        setupCategories();
        load();
    }

    public static void load() {
        devastedDesertId = config.get(CATEOGY_BIOMES, "Cold Resistance Potion ID", 29)
            .getInt();
        gravelId = config.get(CATEOGY_BIOMES, "Life Boost Potion ID", 30)
            .getInt();
        devastedId = config.get(CATEOGY_BIOMES, "Arcane Resistance Potion ID", 25)
            .getInt();
        mountainId = config.get(CATEOGY_BIOMES, "Lightning Resistance Potion ID", 28)
            .getInt();
        if (config.hasChanged()) {
            config.save();
        }
    }
}
