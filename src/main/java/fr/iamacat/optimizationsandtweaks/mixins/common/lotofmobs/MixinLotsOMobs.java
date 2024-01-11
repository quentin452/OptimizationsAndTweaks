package fr.iamacat.optimizationsandtweaks.mixins.common.lotofmobs;

import net.minecraftforge.common.config.Configuration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.lom.lotsomobscore.ConfigDetails;
import com.lom.lotsomobscore.LotsOMobs;
import com.lom.lotsomobsinit.LotsOMobsAchievementsBook;
import com.lom.lotsomobsinit.LotsOMobsBiomes;
import com.lom.lotsomobsinit.LotsOMobsBlocks;
import com.lom.lotsomobsinit.LotsOMobsItems;
import com.lom.lotsomobsworldgen.FossilOreGeneration;
import com.lom.lotsomobsworldgen.OreGeneration;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import fr.iamacat.optimizationsandtweaks.utilsformods.lotsOMobs.LotsOMobsConfigBiomeID;

@Mixin(LotsOMobs.class)
public class MixinLotsOMobs {

    @Shadow
    public void initConfiguration(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        ConfigDetails.DeerOn = config.get("Mobs", "Deer", true)
            .getBoolean(true);
        ConfigDetails.BoarOn = config.get("Mobs", "Boar", true)
            .getBoolean(true);
        ConfigDetails.WinterDeerOn = config.get("Mobs", "WinterDeer", true)
            .getBoolean(true);
        ConfigDetails.BearOn = config.get("Mobs", "Bear", true)
            .getBoolean(true);
        ConfigDetails.GorillaOn = config.get("Mobs", "Gorilla", true)
            .getBoolean(true);
        ConfigDetails.WhaleOn = config.get("Mobs", "Whale", true)
            .getBoolean(true);
        ConfigDetails.NarwalOn = config.get("Mobs", "Narwhale", true)
            .getBoolean(true);
        ConfigDetails.FishyOn = config.get("Mobs", "Fishy", true)
            .getBoolean(true);
        ConfigDetails.CamelOn = config.get("Mobs", "Camel", true)
            .getBoolean(true);
        ConfigDetails.BirdOn = config.get("Mobs", "Bird", true)
            .getBoolean(true);
        ConfigDetails.PenguinOn = config.get("Mobs", "Penguin", true)
            .getBoolean(true);
        ConfigDetails.IceBearOn = config.get("Mobs", "IceBear", true)
            .getBoolean(true);
        ConfigDetails.SnakeOn = config.get("Mobs", "Snake", true)
            .getBoolean(true);
        ConfigDetails.ButterFlyOn = config.get("Mobs", "ButterFly", true)
            .getBoolean(true);
        ConfigDetails.GiraffeOn = config.get("Mobs", "Giraffe", true)
            .getBoolean(true);
        ConfigDetails.ElephantOn = config.get("Mobs", "Elephant", true)
            .getBoolean(true);
        ConfigDetails.VultureOn = config.get("Mobs", "Vulture", true)
            .getBoolean(true);
        ConfigDetails.AntOn = config.get("Mobs", "Ant", true)
            .getBoolean(true);
        ConfigDetails.TurtleOn = config.get("Mobs", "Turtle", true)
            .getBoolean(true);
        ConfigDetails.LizardOn = config.get("Mobs", "Lizard", true)
            .getBoolean(true);
        ConfigDetails.GekkoOn = config.get("Mobs", "Gekko", true)
            .getBoolean(true);
        ConfigDetails.SantaOn = config.get("Mobs", "Santa", true)
            .getBoolean(true);
        ConfigDetails.CrocoOn = config.get("Mobs", "Croco", true)
            .getBoolean(true);
        ConfigDetails.TriceratopsOn = config.get("Mobs", "Triceratops", true)
            .getBoolean(true);
        ConfigDetails.BrontosaurusOn = config.get("Mobs", "Brontosaurus", true)
            .getBoolean(true);
        ConfigDetails.RaptorOn = config.get("Mobs", "Raptor", true)
            .getBoolean(true);
        ConfigDetails.TRexOn = config.get("Mobs", "T-Rex", true)
            .getBoolean(true);
        ConfigDetails.PterosaurusOn = config.get("Mobs", "Pterosaur", true)
            .getBoolean(true);
        ConfigDetails.MammothOn = config.get("Mobs", "Mammoth", true)
            .getBoolean(true);
        ConfigDetails.SaberToothOn = config.get("Mobs", "SaberTooth", true)
            .getBoolean(true);
        ConfigDetails.LionOn = config.get("Mobs", "Lion", true)
            .getBoolean(true);
        ConfigDetails.EskimoOn = config.get("Mobs", "Eskimo", true)
            .getBoolean(true);
        ConfigDetails.CavemanOn = config.get("Mobs", "Caveman", true)
            .getBoolean(true);
        ConfigDetails.BunnyOn = config.get("Mobs", "Bunny", true)
            .getBoolean(true);
        ConfigDetails.EasterBunnyOn = config.get("Mobs", "EasterBunny", true)
            .getBoolean(true);
        ConfigDetails.SquirrelOn = config.get("Mobs", "Squirrel", true)
            .getBoolean(true);
        ConfigDetails.KakkerlakOn = config.get("Mobs", "Cockroach", true)
            .getBoolean(true);
        ConfigDetails.BirdyOn = config.get("Mobs", "Birdy", true)
            .getBoolean(true);
        ConfigDetails.MuskOxOn = config.get("Mobs", "Musk Ox", true)
            .getBoolean(true);
        ConfigDetails.PDFrogOn = config.get("Mobs", "Poison Dart Frog", true)
            .getBoolean(true);
        ConfigDetails.FrogOn = config.get("Mobs", "Frog", true)
            .getBoolean(true);
        ConfigDetails.FlyOn = config.get("Mobs", "Fly", true)
            .getBoolean(true);
        ConfigDetails.FireFlyOn = config.get("Mobs", "FireFly", true)
            .getBoolean(true);
        ConfigDetails.BullFrogOn = config.get("Mobs", "BullFrog", true)
            .getBoolean(true);
        ConfigDetails.BeeOn = config.get("Mobs", "Bee", true)
            .getBoolean(true);
        ConfigDetails.WormOn = config.get("Mobs", "Worm", true)
            .getBoolean(true);
        ConfigDetails.HermitCrabOn = config.get("Mobs", "Hermit Crab", true)
            .getBoolean(true);
        ConfigDetails.EmpirosaurusOn = config.get("Mobs", "Empirosaurus", true)
            .getBoolean(true);
        ConfigDetails.GoatOn = config.get("Mobs", "Goat", true)
            .getBoolean(true);
        ConfigDetails.IchtyosaurusOn = config.get("Mobs", "Ichtyosaurus", true)
            .getBoolean(true);
        ConfigDetails.GazelleOn = config.get("Mobs", "Gazelle", true)
            .getBoolean(true);
        ConfigDetails.AntarticaID = config.get("Biome", "Antartica", 40)
            .getInt();
        ConfigDetails.ArcticOceanID = config.get("Biome", "Arctic Ocean", 41)
            .getInt();
        ConfigDetails.DinoTerrainID = config.get("Biome", "Dino Terrain(Uses this + the next three)", 42)
            .getInt();
        ConfigDetails.IceTerrainID = config.get("Biome", "Ice Terrain(Uses this + the next three)", 46)
            .getInt();
        ConfigDetails.dimension = config.get("Dimension", "Dino Dimension", -24)
            .getInt();
        ConfigDetails.dimension2 = config.get("Dimension", "Ice Dimension", -25)
            .getInt();
        ConfigDetails.TriceratopsID = config.get("Mob ID (Only for the DNA Mobs)", "Triceratops ID", 110)
            .getInt();
        ConfigDetails.BrontosaurusID = config.get("Mob ID (Only for the DNA Mobs)", "Brontosaurus ID", 111)
            .getInt();
        ConfigDetails.RaptorID = config.get("Mob ID (Only for the DNA Mobs)", "Raptor ID", 112)
            .getInt();
        ConfigDetails.TRexID = config.get("Mob ID (Only for the DNA Mobs)", "T-Rex ID", 113)
            .getInt();
        ConfigDetails.PterosaurusID = config.get("Mob ID (Only for the DNA Mobs)", "Pterosaurus ID", 114)
            .getInt();
        ConfigDetails.MammothID = config.get("Mob ID (Only for the DNA Mobs)", "Mammoth ID", 115)
            .getInt();
        ConfigDetails.SaberToothID = config.get("Mob ID (Only for the DNA Mobs)", "SaberTooth ID", 116)
            .getInt();
        ConfigDetails.IchtyosaurusID = config.get("Mob ID (Only for the DNA Mobs)", "Ichtyosaurus ID", 117)
            .getInt();
        config.save();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.initConfiguration(event);
        LotsOMobsConfigBiomeID.setupAndLoad(event);
        LotsOMobsBlocks.Init();
        LotsOMobsItems.Init();
        LotsOMobsBiomes.Init();
        LotsOMobsAchievementsBook.Init();
        GameRegistry.registerWorldGenerator(new FossilOreGeneration(), 2);
        GameRegistry.registerWorldGenerator(new OreGeneration(), 2);
    }

}
