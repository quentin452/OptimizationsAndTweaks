package fr.iamacat.optimizationsandtweaks.mixins.common.minenautica;

import com.minenautica.Minenautica.Biomes.BiomeRegistry;
import com.minenautica.Minenautica.Block.WorldGenerator.*;
import com.minenautica.Minenautica.Blocks.TechneRenderings.AcidMushroom.TileEntityAcidMushroom;
import com.minenautica.Minenautica.Blocks.TechneRenderings.BaseLight1.TileEntityBaseLight1;
import com.minenautica.Minenautica.Blocks.TechneRenderings.BaseLight2.TileEntityBaseLight2;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Bed.TileEntityBed;
import com.minenautica.Minenautica.Blocks.TechneRenderings.BluePalm.TileEntityBluePalm;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Chair1.TileEntityChair1;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Chair2.TileEntityChair2;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Chair3.TileEntityChair3;
import com.minenautica.Minenautica.Blocks.TechneRenderings.CommunicationsRelay.TileEntityCommunicationsRelay;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Desk1.TileEntityDesk1;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Desk2.TileEntityDesk2;
import com.minenautica.Minenautica.Blocks.TechneRenderings.DroopingStinger.TileEntityDroopingStinger;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Extinguisher.TileEntityExtinguisher;
import com.minenautica.Minenautica.Blocks.TechneRenderings.ExtinguisherHolder.TileEntityExtinguisherHolder;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Fabricator.TileEntityFabricator;
import com.minenautica.Minenautica.Blocks.TechneRenderings.FernPalm.TileEntityFernPalm;
import com.minenautica.Minenautica.Blocks.TechneRenderings.FurledPapyrus.TileEntityFurledPapyrus;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Ladder.TileEntityLadder;
import com.minenautica.Minenautica.Blocks.TechneRenderings.LargeDecal.TileEntityLargeDecal;
import com.minenautica.Minenautica.Blocks.TechneRenderings.LaunchPad.TileEntityLaunchPad;
import com.minenautica.Minenautica.Blocks.TechneRenderings.LifepodBench.TileEntityLifepodBench;
import com.minenautica.Minenautica.Blocks.TechneRenderings.LifepodChair1.TileEntityLifepodChair1;
import com.minenautica.Minenautica.Blocks.TechneRenderings.LifepodChair2.TileEntityLifepodChair2;
import com.minenautica.Minenautica.Blocks.TechneRenderings.LifepodLadder.TileEntityLifepodLadder;
import com.minenautica.Minenautica.Blocks.TechneRenderings.LifepodLadder2.TileEntityLifepodLadder2;
import com.minenautica.Minenautica.Blocks.TechneRenderings.LightStick.TileEntityLightStick;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Locker.TileEntityLocker;
import com.minenautica.Minenautica.Blocks.TechneRenderings.MedicalKitFabricator.TileEntityMedicalKitFabricator;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Outcrops.LimestoneOutcrop.TileEntityLimestoneOutcrop;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Outcrops.LithiumOutcrop.TileEntityLithiumOutcrop;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Outcrops.SaltDeposit.TileEntitySaltDeposit;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Outcrops.SandstoneOutcrop.TileEntitySandstoneOutcrop;
import com.minenautica.Minenautica.Blocks.TechneRenderings.Panel.TileEntityPanel;
import com.minenautica.Minenautica.Blocks.TechneRenderings.PictureFrame.TileEntityPictureFrame;
import com.minenautica.Minenautica.Blocks.TechneRenderings.PurpleBrainCoral.TileEntityPurpleBrainCoral;
import com.minenautica.Minenautica.Blocks.TechneRenderings.QuartzCrystal.TileEntityQuartzCrystal;
import com.minenautica.Minenautica.Blocks.TechneRenderings.SeamothDamaged.TileEntitySeamothDamaged;
import com.minenautica.Minenautica.Blocks.TechneRenderings.ShellPlate.TileEntityShellPlate;
import com.minenautica.Minenautica.Blocks.TechneRenderings.SingleWallShelf.TileEntitySingleWallShelf;
import com.minenautica.Minenautica.Blocks.TechneRenderings.SlantedShellPlate.TileEntitySlantedShellPlate;
import com.minenautica.Minenautica.Blocks.TechneRenderings.SmallDecal.TileEntitySmallDecal;
import com.minenautica.Minenautica.Blocks.TechneRenderings.SmallDecal2.TileEntitySmallDecal2;
import com.minenautica.Minenautica.Blocks.TechneRenderings.SolarPanel.TileEntitySolarPanel;
import com.minenautica.Minenautica.Blocks.TechneRenderings.SupplyCrate.TileEntitySupplyCrate;
import com.minenautica.Minenautica.Blocks.TechneRenderings.TableCoral.Blue.TileEntityBlueTableCoral;
import com.minenautica.Minenautica.Blocks.TechneRenderings.TableCoral.Green.TileEntityGreenTableCoral;
import com.minenautica.Minenautica.Blocks.TechneRenderings.TableCoral.Purple.TileEntityPurpleTableCoral;
import com.minenautica.Minenautica.Blocks.TechneRenderings.TableCoral.Red.TileEntityRedTableCoral;
import com.minenautica.Minenautica.Blocks.TechneRenderings.TripleDroopingStinger.TileEntityTripleDroopingStinger;
import com.minenautica.Minenautica.Blocks.TechneRenderings.VeinedNettle.TileEntityVeinedNettle;
import com.minenautica.Minenautica.Blocks.TechneRenderings.WallLocker.TileEntityWallLocker;
import com.minenautica.Minenautica.Blocks.TechneRenderings.WallShelf.TileEntityWallShelf;
import com.minenautica.Minenautica.Crafting.CraftingRecipes;
import com.minenautica.Minenautica.CustomRegistry.BlocksAndItems;
import com.minenautica.Minenautica.Entity.airsac.EntityAirsac;
import com.minenautica.Minenautica.Entity.boomerang.EntityBoomerang;
import com.minenautica.Minenautica.Entity.eyeye.EntityEyeye;
import com.minenautica.Minenautica.Entity.floater.EntityFloater;
import com.minenautica.Minenautica.Entity.garryfish.EntityGarryfish;
import com.minenautica.Minenautica.Entity.holefish.EntityHolefish;
import com.minenautica.Minenautica.Entity.hoopfish.EntityHoopfish;
import com.minenautica.Minenautica.Entity.horizon.EntityHorizon;
import com.minenautica.Minenautica.Entity.hoverfish.EntityHoverfish;
import com.minenautica.Minenautica.Entity.lava_boomerang.EntityLavaBoomerang;
import com.minenautica.Minenautica.Entity.lava_eyeye.EntityLavaEyeye;
import com.minenautica.Minenautica.Entity.peeper.EntityPeeper;
import com.minenautica.Minenautica.Entity.reginald.EntityReginald;
import com.minenautica.Minenautica.Entity.spadefish.EntitySpadefish;
import com.minenautica.Minenautica.Entity.spinefish.EntitySpinefish;
import com.minenautica.Minenautica.Entity.warper.EntityWarper;
import com.minenautica.Minenautica.main.Minenautica;
import com.minenautica.Minenautica.main.ServerProxy;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minenautica.MinenauticaBiomeIDConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Minenautica.class)
public class MixinMinenautica {
    @Shadow
    public static ServerProxy proxy;
    @Shadow
    public static LithiumWorldGen worldgen1 = new LithiumWorldGen();
    @Shadow
    public static IronishWorldGen worldgen2 = new IronishWorldGen();
    @Shadow
    public static GoldishWorldGen worldgen3 = new GoldishWorldGen();
    @Shadow
    public static MagnetiteWorldGen worldgen4 = new MagnetiteWorldGen();
    @Shadow
    public static AluminumOxideWorldGen worldgen5 = new AluminumOxideWorldGen();
    @Shadow
    public static int creepvineID = RenderingRegistry.getNextAvailableRenderId();
    @Shadow
    public static int doubleTallHighPlantID = RenderingRegistry.getNextAvailableRenderId();
    @Shadow
    public static int eternalFireID = RenderingRegistry.getNextAvailableRenderId();
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinenauticaBiomeIDConfig.setupAndLoad(event);
        EntityAirsac.mainRegistry();
        EntityPeeper.mainRegistry();
        EntityHoopfish.mainRegistry();
        EntitySpinefish.mainRegistry();
        EntityHolefish.mainRegistry();
        EntityBoomerang.mainRegistry();
        EntityLavaBoomerang.mainRegistry();
        EntityGarryfish.mainRegistry();
        EntityHoverfish.mainRegistry();
        EntityEyeye.mainRegistry();
        EntityLavaEyeye.mainRegistry();
        EntitySpadefish.mainRegistry();
        EntityReginald.mainRegistry();
        EntityFloater.mainRegistry();
        EntityWarper.mainRegistry();
        EntityHorizon.mainRegistry();
        GameRegistry.registerTileEntity(TileEntityAcidMushroom.class, "Minenautica_Acid_Mushroom");
        GameRegistry.registerTileEntity(TileEntityBluePalm.class, "Minenautica_Blue_Palm");
        GameRegistry.registerTileEntity(TileEntityFernPalm.class, "Minenautica_Double_Fern_Plam");
        GameRegistry.registerTileEntity(TileEntityBlueTableCoral.class, "Minenautica_Blue_Table_Coral");
        GameRegistry.registerTileEntity(TileEntityGreenTableCoral.class, "Minenautica_Green_Table_Coral");
        GameRegistry.registerTileEntity(TileEntityPurpleTableCoral.class, "Minenautica_Purple_Table_Coral");
        GameRegistry.registerTileEntity(TileEntityRedTableCoral.class, "Minenautica_Red_Table_Coral");
        GameRegistry.registerTileEntity(TileEntitySlantedShellPlate.class, "Minenautica_Slanted_Shell_Plated");
        GameRegistry.registerTileEntity(TileEntityPurpleBrainCoral.class, "Minenautica_Purple_Brain_Coral");
        GameRegistry.registerTileEntity(TileEntityShellPlate.class, "Minenautica_Shell_Plate");
        GameRegistry.registerTileEntity(TileEntityVeinedNettle.class, "Minenautica_Veined_Nettle");
        GameRegistry.registerTileEntity(TileEntityQuartzCrystal.class, "Minenautica_Quartz_Crystal");
        GameRegistry.registerTileEntity(TileEntityLimestoneOutcrop.class, "Minenautica_Limestone_Outcrop");
        GameRegistry.registerTileEntity(TileEntitySandstoneOutcrop.class, "Minenautica_Sandstone_Outcrop");
        GameRegistry.registerTileEntity(TileEntityLithiumOutcrop.class, "Minenautica_Lithium_Outcrop");
        GameRegistry.registerTileEntity(TileEntityFabricator.class, "Minenautica_Fabricator");
        GameRegistry.registerTileEntity(TileEntityWallLocker.class, "Minenautica_Wall_Locker");
        GameRegistry.registerTileEntity(TileEntityLocker.class, "Minenautica_Locker");
        GameRegistry.registerTileEntity(TileEntitySolarPanel.class, "Minenautica_Solar_Panel");
        GameRegistry.registerTileEntity(TileEntityChair1.class, "Minenautica_Chair_1");
        GameRegistry.registerTileEntity(TileEntityChair2.class, "Minenautica_Chair_2");
        GameRegistry.registerTileEntity(TileEntityChair3.class, "Minenautica_Chair_3");
        GameRegistry.registerTileEntity(TileEntityDesk1.class, "Minenautica_Desk_1");
        GameRegistry.registerTileEntity(TileEntityDesk2.class, "Minenautica_Desk_2");
        GameRegistry.registerTileEntity(TileEntityMedicalKitFabricator.class, "Minenautica_Medical_Kit_Fabricator");
        GameRegistry.registerTileEntity(TileEntityPictureFrame.class, "Minenautica_Picture_Frame");
        GameRegistry.registerTileEntity(TileEntityBed.class, "Minenautica_Bed");
        GameRegistry.registerTileEntity(TileEntityCommunicationsRelay.class, "Minenautica_Communications_Relay");
        GameRegistry.registerTileEntity(TileEntityLadder.class, "Minenautica_Ladder");
        GameRegistry.registerTileEntity(TileEntityLightStick.class, "Minenautica_Light_Stick");
        GameRegistry.registerTileEntity(TileEntitySingleWallShelf.class, "Minenautica_Single_Wall_Shelf");
        GameRegistry.registerTileEntity(TileEntityWallShelf.class, "Minenautica_Wall_Shelf");
        GameRegistry.registerTileEntity(TileEntitySupplyCrate.class, "Minenautica_Supply_Crate");
        GameRegistry.registerTileEntity(TileEntityBaseLight1.class, "Minenautica_Base_Light_1");
        GameRegistry.registerTileEntity(TileEntityBaseLight2.class, "Minenautica_Base_Light_2");
        GameRegistry.registerTileEntity(TileEntityFurledPapyrus.class, "Minenautica_Furled_Papyrus");
        GameRegistry.registerTileEntity(TileEntityDroopingStinger.class, "Minenautica_Drooping_Stinger");
        GameRegistry.registerTileEntity(TileEntityTripleDroopingStinger.class, "Minenautica_Triple_Drooping_Stinger");
        GameRegistry.registerTileEntity(TileEntitySaltDeposit.class, "Minenautica_Salt_Deposit");
        GameRegistry.registerTileEntity(TileEntityLifepodBench.class, "Minenautica_Lifepod_Bench");
        GameRegistry.registerTileEntity(TileEntityLifepodLadder.class, "Minenautica_Lifepod_Ladder_1");
        GameRegistry.registerTileEntity(TileEntityLifepodLadder2.class, "Minenautica_Lifepod_Ladder_2");
        GameRegistry.registerTileEntity(TileEntityLifepodChair1.class, "Minenautica_Lifepod_Chair_1");
        GameRegistry.registerTileEntity(TileEntityLifepodChair2.class, "Minenautica_Lifepod_Chair_2");
        GameRegistry.registerTileEntity(TileEntityExtinguisher.class, "Minenautica_Extinguisher");
        GameRegistry.registerTileEntity(TileEntityExtinguisherHolder.class, "Minenautica_Extinguisher_Holder");
        GameRegistry.registerTileEntity(TileEntitySmallDecal.class, "Minenautica_Small_Decal_1");
        GameRegistry.registerTileEntity(TileEntitySmallDecal2.class, "Minenautica_Small_Decal_2");
        GameRegistry.registerTileEntity(TileEntityLargeDecal.class, "Minenautica_Large_Decal");
        GameRegistry.registerTileEntity(TileEntityPanel.class, "Minenautica_Panel");
        GameRegistry.registerTileEntity(TileEntitySeamothDamaged.class, "Minenautica_Seamoth_Damaged");
        GameRegistry.registerTileEntity(TileEntityLaunchPad.class, "Minenautica_Launch_Pad");
        proxy.registerRenderThings();
        BiomeRegistry.mainRegistry();
        BlocksAndItems.mainRegistry();
        CraftingRecipes.registerRecipes();
        GameRegistry.registerWorldGenerator(worldgen1, 0);
        GameRegistry.registerWorldGenerator(worldgen2, 0);
        GameRegistry.registerWorldGenerator(worldgen3, 0);
        GameRegistry.registerWorldGenerator(worldgen4, 0);
        GameRegistry.registerWorldGenerator(worldgen5, 0);
    }
}
