package fr.iamacat.optimizationsandtweaks.utilsformods.lotr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lotr.common.LOTRMod;

public class LOTRConfigBiomeID {

    public static Configuration config;
    private static final List<ConfigCategory> allCategories = new ArrayList();
    private static String CATEOGY_BIOMES;
    public static int riverid;
    public static int rohanid;

    public static int mistyMountainsid;

    public static int shireid;
    public static int shireWoodlandsid;
    public static int mordorid;
    public static int mordorMountainsid;
    public static int gondorid;
    public static int whiteMountainsid;
    public static int lothlorienid;
    public static int celebrantid;
    public static int ironHillsid;

    public static int deadMarshesid;

    public static int trollshawsid;
    public static int woodlandRealmid;
    public static int mirkwoodCorruptedid;
    public static int rohanUrukHighlandsid;
    public static int emynMuilid;
    public static int ithilienid;
    public static int pelargirid;
    public static int loneLandsid;
    public static int loneLandsHillsid;

    public static int dunlandid;

    public static int fangornid;
    public static int angleid;
    public static int ettenmoorsid;
    public static int oldForestid;
    public static int harondorid;
    public static int eriadorid;
    public static int eriadorDownsid;
    public static int erynVornid;
    public static int greyMountainsid;

    public static int midgewaterid;

    public static int brownLandsid;
    public static int oceanid;
    public static int anduinHillsid;
    public static int meneltarmaid;
    public static int gladdenFieldsid;
    public static int lothlorienEdgeid;
    public static int forodwaithid;
    public static int enedwaithid;
    public static int angmarid;

    public static int eregionid;

    public static int lindonid;
    public static int lindonWoodlandsid;
    public static int eastBightid;
    public static int blueMountainsid;
    public static int mirkwoodMountainsid;
    public static int wilderlandid;
    public static int dagorladid;
    public static int nurnid;
    public static int nurnenid;

    public static int nurnMarshesid;

    public static int adornlandid;
    public static int angmarMountainsid;
    public static int anduinMouthid;
    public static int entwashMouthid;
    public static int dorEnErnilid;
    public static int dorEnErnilHillsid;
    public static int fangornWastelandid;
    public static int rohanWoodlandsid;
    public static int gondorWoodlandsid;

    public static int lakeid;

    public static int lindonCoastid;
    public static int barrowDownsid;
    public static int longMarshesid;
    public static int fangornClearingid;
    public static int ithilienHillsid;
    public static int ithilienWastelandid;
    public static int nindalfid;
    public static int coldfellsid;
    public static int nanCurunirid;

    public static int whiteDownsid;

    public static int swanfleetid;
    public static int pelennorid;
    public static int minhiriathid;
    public static int ereborid;
    public static int mirkwoodNorthid;
    public static int woodlandRealmHillsid;
    public static int nanUngolid;
    public static int pinnathGelinid;
    public static int islandid;

    public static int forodwaithMountainsid;

    public static int mistyMountainsFoothillsid;
    public static int greyMountainsFoothillsid;
    public static int blueMountainsFoothillsid;
    public static int tundraid;
    public static int taigaid;
    public static int breelandid;
    public static int chetwoodid;
    public static int forodwaithGlacierid;
    public static int whiteMountainsFoothillsid;

    public static int beachid;

    public static int beachGravelid;
    public static int nearHaradid;
    public static int farHaradid;
    public static int haradMountainsid;
    public static int umbarid;
    public static int farHaradJungleid;
    public static int umbarHillsid;
    public static int nearHaradHillsid;
    public static int farHaradJungleLakeid;

    public static int lostladenid;

    public static int farHaradForestid;
    public static int nearHaradFertileid;
    public static int pertorogwaithid;
    public static int umbarForestid;
    public static int farHaradJungleEdgeid;
    public static int tauredainClearingid;
    public static int gulfHaradid;
    public static int dorwinionHillsid;
    public static int tolfalasid;

    public static int lebenninid;

    public static int rhunid;
    public static int rhunForestid;
    public static int redMountainsid;
    public static int redMountainsFoothillsid;
    public static int dolGuldurid;
    public static int nearHaradSemiDesertid;
    public static int farHaradAridid;
    public static int farHaradAridHillsid;
    public static int farHaradSwampid;

    public static int farHaradCloudForestid;

    public static int farHaradBushlandid;
    public static int farHaradBushlandHillsid;
    public static int farHaradMangroveid;
    public static int nearHaradFertileForestid;
    public static int anduinValeid;
    public static int woldid;
    public static int shireMoorsid;
    public static int shireMarshesid;
    public static int nearHaradRedDesertid;

    public static int farHaradVolcanoid;

    public static int udunid;
    public static int gorgorothid;
    public static int morgulValeid;
    public static int easternDesolationid;
    public static int daleid;
    public static int dorwinionid;
    public static int towerHillsid;
    public static int gulfHaradForestid;
    public static int wilderlandNorthid;

    public static int forodwaithCoastid;

    public static int farHaradCoastid;
    public static int nearHaradRiverbankid;
    public static int lossarnachid;
    public static int imlothMeluiid;
    public static int nearHaradOasisid;
    public static int beachWhiteid;
    public static int harnedorid;
    public static int lamedonid;
    public static int lamedonHillsid;

    public static int blackrootValeid;

    public static int andrastid;
    public static int pukelid;
    public static int rhunLandid;
    public static int rhunLandSteppeid;
    public static int rhunLandHillsid;
    public static int rhunRedForestid;
    public static int rhunIslandid;
    public static int rhunIslandForestid;
    public static int lastDesertid;

    public static int windMountainsid;

    public static int windMountainsFoothillsid;
    public static int rivendellid;
    public static int rivendellHillsid;
    public static int farHaradJungleMountainsid;
    public static int halfTrollForestid;
    public static int farHaradKanukaid;
    public static int utumnoid;

    private static void setupCategories() {
        CATEOGY_BIOMES = makeCategory("biome ids");
    }

    private static String makeCategory(String name) {
        ConfigCategory category = config.getCategory(name);
        category.setLanguageKey(
            LOTRMod.getModContainer()
                .getModId() + ".config."
                + name);
        allCategories.add(category);
        return name;
    }

    public static void setupAndLoad(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "lotrbiomeids.cfg"));
        setupCategories();
        load();
    }

    public static void load() {
        riverid = config.get(CATEOGY_BIOMES, "River Biome ID", 0, "Biome id config")
            .getInt();
        rohanid = config.get(CATEOGY_BIOMES, "Rohan Biome ID", 1, "Biome id config")
            .getInt();
        mistyMountainsid = config.get(CATEOGY_BIOMES, "Misty Mountains Biome ID", 2, "Biome id config")
            .getInt();
        shireid = config.get(CATEOGY_BIOMES, "Shire Biome ID", 3, "Biome id config")
            .getInt();
        shireWoodlandsid = config.get(CATEOGY_BIOMES, "Shire Wood Land Biome ID", 4, "Biome id config")
            .getInt();
        mordorid = config.get(CATEOGY_BIOMES, "Mordor Biome ID", 5, "Biome id config")
            .getInt();
        mordorMountainsid = config.get(CATEOGY_BIOMES, "Mordor Mountains Biome ID", 6, "Biome id config")
            .getInt();
        gondorid = config.get(CATEOGY_BIOMES, "Gondor Biome ID", 7, "Biome id config")
            .getInt();
        whiteMountainsid = config.get(CATEOGY_BIOMES, "White Mountains Biome ID", 8, "Biome id config")
            .getInt();
        lothlorienid = config.get(CATEOGY_BIOMES, "Lothlorien Biome ID", 9, "Biome id config")
            .getInt();
        celebrantid = config.get(CATEOGY_BIOMES, "Celebrant Biome ID", 10, "Biome id config")
            .getInt();
        ironHillsid = config.get(CATEOGY_BIOMES, "Iron Hills Biome ID", 11, "Biome id config")
            .getInt();
        deadMarshesid = config.get(CATEOGY_BIOMES, "Dead Marshes Biome ID", 12, "Biome id config")
            .getInt();
        trollshawsid = config.get(CATEOGY_BIOMES, "Trollshaws Biome ID", 13, "Biome id config")
            .getInt();
        woodlandRealmid = config.get(CATEOGY_BIOMES, "WoodlandRealm Biome ID", 14, "Biome id config")
            .getInt();
        mirkwoodCorruptedid = config.get(CATEOGY_BIOMES, "Mirkwood Corrupted Biome ID", 15, "Biome id config")
            .getInt();
        rohanUrukHighlandsid = config.get(CATEOGY_BIOMES, "Rohan Uruk Hidhlands Biome ID", 16, "Biome id config")
            .getInt();
        emynMuilid = config.get(CATEOGY_BIOMES, "Emyn Muilid Biome ID", 17, "Biome id config")
            .getInt();
        ithilienid = config.get(CATEOGY_BIOMES, "Ithilien Biome ID", 18, "Biome id config")
            .getInt();
        pelargirid = config.get(CATEOGY_BIOMES, "Pelargiri Biome ID", 19, "Biome id config")
            .getInt();
        loneLandsid = config.get(CATEOGY_BIOMES, "Lone Lands Biome ID", 20, "Biome id config")
            .getInt();
        loneLandsHillsid = config.get(CATEOGY_BIOMES, "Lone Lands Hills Biome ID", 21, "Biome id config")
            .getInt();
        dunlandid = config.get(CATEOGY_BIOMES, "Dunland Biome ID", 22, "Biome id config")
            .getInt();
        fangornid = config.get(CATEOGY_BIOMES, "Fangorn Biome ID", 23, "Biome id config")
            .getInt();
        angleid = config.get(CATEOGY_BIOMES, "Angle Biome ID", 24, "Biome id config")
            .getInt();
        ettenmoorsid = config.get(CATEOGY_BIOMES, "Etten Moors Biome ID", 25, "Biome id config")
            .getInt();
        oldForestid = config.get(CATEOGY_BIOMES, "Old Forest Biome ID", 26, "Biome id config")
            .getInt();
        harondorid = config.get(CATEOGY_BIOMES, "Harondor Biome ID", 27, "Biome id config")
            .getInt();
        eriadorid = config.get(CATEOGY_BIOMES, "Eriador Biome ID", 28, "Biome id config")
            .getInt();
        eriadorDownsid = config.get(CATEOGY_BIOMES, "Eriador Down Biome ID", 29, "Biome id config")
            .getInt();
        erynVornid = config.get(CATEOGY_BIOMES, "Eryn Vorn Biome ID", 30, "Biome id config")
            .getInt();
        greyMountainsid = config.get(CATEOGY_BIOMES, "Grey Mountains Biome ID", 31, "Biome id config")
            .getInt();
        midgewaterid = config.get(CATEOGY_BIOMES, "MidgeWater Biome ID", 32, "Biome id config")
            .getInt();
        brownLandsid = config.get(CATEOGY_BIOMES, "Brown Lands Biome ID", 33, "Biome id config")
            .getInt();
        oceanid = config.get(CATEOGY_BIOMES, "Ocean Biome ID", 34, "Biome id config")
            .getInt();
        anduinHillsid = config.get(CATEOGY_BIOMES, "Anduin Hills Biome ID", 35, "Biome id config")
            .getInt();
        meneltarmaid = config.get(CATEOGY_BIOMES, "Meneltarma Biome ID", 36, "Biome id config")
            .getInt();
        gladdenFieldsid = config.get(CATEOGY_BIOMES, "Gladden Fields Biome ID", 37, "Biome id config")
            .getInt();
        lothlorienEdgeid = config.get(CATEOGY_BIOMES, "Lothlorien Eldge Biome ID", 38, "Biome id config")
            .getInt();
        forodwaithid = config.get(CATEOGY_BIOMES, "Forodwaith Biome ID", 39, "Biome id config")
            .getInt();
        enedwaithid = config.get(CATEOGY_BIOMES, "Enedwaith Biome ID", 40, "Biome id config")
            .getInt();
        angmarid = config.get(CATEOGY_BIOMES, "Angmar Biome ID", 41, "Biome id config")
            .getInt();
        eregionid = config.get(CATEOGY_BIOMES, "Eregion Biome ID", 42, "Biome id config")
            .getInt();
        lindonid = config.get(CATEOGY_BIOMES, "Lindon Biome ID", 43, "Biome id config")
            .getInt();
        lindonWoodlandsid = config.get(CATEOGY_BIOMES, "Lindon Woodlands Biome ID", 44, "Biome id config")
            .getInt();
        eastBightid = config.get(CATEOGY_BIOMES, "East Bightid Biome ID", 45, "Biome id config")
            .getInt();
        blueMountainsid = config.get(CATEOGY_BIOMES, "Blue Mountains Biome ID", 46, "Biome id config")
            .getInt();
        mirkwoodMountainsid = config.get(CATEOGY_BIOMES, "Mirkwood Mountains Biome ID", 47, "Biome id config")
            .getInt();
        wilderlandid = config.get(CATEOGY_BIOMES, "Wilderland Biome ID", 48, "Biome id config")
            .getInt();
        dagorladid = config.get(CATEOGY_BIOMES, "Dagorlad Biome ID", 49, "Biome id config")
            .getInt();
        nurnid = config.get(CATEOGY_BIOMES, "Nurn Biome ID", 50, "Biome id config")
            .getInt();
        nurnenid = config.get(CATEOGY_BIOMES, "Nurnen Biome ID", 51, "Biome id config")
            .getInt();
        nurnMarshesid = config.get(CATEOGY_BIOMES, "Nurn Mashes Biome ID", 52, "Biome id config")
            .getInt();
        adornlandid = config.get(CATEOGY_BIOMES, "Adornland Biome ID", 53, "Biome id config")
            .getInt();
        angmarMountainsid = config.get(CATEOGY_BIOMES, "Angmar Mountains Biome ID", 54, "Biome id config")
            .getInt();
        anduinMouthid = config.get(CATEOGY_BIOMES, "Anduin Mouthid Biome ID", 55, "Biome id config")
            .getInt();
        entwashMouthid = config.get(CATEOGY_BIOMES, "Entwash Mounthid Biome ID", 56, "Biome id config")
            .getInt();
        dorEnErnilid = config.get(CATEOGY_BIOMES, "Dor En Ernil Biome ID", 57, "Biome id config")
            .getInt();
        dorEnErnilHillsid = config.get(CATEOGY_BIOMES, "Dor En Ernil Hills Biome ID", 58, "Biome id config")
            .getInt();
        fangornWastelandid = config.get(CATEOGY_BIOMES, "Fangorn Wasteland Biome ID", 59, "Biome id config")
            .getInt();
        rohanWoodlandsid = config.get(CATEOGY_BIOMES, "Rohan Woodland Biome ID", 60, "Biome id config")
            .getInt();
        gondorWoodlandsid = config.get(CATEOGY_BIOMES, "Gondor Woodland Biome ID", 61, "Biome id config")
            .getInt();
        lakeid = config.get(CATEOGY_BIOMES, "Lake Biome ID", 62, "Biome id config")
            .getInt();
        lindonCoastid = config.get(CATEOGY_BIOMES, "Lindon Coast Biome ID", 63, "Biome id config")
            .getInt();
        barrowDownsid = config.get(CATEOGY_BIOMES, "Barrow Down Biome ID", 64, "Biome id config")
            .getInt();
        longMarshesid = config.get(CATEOGY_BIOMES, "Long Marshes Biome ID", 65, "Biome id config")
            .getInt();
        fangornClearingid = config.get(CATEOGY_BIOMES, "Fangorn Clearing Biome ID", 66, "Biome id config")
            .getInt();
        ithilienHillsid = config.get(CATEOGY_BIOMES, "Ithilien Hills Biome ID", 67, "Biome id config")
            .getInt();
        ithilienWastelandid = config.get(CATEOGY_BIOMES, "Ithilien Wasteland Biome ID", 68, "Biome id config")
            .getInt();
        nindalfid = config.get(CATEOGY_BIOMES, "Nindalfid Biome ID", 69, "Biome id config")
            .getInt();
        coldfellsid = config.get(CATEOGY_BIOMES, "Coldfells Biome ID", 70, "Biome id config")
            .getInt();
        nanCurunirid = config.get(CATEOGY_BIOMES, "Nan Curunir Biome ID", 71, "Biome id config")
            .getInt();
        whiteDownsid = config.get(CATEOGY_BIOMES, "White Downs Biome ID", 72, "Biome id config")
            .getInt();
        swanfleetid = config.get(CATEOGY_BIOMES, "Swanfleet Biome ID", 73, "Biome id config")
            .getInt();
        pelennorid = config.get(CATEOGY_BIOMES, "Pelennor Biome ID", 74, "Biome id config")
            .getInt();
        minhiriathid = config.get(CATEOGY_BIOMES, "Minhiriath Biome ID", 75, "Biome id config")
            .getInt();
        ereborid = config.get(CATEOGY_BIOMES, "Erebor Biome ID", 76, "Biome id config")
            .getInt();
        mirkwoodNorthid = config.get(CATEOGY_BIOMES, "Mirkwood North Biome ID", 77, "Biome id config")
            .getInt();
        woodlandRealmHillsid = config.get(CATEOGY_BIOMES, "Woodland Realm Hills Biome ID", 78, "Biome id config")
            .getInt();
        nanUngolid = config.get(CATEOGY_BIOMES, "Nan Ungol Biome ID", 79, "Biome id config")
            .getInt();
        pinnathGelinid = config.get(CATEOGY_BIOMES, "Pinnath Gelin Biome ID", 80, "Biome id config")
            .getInt();
        islandid = config.get(CATEOGY_BIOMES, "Island Biome ID", 81, "Biome id config")
            .getInt();
        forodwaithMountainsid = config.get(CATEOGY_BIOMES, "Forodwaith Mountains Biome ID", 82, "Biome id config")
            .getInt();
        mistyMountainsFoothillsid = config
            .get(CATEOGY_BIOMES, "Misty Mountains Foothills Biome ID", 83, "Biome id config")
            .getInt();
        greyMountainsFoothillsid = config
            .get(CATEOGY_BIOMES, "Grey Mountains Foothills Biome ID", 84, "Biome id config")
            .getInt();
        blueMountainsFoothillsid = config
            .get(CATEOGY_BIOMES, "Blue Mountains Foothills Biome ID", 85, "Biome id config")
            .getInt();
        tundraid = config.get(CATEOGY_BIOMES, "Tundra Biome ID", 86, "Biome id config")
            .getInt();
        taigaid = config.get(CATEOGY_BIOMES, "Taiga Biome ID", 87, "Biome id config")
            .getInt();
        breelandid = config.get(CATEOGY_BIOMES, "Breeland Biome ID", 88, "Biome id config")
            .getInt();
        chetwoodid = config.get(CATEOGY_BIOMES, "Chetwood Biome ID", 89, "Biome id config")
            .getInt();
        forodwaithGlacierid = config.get(CATEOGY_BIOMES, "Forodwaith Glacierid Biome ID", 90, "Biome id config")
            .getInt();
        whiteMountainsFoothillsid = config
            .get(CATEOGY_BIOMES, "White Mountains Foothills Biome ID", 91, "Biome id config")
            .getInt();
        beachid = config.get(CATEOGY_BIOMES, "Beach Biome ID", 92, "Biome id config")
            .getInt();
        beachGravelid = config.get(CATEOGY_BIOMES, "Beach Gravel Biome ID", 93, "Biome id config")
            .getInt();
        nearHaradid = config.get(CATEOGY_BIOMES, "Near Harad Biome ID", 94, "Biome id config")
            .getInt();
        farHaradid = config.get(CATEOGY_BIOMES, "Far Harad Biome ID", 95, "Biome id config")
            .getInt();
        haradMountainsid = config.get(CATEOGY_BIOMES, "Harad Mountains Biome ID", 96, "Biome id config")
            .getInt();
        umbarid = config.get(CATEOGY_BIOMES, "Umbar Biome ID", 97, "Biome id config")
            .getInt();
        farHaradJungleid = config.get(CATEOGY_BIOMES, "Far Harad Jungle Biome ID", 98, "Biome id config")
            .getInt();
        umbarHillsid = config.get(CATEOGY_BIOMES, "Umbar Hills Biome ID", 99, "Biome id config")
            .getInt();
        nearHaradHillsid = config.get(CATEOGY_BIOMES, "Near Harad Hills Biome ID", 100, "Biome id config")
            .getInt();
        farHaradJungleLakeid = config.get(CATEOGY_BIOMES, "Far Harad Jungle Lake Biome ID", 101, "Biome id config")
            .getInt();
        lostladenid = config.get(CATEOGY_BIOMES, "Lostaden Biome ID", 102, "Biome id config")
            .getInt();
        farHaradForestid = config.get(CATEOGY_BIOMES, "Far Harad Forest Biome ID", 103, "Biome id config")
            .getInt();
        nearHaradFertileid = config.get(CATEOGY_BIOMES, "Near Harad Fertile Biome ID", 104, "Biome id config")
            .getInt();
        pertorogwaithid = config.get(CATEOGY_BIOMES, "Pertorogwaith Biome ID", 105, "Biome id config")
            .getInt();
        umbarForestid = config.get(CATEOGY_BIOMES, "Umbar Forest Biome ID", 106, "Biome id config")
            .getInt();
        farHaradJungleEdgeid = config.get(CATEOGY_BIOMES, "Far Harad Jungle Edge Biome ID", 107, "Biome id config")
            .getInt();
        tauredainClearingid = config.get(CATEOGY_BIOMES, "Tauredain Clearing Biome ID", 108, "Biome id config")
            .getInt();
        gulfHaradid = config.get(CATEOGY_BIOMES, "Gulf Harad Biome ID", 109, "Biome id config")
            .getInt();
        dorwinionHillsid = config.get(CATEOGY_BIOMES, "Dorwinion Hills Biome ID", 110, "Biome id config")
            .getInt();
        tolfalasid = config.get(CATEOGY_BIOMES, "Tolfalas Biome ID", 111, "Biome id config")
            .getInt();
        lebenninid = config.get(CATEOGY_BIOMES, "Lebennin Biome ID", 112, "Biome id config")
            .getInt();
        rhunid = config.get(CATEOGY_BIOMES, "Rhun Biome ID", 113, "Biome id config")
            .getInt();
        rhunForestid = config.get(CATEOGY_BIOMES, "Rhun Forest Biome ID", 114, "Biome id config")
            .getInt();
        redMountainsid = config.get(CATEOGY_BIOMES, "Red Mountains Biome ID", 115, "Biome id config")
            .getInt();
        redMountainsFoothillsid = config.get(CATEOGY_BIOMES, "Red Mountains Foothills Biome ID", 116, "Biome id config")
            .getInt();
        dolGuldurid = config.get(CATEOGY_BIOMES, "Dol Guldur Biome ID", 117, "Biome id config")
            .getInt();
        nearHaradSemiDesertid = config.get(CATEOGY_BIOMES, "Near Harad Semi Desert Biome ID", 118, "Biome id config")
            .getInt();
        farHaradAridid = config.get(CATEOGY_BIOMES, "Far Harad Arid Biome ID", 119, "Biome id config")
            .getInt();
        farHaradAridHillsid = config.get(CATEOGY_BIOMES, "Far Harad Arid Hills Biome ID", 120, "Biome id config")
            .getInt();
        farHaradSwampid = config.get(CATEOGY_BIOMES, "Far Harad Swamp Biome ID", 121, "Biome id config")
            .getInt();
        farHaradCloudForestid = config.get(CATEOGY_BIOMES, "Far Harad Cloud Forest Biome ID", 122, "Biome id config")
            .getInt();
        farHaradBushlandid = config.get(CATEOGY_BIOMES, "Far Harad Bushland Biome ID", 123, "Biome id config")
            .getInt();
        farHaradBushlandHillsid = config
            .get(CATEOGY_BIOMES, "Far Harad Bushland Hills Biome ID", 124, "Biome id config")
            .getInt();
        farHaradMangroveid = config.get(CATEOGY_BIOMES, "Far Harad Mangrove Biome ID", 125, "Biome id config")
            .getInt();
        nearHaradFertileForestid = config
            .get(CATEOGY_BIOMES, "Near Harad Fertile Forest Biome ID", 126, "Biome id config")
            .getInt();
        anduinValeid = config.get(CATEOGY_BIOMES, "Anduin Vale Biome ID", 127, "Biome id config")
            .getInt();
        woldid = config.get(CATEOGY_BIOMES, "Wold Biome ID", 128, "Biome id config")
            .getInt();
        shireMoorsid = config.get(CATEOGY_BIOMES, "Shire Moors Biome ID", 129, "Biome id config")
            .getInt();
        shireMarshesid = config.get(CATEOGY_BIOMES, "Shire Marshes Biome ID", 130, "Biome id config")
            .getInt();
        nearHaradRedDesertid = config.get(CATEOGY_BIOMES, "Near Harad Red Desert Biome ID", 131, "Biome id config")
            .getInt();
        farHaradVolcanoid = config.get(CATEOGY_BIOMES, "Far Harad Volcano Biome ID", 132, "Biome id config")
            .getInt();
        udunid = config.get(CATEOGY_BIOMES, "Udun Biome ID", 133, "Biome id config")
            .getInt();
        gorgorothid = config.get(CATEOGY_BIOMES, "Gorgoroth Biome ID", 134, "Biome id config")
            .getInt();
        morgulValeid = config.get(CATEOGY_BIOMES, "Morgul Vale Biome ID", 135, "Biome id config")
            .getInt();
        easternDesolationid = config.get(CATEOGY_BIOMES, "Eastern Desolation Biome ID", 136, "Biome id config")
            .getInt();
        daleid = config.get(CATEOGY_BIOMES, "Dale Biome ID", 137, "Biome id config")
            .getInt();
        dorwinionid = config.get(CATEOGY_BIOMES, "Dorwinion Biome ID", 138, "Biome id config")
            .getInt();
        towerHillsid = config.get(CATEOGY_BIOMES, "Tower Hills Biome ID", 139, "Biome id config")
            .getInt();
        gulfHaradForestid = config.get(CATEOGY_BIOMES, "Gulf Harad Forest Biome ID", 140, "Biome id config")
            .getInt();
        wilderlandNorthid = config.get(CATEOGY_BIOMES, "Wilderland North Biome ID", 141, "Biome id config")
            .getInt();
        forodwaithCoastid = config.get(CATEOGY_BIOMES, "Forodwaith Coast Biome ID", 142, "Biome id config")
            .getInt();
        farHaradCoastid = config.get(CATEOGY_BIOMES, "Far Harad Coast Biome ID", 143, "Biome id config")
            .getInt();
        nearHaradRiverbankid = config.get(CATEOGY_BIOMES, "Near Harad Riverbank Biome ID", 144, "Biome id config")
            .getInt();
        lossarnachid = config.get(CATEOGY_BIOMES, "Lossarnach Biome ID", 145, "Biome id config")
            .getInt();
        imlothMeluiid = config.get(CATEOGY_BIOMES, "Imloth Melui Biome ID", 146, "Biome id config")
            .getInt();
        nearHaradOasisid = config.get(CATEOGY_BIOMES, "Near Harad Oasis Biome ID", 147, "Biome id config")
            .getInt();
        beachWhiteid = config.get(CATEOGY_BIOMES, "Beach White Biome ID", 148, "Biome id config")
            .getInt();
        harnedorid = config.get(CATEOGY_BIOMES, "Harnedor Biome ID", 149, "Biome id config")
            .getInt();
        lamedonid = config.get(CATEOGY_BIOMES, "Lamedon Biome ID", 150, "Biome id config")
            .getInt();
        lamedonHillsid = config.get(CATEOGY_BIOMES, "Lamedon Hills Biome ID", 151, "Biome id config")
            .getInt();
        blackrootValeid = config.get(CATEOGY_BIOMES, "Blackroot Vale Biome ID", 152, "Biome id config")
            .getInt();
        andrastid = config.get(CATEOGY_BIOMES, "Andrast Biome ID", 153, "Biome id config")
            .getInt();
        pukelid = config.get(CATEOGY_BIOMES, "Pukel Biome ID", 154, "Biome id config")
            .getInt();
        rhunLandid = config.get(CATEOGY_BIOMES, "Rhun Land Biome ID", 155, "Biome id config")
            .getInt();
        rhunLandSteppeid = config.get(CATEOGY_BIOMES, "Rhun Land Steppe Biome ID", 156, "Biome id config")
            .getInt();
        rhunLandHillsid = config.get(CATEOGY_BIOMES, "Rhun Land Hills Biome ID", 157, "Biome id config")
            .getInt();
        rhunRedForestid = config.get(CATEOGY_BIOMES, "Rhun Red Forest Biome ID", 158, "Biome id config")
            .getInt();
        rhunIslandid = config.get(CATEOGY_BIOMES, "Rhun Island Biome ID", 159, "Biome id config")
            .getInt();
        rhunIslandForestid = config.get(CATEOGY_BIOMES, "Rhun Island Forest Biome ID", 160, "Biome id config")
            .getInt();
        lastDesertid = config.get(CATEOGY_BIOMES, "Last Desert Biome ID", 161, "Biome id config")
            .getInt();
        windMountainsid = config.get(CATEOGY_BIOMES, "Wind Mountains Biome ID", 162, "Biome id config")
            .getInt();
        windMountainsFoothillsid = config
            .get(CATEOGY_BIOMES, "Wind Mountains Foothills Biome ID", 163, "Biome id config")
            .getInt();
        rivendellid = config.get(CATEOGY_BIOMES, "Rivendell Biome ID", 164, "Biome id config")
            .getInt();
        rivendellHillsid = config.get(CATEOGY_BIOMES, "Rivendell Hills Biome ID", 165, "Biome id config")
            .getInt();
        farHaradJungleMountainsid = config
            .get(CATEOGY_BIOMES, "Far Harad Jungle Mountains Biome ID", 166, "Biome id config")
            .getInt();
        halfTrollForestid = config.get(CATEOGY_BIOMES, "Half Troll Forest Biome ID", 167, "Biome id config")
            .getInt();
        farHaradKanukaid = config.get(CATEOGY_BIOMES, "Far Harad Kanukaid Biome ID", 168, "Biome id config")
            .getInt();
        utumnoid = config.get(CATEOGY_BIOMES, "Utumno Biome ID", 169, "Biome id config")
            .getInt();
        if (config.hasChanged()) {
            config.save();
        }
    }
}
