package fr.iamacat.optimizationsandtweaks.mixins.common.aether;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gildedgames.the_aether.tileentity.*;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Renames Aether's tile entities to avoid tile-entity name conflicts with other mods (e.g. Essence of the God,
 * Fantastic Fish).
 */
@Mixin(AetherTileEntities.class)
public class MixinAetherTileEntities {

    /**
     * @reason change names of tile entities to fix duplicated names. HEAD-cancel instead of a full-method
     *         replace so any other transform on this method still applies.
     */
    @Inject(method = "initialization", at = @At("HEAD"), remap = false, cancellable = true)
    private static void initialization(CallbackInfo ci) {
        GameRegistry.registerTileEntity(TileEntityEnchanter.class, "enchanter_aether");
        GameRegistry.registerTileEntity(TileEntityFreezer.class, "freezer_aether");
        GameRegistry.registerTileEntity(TileEntityIncubator.class, "incubator_aether");
        GameRegistry.registerTileEntity(TileEntityTreasureChest.class, "treasure_chest_aether");
        GameRegistry.registerTileEntity(TileEntityChestMimic.class, "chest_mimic_aether");
        ci.cancel();
    }
}
