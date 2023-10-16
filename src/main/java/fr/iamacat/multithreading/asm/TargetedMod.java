package fr.iamacat.multithreading.asm;

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
     * FASTCRAFT("FastCraft", false, startsWith("fastcraft")),
     * OPTIFINE("OptiFine", false, startsWith("optifine")),
     * MEKANISM("Mekanism", false, startsWith("mekanism")),
     * BOTANIA("Botania", false, startsWith("botania+").or(startsWith("botania-")).or(startsWith("botania "))),
     * EXTRAUTILS("ExtraUtilities", false, startsWith("extrautilities"))
     */
    THAUMCRAFT4("Thaumcraft", false, startsWith("thaumcraft")),

    PAMSHARVESTCRAFTGTNHCOMPAT("Pam's HarvestCraft", false, startsWith("harvestcraft")),
    PAMSHARVESTCRAFT("Pam's HarvestCraft", false, startsWith("pam")),
    SHINCOLLE("Shinkeiseikan Collection", false, startsWith("shincolle")),
    COFHCORE("CoFHCore", false, startsWith("cofhcore")),
    MINEFACTORYRELOADED("MinefactoryReloaded", false, startsWith("minefactoryreloaded")),
    MINEFACTORYRELOADED2("MinefactoryReloaded", false, startsWith("cofh_mine")),

    ATUM("Atum", false, startsWith("atum")),
    FARLANDERS("The Farlanders", false, startsWith("farlanders")),
    SLIMECARNAGE("Slime Carnage", false, startsWith("slimecarnage")),
    ATOMICSTYKERSBATTLETOWERS("AtomicStryker's Battletowers", false, startsWith("battletower")),
    SALTYMOD_EXPANDED("Salty Mod Expanded", false, startsWith("SaltMod")),
    BUILDCRAFT("BuildCraft", false, startsWith("BuildCraft|Core")),
    TCONSTRUCT("Tinkers' Construct", false, startsWith("TConstruct")),
    ADVENTOFASCENSION("Advent of Ascension", false, startsWith("nevermine")),
    KINGDOMSOFTHEOVERWORLD("Kingdoms of the Overworld Mod", false, startsWith("Kingdoms of the Overworld")),
    MINERALOGY("Mineralogy", false, startsWith("mineralogy")),
    SHIPEWRECK("Shipwrecks!", false, startsWith("shipwrecks")),
    STEAMCRAFT2("Steamcraft", false, startsWith("steamcraft2")),;

    @Getter
    private final String modName;
    @Getter
    private final boolean loadInDevelopment;
    @Getter
    private final Predicate<String> condition;
}
