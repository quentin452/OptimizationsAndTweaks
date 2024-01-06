package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minegicka;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import com.williameze.minegicka3.ModBase;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class MinegickaConfigPotionID {

    public static Configuration config;
    private static final List<ConfigCategory> allCategories = new ArrayList();
    private static String CATEOGY_BIOMES;
    public static int coldResistanceId;
    public static int lifeBoostId;
    public static int arcaneResistanceId;
    public static int lightningResistanceId;

    private static void setupCategories() {
        CATEOGY_BIOMES = makeCategory("potion ids");
    }

    private static String makeCategory(String name) {
        ConfigCategory category = config.getCategory(name);
        category.setLanguageKey(ModBase.instance + ".config." + name);
        allCategories.add(category);
        return name;
    }

    public static void setupAndLoad(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "minegickapotionidsconf.cfg"));
        setupCategories();
        load();
    }

    public static void load() {
        coldResistanceId = config.get(CATEOGY_BIOMES, "Cold Resistance Potion ID", 28)
            .getInt();
        lifeBoostId = config.get(CATEOGY_BIOMES, "Life Boost Potion ID", 29)
            .getInt();
        arcaneResistanceId = config.get(CATEOGY_BIOMES, "Arcane Resistance Potion ID", 30)
            .getInt();
        lightningResistanceId = config.get(CATEOGY_BIOMES, "Lightning Resistance Potion ID", 31)
            .getInt();
        if (config.hasChanged()) {
            config.save();
        }
    }
}
