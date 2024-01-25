package fr.iamacat.optimizationsandtweaks.mixins.common.tumbleweed;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import tumbleweed.Tumbleweed;
import tumbleweed.common.CommonEventHandler;
import tumbleweed.common.EntityTumbleweed;

@Mixin(Tumbleweed.class)
public class MixinTumbleweed {
    @Shadow
    public static Tumbleweed instance;
    @Overwrite
    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(EntityTumbleweed.class, "Tumbleweed", EntityRegistry.findGlobalUniqueEntityId(), instance, 64, 20, true);
        FMLCommonHandler.instance().bus().register(new CommonEventHandler());
    }
}
