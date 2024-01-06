package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.lotsOMobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import com.lom.lotsomobscore.LotsOMobs;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class LotsOMobsConfigBiomeID {

    public static Configuration config;
    private static final List<ConfigCategory> allCategories = new ArrayList();
    private static String CATEOGY_BIOMES;
    public static int antarticaId;
    public static int articOceanId;
    public static int dinoIslandsId;
    public static int dinoJungleId;
    public static int dinoMountainsId;
    public static int dinoOceanId;
    public static int dinoPlainsId;
    public static int IceIslandsId;
    public static int IceMountainsId;
    public static int IceOceanId;
    public static int IcePlainsId;

    private static void setupCategories() {
        CATEOGY_BIOMES = makeCategory("biome ids");
    }

    private static String makeCategory(String name) {
        ConfigCategory category = config.getCategory(name);
        category.setLanguageKey(LotsOMobs.instance + ".config." + name);
        allCategories.add(category);
        return name;
    }

    public static void setupAndLoad(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "lotsomobsbiomeidsconfig.cfg"));
        setupCategories();
        load();
    }

    public static void load() {
        antarticaId = config.get(CATEOGY_BIOMES, "Antartica Biome ID", 100)
            .getInt();
        articOceanId = config.get(CATEOGY_BIOMES, "Artic Biome ID", 101)
            .getInt();
        dinoIslandsId = config.get(CATEOGY_BIOMES, "Dino Islands Biome ID", 102)
            .getInt();
        dinoJungleId = config.get(CATEOGY_BIOMES, "Dino Jungle Biome ID", 103)
            .getInt();
        dinoMountainsId = config.get(CATEOGY_BIOMES, "Dino Moutains Biome ID", 104)
            .getInt();
        dinoOceanId = config.get(CATEOGY_BIOMES, "Dino Ocean Biome ID", 105)
            .getInt();
        dinoPlainsId = config.get(CATEOGY_BIOMES, "Dino Plains Biome ID", 106)
            .getInt();
        IceIslandsId = config.get(CATEOGY_BIOMES, "Ice Islands Biome ID", 107)
            .getInt();
        IceMountainsId = config.get(CATEOGY_BIOMES, "Ice Mountains Biome ID", 108)
            .getInt();
        IceOceanId = config.get(CATEOGY_BIOMES, "Ice Ocean Biome ID", 109)
            .getInt();
        IcePlainsId = config.get(CATEOGY_BIOMES, "Ice Plains Biome ID", 110)
            .getInt();
        if (config.hasChanged()) {
            config.save();
        }
    }
}
