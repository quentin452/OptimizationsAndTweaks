package fr.iamacat.optimizationsandtweaks.mixins.common.aether;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gildedgames.the_aether.tileentity.*;

import cpw.mods.fml.common.registry.GameRegistry;

@Mixin(AetherTileEntities.class)
public class MixinAetherTileEntities {

    /**
     * @author iamacatfr
     * @reason change names of tile entities to fix dupplicated names
     */
    @Overwrite
    public static void initialization() {
        GameRegistry.registerTileEntity(TileEntityEnchanter.class, "enchanter_aether");
        GameRegistry.registerTileEntity(TileEntityFreezer.class, "freezer_aether");
        GameRegistry.registerTileEntity(TileEntityIncubator.class, "incubator_aether");
        GameRegistry.registerTileEntity(TileEntityTreasureChest.class, "treasure_chest_aether");
        GameRegistry.registerTileEntity(TileEntityChestMimic.class, "chest_mimic_aether");
    }
}
