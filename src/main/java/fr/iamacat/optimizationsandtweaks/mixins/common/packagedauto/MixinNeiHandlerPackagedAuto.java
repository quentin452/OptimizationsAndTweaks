package fr.iamacat.optimizationsandtweaks.mixins.common.packagedauto;

import codechicken.nei.api.API;
import codechicken.nei.event.NEIConfigsLoadedEvent;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import thelm.packagedauto.client.gui.GuiEncoder;
import thelm.packagedauto.integration.nei.EncoderOverlayHandler;
import thelm.packagedauto.integration.nei.NEIHandler;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(NEIHandler.class)
public class MixinNeiHandlerPackagedAuto {
    @Shadow
    private static Function<TemplateRecipeHandler.RecipeTransferRect, String> getCategory;

    @Shadow
    private static final ListMultimap<String, ICraftingHandler> HANDLERS = MultimapBuilder.treeKeys().arrayListValues().build();
    // Disabling GuiCraftingRecipe Load because Made too much ram usage on large modpacks
    @SubscribeEvent(
        priority = EventPriority.LOWEST
    )
    @Overwrite(remap = false )
    public void onNEIConfigsLoaded(NEIConfigsLoadedEvent event) {
        HANDLERS.clear();
      /*  Stream.concat(GuiCraftingRecipe.craftinghandlers.stream(), GuiCraftingRecipe.serialCraftingHandlers.stream()).forEach((handler) -> {
            Iterator var2 = this.getRecipeCategories(handler).iterator();

            while(var2.hasNext()) {
                String category = (String)var2.next();
                API.registerGuiOverlayHandler(GuiEncoder.class, EncoderOverlayHandler.INSTANCE, category);
                HANDLERS.put(category, handler.getRecipeHandler(category, new Object[0]));
            }

        });

       */
    }
    @Shadow
    public Set<String> getRecipeCategories(IRecipeHandler recipeHandler) {
        Set<String> categories = new TreeSet();
        if (recipeHandler instanceof TemplateRecipeHandler) {
            ((TemplateRecipeHandler)recipeHandler).transferRects.stream().map(getCategory).filter(Objects::nonNull).distinct().forEach(categories::add);
        }

        String cat = recipeHandler.getOverlayIdentifier();
        if (cat != null) {
            categories.add(cat);
        }

        return categories;
    }

}
