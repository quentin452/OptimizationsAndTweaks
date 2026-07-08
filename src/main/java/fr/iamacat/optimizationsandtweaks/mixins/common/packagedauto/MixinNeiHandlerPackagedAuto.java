package fr.iamacat.optimizationsandtweaks.mixins.common.packagedauto;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import codechicken.nei.event.NEIConfigsLoadedEvent;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import thelm.packagedauto.integration.nei.NEIHandler;

/**
 * Original {@code onNEIConfigsLoaded} rebuilds the recipe-category handler map from every loaded
 * GuiCraftingRecipe handler; that's the RAM cost this mixin disables (see javadoc). Instead of copying
 * the whole method with the real body deleted, this injects at HEAD, clears the map as a side effect and
 * cancels -- the original body (which would repopulate it) never runs.
 *
 * @author OptimizationsAndTweaks
 * @reason Disables the recipe viewer on NeiHandlerPackagedAuto to reduce RAM usage (Packaged Auto mod).
 */
@Mixin(NEIHandler.class)
public class MixinNeiHandlerPackagedAuto {

    @Shadow
    private static Function<TemplateRecipeHandler.RecipeTransferRect, String> getCategory;

    @Shadow
    private static final ListMultimap<String, ICraftingHandler> HANDLERS = MultimapBuilder.treeKeys()
        .arrayListValues()
        .build();

    @Inject(method = "onNEIConfigsLoaded", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$disableRecipeViewer(NEIConfigsLoadedEvent event, CallbackInfo ci) {
        HANDLERS.clear();
        ci.cancel();
    }

    @Shadow
    public Set<String> getRecipeCategories(IRecipeHandler recipeHandler) {
        Set<String> categories = new TreeSet();
        if (recipeHandler instanceof TemplateRecipeHandler) {
            ((TemplateRecipeHandler) recipeHandler).transferRects.stream()
                .map(getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(categories::add);
        }

        String cat = recipeHandler.getOverlayIdentifier();
        if (cat != null) {
            categories.add(cat);
        }

        return categories;
    }

}
