package fr.iamacat.multithreading.asm;

import static com.falsepattern.lib.mixin.ITargetedMod.PredicateHelpers.startsWith;

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
     * COFHCORE("CoFHCore", false, startsWith("cofhcore")),
     * EXTRAUTILS("ExtraUtilities", false, startsWith("extrautilities"))
     */
    PAMSHARVESTCRAFT("Pam's HarvestCraft", true, startsWith("harvestcraft")),
    MINEFACTORYRELOADED("MinefactoryReloaded", true, startsWith("minefactoryreloaded")),;

    @Getter
    private final String modName;
    @Getter
    private final boolean loadInDevelopment;
    @Getter
    private final Predicate<String> condition;
}
