package fr.iamacat.optimizationsandtweaks.mixins.common.lotofmobs;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.lom.lotsomobsbiomes.*;
import com.lom.lotsomobscore.ConfigDetails;
import com.lom.lotsomobsdino.WorldProviderDino;
import com.lom.lotsomobsiceage.WorldProviderIceAge;
import com.lom.lotsomobsinit.LotsOMobsBiomes;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.lotsOMobs.LotsOMobsConfigBiomeID;

@Mixin(LotsOMobsBiomes.class)
public class MixinLotsOMobsBiomes {

    @Shadow
    public static BiomeGenBase modBiomeAntartica;
    @Shadow
    public static BiomeGenBase modBiomeArcticOcean;
    @Shadow
    public static BiomeGenBase modBiomeDinoPlains;
    @Shadow
    public static BiomeGenBase modBiomeDinoMountains;
    @Shadow
    public static BiomeGenBase modBiomeDinoOcean;
    @Shadow
    public static BiomeGenBase modBiomeDinoIslands;
    @Shadow
    public static BiomeGenBase modBiomeDinoJungle;
    @Shadow
    public static BiomeGenBase modBiomeIcePlains;
    @Shadow
    public static BiomeGenBase modBiomeIceMountains;
    @Shadow
    public static BiomeGenBase modBiomeIceOcean;
    @Shadow
    public static BiomeGenBase modBiomeIceIslands;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void Init() {
        modBiomeAntartica = (new BiomeGenAntartica(LotsOMobsConfigBiomeID.antarticaId)).setColor(747097)
            .setBiomeName("Antartica");
        modBiomeArcticOcean = (new BiomeGenArcticOcean(LotsOMobsConfigBiomeID.articOceanId)).setColor(747097)
            .setBiomeName("Arctic Ocean");
        modBiomeDinoPlains = (new BiomeGenDinoPlains(LotsOMobsConfigBiomeID.dinoPlainsId)).setColor(6546587)
            .setBiomeName("Dino Plains");
        modBiomeDinoMountains = (new BiomeGenDinoMountains(LotsOMobsConfigBiomeID.dinoMountainsId + 1))
            .setColor(6546587)
            .setBiomeName("Dino Mountains");
        modBiomeDinoOcean = (new BiomeGenDinoOcean(LotsOMobsConfigBiomeID.dinoOceanId + 2)).setColor(6546587)
            .setBiomeName("Dino Ocean");
        modBiomeDinoIslands = (new BiomeGenDinoIslands(LotsOMobsConfigBiomeID.dinoIslandsId + 3)).setColor(6546587)
            .setBiomeName("Dino Islands");
        modBiomeDinoJungle = (new BiomeGenDinoJungle(LotsOMobsConfigBiomeID.dinoJungleId + 4)).setColor(6546587)
            .setBiomeName("Dino Jungle");
        modBiomeIcePlains = (new BiomeGenIcePlains(LotsOMobsConfigBiomeID.IcePlainsId + 1)).setColor(6546587)
            .setBiomeName("Ice Plains");
        modBiomeIceMountains = (new BiomeGenIceMountains(LotsOMobsConfigBiomeID.IceMountainsId + 2)).setColor(6546587)
            .setBiomeName("Ice Mountains");
        modBiomeIceOcean = (new BiomeGenIceOcean(LotsOMobsConfigBiomeID.IceOceanId + 3)).setColor(6546587)
            .setBiomeName("Ice Ocean");
        modBiomeIceIslands = (new BiomeGenIceIslands(LotsOMobsConfigBiomeID.IceIslandsId + 4)).setColor(6546587)
            .setBiomeName("Ice Islands");
        DimensionManager.registerProviderType(ConfigDetails.dimension, WorldProviderDino.class, false);
        DimensionManager.registerDimension(ConfigDetails.dimension, ConfigDetails.dimension);
        DimensionManager.registerProviderType(ConfigDetails.dimension2, WorldProviderIceAge.class, false);
        DimensionManager.registerDimension(ConfigDetails.dimension2, ConfigDetails.dimension2);
    }
}
