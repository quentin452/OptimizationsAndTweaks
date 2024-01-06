package fr.iamacat.optimizationsandtweaks.mixins.common.birdsnest;

import net.minecraft.item.Item;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import panda.birdsnests.BirdsNests;
import panda.birdsnests.dropRegistry;

@Mixin(BirdsNests.class)
public class MixinBirdNests {

    @Shadow
    public static String[] mega;
    @Shadow
    public static Logger log;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @Mod.EventHandler
    public void PostInitialize(FMLPostInitializationEvent event) {
        if (mega != null && mega.length > 0 && mega[0].length() > 1) {
            for (String s : mega) {
                String[] tempsplit = s.split(",");
                if (tempsplit.length == 3) {
                    if (Item.itemRegistry.getObject(tempsplit[0]) != null) {
                        dropRegistry.registerConfigRarity(
                            tempsplit[2].split("-"),
                            (Item) Item.itemRegistry.getObject(tempsplit[0]),
                            Integer.parseInt(tempsplit[1]));
                        log.info("Custom drop " + tempsplit[0] + " added");
                    } else {
                        log.error("UNABLE TO RESOLVE CUSTOM DROP " + tempsplit[0]);
                    }
                } else {
                    log.error("UNABLE TO TRANSLATE CUSTOM DROP " + s);
                }
            }
        }

    }
}
