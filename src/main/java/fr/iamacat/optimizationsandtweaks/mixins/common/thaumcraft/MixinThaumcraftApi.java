package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

@Mixin(ThaumcraftApi.class)
public class MixinThaumcraftApi {

    @Shadow
    private static HashMap<int[], Object[]> keyCache = new HashMap();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Object[] getCraftingRecipeKey(EntityPlayer player, ItemStack stack) {
        int[] key = new int[] { Item.getIdFromItem(stack.getItem()), stack.getItemDamage() };
        if (keyCache.containsKey(key)) {
            if (keyCache.get(key) == null) {
                return null;
            } else {
                return ThaumcraftApiHelper
                    .isResearchComplete(player.getCommandSenderName(), (String) ((Object[]) keyCache.get(key))[0])
                        ? (Object[]) keyCache.get(key)
                        : null;
            }
        } else {
            Iterator<ResearchCategoryList> iterator = ResearchCategories.researchCategories.values()
                .iterator();

            while (iterator.hasNext()) {
                ResearchCategoryList rcl = iterator.next();

                for (ResearchItem ri : rcl.research.values()) {
                    if (ri.getPages() != null) {
                        for (int a = 0; a < ri.getPages().length; ++a) {
                            ResearchPage page = ri.getPages()[a];
                            if (page.recipe != null && page.recipe instanceof CrucibleRecipe[]) {
                                CrucibleRecipe[] crs = (CrucibleRecipe[]) ((CrucibleRecipe[]) page.recipe);
                                CrucibleRecipe[] arrCrs = crs;
                                int lenCrs = crs.length;

                                for (int j = 0; j < lenCrs; ++j) {
                                    CrucibleRecipe cr = arrCrs[j];
                                    if (cr.getRecipeOutput()
                                        .isItemEqual(stack)) {
                                        keyCache.put(key, new Object[] { ri.key, a });
                                        if (ThaumcraftApiHelper
                                            .isResearchComplete(player.getCommandSenderName(), ri.key)) {
                                            return new Object[] { ri.key, a };
                                        }
                                    }
                                }
                            } else if (page.recipeOutput != null && stack != null
                                && page.recipeOutput.isItemEqual(stack)) {
                                    keyCache.put(key, new Object[] { ri.key, a });
                                    if (ThaumcraftApiHelper.isResearchComplete(player.getCommandSenderName(), ri.key)) {
                                        return new Object[] { ri.key, a };
                                    }

                                    return null;
                                }
                        }
                    }
                }
            }

            keyCache.put(key, null);
            return null;
        }
    }
}
