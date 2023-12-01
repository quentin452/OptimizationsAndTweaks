package fr.iamacat.optimizationsandtweaks.mixins.common.lotr;

import java.awt.*;

import net.minecraft.init.Blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.lotr.LOTRConfigBiomeID;
import lotr.common.LOTRMod;
import lotr.common.world.biome.*;

@Mixin(LOTRBiome.class)
public class MixinLOTRBiome {

    @Shadow
    public static LOTRBiome river;
    @Shadow
    public static LOTRBiome rohan;
    @Shadow
    public static LOTRBiome mistyMountains;
    @Shadow
    public static LOTRBiome shire;
    @Shadow
    public static LOTRBiome shireWoodlands;
    @Shadow
    public static LOTRBiome mordor;
    @Shadow
    public static LOTRBiome mordorMountains;
    @Shadow
    public static LOTRBiome gondor;
    @Shadow
    public static LOTRBiome whiteMountains;
    @Shadow
    public static LOTRBiome lothlorien;
    @Shadow
    public static LOTRBiome celebrant;
    @Shadow
    public static LOTRBiome ironHills;
    @Shadow
    public static LOTRBiome deadMarshes;
    @Shadow
    public static LOTRBiome trollshaws;
    @Shadow
    public static LOTRBiome woodlandRealm;
    @Shadow
    public static LOTRBiome mirkwoodCorrupted;
    @Shadow
    public static LOTRBiome rohanUrukHighlands;
    @Shadow
    public static LOTRBiome emynMuil;
    @Shadow
    public static LOTRBiome ithilien;
    @Shadow
    public static LOTRBiome pelargir;
    @Shadow
    public static LOTRBiome loneLands;
    @Shadow
    public static LOTRBiome loneLandsHills;
    @Shadow
    public static LOTRBiome dunland;
    @Shadow
    public static LOTRBiome fangorn;
    @Shadow
    public static LOTRBiome angle;
    @Shadow
    public static LOTRBiome ettenmoors;
    @Shadow
    public static LOTRBiome oldForest;
    @Shadow
    public static LOTRBiome harondor;
    @Shadow
    public static LOTRBiome eriador;
    @Shadow
    public static LOTRBiome eriadorDowns;
    @Shadow
    public static LOTRBiome erynVorn;
    @Shadow
    public static LOTRBiome greyMountains;
    @Shadow
    public static LOTRBiome midgewater;
    @Shadow
    public static LOTRBiome brownLands;
    @Shadow
    public static LOTRBiome ocean;
    @Shadow
    public static LOTRBiome anduinHills;
    @Shadow
    public static LOTRBiome meneltarma;
    @Shadow
    public static LOTRBiome gladdenFields;
    @Shadow
    public static LOTRBiome lothlorienEdge;
    @Shadow
    public static LOTRBiome forodwaith;
    @Shadow
    public static LOTRBiome enedwaith;
    @Shadow
    public static LOTRBiome angmar;
    @Shadow
    public static LOTRBiome eregion;
    @Shadow
    public static LOTRBiome lindon;
    @Shadow
    public static LOTRBiome lindonWoodlands;
    @Shadow
    public static LOTRBiome eastBight;
    @Shadow
    public static LOTRBiome blueMountains;
    @Shadow
    public static LOTRBiome mirkwoodMountains;
    @Shadow
    public static LOTRBiome wilderland;
    @Shadow
    public static LOTRBiome dagorlad;
    @Shadow
    public static LOTRBiome nurn;
    @Shadow
    public static LOTRBiome nurnen;
    @Shadow
    public static LOTRBiome nurnMarshes;
    @Shadow
    public static LOTRBiome angmarMountains;
    @Shadow
    public static LOTRBiome anduinMouth;
    @Shadow
    public static LOTRBiome entwashMouth;
    @Shadow
    public static LOTRBiome dorEnErnil;
    @Shadow
    public static LOTRBiome dorEnErnilHills;
    @Shadow
    public static LOTRBiome fangornWasteland;
    @Shadow
    public static LOTRBiome rohanWoodlands;
    @Shadow
    public static LOTRBiome gondorWoodlands;
    @Shadow
    public static LOTRBiome lake;
    @Shadow
    public static LOTRBiome lindonCoast;
    @Shadow
    public static LOTRBiome barrowDowns;
    @Shadow
    public static LOTRBiome longMarshes;
    @Shadow
    public static LOTRBiome fangornClearing;
    @Shadow
    public static LOTRBiome ithilienHills;
    @Shadow
    public static LOTRBiome ithilienWasteland;
    @Shadow
    public static LOTRBiome nindalf;
    @Shadow
    public static LOTRBiome coldfells;
    @Shadow
    public static LOTRBiome nanCurunir;
    @Shadow
    public static LOTRBiome adornland;
    @Shadow
    public static LOTRBiome whiteDowns;
    @Shadow
    public static LOTRBiome swanfleet;
    @Shadow
    public static LOTRBiome pelennor;
    @Shadow
    public static LOTRBiome minhiriath;
    @Shadow
    public static LOTRBiome erebor;
    @Shadow
    public static LOTRBiome mirkwoodNorth;
    @Shadow
    public static LOTRBiome woodlandRealmHills;
    @Shadow
    public static LOTRBiome nanUngol;
    @Shadow
    public static LOTRBiome pinnathGelin;
    @Shadow
    public static LOTRBiome island;
    @Shadow
    public static LOTRBiome forodwaithMountains;
    @Shadow
    public static LOTRBiome mistyMountainsFoothills;
    @Shadow
    public static LOTRBiome greyMountainsFoothills;
    @Shadow
    public static LOTRBiome blueMountainsFoothills;
    @Shadow
    public static LOTRBiome tundra;
    @Shadow
    public static LOTRBiome taiga;
    @Shadow
    public static LOTRBiome breeland;
    @Shadow
    public static LOTRBiome chetwood;
    @Shadow
    public static LOTRBiome forodwaithGlacier;
    @Shadow
    public static LOTRBiome whiteMountainsFoothills;
    @Shadow
    public static LOTRBiome beach;
    @Shadow
    public static LOTRBiome beachGravel;
    @Shadow
    public static LOTRBiome nearHarad;
    @Shadow
    public static LOTRBiome farHarad;
    @Shadow
    public static LOTRBiome haradMountains;
    @Shadow
    public static LOTRBiome umbar;
    @Shadow
    public static LOTRBiome farHaradJungle;
    @Shadow
    public static LOTRBiome umbarHills;
    @Shadow
    public static LOTRBiome nearHaradHills;
    @Shadow
    public static LOTRBiome farHaradJungleLake;
    @Shadow
    public static LOTRBiome lostladen;
    @Shadow
    public static LOTRBiome farHaradForest;
    @Shadow
    public static LOTRBiome nearHaradFertile;
    @Shadow
    public static LOTRBiome pertorogwaith;
    @Shadow
    public static LOTRBiome umbarForest;
    @Shadow
    public static LOTRBiome farHaradJungleEdge;
    @Shadow
    public static LOTRBiome tauredainClearing;
    @Shadow
    public static LOTRBiome gulfHarad;
    @Shadow
    public static LOTRBiome dorwinionHills;
    @Shadow
    public static LOTRBiome tolfalas;
    @Shadow
    public static LOTRBiome lebennin;
    @Shadow
    public static LOTRBiome rhun;
    @Shadow
    public static LOTRBiome rhunForest;
    @Shadow
    public static LOTRBiome redMountains;
    @Shadow
    public static LOTRBiome redMountainsFoothills;
    @Shadow
    public static LOTRBiome dolGuldur;
    @Shadow
    public static LOTRBiome nearHaradSemiDesert;
    @Shadow
    public static LOTRBiome farHaradArid;
    @Shadow
    public static LOTRBiome farHaradAridHills;
    @Shadow
    public static LOTRBiome farHaradSwamp;
    @Shadow
    public static LOTRBiome farHaradCloudForest;
    @Shadow
    public static LOTRBiome farHaradBushland;
    @Shadow
    public static LOTRBiome farHaradBushlandHills;
    @Shadow
    public static LOTRBiome farHaradMangrove;
    @Shadow
    public static LOTRBiome nearHaradFertileForest;
    @Shadow
    public static LOTRBiome anduinVale;
    @Shadow
    public static LOTRBiome wold;
    @Shadow
    public static LOTRBiome shireMoors;
    @Shadow
    public static LOTRBiome shireMarshes;
    @Shadow
    public static LOTRBiome nearHaradRedDesert;
    @Shadow
    public static LOTRBiome farHaradVolcano;
    @Shadow
    public static LOTRBiome udun;
    @Shadow
    public static LOTRBiome gorgoroth;
    @Shadow
    public static LOTRBiome morgulVale;
    @Shadow
    public static LOTRBiome easternDesolation;
    @Shadow
    public static LOTRBiome dale;
    @Shadow
    public static LOTRBiome dorwinion;
    @Shadow
    public static LOTRBiome towerHills;
    @Shadow
    public static LOTRBiome gulfHaradForest;
    @Shadow
    public static LOTRBiome wilderlandNorth;
    @Shadow
    public static LOTRBiome forodwaithCoast;
    @Shadow
    public static LOTRBiome farHaradCoast;
    @Shadow
    public static LOTRBiome nearHaradRiverbank;
    @Shadow
    public static LOTRBiome lossarnach;
    @Shadow
    public static LOTRBiome imlothMelui;
    @Shadow
    public static LOTRBiome nearHaradOasis;
    @Shadow
    public static LOTRBiome beachWhite;
    @Shadow
    public static LOTRBiome harnedor;
    @Shadow
    public static LOTRBiome lamedon;
    @Shadow
    public static LOTRBiome lamedonHills;
    @Shadow
    public static LOTRBiome blackrootVale;
    @Shadow
    public static LOTRBiome andrast;
    @Shadow
    public static LOTRBiome pukel;
    @Shadow
    public static LOTRBiome rhunLand;
    @Shadow
    public static LOTRBiome rhunLandSteppe;
    @Shadow
    public static LOTRBiome rhunLandHills;
    @Shadow
    public static LOTRBiome rhunRedForest;
    @Shadow
    public static LOTRBiome rhunIsland;
    @Shadow
    public static LOTRBiome rhunIslandForest;
    @Shadow
    public static LOTRBiome lastDesert;
    @Shadow
    public static LOTRBiome windMountains;
    @Shadow
    public static LOTRBiome windMountainsFoothills;
    @Shadow
    public static LOTRBiome rivendell;
    @Shadow
    public static LOTRBiome rivendellHills;
    @Shadow
    public static LOTRBiome farHaradJungleMountains;
    @Shadow
    public static LOTRBiome halfTrollForest;
    @Shadow
    public static LOTRBiome farHaradKanuka;
    @Shadow
    public static LOTRBiome utumno;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void initBiomes() {
        river = (new LOTRBiomeGenRiver(LOTRConfigBiomeID.riverid, false)).setMinMaxHeight(-0.5F, 0.0F)
            .setColor(3570869)
            .setBiomeName("river");
        rohan = (new LOTRBiomeGenRohan(LOTRConfigBiomeID.rohanid, true)).setTemperatureRainfall(0.8F, 0.8F)
            .setMinMaxHeight(0.2F, 0.15F)
            .setColor(7384389)
            .setBiomeName("rohan");
        mistyMountains = (new LOTRBiomeGenMistyMountains(LOTRConfigBiomeID.mistyMountainsid, true))
            .setTemperatureRainfall(0.2F, 0.5F)
            .setMinMaxHeight(2.0F, 2.0F)
            .setColor(15263713)
            .setBiomeName("mistyMountains");
        shire = (new LOTRBiomeGenShire(LOTRConfigBiomeID.shireid, true)).setTemperatureRainfall(0.8F, 0.9F)
            .setMinMaxHeight(0.15F, 0.3F)
            .setColor(6794549)
            .setBiomeName("shire");
        shireWoodlands = (new LOTRBiomeGenShireWoodlands(LOTRConfigBiomeID.shireWoodlandsid, true))
            .setTemperatureRainfall(0.8F, 0.9F)
            .setMinMaxHeight(0.3F, 0.5F)
            .setColor(4486966)
            .setBiomeName("shireWoodlands");
        mordor = (new LOTRBiomeGenMordor(LOTRConfigBiomeID.mordorid, true)).setTemperatureRainfall(2.0F, 0.0F)
            .setMinMaxHeight(0.3F, 0.5F)
            .setColor(1118222)
            .setBiomeName("mordor");
        mordorMountains = (new LOTRBiomeGenMordorMountains(LOTRConfigBiomeID.mordorMountainsid, true))
            .setTemperatureRainfall(2.0F, 0.0F)
            .setMinMaxHeight(2.0F, 3.0F)
            .setColor(5328200)
            .setBiomeName("mordorMountains");
        gondor = (new LOTRBiomeGenGondor(LOTRConfigBiomeID.gondorid, true)).setTemperatureRainfall(0.8F, 0.8F)
            .setMinMaxHeight(0.1F, 0.15F)
            .setColor(8959045)
            .setBiomeName("gondor");
        whiteMountains = (new LOTRBiomeGenWhiteMountains(LOTRConfigBiomeID.whiteMountainsid, true))
            .setTemperatureRainfall(0.6F, 0.8F)
            .setMinMaxHeight(1.5F, 2.0F)
            .setColor(15066600)
            .setBiomeName("whiteMountains");
        lothlorien = (new LOTRBiomeGenLothlorien(LOTRConfigBiomeID.lothlorienid, true))
            .setTemperatureRainfall(0.9F, 1.0F)
            .setMinMaxHeight(0.1F, 0.3F)
            .setColor(16504895)
            .setBiomeName("lothlorien");
        celebrant = (new LOTRBiomeGenCelebrant(LOTRConfigBiomeID.celebrantid, true)).setTemperatureRainfall(1.1F, 1.1F)
            .setMinMaxHeight(0.1F, 0.05F)
            .setColor(7647046)
            .setBiomeName("celebrant");
        ironHills = (new LOTRBiomeGenIronHills(LOTRConfigBiomeID.ironHillsid, true)).setTemperatureRainfall(0.27F, 0.4F)
            .setMinMaxHeight(0.3F, 1.4F)
            .setColor(9142093)
            .setBiomeName("ironHills");
        deadMarshes = (new LOTRBiomeGenDeadMarshes(LOTRConfigBiomeID.deadMarshesid, true))
            .setTemperatureRainfall(0.4F, 1.0F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(7303999)
            .setBiomeName("deadMarshes");
        trollshaws = (new LOTRBiomeGenTrollshaws(LOTRConfigBiomeID.trollshawsid, true))
            .setTemperatureRainfall(0.6F, 0.8F)
            .setMinMaxHeight(0.15F, 1.0F)
            .setColor(5798959)
            .setBiomeName("trollshaws");
        woodlandRealm = (new LOTRBiomeGenWoodlandRealm(LOTRConfigBiomeID.woodlandRealmid, true))
            .setTemperatureRainfall(0.8F, 0.9F)
            .setMinMaxHeight(0.2F, 0.3F)
            .setColor(4089126)
            .setBiomeName("woodlandRealm");
        mirkwoodCorrupted = (new LOTRBiomeGenMirkwoodCorrupted(LOTRConfigBiomeID.mirkwoodCorruptedid, true))
            .setTemperatureRainfall(0.6F, 0.8F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(3032091)
            .setBiomeName("mirkwoodCorrupted");
        rohanUrukHighlands = (new LOTRBiomeGenRohanUruk(LOTRConfigBiomeID.rohanUrukHighlandsid, true))
            .setTemperatureRainfall(0.7F, 0.4F)
            .setMinMaxHeight(0.8F, 0.3F)
            .setColor(8295258)
            .setBiomeName("rohanUrukHighlands");
        emynMuil = (new LOTRBiomeGenEmynMuil(LOTRConfigBiomeID.emynMuilid, true)).setTemperatureRainfall(0.5F, 0.9F)
            .setMinMaxHeight(0.2F, 0.8F)
            .setColor(9866354)
            .setBiomeName("emynMuil");
        ithilien = (new LOTRBiomeGenIthilien(LOTRConfigBiomeID.ithilienid, true)).setTemperatureRainfall(0.9F, 0.9F)
            .setMinMaxHeight(0.15F, 0.5F)
            .setColor(7710516)
            .setBiomeName("ithilien");
        pelargir = (new LOTRBiomeGenPelargir(LOTRConfigBiomeID.pelargirid, true)).setTemperatureRainfall(1.0F, 1.0F)
            .setMinMaxHeight(0.08F, 0.2F)
            .setColor(11256145)
            .setBiomeName("pelargir");
        loneLands = (new LOTRBiomeGenLoneLands(LOTRConfigBiomeID.loneLandsid, true)).setTemperatureRainfall(0.6F, 0.5F)
            .setMinMaxHeight(0.15F, 0.4F)
            .setColor(8562762)
            .setBiomeName("loneLands");
        loneLandsHills = (new LOTRBiomeGenLoneLandsHills(LOTRConfigBiomeID.loneLandsHillsid, false))
            .setTemperatureRainfall(0.6F, 0.5F)
            .setMinMaxHeight(0.6F, 0.8F)
            .setColor(8687182)
            .setBiomeName("loneLandsHills");
        dunland = (new LOTRBiomeGenDunland(LOTRConfigBiomeID.dunlandid, true)).setTemperatureRainfall(0.4F, 0.7F)
            .setMinMaxHeight(0.3F, 0.5F)
            .setColor(6920524)
            .setBiomeName("dunland");
        fangorn = (new LOTRBiomeGenFangorn(LOTRConfigBiomeID.fangornid, true)).setTemperatureRainfall(0.7F, 0.8F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(4355353)
            .setBiomeName("fangorn");
        angle = (new LOTRBiomeGenAngle(LOTRConfigBiomeID.angleid, true)).setTemperatureRainfall(0.6F, 0.8F)
            .setMinMaxHeight(0.15F, 0.3F)
            .setColor(9416527)
            .setBiomeName("angle");
        ettenmoors = (new LOTRBiomeGenEttenmoors(LOTRConfigBiomeID.ettenmoorsid, true))
            .setTemperatureRainfall(0.2F, 0.6F)
            .setMinMaxHeight(0.5F, 0.6F)
            .setColor(8161626)
            .setBiomeName("ettenmoors");
        oldForest = (new LOTRBiomeGenOldForest(LOTRConfigBiomeID.oldForestid, true)).setTemperatureRainfall(0.5F, 1.0F)
            .setMinMaxHeight(0.2F, 0.3F)
            .setColor(4551995)
            .setBiomeName("oldForest");
        harondor = (new LOTRBiomeGenHarondor(LOTRConfigBiomeID.harondorid, true)).setTemperatureRainfall(1.0F, 0.6F)
            .setMinMaxHeight(0.2F, 0.3F)
            .setColor(10663238)
            .setBiomeName("harondor");
        eriador = (new LOTRBiomeGenEriador(LOTRConfigBiomeID.eriadorid, true)).setTemperatureRainfall(0.9F, 0.8F)
            .setMinMaxHeight(0.1F, 0.4F)
            .setColor(7054916)
            .setBiomeName("eriador");
        eriadorDowns = (new LOTRBiomeGenEriadorDowns(LOTRConfigBiomeID.eriadorDownsid, true))
            .setTemperatureRainfall(0.6F, 0.7F)
            .setMinMaxHeight(0.5F, 0.5F)
            .setColor(7638087)
            .setBiomeName("eriadorDowns");
        erynVorn = (new LOTRBiomeGenErynVorn(LOTRConfigBiomeID.erynVornid, false)).setTemperatureRainfall(0.8F, 0.9F)
            .setMinMaxHeight(0.1F, 0.4F)
            .setColor(4357965)
            .setBiomeName("erynVorn");
        greyMountains = (new LOTRBiomeGenGreyMountains(LOTRConfigBiomeID.greyMountainsid, true))
            .setTemperatureRainfall(0.28F, 0.2F)
            .setMinMaxHeight(1.8F, 2.0F)
            .setColor(13290689)
            .setBiomeName("greyMountains");
        midgewater = (new LOTRBiomeGenMidgewater(LOTRConfigBiomeID.midgewaterid, true))
            .setTemperatureRainfall(0.6F, 1.0F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(6001495)
            .setBiomeName("midgewater");
        brownLands = (new LOTRBiomeGenBrownLands(LOTRConfigBiomeID.brownLandsid, true))
            .setTemperatureRainfall(1.0F, 0.2F)
            .setMinMaxHeight(0.2F, 0.2F)
            .setColor(8552016)
            .setBiomeName("brownLands");
        ocean = (new LOTRBiomeGenOcean(LOTRConfigBiomeID.oceanid, false)).setTemperatureRainfall(0.8F, 0.8F)
            .setMinMaxHeight(-1.0F, 0.3F)
            .setColor(153997)
            .setBiomeName("ocean");
        anduinHills = (new LOTRBiomeGenAnduin(LOTRConfigBiomeID.anduinHillsid, true)).setTemperatureRainfall(0.7F, 0.7F)
            .setMinMaxHeight(0.6F, 0.4F)
            .setColor(7058012)
            .setBiomeName("anduinHills");
        meneltarma = (new LOTRBiomeGenMeneltarma(LOTRConfigBiomeID.meneltarmaid, false))
            .setTemperatureRainfall(0.9F, 0.8F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(9549658)
            .setBiomeName("meneltarma");
        gladdenFields = (new LOTRBiomeGenGladdenFields(LOTRConfigBiomeID.gladdenFieldsid, true))
            .setTemperatureRainfall(0.6F, 1.2F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(5020505)
            .setBiomeName("gladdenFields");
        lothlorienEdge = (new LOTRBiomeGenLothlorienEdge(LOTRConfigBiomeID.lothlorienEdgeid, true))
            .setTemperatureRainfall(0.9F, 1.0F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(13944387)
            .setBiomeName("lothlorienEdge");
        forodwaith = (new LOTRBiomeGenForodwaith(LOTRConfigBiomeID.forodwaithid, true))
            .setTemperatureRainfall(0.0F, 0.2F)
            .setMinMaxHeight(0.1F, 0.1F)
            .setColor(14211282)
            .setBiomeName("forodwaith");
        enedwaith = (new LOTRBiomeGenEnedwaith(LOTRConfigBiomeID.enedwaithid, true)).setTemperatureRainfall(0.6F, 0.8F)
            .setMinMaxHeight(0.2F, 0.3F)
            .setColor(8038479)
            .setBiomeName("enedwaith");
        angmar = (new LOTRBiomeGenAngmar(LOTRConfigBiomeID.angmarid, true)).setTemperatureRainfall(0.2F, 0.2F)
            .setMinMaxHeight(0.2F, 0.6F)
            .setColor(5523247)
            .setBiomeName("angmar");
        eregion = (new LOTRBiomeGenEregion(LOTRConfigBiomeID.eregionid, true)).setTemperatureRainfall(0.6F, 0.7F)
            .setMinMaxHeight(0.2F, 0.3F)
            .setColor(6656072)
            .setBiomeName("eregion");
        lindon = (new LOTRBiomeGenLindon(LOTRConfigBiomeID.lindonid, true)).setTemperatureRainfall(0.9F, 0.9F)
            .setMinMaxHeight(0.15F, 0.2F)
            .setColor(7646533)
            .setBiomeName("lindon");
        lindonWoodlands = (new LOTRBiomeGenLindonWoodlands(LOTRConfigBiomeID.lindonWoodlandsid, false))
            .setTemperatureRainfall(0.9F, 1.0F)
            .setMinMaxHeight(0.2F, 0.5F)
            .setColor(1996591)
            .setBiomeName("lindonWoodlands");
        eastBight = (new LOTRBiomeGenEastBight(LOTRConfigBiomeID.eastBightid, true)).setTemperatureRainfall(0.8F, 0.3F)
            .setMinMaxHeight(0.15F, 0.05F)
            .setColor(9082205)
            .setBiomeName("eastBight");
        blueMountains = (new LOTRBiomeGenBlueMountains(LOTRConfigBiomeID.blueMountainsid, true))
            .setTemperatureRainfall(0.22F, 0.8F)
            .setMinMaxHeight(1.0F, 2.5F)
            .setColor(13228770)
            .setBiomeName("blueMountains");
        mirkwoodMountains = (new LOTRBiomeGenMirkwoodMountains(LOTRConfigBiomeID.mirkwoodMountainsid, true))
            .setTemperatureRainfall(0.28F, 0.9F)
            .setMinMaxHeight(1.2F, 1.5F)
            .setColor(2632989)
            .setBiomeName("mirkwoodMountains");
        wilderland = (new LOTRBiomeGenWilderland(LOTRConfigBiomeID.wilderlandid, true))
            .setTemperatureRainfall(0.9F, 0.4F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(9612368)
            .setBiomeName("wilderland");
        dagorlad = (new LOTRBiomeGenDagorlad(LOTRConfigBiomeID.dagorladid, true)).setTemperatureRainfall(1.0F, 0.2F)
            .setMinMaxHeight(0.1F, 0.05F)
            .setColor(7036741)
            .setBiomeName("dagorlad");
        nurn = (new LOTRBiomeGenNurn(LOTRConfigBiomeID.nurnid, true)).setTemperatureRainfall(0.9F, 0.4F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(2630683)
            .setBiomeName("nurn");
        nurnen = (new LOTRBiomeGenNurnen(LOTRConfigBiomeID.nurnenid, false)).setTemperatureRainfall(0.9F, 0.4F)
            .setMinMaxHeight(-1.0F, 0.3F)
            .setColor(931414)
            .setBiomeName("nurnen");
        nurnMarshes = (new LOTRBiomeGenNurnMarshes(LOTRConfigBiomeID.nurnMarshesid, true))
            .setTemperatureRainfall(0.9F, 0.4F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(4012843)
            .setBiomeName("nurnMarshes");
        adornland = (new LOTRBiomeGenAdornland(LOTRConfigBiomeID.adornlandid, true)).setTemperatureRainfall(0.7F, 0.6F)
            .setMinMaxHeight(0.2F, 0.2F)
            .setColor(7838543)
            .setBiomeName("adornland");
        angmarMountains = (new LOTRBiomeGenAngmarMountains(LOTRConfigBiomeID.angmarMountainsid, true))
            .setTemperatureRainfall(0.25F, 0.1F)
            .setMinMaxHeight(1.6F, 1.5F)
            .setColor(13619147)
            .setBiomeName("angmarMountains");
        anduinMouth = (new LOTRBiomeGenAnduinMouth(LOTRConfigBiomeID.anduinMouthid, true))
            .setTemperatureRainfall(0.9F, 1.0F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(5089363)
            .setBiomeName("anduinMouth");
        entwashMouth = (new LOTRBiomeGenEntwashMouth(LOTRConfigBiomeID.entwashMouthid, true))
            .setTemperatureRainfall(0.5F, 1.0F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(5612358)
            .setBiomeName("entwashMouth");
        dorEnErnil = (new LOTRBiomeGenDorEnErnil(LOTRConfigBiomeID.dorEnErnilid, true))
            .setTemperatureRainfall(0.9F, 0.9F)
            .setMinMaxHeight(0.07F, 0.2F)
            .setColor(9355077)
            .setBiomeName("dorEnErnil");
        dorEnErnilHills = (new LOTRBiomeGenDorEnErnilHills(LOTRConfigBiomeID.dorEnErnilHillsid, false))
            .setTemperatureRainfall(0.8F, 0.7F)
            .setMinMaxHeight(0.5F, 0.5F)
            .setColor(8560707)
            .setBiomeName("dorEnErnilHills");
        fangornWasteland = (new LOTRBiomeGenFangornWasteland(LOTRConfigBiomeID.fangornWastelandid, true))
            .setTemperatureRainfall(0.7F, 0.4F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(6782028)
            .setBiomeName("fangornWasteland");
        rohanWoodlands = (new LOTRBiomeGenRohanWoodlands(LOTRConfigBiomeID.rohanWoodlandsid, false))
            .setTemperatureRainfall(0.9F, 0.9F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(5736246)
            .setBiomeName("rohanWoodlands");
        gondorWoodlands = (new LOTRBiomeGenGondorWoodlands(LOTRConfigBiomeID.gondorWoodlandsid, false))
            .setTemperatureRainfall(0.8F, 0.9F)
            .setMinMaxHeight(0.2F, 0.2F)
            .setColor(5867307)
            .setBiomeName("gondorWoodlands");
        lake = (new LOTRBiomeGenLake(LOTRConfigBiomeID.lakeid, false)).setColor(3433630)
            .setBiomeName("lake");
        lindonCoast = (new LOTRBiomeGenLindonCoast(LOTRConfigBiomeID.lindonCoastid, false))
            .setTemperatureRainfall(0.9F, 0.9F)
            .setMinMaxHeight(0.0F, 0.5F)
            .setColor(9278870)
            .setBiomeName("lindonCoast");
        barrowDowns = (new LOTRBiomeGenBarrowDowns(LOTRConfigBiomeID.barrowDownsid, true))
            .setTemperatureRainfall(0.6F, 0.7F)
            .setMinMaxHeight(0.3F, 0.4F)
            .setColor(8097362)
            .setBiomeName("barrowDowns");
        longMarshes = (new LOTRBiomeGenLongMarshes(LOTRConfigBiomeID.longMarshesid, true))
            .setTemperatureRainfall(0.6F, 0.9F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(7178054)
            .setBiomeName("longMarshes");
        fangornClearing = (new LOTRBiomeGenFangornClearing(LOTRConfigBiomeID.fangornClearingid, false))
            .setTemperatureRainfall(0.7F, 0.8F)
            .setMinMaxHeight(0.2F, 0.1F)
            .setColor(5877050)
            .setBiomeName("fangornClearing");
        ithilienHills = (new LOTRBiomeGenIthilienHills(LOTRConfigBiomeID.ithilienHillsid, false))
            .setTemperatureRainfall(0.7F, 0.7F)
            .setMinMaxHeight(0.6F, 0.6F)
            .setColor(6985792)
            .setBiomeName("ithilienHills");
        ithilienWasteland = (new LOTRBiomeGenIthilienWasteland(LOTRConfigBiomeID.ithilienWastelandid, true))
            .setTemperatureRainfall(0.6F, 0.6F)
            .setMinMaxHeight(0.15F, 0.2F)
            .setColor(8030031)
            .setBiomeName("ithilienWasteland");
        nindalf = (new LOTRBiomeGenNindalf(LOTRConfigBiomeID.nindalfid, true)).setTemperatureRainfall(0.4F, 1.0F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(7111750)
            .setBiomeName("nindalf");
        coldfells = (new LOTRBiomeGenColdfells(LOTRConfigBiomeID.coldfellsid, true)).setTemperatureRainfall(0.25F, 0.8F)
            .setMinMaxHeight(0.4F, 0.8F)
            .setColor(8296018)
            .setBiomeName("coldfells");
        nanCurunir = (new LOTRBiomeGenNanCurunir(LOTRConfigBiomeID.nanCurunirid, true))
            .setTemperatureRainfall(0.6F, 0.4F)
            .setMinMaxHeight(0.2F, 0.1F)
            .setColor(7109714)
            .setBiomeName("nanCurunir");
        whiteDowns = (new LOTRBiomeGenWhiteDowns(LOTRConfigBiomeID.whiteDownsid, true))
            .setTemperatureRainfall(0.6F, 0.7F)
            .setMinMaxHeight(0.6F, 0.6F)
            .setColor(10210937)
            .setBiomeName("whiteDowns");
        swanfleet = (new LOTRBiomeGenSwanfleet(LOTRConfigBiomeID.swanfleetid, true)).setTemperatureRainfall(0.8F, 1.0F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(6265945)
            .setBiomeName("swanfleet");
        pelennor = (new LOTRBiomeGenPelennor(LOTRConfigBiomeID.pelennorid, true)).setTemperatureRainfall(0.9F, 0.9F)
            .setMinMaxHeight(0.1F, 0.02F)
            .setColor(11258955)
            .setBiomeName("pelennor");
        minhiriath = (new LOTRBiomeGenMinhiriath(LOTRConfigBiomeID.minhiriathid, true))
            .setTemperatureRainfall(0.7F, 0.4F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(7380550)
            .setBiomeName("minhiriath");
        erebor = (new LOTRBiomeGenErebor(LOTRConfigBiomeID.ereborid, true)).setTemperatureRainfall(0.6F, 0.7F)
            .setMinMaxHeight(0.4F, 0.6F)
            .setColor(7499093)
            .setBiomeName("erebor");
        mirkwoodNorth = (new LOTRBiomeGenMirkwoodNorth(LOTRConfigBiomeID.mirkwoodNorthid, true))
            .setTemperatureRainfall(0.7F, 0.7F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(3822115)
            .setBiomeName("mirkwoodNorth");
        woodlandRealmHills = (new LOTRBiomeGenWoodlandRealmHills(LOTRConfigBiomeID.woodlandRealmHillsid, false))
            .setTemperatureRainfall(0.8F, 0.6F)
            .setMinMaxHeight(0.9F, 0.7F)
            .setColor(3624991)
            .setBiomeName("woodlandRealmHills");
        nanUngol = (new LOTRBiomeGenNanUngol(LOTRConfigBiomeID.nanUngolid, true)).setTemperatureRainfall(2.0F, 0.0F)
            .setMinMaxHeight(0.1F, 0.4F)
            .setColor(656641)
            .setBiomeName("nanUngol");
        pinnathGelin = (new LOTRBiomeGenPinnathGelin(LOTRConfigBiomeID.pinnathGelinid, true))
            .setTemperatureRainfall(0.8F, 0.8F)
            .setMinMaxHeight(0.5F, 0.5F)
            .setColor(9946693)
            .setBiomeName("pinnathGelin");
        island = (new LOTRBiomeGenOcean(LOTRConfigBiomeID.islandid, false)).setTemperatureRainfall(0.9F, 0.8F)
            .setMinMaxHeight(0.0F, 0.3F)
            .setColor(10138963)
            .setBiomeName("island");
        forodwaithMountains = (new LOTRBiomeGenForodwaithMountains(LOTRConfigBiomeID.forodwaithMountainsid, true))
            .setTemperatureRainfall(0.0F, 0.2F)
            .setMinMaxHeight(2.0F, 2.0F)
            .setColor(15592942)
            .setBiomeName("forodwaithMountains");
        mistyMountainsFoothills = (new LOTRBiomeGenMistyMountainsFoothills(
            LOTRConfigBiomeID.mistyMountainsFoothillsid,
            true)).setTemperatureRainfall(0.25F, 0.6F)
                .setMinMaxHeight(0.7F, 0.9F)
                .setColor(12501430)
                .setBiomeName("mistyMountainsFoothills");
        greyMountainsFoothills = (new LOTRBiomeGenGreyMountainsFoothills(
            LOTRConfigBiomeID.greyMountainsFoothillsid,
            true)).setTemperatureRainfall(0.5F, 0.7F)
                .setMinMaxHeight(0.5F, 0.9F)
                .setColor(9148000)
                .setBiomeName("greyMountainsFoothills");
        blueMountainsFoothills = (new LOTRBiomeGenBlueMountainsFoothills(
            LOTRConfigBiomeID.blueMountainsFoothillsid,
            true)).setTemperatureRainfall(0.5F, 0.8F)
                .setMinMaxHeight(0.5F, 0.9F)
                .setColor(11253170)
                .setBiomeName("blueMountainsFoothills");
        tundra = (new LOTRBiomeGenTundra(LOTRConfigBiomeID.tundraid, true)).setTemperatureRainfall(0.1F, 0.3F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(12366486)
            .setBiomeName("tundra");
        taiga = (new LOTRBiomeGenTaiga(LOTRConfigBiomeID.taigaid, true)).setTemperatureRainfall(0.1F, 0.7F)
            .setMinMaxHeight(0.1F, 0.5F)
            .setColor(6526543)
            .setBiomeName("taiga");
        breeland = (new LOTRBiomeGenBreeland(LOTRConfigBiomeID.breelandid, true)).setTemperatureRainfall(0.8F, 0.7F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(6861625)
            .setBiomeName("breeland");
        chetwood = (new LOTRBiomeGenChetwood(LOTRConfigBiomeID.chetwoodid, true)).setTemperatureRainfall(0.8F, 0.9F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(4424477)
            .setBiomeName("chetwood");
        forodwaithGlacier = (new LOTRBiomeGenForodwaithGlacier(LOTRConfigBiomeID.forodwaithGlacierid, true))
            .setTemperatureRainfall(0.0F, 0.1F)
            .setMinMaxHeight(1.0F, 0.1F)
            .setColor(9424096)
            .setBiomeName("forodwaithGlacier");
        whiteMountainsFoothills = (new LOTRBiomeGenWhiteMountainsFoothills(
            LOTRConfigBiomeID.whiteMountainsFoothillsid,
            true)).setTemperatureRainfall(0.6F, 0.7F)
                .setMinMaxHeight(0.5F, 0.9F)
                .setColor(12635575)
                .setBiomeName("whiteMountainsFoothills");
        beach = (new LOTRBiomeGenBeach(LOTRConfigBiomeID.beachid, false)).setBeachBlock(Blocks.sand, 0)
            .setColor(14404247)
            .setBiomeName("beach");
        beachGravel = (new LOTRBiomeGenBeach(LOTRConfigBiomeID.beachGravelid, false)).setBeachBlock(Blocks.gravel, 0)
            .setColor(9868704)
            .setBiomeName("beachGravel");
        nearHarad = (new LOTRBiomeGenNearHarad(LOTRConfigBiomeID.nearHaradid, true)).setTemperatureRainfall(1.5F, 0.1F)
            .setMinMaxHeight(0.2F, 0.1F)
            .setColor(14205815)
            .setBiomeName("nearHarad");
        farHarad = (new LOTRBiomeGenFarHaradSavannah(LOTRConfigBiomeID.farHaradid, true))
            .setTemperatureRainfall(1.2F, 0.2F)
            .setMinMaxHeight(0.1F, 0.1F)
            .setColor(9740353)
            .setBiomeName("farHarad");
        haradMountains = (new LOTRBiomeGenHaradMountains(LOTRConfigBiomeID.haradMountainsid, true))
            .setTemperatureRainfall(0.9F, 0.5F)
            .setMinMaxHeight(1.8F, 2.0F)
            .setColor(9867381)
            .setBiomeName("haradMountains");
        umbar = (new LOTRBiomeGenUmbar(LOTRConfigBiomeID.umbarid, true)).setTemperatureRainfall(0.9F, 0.6F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(9542740)
            .setBiomeName("umbar");
        farHaradJungle = (new LOTRBiomeGenFarHaradJungle(LOTRConfigBiomeID.farHaradJungleid, true))
            .setTemperatureRainfall(1.2F, 0.9F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(4944931)
            .setBiomeName("farHaradJungle");
        umbarHills = (new LOTRBiomeGenUmbar(LOTRConfigBiomeID.umbarHillsid, false)).setTemperatureRainfall(0.8F, 0.5F)
            .setMinMaxHeight(1.2F, 0.8F)
            .setColor(8226378)
            .setBiomeName("umbarHills");
        nearHaradHills = (new LOTRBiomeGenNearHaradHills(LOTRConfigBiomeID.nearHaradHillsid, false))
            .setTemperatureRainfall(1.2F, 0.3F)
            .setMinMaxHeight(0.5F, 0.8F)
            .setColor(12167010)
            .setBiomeName("nearHaradHills");
        farHaradJungleLake = (new LOTRBiomeGenFarHaradJungleLake(LOTRConfigBiomeID.farHaradJungleLakeid, false))
            .setTemperatureRainfall(1.2F, 0.9F)
            .setMinMaxHeight(-0.5F, 0.2F)
            .setColor(2271948)
            .setBiomeName("farHaradJungleLake");
        lostladen = (new LOTRBiomeGenLostladen(LOTRConfigBiomeID.lostladenid, true)).setTemperatureRainfall(1.2F, 0.2F)
            .setMinMaxHeight(0.2F, 0.1F)
            .setColor(10658661)
            .setBiomeName("lostladen");
        farHaradForest = (new LOTRBiomeGenFarHaradForest(LOTRConfigBiomeID.farHaradForestid, true))
            .setTemperatureRainfall(1.0F, 1.0F)
            .setMinMaxHeight(0.3F, 0.4F)
            .setColor(3703325)
            .setBiomeName("farHaradForest");
        nearHaradFertile = (new LOTRBiomeGenNearHaradFertile(LOTRConfigBiomeID.nearHaradFertileid, true))
            .setTemperatureRainfall(1.2F, 0.7F)
            .setMinMaxHeight(0.2F, 0.1F)
            .setColor(10398286)
            .setBiomeName("nearHaradFertile");
        pertorogwaith = (new LOTRBiomeGenPertorogwaith(LOTRConfigBiomeID.pertorogwaithid, true))
            .setTemperatureRainfall(0.7F, 0.1F)
            .setMinMaxHeight(0.2F, 0.5F)
            .setColor(8879706)
            .setBiomeName("pertorogwaith");
        umbarForest = (new LOTRBiomeGenUmbarForest(LOTRConfigBiomeID.umbarForestid, false))
            .setTemperatureRainfall(0.8F, 0.8F)
            .setMinMaxHeight(0.2F, 0.3F)
            .setColor(7178042)
            .setBiomeName("umbarForest");
        farHaradJungleEdge = (new LOTRBiomeGenFarHaradJungleEdge(LOTRConfigBiomeID.farHaradJungleEdgeid, true))
            .setTemperatureRainfall(1.2F, 0.8F)
            .setMinMaxHeight(0.2F, 0.2F)
            .setColor(7440430)
            .setBiomeName("farHaradJungleEdge");
        tauredainClearing = (new LOTRBiomeGenTauredainClearing(LOTRConfigBiomeID.tauredainClearingid, true))
            .setTemperatureRainfall(1.2F, 0.8F)
            .setMinMaxHeight(0.2F, 0.2F)
            .setColor(10796101)
            .setBiomeName("tauredainClearing");
        gulfHarad = (new LOTRBiomeGenGulfHarad(LOTRConfigBiomeID.gulfHaradid, true)).setTemperatureRainfall(1.0F, 0.5F)
            .setMinMaxHeight(0.15F, 0.1F)
            .setColor(9152592)
            .setBiomeName("gulfHarad");
        dorwinionHills = (new LOTRBiomeGenDorwinionHills(LOTRConfigBiomeID.dorwinionHillsid, true))
            .setTemperatureRainfall(0.9F, 0.8F)
            .setMinMaxHeight(0.8F, 0.8F)
            .setColor(13357993)
            .setBiomeName("dorwinionHills");
        tolfalas = (new LOTRBiomeGenTolfalas(LOTRConfigBiomeID.tolfalasid, true)).setTemperatureRainfall(0.8F, 0.4F)
            .setMinMaxHeight(0.3F, 1.0F)
            .setColor(10199149)
            .setBiomeName("tolfalas");
        lebennin = (new LOTRBiomeGenLebennin(LOTRConfigBiomeID.lebenninid, true)).setTemperatureRainfall(1.0F, 0.9F)
            .setMinMaxHeight(0.1F, 0.3F)
            .setColor(7845418)
            .setBiomeName("lebennin");
        rhun = (new LOTRBiomeGenRhun(LOTRConfigBiomeID.rhunid, true)).setTemperatureRainfall(0.9F, 0.3F)
            .setMinMaxHeight(0.3F, 0.0F)
            .setColor(10465880)
            .setBiomeName("rhun");
        rhunForest = (new LOTRBiomeGenRhunForest(LOTRConfigBiomeID.rhunForestid, true))
            .setTemperatureRainfall(0.8F, 0.9F)
            .setMinMaxHeight(0.3F, 0.5F)
            .setColor(7505723)
            .setBiomeName("rhunForest");
        redMountains = (new LOTRBiomeGenRedMountains(LOTRConfigBiomeID.redMountainsid, true))
            .setTemperatureRainfall(0.3F, 0.4F)
            .setMinMaxHeight(1.5F, 2.0F)
            .setColor(9662796)
            .setBiomeName("redMountains");
        redMountainsFoothills = (new LOTRBiomeGenRedMountainsFoothills(LOTRConfigBiomeID.redMountainsFoothillsid, true))
            .setTemperatureRainfall(0.7F, 0.4F)
            .setMinMaxHeight(0.5F, 0.9F)
            .setColor(10064978)
            .setBiomeName("redMountainsFoothills");
        dolGuldur = (new LOTRBiomeGenDolGuldur(LOTRConfigBiomeID.dolGuldurid, true)).setTemperatureRainfall(0.6F, 0.8F)
            .setMinMaxHeight(0.2F, 0.5F)
            .setColor(2371343)
            .setBiomeName("dolGuldur");
        nearHaradSemiDesert = (new LOTRBiomeGenNearHaradSemiDesert(LOTRConfigBiomeID.nearHaradSemiDesertid, true))
            .setTemperatureRainfall(1.5F, 0.2F)
            .setMinMaxHeight(0.2F, 0.1F)
            .setColor(12434282)
            .setBiomeName("nearHaradSemiDesert");
        farHaradArid = (new LOTRBiomeGenFarHaradArid(LOTRConfigBiomeID.farHaradAridid, true))
            .setTemperatureRainfall(1.5F, 0.3F)
            .setMinMaxHeight(0.2F, 0.15F)
            .setColor(11185749)
            .setBiomeName("farHaradArid");
        farHaradAridHills = (new LOTRBiomeGenFarHaradArid(LOTRConfigBiomeID.farHaradAridHillsid, false))
            .setTemperatureRainfall(1.5F, 0.3F)
            .setMinMaxHeight(1.0F, 0.6F)
            .setColor(10063195)
            .setBiomeName("farHaradAridHills");
        farHaradSwamp = (new LOTRBiomeGenFarHaradSwamp(LOTRConfigBiomeID.farHaradSwampid, true))
            .setTemperatureRainfall(0.8F, 1.0F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(5608267)
            .setBiomeName("farHaradSwamp");
        farHaradCloudForest = (new LOTRBiomeGenFarHaradCloudForest(LOTRConfigBiomeID.farHaradCloudForestid, true))
            .setTemperatureRainfall(1.2F, 1.2F)
            .setMinMaxHeight(0.7F, 0.4F)
            .setColor(3046208)
            .setBiomeName("farHaradCloudForest");
        farHaradBushland = (new LOTRBiomeGenFarHaradBushland(LOTRConfigBiomeID.farHaradBushlandid, true))
            .setTemperatureRainfall(1.0F, 0.4F)
            .setMinMaxHeight(0.2F, 0.1F)
            .setColor(10064190)
            .setBiomeName("farHaradBushland");
        farHaradBushlandHills = (new LOTRBiomeGenFarHaradBushland(LOTRConfigBiomeID.farHaradBushlandHillsid, false))
            .setTemperatureRainfall(0.8F, 0.4F)
            .setMinMaxHeight(0.8F, 0.8F)
            .setColor(8354100)
            .setBiomeName("farHaradBushlandHills");
        farHaradMangrove = (new LOTRBiomeGenFarHaradMangrove(LOTRConfigBiomeID.farHaradMangroveid, true))
            .setTemperatureRainfall(1.0F, 0.9F)
            .setMinMaxHeight(-0.05F, 0.05F)
            .setColor(8883789)
            .setBiomeName("farHaradMangrove");
        nearHaradFertileForest = (new LOTRBiomeGenNearHaradFertileForest(
            LOTRConfigBiomeID.nearHaradFertileForestid,
            false)).setTemperatureRainfall(1.2F, 1.0F)
                .setMinMaxHeight(0.2F, 0.4F)
                .setColor(6915122)
                .setBiomeName("nearHaradFertileForest");
        anduinVale = (new LOTRBiomeGenAnduinVale(LOTRConfigBiomeID.anduinValeid, true))
            .setTemperatureRainfall(0.9F, 1.1F)
            .setMinMaxHeight(0.05F, 0.05F)
            .setColor(7447880)
            .setBiomeName("anduinVale");
        wold = (new LOTRBiomeGenWold(LOTRConfigBiomeID.woldid, true)).setTemperatureRainfall(0.9F, 0.1F)
            .setMinMaxHeight(0.4F, 0.3F)
            .setColor(9483599)
            .setBiomeName("wold");
        shireMoors = (new LOTRBiomeGenShireMoors(LOTRConfigBiomeID.shireMoorsid, true))
            .setTemperatureRainfall(0.6F, 1.6F)
            .setMinMaxHeight(0.4F, 0.6F)
            .setColor(6921036)
            .setBiomeName("shireMoors");
        shireMarshes = (new LOTRBiomeGenShireMarshes(LOTRConfigBiomeID.shireMarshesid, true))
            .setTemperatureRainfall(0.8F, 1.2F)
            .setMinMaxHeight(0.0F, 0.1F)
            .setColor(4038751)
            .setBiomeName("shireMarshes");
        nearHaradRedDesert = (new LOTRBiomeGenNearHaradRed(LOTRConfigBiomeID.nearHaradRedDesertid, true))
            .setTemperatureRainfall(1.5F, 0.1F)
            .setMinMaxHeight(0.2F, 0.0F)
            .setColor(13210447)
            .setBiomeName("nearHaradRedDesert");
        farHaradVolcano = (new LOTRBiomeGenFarHaradVolcano(LOTRConfigBiomeID.farHaradVolcanoid, true))
            .setTemperatureRainfall(1.5F, 0.0F)
            .setMinMaxHeight(0.6F, 1.2F)
            .setColor(6838068)
            .setBiomeName("farHaradVolcano");
        udun = (new LOTRBiomeGenUdun(LOTRConfigBiomeID.udunid, true)).setTemperatureRainfall(1.5F, 0.0F)
            .setMinMaxHeight(0.2F, 0.7F)
            .setColor(65536)
            .setBiomeName("udun");
        gorgoroth = (new LOTRBiomeGenGorgoroth(LOTRConfigBiomeID.gorgorothid, true)).setTemperatureRainfall(2.0F, 0.0F)
            .setMinMaxHeight(0.6F, 0.2F)
            .setColor(2170141)
            .setBiomeName("gorgoroth");
        morgulVale = (new LOTRBiomeGenMorgulVale(LOTRConfigBiomeID.morgulValeid, true))
            .setTemperatureRainfall(1.0F, 0.0F)
            .setMinMaxHeight(0.2F, 0.1F)
            .setColor(1387801)
            .setBiomeName("morgulVale");
        easternDesolation = (new LOTRBiomeGenEasternDesolation(LOTRConfigBiomeID.easternDesolationid, true))
            .setTemperatureRainfall(1.0F, 0.3F)
            .setMinMaxHeight(0.2F, 0.2F)
            .setColor(6052935)
            .setBiomeName("easternDesolation");
        dale = (new LOTRBiomeGenDale(LOTRConfigBiomeID.daleid, true)).setTemperatureRainfall(0.8F, 0.7F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(8233807)
            .setBiomeName("dale");
        dorwinion = (new LOTRBiomeGenDorwinion(LOTRConfigBiomeID.dorwinionid, true)).setTemperatureRainfall(0.9F, 0.9F)
            .setMinMaxHeight(0.1F, 0.3F)
            .setColor(7120197)
            .setBiomeName("dorwinion");
        towerHills = (new LOTRBiomeGenTowerHills(LOTRConfigBiomeID.towerHillsid, true))
            .setTemperatureRainfall(0.8F, 0.8F)
            .setMinMaxHeight(0.5F, 0.5F)
            .setColor(6854209)
            .setBiomeName("towerHills");
        gulfHaradForest = (new LOTRBiomeGenGulfHaradForest(LOTRConfigBiomeID.gulfHaradForestid, false))
            .setTemperatureRainfall(1.0F, 1.0F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(5868590)
            .setBiomeName("gulfHaradForest");
        wilderlandNorth = (new LOTRBiomeGenWilderlandNorth(LOTRConfigBiomeID.wilderlandNorthid, true))
            .setTemperatureRainfall(0.6F, 0.6F)
            .setMinMaxHeight(0.2F, 0.5F)
            .setColor(9676396)
            .setBiomeName("wilderlandNorth");
        forodwaithCoast = (new LOTRBiomeGenForodwaithCoast(LOTRConfigBiomeID.forodwaithCoastid, false))
            .setTemperatureRainfall(0.0F, 0.4F)
            .setMinMaxHeight(0.0F, 0.5F)
            .setColor(9214637)
            .setBiomeName("forodwaithCoast");
        farHaradCoast = (new LOTRBiomeGenFarHaradCoast(LOTRConfigBiomeID.farHaradCoastid, false))
            .setTemperatureRainfall(1.2F, 0.8F)
            .setMinMaxHeight(0.0F, 0.5F)
            .setColor(8356472)
            .setBiomeName("farHaradCoast");
        nearHaradRiverbank = (new LOTRBiomeGenNearHaradRiverbank(LOTRConfigBiomeID.nearHaradRiverbankid, false))
            .setTemperatureRainfall(1.2F, 0.8F)
            .setMinMaxHeight(0.1F, 0.1F)
            .setColor(7183952)
            .setBiomeName("nearHaradRiverbank");
        lossarnach = (new LOTRBiomeGenLossarnach(LOTRConfigBiomeID.lossarnachid, true))
            .setTemperatureRainfall(1.0F, 1.0F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(8439086)
            .setBiomeName("lossarnach");
        imlothMelui = (new LOTRBiomeGenImlothMelui(LOTRConfigBiomeID.imlothMeluiid, true))
            .setTemperatureRainfall(1.0F, 1.2F)
            .setMinMaxHeight(0.1F, 0.2F)
            .setColor(14517608)
            .setBiomeName("imlothMelui");
        nearHaradOasis = (new LOTRBiomeGenNearHaradOasis(LOTRConfigBiomeID.nearHaradOasisid, false))
            .setTemperatureRainfall(1.2F, 0.8F)
            .setMinMaxHeight(0.1F, 0.1F)
            .setColor(832768)
            .setBiomeName("nearHaradOasis");
        beachWhite = (new LOTRBiomeGenBeach(LOTRConfigBiomeID.beachWhiteid, false)).setBeachBlock(LOTRMod.whiteSand, 0)
            .setColor(15592941)
            .setBiomeName("beachWhite");
        harnedor = (new LOTRBiomeGenHarnedor(LOTRConfigBiomeID.harnedorid, true)).setTemperatureRainfall(1.0F, 0.3F)
            .setMinMaxHeight(0.1F, 0.3F)
            .setColor(11449173)
            .setBiomeName("harnedor");
        lamedon = (new LOTRBiomeGenLamedon(LOTRConfigBiomeID.lamedonid, true)).setTemperatureRainfall(0.9F, 0.5F)
            .setMinMaxHeight(0.2F, 0.2F)
            .setColor(10927460)
            .setBiomeName("lamedon");
        lamedonHills = (new LOTRBiomeGenLamedonHills(LOTRConfigBiomeID.lamedonHillsid, true))
            .setTemperatureRainfall(0.6F, 0.4F)
            .setMinMaxHeight(0.6F, 0.9F)
            .setColor(13555369)
            .setBiomeName("lamedonHills");
        blackrootVale = (new LOTRBiomeGenBlackrootVale(LOTRConfigBiomeID.blackrootValeid, true))
            .setTemperatureRainfall(0.8F, 0.9F)
            .setMinMaxHeight(0.2F, 0.12F)
            .setColor(7183921)
            .setBiomeName("blackrootVale");
        andrast = (new LOTRBiomeGenAndrast(LOTRConfigBiomeID.andrastid, true)).setTemperatureRainfall(0.8F, 0.8F)
            .setMinMaxHeight(0.2F, 0.2F)
            .setColor(8885856)
            .setBiomeName("andrast");
        pukel = (new LOTRBiomeGenPukel(LOTRConfigBiomeID.pukelid, true)).setTemperatureRainfall(0.7F, 0.7F)
            .setMinMaxHeight(0.2F, 0.4F)
            .setColor(5667394)
            .setBiomeName("pukel");
        rhunLand = (new LOTRBiomeGenRhunLand(LOTRConfigBiomeID.rhunLandid, true)).setTemperatureRainfall(1.0F, 0.8F)
            .setMinMaxHeight(0.1F, 0.3F)
            .setColor(11381583)
            .setBiomeName("rhunLand");
        rhunLandSteppe = (new LOTRBiomeGenRhunLandSteppe(LOTRConfigBiomeID.rhunLandSteppeid, true))
            .setTemperatureRainfall(1.0F, 0.3F)
            .setMinMaxHeight(0.2F, 0.05F)
            .setColor(11712354)
            .setBiomeName("rhunLandSteppe");
        rhunLandHills = (new LOTRBiomeGenRhunLandHills(LOTRConfigBiomeID.rhunLandHillsid, true))
            .setTemperatureRainfall(1.0F, 0.5F)
            .setMinMaxHeight(0.6F, 0.8F)
            .setColor(9342286)
            .setBiomeName("rhunLandHills");
        rhunRedForest = (new LOTRBiomeGenRhunRedForest(LOTRConfigBiomeID.rhunRedForestid, true))
            .setTemperatureRainfall(0.9F, 1.0F)
            .setMinMaxHeight(0.1F, 0.3F)
            .setColor(9530430)
            .setBiomeName("rhunRedForest");
        rhunIsland = (new LOTRBiomeGenRhunIsland(LOTRConfigBiomeID.rhunIslandid, false))
            .setTemperatureRainfall(1.0F, 0.8F)
            .setMinMaxHeight(0.1F, 0.4F)
            .setColor(10858839)
            .setBiomeName("rhunIsland");
        rhunIslandForest = (new LOTRBiomeGenRhunIslandForest(LOTRConfigBiomeID.rhunIslandForestid, false))
            .setTemperatureRainfall(0.9F, 1.0F)
            .setMinMaxHeight(0.1F, 0.4F)
            .setColor(9533758)
            .setBiomeName("rhunIslandForest");
        lastDesert = (new LOTRBiomeGenLastDesert(LOTRConfigBiomeID.lastDesertid, true))
            .setTemperatureRainfall(0.7F, 0.0F)
            .setMinMaxHeight(0.2F, 0.05F)
            .setColor(13878151)
            .setBiomeName("lastDesert");
        windMountains = (new LOTRBiomeGenWindMountains(LOTRConfigBiomeID.windMountainsid, true))
            .setTemperatureRainfall(0.28F, 0.2F)
            .setMinMaxHeight(2.0F, 2.0F)
            .setColor(13882323)
            .setBiomeName("windMountains");
        windMountainsFoothills = (new LOTRBiomeGenWindMountainsFoothills(
            LOTRConfigBiomeID.windMountainsFoothillsid,
            true)).setTemperatureRainfall(0.4F, 0.6F)
                .setMinMaxHeight(0.5F, 0.6F)
                .setColor(10133354)
                .setBiomeName("windMountainsFoothills");
        rivendell = (new LOTRBiomeGenRivendell(LOTRConfigBiomeID.rivendellid, true)).setTemperatureRainfall(0.9F, 1.0F)
            .setMinMaxHeight(0.15F, 0.3F)
            .setColor(8828714)
            .setBiomeName("rivendell");
        rivendellHills = (new LOTRBiomeGenRivendellHills(LOTRConfigBiomeID.rivendellHillsid, true))
            .setTemperatureRainfall(0.7F, 0.8F)
            .setMinMaxHeight(2.0F, 0.5F)
            .setColor(14210481)
            .setBiomeName("rivendellHills");
        farHaradJungleMountains = (new LOTRBiomeGenFarHaradJungleMountains(
            LOTRConfigBiomeID.farHaradJungleMountainsid,
            true)).setTemperatureRainfall(1.0F, 1.0F)
                .setMinMaxHeight(1.8F, 1.5F)
                .setColor(6511174)
                .setBiomeName("farHaradJungleMountains");
        halfTrollForest = (new LOTRBiomeGenHalfTrollForest(LOTRConfigBiomeID.halfTrollForestid, true))
            .setTemperatureRainfall(0.8F, 0.2F)
            .setMinMaxHeight(0.3F, 0.4F)
            .setColor(5992500)
            .setBiomeName("halfTrollForest");
        farHaradKanuka = (new LOTRBiomeGenKanuka(LOTRConfigBiomeID.farHaradKanukaid, true))
            .setTemperatureRainfall(1.0F, 1.0F)
            .setMinMaxHeight(0.3F, 0.5F)
            .setColor(5142552)
            .setBiomeName("farHaradKanuka");
        utumno = (new LOTRBiomeGenUtumno(LOTRConfigBiomeID.utumnoid)).setTemperatureRainfall(2.0F, 0.0F)
            .setMinMaxHeight(0.0F, 0.0F)
            .setColor(0)
            .setBiomeName("utumno");
    }

}
