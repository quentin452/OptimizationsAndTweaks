package fr.iamacat.multithreading;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fr.iamacat.multithreading.proxy.CommonProxy;
import fr.iamacat.multithreading.utils.Reference;

        @Mod(modid = Tags.MODID,
        version = Tags.VERSION,
        name = Tags.MODNAME,
        acceptedMinecraftVersions = "[1.7.10]",
            dependencies = "required-after:falsepatternlib@[0.10.13")
    // dependencies = "required-after:falsepatternlib@[0.10.13,);required-after:gasstation@[0.5.0,);after:antiidconflict")


public class Multithreaded {

    @Mod.Instance(Reference.MOD_ID)
    public static Multithreaded instance;
    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {}

    public static class WorldLoadHandler {

    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register the proxy as an event handler
        MinecraftForge.EVENT_BUS.register(proxy);
    }
}
