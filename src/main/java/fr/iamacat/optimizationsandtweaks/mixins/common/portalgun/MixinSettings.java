package fr.iamacat.optimizationsandtweaks.mixins.common.portalgun;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import portalgun.common.core.Settings;

/**
 * Both {@code handlePortalGunRecipe} and {@code handleLFBRecipe} call
 * {@code recipe.getRecipeOutput().isItemEqual(...)} unconditionally while pruning the vanilla recipe list;
 * {@code getRecipeOutput()} can return null for some {@link net.minecraft.item.crafting.ShapedRecipes},
 * NPE-ing the server tick loop. The fix caches the output and null-guards it before comparing, in both
 * methods identically -- redirecting the shared {@code ItemStack#isItemEqual} call covers both call sites
 * (2 in {@code handlePortalGunRecipe}, 1 in {@code handleLFBRecipe}) without copying either method body.
 * <p>
 * {@code getLootOption} was a byte-for-byte behavioral copy of the original (same null-to-empty-string
 * fallback); deleted as a dead dupe -- the original method (called from {@code handlePortalGunRecipe}'s
 * untouched bytecode) already behaves identically.
 *
 * @author iamacatfr
 * @reason fix a null/Exception in server tick loop crash caused by recipes
 */
@Mixin(Settings.class)
public class MixinSettings {

    @Redirect(
        method = { "handlePortalGunRecipe", "handleLFBRecipe" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isItemEqual(Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean optimizationsandtweaks$safeIsItemEqual(ItemStack recipeOutput, ItemStack other) {
        return recipeOutput != null && recipeOutput.isItemEqual(other);
    }
}
