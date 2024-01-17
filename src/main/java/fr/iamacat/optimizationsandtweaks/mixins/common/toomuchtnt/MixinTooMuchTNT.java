package fr.iamacat.optimizationsandtweaks.mixins.common.toomuchtnt;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.toomuchtnt.*;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import fr.iamacat.optimizationsandtweaks.utils.toomuchTNT.BlockNuclearWaste2;

@Mixin(TooMuchTNT.class)
public class MixinTooMuchTNT {

    @Shadow
    public static CommonProxyTooMuchTNT proxy;
    @Shadow
    public static Block TNTx5;
    @Shadow
    public static Block TNTx20;
    @Shadow
    public static Block TNTx100;
    @Shadow
    public static Block TNTx500;
    @Shadow
    public static Block MeteorTNT;
    @Shadow
    public static Block FlatTNT;
    @Shadow
    public static Block MiningFlatTNT;
    @Shadow
    public static Block CompactTNT;
    @Shadow
    public static Block HouseTNT;
    @Shadow
    public static Block WoodHouseTNT;
    @Shadow
    public static Block BrickHouseTNT;
    @Shadow
    public static Block FireTNT;
    @Shadow
    public static Block SnowTNT;
    @Shadow
    public static Block OceanTNT;
    @Shadow
    public static Block HellFireTNT;
    @Shadow
    public static Block VaporizeTNT;
    @Shadow
    public static Block EnderTNT;
    @Shadow
    public static Block NuclearTNT;
    @Shadow
    public static Block DiggingTNT;
    @Shadow
    public static Block DrillingTNT;
    @Shadow
    public static Block GhostTNT;
    @Shadow
    public static Block MultiplyTNT;
    @Shadow
    public static Block CubicTNT;
    @Shadow
    public static Block EruptingTNT;
    @Shadow
    public static Block ChemicalTNT;
    @Shadow
    public static Block FloatingTNT;
    @Shadow
    public static Block IceTNT;
    @Shadow
    public static Block TimerTNT;
    @Shadow
    public static Block ReactionTNT;
    @Shadow
    public static Block AnimalTNT;
    @Shadow
    public static Block SandFirework;
    @Shadow
    public static Block FloatingIslandTNT;
    @Shadow
    public static Block TNTFirework;
    @Shadow
    public static Block GravityTNT;
    @Shadow
    public static Block MeteorShowerTNT;
    @Shadow
    public static Block SpiralTNT;
    @Shadow
    public static Block GroveTNT;
    @Shadow
    public static Block InvertedTNT;
    @Shadow
    public static Block CustomTNT;
    @Shadow
    public static Block EasterEgg;
    @Shadow
    public static Block MountainTopRemoval;
    @Shadow
    public static Block MankindsMark;
    @Shadow
    public static Block PoseidonsWave;
    @Shadow
    public static Block HellsGate;
    @Shadow
    public static Block DustBowl;
    @Shadow
    public static Block Hexahedron;
    @Shadow
    public static Block HeavensGate;
    @Shadow
    public static Block TheRevolution;
    @Shadow
    public static Block Chicxulub;
    @Shadow
    public static Block NuclearWaste;
    @Shadow
    public static Block SulfurOre;
    @Shadow
    public static Block UraniumOre;
    @Shadow
    public static Block Meteorite;
    @Shadow
    public static Item Dynamite;
    @Shadow
    public static Item Dynamitex5;
    @Shadow
    public static Item Dynamitex20;
    @Shadow
    public static Item Dynamitex100;
    @Shadow
    public static Item Dynamitex500;
    @Shadow
    public static Item MeteorDynamite;
    @Shadow
    public static Item FlatDynamite;
    @Shadow
    public static Item MiningFlatDynamite;
    @Shadow
    public static Item CompactDynamite;
    @Shadow
    public static Item FireDynamite;
    @Shadow
    public static Item SnowDynamite;
    @Shadow
    public static Item OceanDynamite;
    @Shadow
    public static Item HellFireDynamite;
    @Shadow
    public static Item VaporizeDynamite;
    @Shadow
    public static Item EnderDynamite;
    @Shadow
    public static Item NuclearDynamite;
    @Shadow
    public static Item GhostDynamite;
    @Shadow
    public static Item CubicDynamite;
    @Shadow
    public static Item EruptingDynamite;
    @Shadow
    public static Item ChemicalDynamite;
    @Shadow
    public static Item FloatingDynamite;
    @Shadow
    public static Item Uranium;
    @Shadow
    public static int TNTx5ID;
    @Shadow
    public static int TNTx20ID;
    @Shadow
    public static int TNTx100ID;
    @Shadow
    public static int TNTx500ID;
    @Shadow
    public static int MeteorTNTID;
    @Shadow
    public static int FlatTNTID;
    @Shadow
    public static int MiningFlatTNTID;
    @Shadow
    public static int CompactTNTID;
    @Shadow
    public static int HouseTNTID;
    @Shadow
    public static int WoodHouseTNTID;
    @Shadow
    public static int BrickHouseTNTID;
    @Shadow
    public static int FireTNTID;
    @Shadow
    public static int SnowTNTID;
    @Shadow
    public static int OceanTNTID;
    @Shadow
    public static int HellFireTNTID;
    @Shadow
    public static int VaporizeTNTID;
    @Shadow
    public static int EnderTNTID;
    @Shadow
    public static int NuclearTNTID;
    @Shadow
    public static int DiggingTNTID;
    @Shadow
    public static int DrillingTNTID;
    @Shadow
    public static int GhostTNTID;
    @Shadow
    public static int MultiplyTNTID;
    @Shadow
    public static int CubicTNTID;
    @Shadow
    public static int EruptingTNTID;
    @Shadow
    public static int ChemicalTNTID;
    @Shadow
    public static int FloatingTNTID;
    @Shadow
    public static int IceTNTID;
    @Shadow
    public static int TimerTNTID;
    @Shadow
    public static int ReactionTNTID;
    @Shadow
    public static int AnimalTNTID;
    @Shadow
    public static int SandFireworkID;
    @Shadow
    public static int FloatingIslandTNTID;
    @Shadow
    public static int TNTFireworkID;
    @Shadow
    public static int GravityTNTID;
    @Shadow
    public static int MeteorShowerTNTID;
    @Shadow
    public static int SpiralTNTID;
    @Shadow
    public static int GroveTNTID;
    @Shadow
    public static int InvertedTNTID;
    @Shadow
    public static int CustomTNTID;
    @Shadow
    public static int EasterEggID;
    @Shadow
    public static int MountainTopRemovalID;
    @Shadow
    public static int MankindsMarkID;
    @Shadow
    public static int PoseidonsWaveID;
    @Shadow
    public static int HellsGateID;
    @Shadow
    public static int DustBowlID;
    @Shadow
    public static int HexahedronID;
    @Shadow
    public static int HeavensGateID;
    @Shadow
    public static int TheRevolutionID;
    @Shadow
    public static int ChicxulubID;
    @Shadow
    public static int NuclearWasteID;
    @Shadow
    public static int SulfurOreID;
    @Shadow
    public static int UraniumOreID;
    @Shadow
    public static int MeteoriteID;
    @Shadow
    public static int DynamiteID;
    @Shadow
    public static int Dynamitex5ID;
    @Shadow
    public static int Dynamitex20ID;
    @Shadow
    public static int Dynamitex100ID;
    @Shadow
    public static int Dynamitex500ID;
    @Shadow
    public static int MeteorDynamiteID;
    @Shadow
    public static int FlatDynamiteID;
    @Shadow
    public static int MiningFlatDynamiteID;
    @Shadow
    public static int CompactDynamiteID;
    @Shadow
    public static int FireDynamiteID;
    @Shadow
    public static int SnowDynamiteID;
    @Shadow
    public static int OceanDynamiteID;
    @Shadow
    public static int HellFireDynamiteID;
    @Shadow
    public static int VaporizeDynamiteID;
    @Shadow
    public static int EnderDynamiteID;
    @Shadow
    public static int NuclearDynamiteID;
    @Shadow
    public static int GhostDynamiteID;
    @Shadow
    public static int CubicDynamiteID;
    @Shadow
    public static int EruptingDynamiteID;
    @Shadow
    public static int ChemicalDynamiteID;
    @Shadow
    public static int FloatingDynamiteID;
    @Shadow
    public static int UraniumID;
    @Shadow
    public static boolean EasterEggTNTinGame;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        int entityId;
        do {
            entityId = EntityRegistry.findGlobalUniqueEntityId();
        } while (entityId < 1001);
        proxy.registerRenderInformation();
        this.Set(EntityTNTx5Primed.class, "entitytntx5", entityId);
        this.Render(EntityTNTx5Primed.class, "entitytntx5", 50, this, 256, 1, false);
        this.Set(EntityTNTx20Primed.class, "entitytntx20", entityId);
        this.Render(EntityTNTx20Primed.class, "entitytntx20", 51, this, 256, 1, false);
        this.Set(EntityTNTx100Primed.class, "entitytntx100", entityId);
        this.Render(EntityTNTx100Primed.class, "entitytntx100", 52, this, 256, 1, false);
        this.Set(EntityTNTx500Primed.class, "entitytntx500", entityId);
        this.Render(EntityTNTx500Primed.class, "entitytntx500", 53, this, 256, 1, false);
        this.Set(EntityMeteorTNTPrimed2.class, "entitymeteortnt", entityId);
        this.Render(EntityMeteorTNTPrimed2.class, "entitymeteortnt", 54, this, 1024, 1, false);
        this.Set(EntityFlatTNTPrimed.class, "entityFlatTNT", entityId);
        this.Render(EntityFlatTNTPrimed.class, "entityFlatTNT", 55, this, 256, 1, false);
        this.Set(EntityMiningFlatTNTPrimed.class, "entityminingFlatTNT", entityId);
        this.Render(EntityMiningFlatTNTPrimed.class, "entityminingFlatTNT", 56, this, 256, 1, false);
        this.Set(EntityCompactTNTPrimed.class, "entitycompacttnt", entityId);
        this.Render(EntityCompactTNTPrimed.class, "entitycompacttnt", 57, this, 256, 1, false);
        this.Set(EntityHouseTNTPrimed.class, "entityhousetnt", entityId);
        this.Render(EntityHouseTNTPrimed.class, "entityhousetnt", 58, this, 256, 1, false);
        this.Set(EntityWoodHouseTNTPrimed.class, "entitywoodhousetnt", entityId);
        this.Render(EntityWoodHouseTNTPrimed.class, "entitywoodhousetnt", 59, this, 256, 1, false);
        this.Set(EntityBrickHouseTNTPrimed.class, "entitybrickhousetnt", entityId);
        this.Render(EntityBrickHouseTNTPrimed.class, "entitybrickhousetnt", 60, this, 256, 1, false);
        this.Set(EntityFireTNTPrimed.class, "entityfiretnt", entityId);
        this.Render(EntityFireTNTPrimed.class, "entityfiretnt", 61, this, 256, 1, false);
        this.Set(EntitySnowTNTPrimed.class, "entitysnowtnt", entityId);
        this.Render(EntitySnowTNTPrimed.class, "entitysnowtnt", 62, this, 256, 1, false);
        this.Set(EntityOceanTNTPrimed.class, "entityoceantnt", entityId);
        this.Render(EntityOceanTNTPrimed.class, "entityoceantnt", 63, this, 256, 1, false);
        this.Set(EntityHellFireTNTPrimed.class, "entityhellfiretnt", entityId);
        this.Render(EntityHellFireTNTPrimed.class, "entityhellfiretnt", 64, this, 256, 1, false);
        this.Set(EntityVaporizeTNTPrimed.class, "entityvaporizetnt", entityId);
        this.Render(EntityVaporizeTNTPrimed.class, "entityvaporizetnt", 65, this, 256, 1, false);
        this.Set(EntityEnderTNTPrimed.class, "entityenderTNT", entityId);
        this.Render(EntityEnderTNTPrimed.class, "entityenderTNT", 66, this, 256, 1, false);
        this.Set(EntityNuclearTNTPrimed.class, "entitynucleartnt", entityId);
        this.Render(EntityNuclearTNTPrimed.class, "entitynucleartnt", 67, this, 256, 1, false);
        this.Set(EntityDiggingTNTPrimed.class, "entitydiggingtnt", entityId);
        this.Render(EntityDiggingTNTPrimed.class, "entitydiggingtnt", 68, this, 256, 1, false);
        this.Set(EntityDrillingTNTPrimed.class, "entitydrillingtnt", entityId);
        this.Render(EntityDrillingTNTPrimed.class, "entitydrillingtnt", 69, this, 256, 1, false);
        this.Set(EntityGhostTNTPrimed.class, "entityghosttnt", entityId);
        this.Render(EntityGhostTNTPrimed.class, "entityghosttnt", 70, this, 256, 1, false);
        this.Set(EntityMultiplyTNTPrimed.class, "entitymultiplytnt", entityId);
        this.Render(EntityMultiplyTNTPrimed.class, "entitymultiplytnt", 71, this, 256, 1, false);
        this.Set(EntityCubicTNTPrimed.class, "entitycubictnt", entityId);
        this.Render(EntityCubicTNTPrimed.class, "entitycubictnt", 72, this, 256, 1, false);
        this.Set(EntityEruptingTNTPrimed.class, "entityeruptingtnt", entityId);
        this.Render(EntityEruptingTNTPrimed.class, "entityeruptingtnt", 73, this, 512, 1, false);
        this.Set(EntityChemicalTNTPrimed.class, "entitychemicaltnt", entityId);
        this.Render(EntityChemicalTNTPrimed.class, "entitychemicaltnt", 74, this, 512, 1, false);
        this.Set(EntityFloatingTNTPrimed.class, "entityfloatingtnt", entityId);
        this.Render(EntityFloatingTNTPrimed.class, "entityfloatingtnt", 75, this, 512, 1, false);
        this.Set(EntityIceTNTPrimed.class, "entityicetnt", entityId);
        this.Render(EntityIceTNTPrimed.class, "entityicetnt", 76, this, 256, 1, false);
        this.Set(EntityTimerTNTPrimed.class, "entitytimertnt", entityId);
        this.Render(EntityTimerTNTPrimed.class, "entitytimertnt", 77, this, 512, 1, false);
        this.Set(EntityReactionTNTPrimed.class, "entityreactiontnt", entityId);
        this.Render(EntityReactionTNTPrimed.class, "entityreactiontnt", 78, this, 512, 1, false);
        this.Set(EntityAnimalTNTPrimed.class, "entityanimaltnt", entityId);
        this.Render(EntityAnimalTNTPrimed.class, "entityanimaltnt", 79, this, 256, 1, false);
        this.Set(EntitySandFireworkPrimed.class, "entitysandfirework", entityId);
        this.Render(EntitySandFireworkPrimed.class, "entitysandfirework", 80, this, 512, 1, false);
        this.Set(EntitySandFireworkPrimed2.class, "entitysandfirework2", entityId);
        this.Render(EntitySandFireworkPrimed2.class, "entitysandfirework2", 81, this, 512, 1, false);
        this.Set(EntityFloatingIslandTNTPrimed.class, "entityfloatingislandtnt", entityId);
        this.Render(EntityFloatingIslandTNTPrimed.class, "entityfloatingislandtnt", 82, this, 256, 1, false);
        this.Set(EntityTNTFireworkPrimed.class, "entitytntfirework", entityId);
        this.Render(EntityTNTFireworkPrimed.class, "entitytntfirework", 83, this, 512, 1, false);
        this.Set(EntityTNTFireworkPrimed2.class, "entitytntfirework2", entityId);
        this.Render(EntityTNTFireworkPrimed2.class, "entitytntfirework2", 84, this, 512, 1, false);
        this.Set(EntityGravityTNTPrimed.class, "entitygravitytnt", entityId);
        this.Render(EntityGravityTNTPrimed.class, "entitygravitytnt", 85, this, 256, 1, false);
        this.Set(EntityMeteorShowerTNTPrimed2.class, "entitymeteorshowertnt", entityId);
        this.Render(EntityMeteorShowerTNTPrimed2.class, "entitymeteorshowertnt", 86, this, 1024, 1, false);
        this.Set(EntitySpiralTNTPrimed.class, "entityspiraltnt", entityId);
        this.Render(EntitySpiralTNTPrimed.class, "entityspiraltnt", 87, this, 256, 1, false);
        this.Set(EntityGroveTNTPrimed.class, "entitygrovetnt", entityId);
        this.Render(EntityGroveTNTPrimed.class, "entitygrovetnt", 88, this, 256, 1, false);
        this.Set(EntityInvertedTNTPrimed.class, "entityinvertedtnt", entityId);
        this.Render(EntityInvertedTNTPrimed.class, "entityinvertedtnt", 89, this, 256, 1, false);
        this.Set(EntityCustomTNTPrimed.class, "entitycustomtnt", entityId);
        this.Render(EntityCustomTNTPrimed.class, "entitycustomtnt", 90, this, 512, 1, false);
        this.Set(EntityPickleHamPrimed.class, "entitypickleham", entityId);
        this.Render(EntityPickleHamPrimed.class, "entitypickleham", 91, this, 1024, 1, false);
        this.Set(EntityPickleHam2Primed.class, "entitypickleham2", entityId);
        this.Render(EntityPickleHam2Primed.class, "entitypickleham2", 92, this, 1024, 1, false);
        this.Set(EntityPickleHam3Primed.class, "entitypickleham3", entityId);
        this.Render(EntityPickleHam3Primed.class, "entitypickleham3", 93, this, 1024, 1, false);
        this.Set(EntityUraniumPrimed.class, "entityuraniumprimed", entityId);
        this.Render(EntityUraniumPrimed.class, "entityuraniumprimed", 94, this, 1024, 1, false);
        this.Set(EntityUranium2Primed.class, "entityuranium2primed", entityId);
        this.Render(EntityUranium2Primed.class, "entityuranium2primed", 95, this, 1024, 1, false);
        this.Set(EntityMountainTopRemovalPrimed.class, "entitymountaintopremovalprimed", entityId);
        this.Render(EntityMountainTopRemovalPrimed.class, "entitymountaintopremovalprimed", 96, this, 512, 1, false);
        this.Set(EntityMankindsMarkPrimed.class, "entitymankindsmarkprimed", entityId);
        this.Render(EntityMankindsMarkPrimed.class, "entitymankindsmarkprimed", 97, this, 512, 1, false);
        this.Set(EntityPoseidonsWavePrimed.class, "entityposeidonswaveprimed", entityId);
        this.Render(EntityPoseidonsWavePrimed.class, "entityposeidonswaveprimed", 98, this, 1024, 1, false);
        this.Set(EntityHellsGatePrimed.class, "entityhellsgateprimed", entityId);
        this.Render(EntityHellsGatePrimed.class, "entityhellsgateprimed", 99, this, 1024, 1, false);
        this.Set(EntityDustBowlPrimed.class, "entitydustbowlprimed", entityId);
        this.Render(EntityDustBowlPrimed.class, "entitydustbowlprimed", 100, this, 1024, 1, false);
        this.Set(EntityHexahedronPrimed.class, "entityhexahedronprimed", entityId);
        this.Render(EntityHexahedronPrimed.class, "entityhexahedronprimed", 101, this, 1024, 1, false);
        this.Set(EntityHeavensGatePrimed.class, "entityheavensgateprimed", entityId);
        this.Render(EntityHeavensGatePrimed.class, "entityheavensgateprimed", 102, this, 1024, 1, false);
        this.Set(EntityTheRevolutionPrimed.class, "entitytherevolutionprimed", entityId);
        this.Render(EntityTheRevolutionPrimed.class, "entitytherevolutionprimed", 103, this, 1024, 1, false);
        this.Set(EntityTheRevolutionPrimed2.class, "entitytherevolutionprimed2", entityId);
        this.Render(EntityTheRevolutionPrimed2.class, "entitytherevolutionprimed2", 104, this, 1024, 1, false);
        this.Set(EntityChicxulubPrimed.class, "entitychicxulubprimed", entityId);
        this.Render(EntityChicxulubPrimed.class, "entitychicxulubprimed", 105, this, 1024, 1, false);
        this.Set(EntityAstroidPrimed.class, "entityastroidprimed", entityId);
        this.Render(EntityAstroidPrimed.class, "entityastroidprimed", 106, this, 1024, 1, false);
        this.Set(EntityAstroid2Primed.class, "entityastroid2primed", entityId);
        this.Render(EntityAstroid2Primed.class, "entityastroid2primed", 107, this, 1024, 1, false);
        this.Set(EntityDynamite.class, "entitydynamite", entityId);
        this.Render(EntityDynamite.class, "entitydynamite", 110, this, 512, 1, true);
        this.Set(EntityDynamitex5.class, "entitydynamitex5", entityId);
        this.Render(EntityDynamitex5.class, "entitydynamitex5", 111, this, 512, 1, true);
        this.Set(EntityDynamitex20.class, "entitydynamitex20", entityId);
        this.Render(EntityDynamitex20.class, "entitydynamitex20", 112, this, 512, 1, true);
        this.Set(EntityDynamitex100.class, "entitydynamitex100", entityId);
        this.Render(EntityDynamitex100.class, "entitydynamitex100", 113, this, 512, 1, true);
        this.Set(EntityDynamitex500.class, "entitydynamitex500", entityId);
        this.Render(EntityDynamitex500.class, "entitydynamitex500", 114, this, 512, 1, true);
        this.Set(EntityMeteorDynamite.class, "entitymeteordynamite", entityId);
        this.Render(EntityMeteorDynamite.class, "entitymeteordynamite", 115, this, 512, 1, true);
        this.Set(EntityMeteorDynamitePrimed.class, "entitymeteordynamiteprimed", entityId);
        this.Render(EntityMeteorDynamitePrimed.class, "entitymeteordynamiteprimed", 116, this, 512, 1, true);
        this.Set(EntityFlatDynamite.class, "entityflatdynamite", entityId);
        this.Render(EntityFlatDynamite.class, "entityflatdynamite", 117, this, 512, 1, true);
        this.Set(EntityMiningFlatDynamite.class, "entitymimingflatdynamite", entityId);
        this.Render(EntityMiningFlatDynamite.class, "entitymimingflatdynamite", 118, this, 512, 1, true);
        this.Set(EntityCompactDynamite.class, "entitycompactdynamite", entityId);
        this.Render(EntityCompactDynamite.class, "entitycompactdynamite", 119, this, 512, 1, true);
        this.Set(EntityFireDynamite.class, "entityfiredynamite", entityId);
        this.Render(EntityFireDynamite.class, "entityfiredynamite", 120, this, 512, 1, true);
        this.Set(EntitySnowDynamite.class, "entitysnowdynamite", entityId);
        this.Render(EntitySnowDynamite.class, "entitysnowdynamite", 121, this, 512, 1, true);
        this.Set(EntityOceanDynamite.class, "entityoceandynamite", entityId);
        this.Render(EntityOceanDynamite.class, "entityoceandynamite", 122, this, 512, 1, true);
        this.Set(EntityHellFireDynamite.class, "entityhellfiredynamite", entityId);
        this.Render(EntityHellFireDynamite.class, "entityhellfiredynamite", 123, this, 512, 1, true);
        this.Set(EntityVaporizeDynamite.class, "entityvaporizedynamite", entityId);
        this.Render(EntityVaporizeDynamite.class, "entityvaporizedynamite", 124, this, 512, 1, true);
        this.Set(EntityEnderDynamite.class, "entityenderdynamite", entityId);
        this.Render(EntityEnderDynamite.class, "entityenderdynamite", 125, this, 512, 1, true);
        this.Set(EntityNuclearDynamite.class, "entitynucleardynamiteprimed", entityId);
        this.Render(EntityNuclearDynamite.class, "entitynucleardynamiteprimed", 126, this, 512, 1, true);
        this.Set(EntityGhostDynamite.class, "entityghostdynamiteprimed", entityId);
        this.Render(EntityGhostDynamite.class, "entityghostdynamiteprimed", 127, this, 512, 1, true);
        this.Set(EntityCubicDynamite.class, "entitycubicdynamiteprimed", entityId);
        this.Render(EntityCubicDynamite.class, "entitycubicdynamiteprimed", 128, this, 512, 1, true);
        this.Set(EntityEruptingDynamite.class, "entityeruptingdynamiteprimed", entityId);
        this.Render(EntityEruptingDynamite.class, "entityeruptingdynamiteprimed", 129, this, 512, 1, true);
        this.Set(EntityChemicalDynamite.class, "entitychemicaldynamiteprimed", entityId);
        this.Render(EntityChemicalDynamite.class, "entitychemicaldynamiteprimed", 130, this, 512, 1, true);
        this.Set(EntityFloatingDynamite.class, "entityfloatingdynamiteprimed", entityId);
        this.Render(EntityFloatingDynamite.class, "entityfloatingdynamiteprimed", 131, this, 512, 1, true);
        this.Set(EntityUranium.class, "entityuranium", entityId);
        this.Render(EntityUranium.class, "entityuranium", 132, this, 512, 1, true);
        TNTx5 = (new BlockTNTx5(TNTx5ID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("TNTx5");
        TNTx20 = (new BlockTNTx20(TNTx20ID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("TNTx20");
        TNTx100 = (new BlockTNTx100(TNTx100ID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("TNTx100");
        TNTx500 = (new BlockTNTx500(TNTx500ID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("TNTx500");
        MeteorTNT = (new BlockMeteorTNT(MeteorTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeStone)
            .setLightLevel(0.5F)
            .setBlockName("MeteorTNT");
        FlatTNT = (new BlockFlatTNT(FlatTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("FlatTNT");
        MiningFlatTNT = (new BlockMiningFlatTNT(MiningFlatTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("MiningFlatTNT");
        CompactTNT = (new BlockCompactTNT(CompactTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("CompactTNT");
        HouseTNT = (new BlockHouseTNT(HouseTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("HouseTNT");
        WoodHouseTNT = (new BlockWoodHouseTNT(WoodHouseTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("WoodHouseTNT");
        BrickHouseTNT = (new BlockBrickHouseTNT(BrickHouseTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("BrickHouseTNT");
        FireTNT = (new BlockFireTNT(FireTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("FireTNT");
        SnowTNT = (new BlockSnowTNT(SnowTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("SnowTNT");
        OceanTNT = (new BlockOceanTNT(OceanTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("OceanTNT");
        HellFireTNT = (new BlockHellFireTNT(HellFireTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("HellFireTNT");
        VaporizeTNT = (new BlockVaporizeTNT(VaporizeTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("VaporizeTNT");
        EnderTNT = (new BlockEnderTNT(EnderTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("EnderTNT");
        NuclearTNT = (new BlockNuclearTNT(NuclearTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("NuclearTNT");
        DiggingTNT = (new BlockDiggingTNT(DiggingTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("DiggingTNT");
        DrillingTNT = (new BlockDrillingTNT(DrillingTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("DrillingTNT");
        GhostTNT = (new BlockGhostTNT(GhostTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("GhostTNT");
        MultiplyTNT = (new BlockMultiplyTNT(MultiplyTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("MultiplyTNT");
        CubicTNT = (new BlockCubicTNT(CubicTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("CubicTNT");
        EruptingTNT = (new BlockEruptingTNT(EruptingTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("EruptingTNT");
        ChemicalTNT = (new BlockChemicalTNT(ChemicalTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("ChemicalTNT");
        FloatingTNT = (new BlockFloatingTNT(FloatingTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("FloatingTNT");
        IceTNT = (new BlockIceTNT(IceTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("IceTNT");
        TimerTNT = (new BlockTimerTNT(TimerTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("TimerTNT");
        ReactionTNT = (new BlockReactionTNT(ReactionTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("ReactionTNT");
        AnimalTNT = (new BlockAnimalTNT(AnimalTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("AniamlTNT");
        SandFirework = (new BlockSandFirework(SandFireworkID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeSand)
            .setBlockName("SandFirework");
        FloatingIslandTNT = (new BlockFloatingIslandTNT(FloatingIslandTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("FloatingIslandTNT");
        TNTFirework = (new BlockTNTFirework(TNTFireworkID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("TNTFirework");
        GravityTNT = (new BlockGravityTNT(GravityTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("GravityTNT");
        MeteorShowerTNT = (new BlockMeteorShowerTNT(MeteorShowerTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("MeteorShowerTNT");
        SpiralTNT = (new BlockSpiralTNT(SpiralTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("SpiralTNT");
        GroveTNT = (new BlockGroveTNT(GroveTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("GroveTNT");
        InvertedTNT = (new BlockInvertedTNT(InvertedTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("InvertedTNT");
        CustomTNT = (new BlockCustomTNT(CustomTNTID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("CustomTNT");
        EasterEgg = (new BlockPickleHam(EasterEggID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("EasterEgg");
        MountainTopRemoval = (new BlockMountainTopRemoval(MountainTopRemovalID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeStone)
            .setBlockName("MountainTopRemoval");
        MankindsMark = (new BlockMankindsMark(MankindsMarkID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeStone)
            .setBlockName("MankindsMark");
        PoseidonsWave = (new BlockPoseidonsWave(PoseidonsWaveID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeStone)
            .setBlockName("Poseidon'sWave");
        HellsGate = (new BlockHellsGate(HellsGateID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeStone)
            .setBlockName("HellsGate");
        DustBowl = (new BlockDustBowl(DustBowlID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeSand)
            .setBlockName("DustBowl");
        Hexahedron = (new BlockHexahedron(HexahedronID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeStone)
            .setBlockName("Hexahedron");
        HeavensGate = (new BlockHeavensGate(HeavensGateID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeStone)
            .setBlockName("HeavensGate");
        TheRevolution = (new BlockTheRevolution(TheRevolutionID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeStone)
            .setBlockName("TheRevolution");
        Chicxulub = (new BlockChicxulub(ChicxulubID)).setHardness(0.0F)
            .setStepSound(Block.soundTypeStone)
            .setLightLevel(0.75F)
            .setBlockName("Chicxulub");
        NuclearWaste = (new BlockNuclearWaste2(NuclearWasteID)).setHardness(0.3F)
            .setStepSound(Block.soundTypeSnow)
            .setLightLevel(0.5F)
            .setBlockName("NuclearWaste");
        SulfurOre = (new BlockSulfurOre(SulfurOreID)).setHardness(3.0F)
            .setResistance(5.0F)
            .setStepSound(Block.soundTypeStone)
            .setBlockName("SulfurOre");
        UraniumOre = (new BlockUraniumOre(UraniumOreID)).setHardness(3.0F)
            .setResistance(5.0F)
            .setStepSound(Block.soundTypeStone)
            .setLightLevel(0.5F)
            .setBlockName("UraniumOre");
        Meteorite = (new BlockMeteorite(MeteoriteID)).setHardness(3.0F)
            .setResistance(5.0F)
            .setStepSound(Block.soundTypeStone)
            .setLightLevel(0.5F)
            .setBlockName("Meteorite");
        Dynamite = (new ItemDynamite(DynamiteID)).setTextureName("Dynamite")
            .setUnlocalizedName("Dynamite");
        Dynamitex5 = (new ItemDynamitex5(Dynamitex5ID)).setTextureName("Dynamitex5")
            .setUnlocalizedName("Dynamitex5");
        Dynamitex20 = (new ItemDynamitex20(Dynamitex20ID)).setTextureName("Dynamitex20")
            .setUnlocalizedName("Dynamitex20");
        Dynamitex100 = (new ItemDynamitex100(Dynamitex100ID)).setTextureName("Dynamitex100")
            .setUnlocalizedName("Dynamitex100");
        Dynamitex500 = (new ItemDynamitex500(Dynamitex500ID)).setTextureName("Dynamitex500")
            .setUnlocalizedName("Dynamitex500");
        MeteorDynamite = (new ItemMeteorDynamite(MeteorDynamiteID)).setTextureName("MeteorDynamite")
            .setUnlocalizedName("MeteorDynamite");
        FlatDynamite = (new ItemFlatDynamite(FlatDynamiteID)).setTextureName("FlatDynamite")
            .setUnlocalizedName("FlatDynamite");
        MiningFlatDynamite = (new ItemMiningFlatDynamite(MiningFlatDynamiteID)).setTextureName("MiningFlatDynamite")
            .setUnlocalizedName("MiningFlatDynamite");
        CompactDynamite = (new ItemCompactDynamite(CompactDynamiteID)).setTextureName("CompactDynamite")
            .setUnlocalizedName("CompactDynamite");
        FireDynamite = (new ItemFireDynamite(FireDynamiteID)).setTextureName("FireDynamite")
            .setUnlocalizedName("FireDynamite");
        SnowDynamite = (new ItemSnowDynamite(SnowDynamiteID)).setTextureName("SnowDynamite")
            .setUnlocalizedName("SnowDynamite");
        OceanDynamite = (new ItemOceanDynamite(OceanDynamiteID)).setTextureName("OceanDynamite")
            .setUnlocalizedName("OceanDynamite");
        HellFireDynamite = (new ItemHellFireDynamite(HellFireDynamiteID)).setTextureName("HellFireDynamite")
            .setUnlocalizedName("HellFireDynamite");
        VaporizeDynamite = (new ItemVaporizeDynamite(VaporizeDynamiteID)).setTextureName("VaporizeDynamite")
            .setUnlocalizedName("VaporizeDynamite");
        EnderDynamite = (new ItemEnderDynamite(EnderDynamiteID)).setTextureName("EnderDynamite")
            .setUnlocalizedName("EnderDynamite");
        NuclearDynamite = (new ItemNuclearDynamite(NuclearDynamiteID)).setTextureName("NuclearDynamite")
            .setUnlocalizedName("NuclearDynamite");
        GhostDynamite = (new ItemGhostDynamite(GhostDynamiteID)).setTextureName("GhostDynamite")
            .setUnlocalizedName("GhostDynamite");
        CubicDynamite = (new ItemCubicDynamite(CubicDynamiteID)).setTextureName("CubicDynamite")
            .setUnlocalizedName("CubicDynamite");
        EruptingDynamite = (new ItemEruptingDynamite(EruptingDynamiteID)).setTextureName("EruptingDynamite")
            .setUnlocalizedName("EruptingDynamite");
        ChemicalDynamite = (new ItemChemicalDynamite(ChemicalDynamiteID)).setTextureName("ChemicalDynamite")
            .setUnlocalizedName("ChemicalDynamite");
        FloatingDynamite = (new ItemFloatingDynamite(FloatingDynamiteID)).setTextureName("FloatingDynamite")
            .setUnlocalizedName("FloatingDynamite");
        Uranium = (new ItemUranium(UraniumID)).setTextureName("Uranium")
            .setUnlocalizedName("Uranium");
        this.LoadBlock(TNTx5, "TNTx5");
        this.LoadBlock(TNTx20, "TNTx20");
        this.LoadBlock(TNTx100, "TNTx100");
        this.LoadBlock(TNTx500, "TNTx500");
        this.LoadBlock(MeteorTNT, "MeteorTNT");
        this.LoadBlock(FlatTNT, "FlatTNT");
        this.LoadBlock(MiningFlatTNT, "MiningFlatTNT");
        this.LoadBlock(CompactTNT, "CompactTNT");
        this.LoadBlock(HouseTNT, "HouseTNT");
        this.LoadBlock(WoodHouseTNT, "WoodHouseTNT");
        this.LoadBlock(BrickHouseTNT, "BrickHouseTNT");
        this.LoadBlock(FireTNT, "FireTNT");
        this.LoadBlock(SnowTNT, "SnowTNT");
        this.LoadBlock(OceanTNT, "OceanTNT");
        this.LoadBlock(HellFireTNT, "HellFireTNT");
        this.LoadBlock(VaporizeTNT, "VaporizeTNT");
        this.LoadBlock(EnderTNT, "EnderTNT");
        this.LoadBlock(NuclearTNT, "NuclearTNT");
        this.LoadBlock(DiggingTNT, "DiggingTNT");
        this.LoadBlock(DrillingTNT, "DrillingTNT");
        this.LoadBlock(GhostTNT, "GhostTNT");
        this.LoadBlock(MultiplyTNT, "MultiplyTNT");
        this.LoadBlock(CubicTNT, "CubicTNT");
        this.LoadBlock(EruptingTNT, "EruptingTNT");
        this.LoadBlock(ChemicalTNT, "ChemicalTNT");
        this.LoadBlock(FloatingTNT, "FloatingTNT");
        this.LoadBlock(IceTNT, "FreezeTNT");
        this.LoadBlock(TimerTNT, "TimerTNT");
        this.LoadBlock(ReactionTNT, "ReactionTNT");
        this.LoadBlock(AnimalTNT, "AnimalTNT");
        this.LoadBlock(SandFirework, "SandFirework");
        this.LoadBlock(FloatingIslandTNT, "FloatingIslandTNT");
        this.LoadBlock(TNTFirework, "TNTFirework");
        this.LoadBlock(GravityTNT, "GravityTNT");
        this.LoadBlock(MeteorShowerTNT, "MeteorShowerTNT");
        this.LoadBlock(SpiralTNT, "SpiralTNT");
        this.LoadBlock(GroveTNT, "GroveTNT");
        this.LoadBlock(InvertedTNT, "InvertedTNT");
        this.LoadBlock(CustomTNT, "CustomTNT");
        this.LoadBlock(MountainTopRemoval, "Mountain Top Removal");
        this.LoadBlock(MankindsMark, "Mankind's Mark");
        this.LoadBlock(PoseidonsWave, "Poseidon's Wave");
        this.LoadBlock(HeavensGate, "Heaven's Gate");
        this.LoadBlock(HellsGate, "Hell's Gate");
        this.LoadBlock(DustBowl, "Dust Bowl");
        this.LoadBlock(Hexahedron, "Hexahedron");
        this.LoadBlock(TheRevolution, "The Revolution");
        this.LoadBlock(Chicxulub, "Chicxulub");
        this.LoadBlock(NuclearWaste, "Nuclear Waste");
        this.LoadBlock(SulfurOre, "Sulfur Ore");
        this.LoadBlock(UraniumOre, "Uranium Ore");
        this.LoadBlock(Meteorite, "Meteorite");
        this.LoadItem(Dynamite, "Dynamite");
        this.LoadItem(Dynamitex5, "Dynamite x5");
        this.LoadItem(Dynamitex20, "Dynamite x20");
        this.LoadItem(Dynamitex100, "Dynamite x100");
        this.LoadItem(Dynamitex500, "Dynamite x500");
        this.LoadItem(MeteorDynamite, "MeteorDynamite");
        this.LoadItem(FlatDynamite, "FlatDynamite");
        this.LoadItem(MiningFlatDynamite, "MiningFlatDynamite");
        this.LoadItem(CompactDynamite, "CompactDynamite");
        this.LoadItem(FireDynamite, "FireDynamite");
        this.LoadItem(SnowDynamite, "SnowDynamite");
        this.LoadItem(OceanDynamite, "OceanDynamite");
        this.LoadItem(HellFireDynamite, "HellFireDynamite");
        this.LoadItem(VaporizeDynamite, "VaporizeDynamite");
        this.LoadItem(EnderDynamite, "EnderDynamite");
        this.LoadItem(NuclearDynamite, "NuclearDynamite");
        this.LoadItem(GhostDynamite, "GhostDynamite");
        this.LoadItem(CubicDynamite, "CubicDynamite");
        this.LoadItem(EruptingDynamite, "EruptingDynamite");
        this.LoadItem(ChemicalDynamite, "ChemicalDynamite");
        this.LoadItem(FloatingDynamite, "FloatingDynamite");
        this.LoadItem(Uranium, "Uranium");
        if (EasterEggTNTinGame) {
            this.LoadBlock(EasterEgg, "EasterEgg");
        }

        this.R(new ItemStack(TNTx5, 1), "##", "##", '#', Blocks.tnt);
        this.R(new ItemStack(TNTx20, 1), "##", "##", '#', TNTx5);
        this.R(new ItemStack(TNTx100, 1), "##", "##", '#', TNTx20);
        this.R(new ItemStack(TNTx500, 1), "##", "##", '#', TNTx100);
        this.R(
            new ItemStack(MeteorTNT, 1),
            "XXX",
            "XMX",
            "X#X",
            '#',
            TNTx500,
            'X',
            Blocks.obsidian,
            'M',
            Items.lava_bucket);
        this.R(new ItemStack(FlatTNT, 1), "#", "X", '#', TNTx20, 'X', Blocks.obsidian);
        this.R(new ItemStack(MiningFlatTNT, 1), "XXX", "M#M", "XXX", '#', FlatTNT, 'X', Blocks.torch, 'M', TNTx5);
        this.R(new ItemStack(CompactTNT, 1), "XXX", "X#X", "XXX", '#', TNTx5, 'X', Blocks.tnt);
        this.R(
            new ItemStack(HouseTNT, 1),
            "XXX",
            "G#G",
            "XMX",
            '#',
            TNTx5,
            'X',
            Blocks.cobblestone,
            'M',
            Blocks.crafting_table,
            'G',
            Blocks.glass);
        this.R(
            new ItemStack(WoodHouseTNT, 1),
            "XXX",
            "M#M",
            "XXX",
            '#',
            HouseTNT,
            'X',
            Blocks.planks,
            'M',
            Items.gold_ingot);
        this.R(
            new ItemStack(BrickHouseTNT, 1),
            "XXX",
            "M#M",
            "XXX",
            '#',
            HouseTNT,
            'X',
            Blocks.brick_block,
            'M',
            Items.diamond);
        this.S(new ItemStack(FireTNT, 1), Items.flint_and_steel, TNTx5);
        this.S(new ItemStack(SnowTNT, 1), Blocks.snow, TNTx5);
        this.R(new ItemStack(OceanTNT, 1), " X ", "X#X", " X ", '#', TNTx100, 'X', Items.water_bucket);
        this.R(new ItemStack(HellFireTNT, 1), " M ", "X#X", " M ", '#', FireTNT, 'X', TNTx20, 'M', Items.ghast_tear);
        this.R(new ItemStack(HellFireTNT, 1), " M ", "X#X", " M ", '#', FireTNT, 'M', TNTx20, 'X', Items.ghast_tear);
        this.R(new ItemStack(VaporizeTNT, 1), " X ", "X#X", " X ", '#', TNTx20, 'X', Items.bucket);
        this.S(new ItemStack(EnderTNT, 1), Items.ender_pearl, Blocks.tnt);
        this.R(
            new ItemStack(NuclearTNT, 1),
            "X#X",
            "#M#",
            "X#X",
            '#',
            TNTx20,
            'X',
            Items.glowstone_dust,
            'M',
            Blocks.gold_block);
        this.R(new ItemStack(DiggingTNT, 1), "#", "#", "X", '#', TNTx5, 'X', Items.water_bucket);
        this.R(new ItemStack(DrillingTNT, 1), "#", "X", '#', DiggingTNT, 'X', TNTx100);
        this.S(new ItemStack(GhostTNT, 1), Items.milk_bucket, TNTx20);
        this.R(
            new ItemStack(MultiplyTNT, 1),
            "MXM",
            "X#X",
            "MXM",
            '#',
            TNTx5,
            'X',
            Items.gold_ingot,
            'M',
            new ItemStack(Items.dye, 1, 15));
        this.R(new ItemStack(CubicTNT, 1), "XXX", "X#X", "XXX", '#', TNTx5, 'X', Blocks.cobblestone);
        this.S(new ItemStack(EruptingTNT, 1), CompactTNT, Items.fire_charge);
        this.S(new ItemStack(ChemicalTNT, 1), TNTx100, Items.water_bucket, Items.gunpowder, Items.redstone);
        this.S(new ItemStack(FloatingTNT, 1), TNTx20, Items.feather);
        this.S(new ItemStack(IceTNT, 1), TNTx20, Items.snowball, Items.water_bucket);
        this.R(new ItemStack(TimerTNT, 1), "X", "#", '#', TNTx5, 'X', Items.redstone);
        this.S(new ItemStack(ReactionTNT, 1), ChemicalTNT, CompactTNT);
        this.S(new ItemStack(AnimalTNT, 1), Blocks.tnt, Items.porkchop, Items.rotten_flesh, Items.chicken, Items.beef);
        this.R(
            new ItemStack(SandFirework, 1),
            "XXX",
            "X#X",
            "XMX",
            '#',
            TNTx5,
            'X',
            Blocks.sand,
            'M',
            Items.firework_charge);
        this.R(new ItemStack(FloatingIslandTNT, 1), "X#X", '#', FloatingTNT, 'X', MultiplyTNT);
        this.R(
            new ItemStack(TNTFirework, 1),
            "XXX",
            "X#X",
            "XMX",
            '#',
            TNTx5,
            'X',
            CompactTNT,
            'M',
            Items.firework_charge);
        this.S(new ItemStack(GravityTNT, 1), EnderTNT, Items.apple);
        this.R(new ItemStack(MeteorShowerTNT, 1), " X ", "X#X", " X ", '#', EruptingTNT, 'X', CompactTNT);
        this.S(new ItemStack(SpiralTNT, 1), EruptingTNT, FloatingTNT);
        this.R(
            new ItemStack(GroveTNT, 1),
            "MMM",
            "F#F",
            "XXX",
            '#',
            TNTx5,
            'X',
            Blocks.dirt,
            'M',
            Blocks.sapling,
            'F',
            new ItemStack(Items.dye, 1, 15));
        this.R(new ItemStack(InvertedTNT, 1), " X ", "X#X", " X ", '#', TNTx100, 'X', MultiplyTNT);
        this.R(
            new ItemStack(EasterEgg, 1),
            "M#M",
            "XXX",
            "P#P",
            '#',
            TNTx20,
            'X',
            new ItemStack(Items.dye, 1, 15),
            'M',
            Items.melon_seeds,
            'P',
            Items.pumpkin_seeds);
        this.S(new ItemStack(NuclearWaste, 2), Items.glowstone_dust, Items.slime_ball);
        this.S(
            new ItemStack(Blocks.sand, 32),
            Items.water_bucket,
            Blocks.cobblestone,
            Blocks.cobblestone,
            Blocks.cobblestone,
            Blocks.cobblestone,
            Blocks.cobblestone,
            Blocks.cobblestone,
            Blocks.cobblestone,
            Blocks.cobblestone);
        this.S(new ItemStack(MountainTopRemoval, 1), FlatTNT, Uranium);
        this.S(new ItemStack(MankindsMark, 1), HouseTNT, Uranium);
        this.S(new ItemStack(PoseidonsWave, 1), OceanTNT, Uranium);
        this.S(new ItemStack(HellsGate, 1), HellFireTNT, Uranium);
        this.S(new ItemStack(DustBowl, 1), VaporizeTNT, Uranium);
        this.S(new ItemStack(Hexahedron, 1), CubicTNT, Uranium);
        this.S(new ItemStack(HeavensGate, 1), FloatingIslandTNT, Uranium);
        this.S(new ItemStack(TheRevolution, 1), SpiralTNT, Uranium);
        this.S(new ItemStack(TheRevolution, 1), SpiralTNT, Uranium);
        this.S(new ItemStack(Dynamite, 4), Blocks.tnt);
        this.S(new ItemStack(Dynamitex5, 4), TNTx5);
        this.S(new ItemStack(Dynamitex20, 4), TNTx20);
        this.S(new ItemStack(Dynamitex100, 4), TNTx100);
        this.S(new ItemStack(Dynamitex500, 4), TNTx500);
        this.S(new ItemStack(MeteorDynamite, 4), MeteorTNT);
        this.S(new ItemStack(FlatDynamite, 4), FlatTNT);
        this.S(new ItemStack(MiningFlatDynamite, 4), MiningFlatTNT);
        this.S(new ItemStack(CompactDynamite, 4), CompactTNT);
        this.S(new ItemStack(FireDynamite, 4), FireTNT);
        this.S(new ItemStack(SnowDynamite, 4), SnowTNT);
        this.S(new ItemStack(OceanDynamite, 4), OceanTNT);
        this.S(new ItemStack(HellFireDynamite, 4), HellFireTNT);
        this.S(new ItemStack(VaporizeDynamite, 4), VaporizeTNT);
        this.S(new ItemStack(EnderDynamite, 4), EnderTNT);
        this.S(new ItemStack(NuclearDynamite, 4), NuclearTNT);
        this.S(new ItemStack(GhostDynamite, 2), GhostTNT);
        this.S(new ItemStack(CubicDynamite, 4), CubicTNT);
        this.S(new ItemStack(EruptingDynamite, 4), EruptingTNT);
        this.S(new ItemStack(ChemicalDynamite, 2), ChemicalTNT);
        this.S(new ItemStack(FloatingDynamite, 4), FloatingTNT);
        GameRegistry.registerWorldGenerator(new WorldGeneratorTooMuchTNT(), 1);
        MinecraftForge.EVENT_BUS.register(new TMTEventHandler());
        System.out.println("Loading TooMuchTNT");
    }

    @Shadow
    public void Set(Class<? extends Entity> par1, String par2, int par3) {
        EntityRegistry.registerGlobalEntityID(par1, par2, par3);
    }

    @Shadow
    public void Render(Class<? extends Entity> par1, String par2, int par3, Object par4, int par5, int par6,
        boolean par7) {
        EntityRegistry.registerModEntity(par1, par2, par3, par4, par5, par6, par7);
    }

    @Shadow
    public void R(ItemStack output, Object... params) {
        GameRegistry.addRecipe(output, params);
    }

    @Shadow
    public void S(ItemStack output, Object... params) {
        GameRegistry.addShapelessRecipe(output, params);
    }

    @Shadow
    public void LoadBlock(Block par1, String par2) {
        GameRegistry.registerBlock(par1, par2);
        LanguageRegistry.addName(par1, par2);
    }

    @Shadow
    public void LoadItem(Item par1, String par2) {
        GameRegistry.registerItem(par1, par2);
        LanguageRegistry.addName(par1, par2);
    }

}
