package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.lib.research.ScanManager;

import java.util.*;

@Mixin(ScanManager.class)
public class MixinScanManager {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static int generateItemHash(Item item, int meta) {
        ItemStack stack = new ItemStack(item, 1, meta);
        if (stack.isItemStackDamageable() || !stack.getHasSubtypes()) {
            meta = -1;
        }
        String hash;
        try {
            GameRegistry.UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(item);
            if (ui != null) {
                hash = ui + ":" + meta;
            } else {
                hash = stack.getUnlocalizedName() + ":" + meta;
            }
        } catch (Exception e) {
            hash = "oops:" + meta;
        }
        if (!ThaumcraftApi.objectTags.containsKey(Arrays.asList(item, meta))) {
            for (Map.Entry<List, int[]> entry : ThaumcraftApi.groupedObjectTags.entrySet()) {
                List key = entry.getKey();
                Object value = entry.getValue();
                if (key.contains(item) && key.contains(meta) && (value instanceof int[])) {
                        int[] range = (int[]) value;
                        Arrays.sort(range);
                        if (Arrays.binarySearch(range, meta) >= 0) {
                            GameRegistry.UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(item);
                            if (ui != null) {
                                hash = ui.toString();
                            } else {
                                hash = stack.getUnlocalizedName();
                            }
                            for (int r : range) {
                                hash = hash + ":" + r;
                            }
                            return hash.hashCode();
                        }

                }
            }
        }
        return hash.hashCode();
    }
}
