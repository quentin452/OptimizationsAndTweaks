package fr.iamacat.catmod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import fr.iamacat.catmod.init.*;
import fr.iamacat.catmod.proxy.CommonProxy;
import fr.iamacat.catmod.utils.CatTab;
import fr.iamacat.catmod.utils.Reference;
import fr.iamacat.catmod.worldgen.oregen.CatOreGen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, acceptedMinecraftVersions = Reference.MC_VERSION)
public class Catmod {
    @Mod.Instance(Reference.MOD_ID)
    public static Catmod instance;
    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;
    public static CreativeTabs catTab = new CatTab("catTab");

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        RegisterItems.init();
        RegisterItems.register();
        RegisterBlocks.init();
        RegisterBlocks.register();
        GameRegistry.registerWorldGenerator(new CatOreGen(), 0);
        RegisterBiomes.init();
    }
    public static class WorldLoadHandler {

    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerFuelHandler(new RegisterFuel());
        // Register the proxy as an event handler
        MinecraftForge.EVENT_BUS.register(proxy);
        proxy.registerRenders();
        proxy.registerEntities();
        }
    }