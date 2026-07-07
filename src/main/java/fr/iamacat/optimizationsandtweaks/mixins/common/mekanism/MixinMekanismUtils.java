package fr.iamacat.optimizationsandtweaks.mixins.common.mekanism;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.utilsformods.mekanism.MekanismSingleItemRecipeIndex;

/**
 * Cuts the ~15s Mekanism post-init spends in {@code OreDictManager} auto-generating recipes.
 * <p>
 * {@code MekanismUtils.findMatchingRecipe} is a copy of vanilla {@code CraftingManager.findMatchingRecipe}: an
 * O(all recipes) linear scan. {@code OreDictManager#addLogRecipes} calls it once per {@code logWood} ore-dict
 * entry (wildcard logs x16), each on a grid holding a SINGLE log, so the cost is O(entries x recipes).
 * <p>
 * This injects a HEAD fast path only for that single-item case: it returns the same first-in-list-order match
 * the scan would (see {@link MekanismSingleItemRecipeIndex} for the correctness argument) via an indexed lookup,
 * and cancels the original. For 0 or 2+ item grids it does nothing and the original method runs unchanged, so
 * the vanilla repair branch and multi-item crafting keep their exact behavior.
 * <p>
 * Mekanism is not a compile dependency, so the target is given by name with {@code remap = false}; all
 * Minecraft/Forge access lives in the re-obfuscated helper, keeping this mixin body free of remappable members.
 */
@Mixin(targets = "mekanism.common.util.MekanismUtils", remap = false)
public class MixinMekanismUtils {

    @Inject(method = "findMatchingRecipe", at = @At("HEAD"), cancellable = true, remap = false)
    private static void optimizationsandtweaks$fastSingleItemLookup(InventoryCrafting inv, World world,
        CallbackInfoReturnable<ItemStack> cir) {
        MekanismSingleItemRecipeIndex.Match match = MekanismSingleItemRecipeIndex.resolveSingleItem(inv, world);
        if (match != null) {
            cir.setReturnValue(match.result);
        }
    }
}
