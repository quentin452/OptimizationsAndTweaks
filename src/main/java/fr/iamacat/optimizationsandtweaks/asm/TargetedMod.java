package fr.iamacat.optimizationsandtweaks.asm;

import static com.falsepattern.lib.mixin.ITargetedMod.PredicateHelpers.*;

import java.util.function.Predicate;

import com.falsepattern.lib.mixin.ITargetedMod;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TargetedMod implements ITargetedMod {

    /*
     * CHICKENCHUNKS("ChickenChunks", false, startsWith("chickenchunks")),
     * MRTJPCORE("MrTJPCore", false, startsWith("mrtjpcore")),
     * CHUNK_PREGENERATOR("ChunkPregenerator", false, startsWith("chunk+pregen")),
     * THERMALEXPANSION("ThermalExpansion", false, startsWith("thermalexpansion")),
     * THERMALFOUNDATION("ThermalFoundation", false, startsWith("thermalfoundation")),
     * GREGTECH6("GregTech", false, startsWith("gregtech")),
     * MATTEROVERDRIVE("MatterOverdrive", false, startsWith("matteroverdrive")),
     * PROJECTE("ProjectE", false, startsWith("projecte")),
     * TC4TWEAKS("TC4Tweaks", false, startsWith("thaumcraft4tweaks")),
     * OPTIFINE("OptiFine", false, startsWith("optifine")),
     * MEKANISM("Mekanism", false, startsWith("mekanism")),
     * BOTANIA("Botania", false, startsWith("botania+").or(startsWith("botania-")).or(startsWith("botania "))),
     */
    MANAMETAL("manametalmod", false, startsWith("manametalmod")),
    SMOOTHFONT("SmoothFont", false, startsWith("SmoothFont")),

    EXTRAUTILS("ExtraUtilities", false, startsWith("extrautilities")),

    INSTRUMENTUS("Instrumentus", false, startsWith("Instrumentus")),
    FASTCRAFT("FastCraft", false, startsWith("fastcraft")),
    BETTERBURNING("BetterBurning", false, startsWith("BetterBurning")),
    FALSETWEAKS("Falsetweaks", false, startsWith("Falsetweaks")),
    DAVINCIVESSELS("archimedesshipsplus", false, startsWith("archimedesshipsplus")),
    MOVINGWORLD("movingworld", false, startsWith("movingworld")),
    INDUSTRIALCRAFT("industrialcraft", false, startsWith("industrialcraft")),

    HARDCOREWITHER("Hardcore Wither", false, startsWith("Hardcore Wither")),
    EASYBREEDING("easybreading", false, startsWith("easybreading")),
    PORTALGUN("PortalGun", false, startsWith("PortalGun")),
    CODECHICKENCORE("CodeChickenCore", false, startsWith("CodeChickenCore")),

    OPTIFINE("OptiFine", false, startsWith("Optifine")),
    NEI("NotEnoughItems", false, startsWith("nei")),
    ADVENTURERS_AMULETS("Adventurer's Amulets", false, startsWith("AdventurersAmulets")),
    JAS("JustAnotherSpawner", false, startsWith("JustAnotherSpawner")),
    GARDENSTUFF("GardenStuff", false, startsWith("GardenStuff")),

    DRAGONBLOCKC("DragonBlockC", false, startsWith("DragonBlockC")),

    UNTHAUMIC("Thaumcraft Minus Thaumcraft", false, startsWith("Thaumcraft Minus Thaumcraft")),

    THAUMCRAFT4("Thaumcraft", false, startsWith("thaumcraft")),
    GEMSNJEWELS("gemsnjewels", false, startsWith("gemsnjewels")),
    ETFUTURMREQUIEM("Et_Futurum_Requiem", false, startsWith("Et_Futurum_Requiem")),
    FAMILIARSAPI("FamiliarsAPI", false, startsWith("FamiliarsAPI")),
    ADVANCEDGENETICS("advancedgenetics", false, startsWith("advancedgenetics")),
    NETHERLICIOUS("netherlicious", false, startsWith("netherlicious")),

    PAMSHARVESTCRAFT("Pam's HarvestCraft", false, contains("harvestcraft")),
    SHINCOLLE("Shinkeiseikan Collection", false, startsWith("shincolle")),
    COFHCORE("CoFHCore", false, startsWith("cofhcore")),
    MINEFACTORYRELOADED("MinefactoryReloaded", false, startsWith("minefactoryreloaded")),
    ESSENCEOFTHEGOD("Essence_of_the_Gods", false, startsWith("Essence_of_the_Gods")),
    ESSENCEOFTHEGODFORK("eotg", false, startsWith("eotg")),
    GROWTHCRAFT("growthcraft", false, startsWith("growthcraft")),
    DISEASECRAFT("DiseaseCraft", false, startsWith("DiseaseCraft")),

    AKATSUKI("Akatsuk", false, startsWith("Akatsuk")),
    INDUSTRIALUPGRADE("IndustrialUpgrade", false, startsWith("IndustrialUpgrade")),
    SMALLSTAIRS("Small Stairs", false, startsWith("Small Stairs")),
    XAEROWORLDMAP("XaerosWorldMap", false, startsWith("XaerosWorldMap")),
    XAEROMINIMAP("Xaeros_Minimap", false, startsWith("Xaeros_Minimap")),

    ATUM("Atum", false, startsWith("atum")),
    FARLANDERS("The Farlanders", false, startsWith("farlanders")),
    SLIMECARNAGE("SlimeCarnage", false, startsWith("SlimeCarnage")),
    ATOMICSTYKERSBATTLETOWERS("AtomicStryker's Battletowers", false, startsWith("battletower")),
    SALTYMOD_EXPANDED("Salty Mod Expanded", false, startsWith("SaltMod")),
    BUILDCRAFT("BuildCraft", false, startsWith("BuildCraft|Core")),
    TCONSTRUCT("TConstruct", false, startsWith("TConstruct")),
    ADVENTOFASCENSION("Advent of Ascension", false, startsWith("nevermine")),
    KINGDOMSOFTHEOVERWORLD("Kingdoms of the Overworld Mod", false, startsWith("Kingdoms of the Overworld")),
    MINERALOGY("Mineralogy", false, startsWith("mineralogy")),
    LORDOFTHERINGS("LOTRMod", false, startsWith("LOTRMod")),
    LORDOFTHERINGSFORK("LOTRModfork", false, startsWith("LOTRModfork")),
    LOTRIMPROVEMENTS("LOTR Improvements", false, startsWith("LOTR Improvements")),
    ADVANCEDWORLDSELECTION("WorldSelectionAdvanced", false, startsWith("WorldSelectionAdvanced")),

    ETERNALFROST("eternalfrost", false, startsWith("eternalfrost")),
    LOOTPLUSPLUS("Loot++", false, startsWith("Loot++")),
    KITCHENCRAFT("KitchenCraft", false, startsWith("KitchenCraft")),

    ENDLESSIDS("endlessids", false, startsWith("endlessids")),
    MALCORE("MalCore", false, startsWith("MalCore")),
    MATMOS("matmos", false, startsWith("matmos")),
    SHIPEWRECK("Shipwrecks!", false, startsWith("shipwrecks")),
    STEAMCRAFT2("Steamcraft", false, startsWith("steamcraft2")),
    PNEUMATICRAFT("pneumaticCraft", false, startsWith("pneumaticCraft")),
    NOTENOUGHPETS("NotEnoughPets", false, startsWith("NotEnoughPets")),
    PRACTICALLOGISTICS("Practical-Logistics", false, startsWith("Practical")),
    SONARCORE("SonarCore", false, startsWith("SonarCore")),
    BUILDCRAFTOILTWEAK("BuildCraftOilTweak", false, startsWith("BuildCraftOilTweak")),
    CATWALK2OFFICIAL("catwalksof", false, startsWith("catwalks")),
    CATWALK2UNOFFICIAL("catwalksunof", false, matches("catwalks-1.7.10-2.1.4-GTNH")),
    BLOCKLINGS("Blocklings", false, startsWith("Blocklings")),
    FLAXBEARDSTEAMPOWER("FSP", false, startsWith("FSP")),
    SKINPORT("SkinPort", false, startsWith("SkinPort")),
    MINESTONES("minestones", false, startsWith("minestones")),
    BLENDTRONIC("blendtronic", false, startsWith("blendtronic")),
    WITCHERY("witchery-", false, startsWith("witchery-")),
    WITCHERYY("witchery", false, startsWith("witchery")),

    JEWELRYCRAFT2("Jewelrycraft2", false, startsWith("Jewelrycraft2")),
    RAGDOLLCORPSE("ragdollCorpses", false, startsWith("ragdollCorpses")),
    OPIS("Opis", false, startsWith("Opis")),

    AETHER("aether", false, startsWith("aether")),;

    @Getter
    private final String modName;
    @Getter
    private final boolean loadInDevelopment;
    @Getter
    private final Predicate<String> condition;
}
