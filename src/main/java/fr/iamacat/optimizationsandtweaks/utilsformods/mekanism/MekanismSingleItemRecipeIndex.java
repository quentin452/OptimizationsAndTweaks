package fr.iamacat.optimizationsandtweaks.utilsformods.mekanism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Indexed fast path for {@code mekanism.common.util.MekanismUtils#findMatchingRecipe} restricted to the
 * single-item crafting grid, which is the shape {@code OreDictManager#addLogRecipes} (and its siblings) feed it
 * during Mekanism's post-init. That method is a copy of vanilla {@code CraftingManager#findMatchingRecipe}: an
 * O(all recipes) linear scan calling {@link IRecipe#matches} on every recipe. With a large modpack it is called
 * once per ore-dictionary log entry (wildcard logs x16), so the total is O(entries x recipes) and dominates boot.
 * <p>
 * This class is a plain (non-mixin) helper on purpose: it is re-obfuscated with the rest of the mod, so its
 * Minecraft/Forge member references are remapped normally in production. The mixin that targets the mod class
 * {@code MekanismUtils} must be declared {@code remap = false}, and delegating the Minecraft-touching work here
 * keeps that mixin body free of any vanilla member access.
 * <h2>Correctness</h2>
 * The single-item path must return EXACTLY what the linear scan would: the first recipe in recipe-list order
 * whose {@code matches(grid, world)} is true, and the same {@code getCraftingResult(grid)} (Mekanism returns it
 * without copying, so neither do we). To guarantee that:
 * <ul>
 * <li>Recipes are classified only by their EXACT class ({@code getClass() ==}), never {@code instanceof}, so the
 * structural reasoning below relies solely on the known vanilla/Forge {@code matches} semantics. Any subclass
 * (possibly with an overridden {@code matches}) and any other {@code IRecipe} implementation goes to a single
 * {@code unindexable} bucket that is ALWAYS tested.</li>
 * <li>A recipe is pruned (never a candidate) only when it PROVABLY cannot match a one-item grid:
 * <ul>
 * <li>{@link ShapelessRecipes}/{@link ShapelessOreRecipe} whose ingredient count is not exactly 1 (their
 * {@code matches} requires the consumed ingredient multiset to equal the grid contents; a one-item grid can only
 * satisfy exactly one ingredient).</li>
 * <li>{@link ShapedRecipes}/{@link ShapedOreRecipe} with two or more non-null pattern cells (their {@code matches}
 * requires every non-null pattern cell to align with a non-null grid slot; one non-null grid slot cannot cover
 * two).</li>
 * </ul>
 * </li>
 * <li>A single-ingredient shapeless/ore-shapeless recipe is indexed under every {@link Item} its ingredient
 * accepts, so {@code index.get(gridItem)} holds every such recipe that could possibly match; the final
 * {@code matches} call still enforces metadata/ore membership.</li>
 * <li>Candidates from {@code index.get(item)} and {@code unindexable} are tested in original recipe-list order:
 * every kept recipe records its list position and the two ascending-by-construction buckets are merged by
 * position, so first-match-wins is identical to the scan.</li>
 * </ul>
 * The index is rebuilt whenever the recipe list size changes (recipes are only ever appended in 1.7.10), so a
 * cache built during Mekanism's post-init stays correct for later callers such as the Formulaic Assemblicator.
 */
public final class MekanismSingleItemRecipeIndex {

    private MekanismSingleItemRecipeIndex() {}

    /** Outcome wrapper: {@code null} return means "not the single-item case, let the original run". */
    public static final class Match {

        public final ItemStack result;

        public Match(ItemStack result) {
            this.result = result;
        }
    }

    /** Immutable, atomically-published snapshot of the index for a given recipe-list size. */
    private static final class IndexData {

        final Map<Item, List<IRecipe>> index;
        final List<IRecipe> unindexable;
        final Map<IRecipe, Integer> position;
        final int size;

        IndexData(Map<Item, List<IRecipe>> index, List<IRecipe> unindexable, Map<IRecipe, Integer> position, int size) {
            this.index = index;
            this.unindexable = unindexable;
            this.position = position;
            this.size = size;
        }
    }

    private static volatile IndexData current;

    /**
     * @return {@code null} when the grid does not hold exactly one non-empty stack (the caller must let the
     *         original method run); otherwise a {@link Match} whose {@code result} is exactly what the original
     *         linear scan would return (possibly {@code null} when nothing matches).
     */
    public static Match resolveSingleItem(InventoryCrafting inv, World world) {
        ItemStack single = null;
        int count = 0;
        int slots = inv.getSizeInventory();
        for (int i = 0; i < slots; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            count++;
            if (count > 1) {
                // Two or more items: fall through to the original (repair handling + full scan).
                return null;
            }
            single = stack;
        }
        if (count != 1) {
            // Empty grid: fall through; the original returns null for this case anyway.
            return null;
        }
        Item item = single.getItem();
        if (item == null) {
            // Mirror the original's guard; it returns null cheaply, so just let it run.
            return null;
        }

        List<?> recipeList = CraftingManager.getInstance()
            .getRecipeList();
        IndexData data = current;
        if (data == null || data.size != recipeList.size()) {
            data = build(recipeList);
            current = data;
        }

        List<IRecipe> byItem = data.index.get(item);
        return new Match(firstMatch(byItem, data.unindexable, data.position, inv, world));
    }

    /**
     * Tests the merged candidate set in original recipe-list order and returns the first match's crafting result
     * (uncopied, exactly like Mekanism's scan), or {@code null} if none match.
     */
    private static ItemStack firstMatch(List<IRecipe> byItem, List<IRecipe> unindexable, Map<IRecipe, Integer> position,
        InventoryCrafting inv, World world) {
        int ia = 0;
        int ib = 0;
        int sa = byItem == null ? 0 : byItem.size();
        int sb = unindexable == null ? 0 : unindexable.size();
        while (ia < sa || ib < sb) {
            IRecipe next;
            if (ib >= sb) {
                next = byItem.get(ia++);
            } else if (ia >= sa) {
                next = unindexable.get(ib++);
            } else {
                IRecipe candidateA = byItem.get(ia);
                IRecipe candidateB = unindexable.get(ib);
                if (position.get(candidateA)
                    .intValue()
                    < position.get(candidateB)
                        .intValue()) {
                    next = candidateA;
                    ia++;
                } else {
                    next = candidateB;
                    ib++;
                }
            }
            if (next.matches(inv, world)) {
                return next.getCraftingResult(inv);
            }
        }
        return null;
    }

    private static IndexData build(List<?> recipeList) {
        Map<Item, List<IRecipe>> index = new HashMap<Item, List<IRecipe>>();
        List<IRecipe> unindexable = new ArrayList<IRecipe>();
        Map<IRecipe, Integer> position = new IdentityHashMap<IRecipe, Integer>();

        int size = recipeList.size();
        for (int i = 0; i < size; i++) {
            Object obj = recipeList.get(i);
            if (!(obj instanceof IRecipe)) {
                // Not a recipe (never happens in practice); it cannot be matched, so skip it entirely.
                continue;
            }
            IRecipe recipe = (IRecipe) obj;
            Class<?> cls = recipe.getClass();

            List<Item> items = null;
            if (cls == ShapelessRecipes.class) {
                List<?> ingredients = ((ShapelessRecipes) recipe).recipeItems;
                if (ingredients != null && ingredients.size() == 1) {
                    items = itemsOf(ingredients.get(0));
                } else {
                    // 0 or >=2 ingredients cannot match a one-item grid.
                    continue;
                }
            } else if (cls == ShapelessOreRecipe.class) {
                List<?> ingredients = ((ShapelessOreRecipe) recipe).getInput();
                if (ingredients != null && ingredients.size() == 1) {
                    items = itemsOf(ingredients.get(0));
                } else {
                    continue;
                }
            } else if (cls == ShapedRecipes.class) {
                if (nonNullCount(((ShapedRecipes) recipe).recipeItems) >= 2) {
                    // Two or more pattern cells need two or more grid items.
                    continue;
                }
                // 0 or 1 cell: could match, and we do not try to pinpoint the item -> always test.
            } else if (cls == ShapedOreRecipe.class) {
                if (nonNullCount(((ShapedOreRecipe) recipe).getInput()) >= 2) {
                    continue;
                }
            }

            if (items != null) {
                for (int k = 0; k < items.size(); k++) {
                    Item it = items.get(k);
                    List<IRecipe> bucket = index.get(it);
                    if (bucket == null) {
                        bucket = new ArrayList<IRecipe>();
                        index.put(it, bucket);
                    }
                    bucket.add(recipe);
                }
                position.put(recipe, Integer.valueOf(i));
            } else {
                // Unknown class, subclass with unknown matches, or a known class we could not cleanly resolve
                // to a concrete item set: always test it.
                unindexable.add(recipe);
                position.put(recipe, Integer.valueOf(i));
            }
        }

        return new IndexData(index, unindexable, position, size);
    }

    /**
     * Resolves the concrete {@link Item}s a single ingredient accepts.
     *
     * @return the item set, or {@code null} when the ingredient cannot be cleanly resolved (the caller then
     *         treats the recipe as unindexable and always tests it).
     */
    private static List<Item> itemsOf(Object ingredient) {
        if (ingredient instanceof ItemStack) {
            Item it = ((ItemStack) ingredient).getItem();
            if (it == null) {
                return null;
            }
            List<Item> items = new ArrayList<Item>(1);
            items.add(it);
            return items;
        }
        if (ingredient instanceof List) {
            List<?> ores = (List<?>) ingredient;
            if (ores.isEmpty()) {
                return null;
            }
            List<Item> items = new ArrayList<Item>(ores.size());
            for (int i = 0; i < ores.size(); i++) {
                Object entry = ores.get(i);
                if (!(entry instanceof ItemStack)) {
                    return null;
                }
                Item it = ((ItemStack) entry).getItem();
                if (it == null) {
                    return null;
                }
                items.add(it);
            }
            return items;
        }
        return null;
    }

    private static int nonNullCount(Object[] cells) {
        if (cells == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] != null) {
                count++;
            }
        }
        return count;
    }
}
