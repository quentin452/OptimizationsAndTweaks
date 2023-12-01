package fr.iamacat.optimizationsandtweaks.mixins.common.portalgun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ChestGenHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.registry.GameRegistry;
import portalgun.common.PortalGun;
import portalgun.common.core.Settings;

@Mixin(Settings.class)
public class MixinSettings {

    @Shadow
    public static Map<Integer, String> lootOptions = new HashMap();

    @Shadow
    public static ArrayList<Integer> enabledChestLoot = new ArrayList();

    /**
     * @author iamacatfr
     * @reason fix a null/Exception in server tick loop crash caused by recipes
     */
    @Overwrite
    public static void handlePortalGunRecipe(boolean easyMode) {
        List<IRecipe> recipes = CraftingManager.getInstance()
            .getRecipeList();

        for (int i = recipes.size() - 1; i >= 0; i--) {
            if (recipes.get(i) instanceof ShapedRecipes) {
                ShapedRecipes recipe = (ShapedRecipes) recipes.get(i);
                ItemStack recipeOutput = recipe.getRecipeOutput();
                if (recipeOutput != null) {
                    if (recipeOutput.isItemEqual(new ItemStack(PortalGun.itemPGBlue, 1, 0))) {
                        recipes.remove(i);
                    }

                    if (recipeOutput.isItemEqual(new ItemStack(PortalGun.itemPortalSpawner, 2))) {
                        recipes.remove(i);
                    }
                }
            }
        }

        ItemStack is = new ItemStack(PortalGun.itemPortalSpawner, 2);
        NBTTagCompound tag = is.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            is.setTagCompound(tag);
        }

        tag.setBoolean("closeWhenNoRedstone", false);
        tag.setString("owner", "def");
        tag.setInteger("colour", 1);
        if (PortalGun.getSettings("hardModePortalGun") != 1) {
            GameRegistry.addRecipe(
                new ItemStack(PortalGun.itemPGBlue, 1, 0),
                "X##",
                "DC#",
                "#X#",
                '#',
                Items.iron_ingot,
                'X',
                Blocks.obsidian,
                'D',
                Items.diamond,
                'C',
                easyMode ? Items.ender_pearl : PortalGun.itemMiniBlackHole);
            GameRegistry.addRecipe(
                is,
                "# #",
                "DRD",
                "# #",
                '#',
                Items.iron_ingot,
                'R',
                PortalGun.itemMiniBlackHole,
                'D',
                Items.diamond);
        } else {
            for (Integer integer : enabledChestLoot) {
                String s = getLootOption(integer);
                if (!s.equalsIgnoreCase("")) {
                    ChestGenHooks.getInfo(s)
                        .removeItem(new ItemStack(PortalGun.itemPGBlue, 1, 32767));
                    ChestGenHooks.getInfo(s)
                        .removeItem(new ItemStack(PortalGun.itemPortalSpawner, 1, 32767));
                }
            }

            GameRegistry.addRecipe(
                is,
                "# #",
                "DRD",
                "# #",
                '#',
                Items.iron_ingot,
                'R',
                new ItemStack(PortalGun.itemPGBlue, 1, 0),
                'D',
                Items.diamond);
        }

    }

    /**
     * @author iamacatfr
     * @reason fix a null/Exception in server tick loop crash caused by recipes
     */
    @Overwrite
    public static void handleLFBRecipe(boolean easyMode) {
        List<IRecipe> recipes = CraftingManager.getInstance()
            .getRecipeList();

        for (int i = recipes.size() - 1; i >= 0; i--) {
            if (recipes.get(i) instanceof ShapedRecipes) {
                ShapedRecipes recipe = (ShapedRecipes) recipes.get(i);
                ItemStack recipeOutput = recipe.getRecipeOutput();
                if (recipeOutput != null && recipeOutput.isItemEqual(new ItemStack(PortalGun.itemLFB, 1))) {
                    recipes.remove(i);
                    break;
                }
            }
        }

        if (easyMode) {
            GameRegistry.addRecipe(
                new ItemStack(PortalGun.itemLFB, 1),
                "# #",
                "#O#",
                "#O#",
                '#',
                Items.iron_ingot,
                'O',
                Blocks.obsidian);
        } else {
            GameRegistry.addRecipe(
                new ItemStack(PortalGun.itemLFB, 1),
                "#O#",
                "#D#",
                "#D#",
                '#',
                Items.iron_ingot,
                'O',
                Blocks.obsidian,
                'D',
                Items.diamond);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static String getLootOption(int i) {
        String s = lootOptions.get(i);
        if (s == null) {
            s = "";
        }

        return s;
    }
}
