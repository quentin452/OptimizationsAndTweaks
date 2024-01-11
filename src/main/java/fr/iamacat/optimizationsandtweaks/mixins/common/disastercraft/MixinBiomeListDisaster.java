package fr.iamacat.optimizationsandtweaks.mixins.common.disastercraft;

import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import fr.emerald.disaster.biome.*;
import fr.emerald.disaster.world.WorldTypeDisaster;
import fr.iamacat.optimizationsandtweaks.utilsformods.disastercraft.DisastercraftConfigBiomeID;

@Mixin(BiomeListDisaster.class)
public class MixinBiomeListDisaster {

    @Shadow
    public static WorldTypeDisaster worldTypeDisaster;
    @Shadow
    public static BiomeGenBase devastedDesert;
    @Shadow
    public static BiomeGenBase gravel;
    @Shadow
    public static BiomeGenBase devasted;
    @Shadow
    public static BiomeGenBase mountain;
    @Shadow
    protected static final BiomeGenBase.Height height_MidHills = new BiomeGenBase.Height(0.8F, 0.4F);
    @Shadow
    protected static final BiomeGenBase.Height height_LowPlains = new BiomeGenBase.Height(0.125F, 0.03F);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void BiomeList() {
        devastedDesert = (new BiomeGenDevastedDesert(DisastercraftConfigBiomeID.devastedDesertId)).setColor(16421912)
            .setBiomeName("Desert")
            .setDisableRain()
            .setTemperatureRainfall(2.0F, 0.0F)
            .setHeight(height_LowPlains);
        gravel = (new BiomeGenGravel(DisastercraftConfigBiomeID.gravelId)).setColor(16421912)
            .setBiomeName("Desert Gravel")
            .setDisableRain()
            .setTemperatureRainfall(2.0F, 0.0F)
            .setHeight(height_LowPlains);
        devasted = (new BiomeGenDevasted(DisastercraftConfigBiomeID.devastedId)).setColor(16421912)
            .setBiomeName("Devastated")
            .setDisableRain()
            .setTemperatureRainfall(2.0F, 0.0F)
            .setHeight(height_LowPlains);
        mountain = (new BiomeGenMountainDevasted(DisastercraftConfigBiomeID.mountainId)).setColor(16421912)
            .setBiomeName("Devastated Mountain")
            .setDisableRain()
            .setTemperatureRainfall(2.0F, 0.0F)
            .setHeight(height_MidHills);
    }
}
