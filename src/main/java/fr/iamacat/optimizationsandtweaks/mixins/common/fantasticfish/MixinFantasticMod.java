package fr.iamacat.optimizationsandtweaks.mixins.common.fantasticfish;

import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import fantastic.FantasticIds;
import fantastic.FantasticMod;
import fantastic.Recipes;
import fantastic.armor.FantasticArmor;
import fantastic.blocks.FantasticBlocks;
import fantastic.entities.FantasticEntities;
import fantastic.events.FantasticEvents;
import fantastic.events.VillagerTradeHandler;
import fantastic.items.FantasticItems;
import fantastic.proxies.CommonProxy;
import fantastic.tiles.TileAirCompressor;
import fantastic.world.ComponentFishermanHut;
import fantastic.world.CoralGenerator;
import fantastic.world.FishermanHandler;

@Mixin(FantasticMod.class)
public class MixinFantasticMod {

    @Shadow
    public static CommonProxy proxy;

    @Shadow
    public static FantasticMod instance;
    @Shadow
    public static SimpleNetworkWrapper network;
    @Shadow
    private static CoralGenerator generator = new CoralGenerator();

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        Random rand = new Random();
        FantasticItems.init();
        FantasticBlocks.init();
        FantasticEntities.init();
        FantasticArmor.init();
        Recipes.init();
        proxy.loadKeys();
        GameRegistry.registerWorldGenerator(generator, 0);
        MinecraftForge.EVENT_BUS.register(new FantasticEvents());
        proxy.registerVillagers();
        new ResourceLocation("fantastic:textures/models/mobs/fantastic_villager");
        VillagerRegistry.instance();
        VillagerRegistry.getRegisteredVillagers();
        VillagerRegistry.instance()
            .registerVillagerId(FantasticIds.fishermanID);
        VillagerTradeHandler newTradeHandler = new VillagerTradeHandler();
        VillagerRegistry.instance()
            .registerVillageTradeHandler(FantasticIds.fishermanID, newTradeHandler);
        FishermanHandler FishermanHut = new FishermanHandler();
        new ComponentFishermanHut();
        VillagerRegistry.instance()
            .registerVillageCreationHandler(FishermanHut);
        MapGenStructureIO.func_143031_a(ComponentFishermanHut.class, "Fisherman");
        ChestGenHooks contents = ChestGenHooks.getInfo("FISHERMAN");
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(
                new WeightedRandomChestContent(new ItemStack(FantasticItems.bigFish, 1, rand.nextInt(7)), 1, 1, 3));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(
                new WeightedRandomChestContent(new ItemStack(FantasticItems.clamShell, 1, rand.nextInt(4)), 1, 1, 8));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(FantasticItems.filletCooked, 1), 2, 4, 12));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(FantasticItems.clawCooked, 1), 1, 3, 10));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(FantasticItems.tailCooked, 1), 1, 2, 8));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(
                new WeightedRandomChestContent(new ItemStack(FantasticItems.lobster, 1, rand.nextInt(2)), 1, 1, 5));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(FantasticItems.fishingNet, 1), 1, 1, 7));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(Items.fishing_rod, 1), 1, 1, 10));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(FantasticItems.tinyFish, 1, 7), 1, 1, 8));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(FantasticItems.tinyFish, 1, 3), 1, 1, 14));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(FantasticItems.pearl, 1), 1, 2, 9));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(Items.string, 1), 1, 4, 20));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(Items.stick, 1), 3, 5, 23));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(Items.lead, 1), 1, 1, 14));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(Items.name_tag, 1), 1, 1, 6));
        ChestGenHooks.getInfo("FISHERMAN")
            .addItem(new WeightedRandomChestContent(new ItemStack(FantasticItems.randomFish, 0), 1, 1, 3));
        contents.setMin(9);
        contents.setMax(5);
        proxy.initTileRenders();
        GameRegistry.registerTileEntity(TileAirCompressor.class, "AirCompressor_FantasticFish");
    }
}
