package fr.iamacat.optimizationsandtweaks.mixins.common.traincraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import train.common.api.crafting.ITierCraftingManager;
import train.common.api.crafting.ITierRecipe;
import train.common.core.managers.TierRecipe;
import train.common.core.managers.TierRecipeManager;

@Mixin(TierRecipeManager.class)
public class MixinTierRecipeManager implements ITierCraftingManager {

    @Shadow
    private final List<ITierRecipe> recipeList = new ArrayList();
    @Shadow
    private static TierRecipeManager instance = new TierRecipeManager();

    @Shadow
    public static ITierCraftingManager getInstance() {
        return instance;
    }

    @Overwrite(remap = false)
    public void addRecipe(int tier, ItemStack planks, ItemStack wheels, ItemStack frame, ItemStack coupler,
        ItemStack chimney, ItemStack cab, ItemStack boiler, ItemStack firebox, ItemStack additional, ItemStack dye,
        ItemStack output, int outputSize) {
        tier = Math.max(1, Math.min(3, tier));
        outputSize = Math.max(1, Math.min(64, outputSize));
        addRecipeFinal(
            tier,
            planks,
            wheels,
            frame,
            coupler,
            chimney,
            cab,
            boiler,
            firebox,
            additional,
            dye,
            output,
            outputSize);
    }

    @Overwrite(remap = false)
    public void addRecipeFinal(int tier, ItemStack planks, ItemStack wheels, ItemStack frame, ItemStack coupler,
        ItemStack chimney, ItemStack cab, ItemStack boiler, ItemStack firebox, ItemStack additional, ItemStack dye,
        ItemStack output, int outputSize) {
        recipeList.add(
            new TierRecipe(
                tier,
                planks,
                wheels,
                frame,
                coupler,
                chimney,
                cab,
                boiler,
                firebox,
                additional,
                dye,
                output,
                outputSize));
    }

    @Overwrite(remap = false)
    public ITierRecipe getTierRecipe(int tier, ItemStack output) {
        if (output == null) {
            return null;
        }

        for (ITierRecipe recipe : recipeList) {
            if (Item.getIdFromItem(
                recipe.getOutput()
                    .getItem())
                == Item.getIdFromItem(output.getItem()) && recipe.getTier() == tier) {
                return recipe;
            }
        }
        return null;
    }

    @Overwrite(remap = false)
    public List<ITierRecipe> getRecipeList() {
        return Collections.unmodifiableList(recipeList);
    }

    @Overwrite(remap = false)
    public List<ITierRecipe> getTierRecipeList(int tier) {
        List<ITierRecipe> tierRecipeList = new ArrayList<>();
        for (ITierRecipe recipe : recipeList) {
            if (recipe.getTier() == tier) {
                tierRecipeList.add(recipe);
            }
        }
        return Collections.unmodifiableList(tierRecipeList);
    }
}
