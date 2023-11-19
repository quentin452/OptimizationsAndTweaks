package fr.iamacat.optimizationsandtweaks.mixins.common.kitchencraft;

import cpw.mods.fml.common.ModAPIManager;
import net.minecraft.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.wyldmods.kitchencraft.machines.KitchenCraftMachines;

@Mixin(KitchenCraftMachines.class)
public class MixinKitchenCraftMachines {

    @Shadow
    public static int renderIDPot;
    @Shadow
    public static Block pot;
    @Shadow
    public static final Logger logger = LogManager.getLogger("KitchenCraft - Machines");
    @Shadow
    private static boolean rfCheckLoaded = false;
    @Shadow
    private static boolean loadRF = false;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean loadRF() {
        if (!rfCheckLoaded) {
            loadRF = ModAPIManager.INSTANCE.hasAPI("CoFHAPI|energy");
            System.out.println("Initialized RF value to " + loadRF);
            rfCheckLoaded = true;
        }
        return loadRF;
    }
}
