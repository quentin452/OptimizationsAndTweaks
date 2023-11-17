package fr.iamacat.optimizationsandtweaks.mixins.common.eternalfrost;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import eternalfrost.EFConfiguration;
import eternalfrost.EternalFrost;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(EFConfiguration.class)
public class MixinEFConfiguration {
    @Shadow
    public static Configuration config;
    @Shadow
    public static final String CATEGORY_GENERAL = "general";
    @Shadow
    public static final String CATEGORY_DIMENSION = "dimension";
    @Shadow
    public static final String CATEGORY_BIOMES = "biomes";
    @Shadow
    public static final String CATEGORY_ENTITIES = "entities";
    @Shadow
    public static final String CATEGORY_OTHERS = "others";
    @Shadow
    public static final boolean DEFAULT_VERSIONCHECKER = true;
    @Shadow
    public static final boolean DEFAULT_SNOW_BOOLEAN = false;
    @Shadow
    public static final boolean DEFAULT_HYPOTHERMIA_BOOLEAN = true;
    @Shadow
    public static final int DEFAULT_DIMENSIONID = 4;
    @Shadow
    public static final int DEFAULT_BIOME_FREEZERID = 47;
    @Shadow
    public static final int DEFAULT_BIOME_FREEZERJUNGLEID = 48;
    @Shadow
    public static final int DEFAULT_BIOME_ETERNALMOUTAINID = 49;
    @Shadow
    public static final int DEFAULT_BIOME_FROZENPLAINID = 50;
    @Shadow
    public static final int DEFAULT_BIOME_ETERNALDESERTID = 51;
    @Shadow
    public static final int DEFAULT_BIOME_TUNDRAID = 52;
    @Shadow
    public static final int DEFAULT_BIOME_GLACIERID = 53;
    @Shadow
    public static boolean versionChecker;
    @Shadow
    public static boolean snowboolean;
    @Shadow
    public static boolean hypothermiaboolean;
    @Shadow
    public static int biomefreezerID;
    @Shadow
    public static int biomefreezerjungleID;
    @Shadow
    public static int biomeEternalMoutainID;
    @Shadow
    public static int biomeFrozenPlainID;
    @Shadow
    public static int biomeEternalDesertID;
    @Shadow
    public static int biomeTundraID;
    @Shadow
    public static int biomeGlacierID;
    @Shadow
    public static int dimensionID;
    @Shadow
    public static String configVersion;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
        }

        try {
            config.load();
            versionChecker = config.getBoolean("Version Checker: ", "general", true, "If the version checker should be enabled.");
            snowboolean = config.getBoolean("Blizzard activation: ", "general", false, "Please be sure that this feature could cause lags!");
            hypothermiaboolean = config.getBoolean("Hypothermia activation", "general", true, "");
            dimensionID = config.getInt("Dimension ID: ", "dimension", 4, 2, 65536, "What ID number to assign to the Eternal Frost");
            FMLCommonHandler.instance().getFMLLogger().log(Level.INFO, "[Eternal Frost] Generating Biome ID's");
            biomefreezerID = config.getInt("Freezer: ", "biomes", 47, 40, 65536, "");
            biomefreezerjungleID = config.getInt("Freezer Jungle: ", "biomes", 48, 40, 65536, "");
            biomeEternalMoutainID = config.getInt("Eternal Mountain: ", "biomes", 49, 40, 65536, "");
            biomeFrozenPlainID = config.getInt("Frozen Plain: ", "biomes", 50, 40, 65536, "");
            biomeEternalDesertID = config.getInt("Eternal Desert: ", "biomes", 51, 40, 65536, "");
            biomeTundraID = config.getInt("Tundra: ", "biomes", 52, 40, 65536, "");
            biomeGlacierID = config.getInt("Glacier: ", "biomes", 53, 40, 65536, "");
            configVersion = config.getString("Config Version: ", "others", "2.0b4", "DO NOT EDIT MANUALLY");
            FMLCommonHandler.instance().getFMLLogger().log(Level.INFO, "[Eternal Frost] Generated Config!");
        } catch (Exception var5) {
            FMLLog.log(Level.FATAL, var5, "Eternal Frost has had a problem loading its configuration");
        } finally {
            if (config.hasChanged()) {
                config.save();
            }

        }

    }
    @Shadow
    public static void onRegistered() {
        if (versionChecker && !configVersion.equals("2.0b4")) {
            EternalFrost.logger.warn("The config file of Eternal Frost is out of date and might cause problems, please remove it so it can be regenerated.");
        }

    }
}
