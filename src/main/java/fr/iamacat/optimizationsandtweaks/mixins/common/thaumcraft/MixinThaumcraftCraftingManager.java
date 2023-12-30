package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.*;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.utils.Utils;

import java.util.*;

@Mixin(ThaumcraftCraftingManager.class)
public class MixinThaumcraftCraftingManager {
    @Shadow
    public static AspectList generateTags(Item item, int meta) {
        return generateTags(item, meta, new ArrayList<>());
    }
    @Unique
    private static HashMap<List<Object>, AspectList> optimizationsAndTweaks$tagCache = new HashMap<>();

    @Unique
    private static AspectList optimizationsAndTweaks$getTags(ItemStack itemstack) {
        Item item;
        int meta;
        try {
            item = itemstack.getItem();
            meta = itemstack.getItemDamage();
        } catch (Exception e) {
            return null;
        }

        List<Object> key = Arrays.asList((Object) item, meta);
        if (optimizationsAndTweaks$tagCache.containsKey(key)) {
            return optimizationsAndTweaks$tagCache.get(key);
        } else {
            // Add the item to the cache with an initial null value to prevent recursive calls
            optimizationsAndTweaks$tagCache.put(key, null);

            AspectList tags = getObjectTags(itemstack);
            optimizationsAndTweaks$tagCache.put(key, tags);
            return tags;
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static AspectList generateTags(Item item, int meta, ArrayList<List> history) {
        int tmeta = meta;
        try {
            tmeta = (!Objects.requireNonNull(new ItemStack(item, 1, meta).getItem()).isDamageable() && new ItemStack(item, 1, meta).getItem().getHasSubtypes()) ? meta : 32767;
        } catch (Exception ignored) {}

        List<Object> key = Arrays.asList((Object)item, tmeta);

        if (ThaumcraftApi.exists(item, tmeta)) {
            return optimizationsAndTweaks$getTags(new ItemStack(item, 1, tmeta));
        } else if (history.contains(key)) {
            return null;
        } else {
            history.add(key);
            if (history.size() < 100) {
                AspectList ret = generateTagsFromRecipes(item, tmeta == 32767 ? 0 : meta, history);
                ret = capAspects(ret, 64);
                ThaumcraftApi.registerObjectTag(new ItemStack(item, 1, tmeta), ret);
                return ret;
            } else {
                return null;
            }
        }
    }

    @Unique
    private static void optimizationsAndTweaks$processItemStack(ItemStack stack, AspectList aspectList, ArrayList<List> history) {
        if (stack == null || stack.getItem() == null) {
            return;
        }

        if (!Utils.isEETransmutionItem(stack.getItem())) {
            AspectList aspects = optimizationsAndTweaks$getTags(stack);

            if (aspects != null && aspects.size() > 0) {
                ItemStack clonedStack = stack.copy();

                if (clonedStack.stackSize != 1) {
                    clonedStack.stackSize = 1;
                }

                aspectList.merge(aspects);
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private static AspectList generateTagsFromRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList ret = generateTagsFromCrucibleRecipes(item, meta, history);

        if (ret != null) {
            return ret;
        }

        ret = generateTagsFromArcaneRecipes(item, meta, history);

        if (ret != null) {
            return ret;
        }

        ret = generateTagsFromInfusionRecipes(item, meta, history);

        if (ret == null) {
            ret = generateTagsFromCraftingRecipes(item, meta, history);
        }

        return ret;
    }
    /**
    /**
     * @author
     * @reason
     */
    @Overwrite
    private static AspectList getAspectsFromIngredients(ArrayList<ItemStack> ingredients, ItemStack recipeOut, ArrayList<List> history) {
        AspectList out = new AspectList();
        AspectList mid = new AspectList();

        for (ItemStack is : ingredients) {
            AspectList obj = generateTags(is.getItem(), is.getItemDamage(), history);

            if (Objects.requireNonNull(is.getItem()).getContainerItem() != null && is.getItem().getContainerItem() != is.getItem()) {
                AspectList objC = generateTags(is.getItem().getContainerItem(), 32767, history);
                assert objC != null;
                Aspect[] containerAspects = objC.getAspects();

                for (Aspect as : containerAspects) {
                    out.reduce(as, objC.getAmount(as));
                }
            }

            if (obj != null) {
                Aspect[] objAspects = obj.getAspects();
                for (Aspect as : objAspects) {
                    if (as != null) {
                        mid.add(as, obj.getAmount(as));
                    }
                }
            }
        }

        Aspect[] midAspects = mid.getAspects();
        for (Aspect as : midAspects) {
            if (as != null) {
                out.add(as, (int) (mid.getAmount(as) * 0.75F / (float) recipeOut.stackSize));
            }
        }

        Aspect[] outAspects = out.getAspects();
        for (Aspect as : outAspects) {
            if (out.getAmount(as) <= 0) {
                out.remove(as);
            }
        }

        return out;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private static AspectList generateTagsFromCraftingRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList ret = null;
        int minValue = Integer.MAX_VALUE;
        List<IRecipe> recipeList = Collections.unmodifiableList(new ArrayList<>(CraftingManager.getInstance().getRecipeList()));

        for (IRecipe object : recipeList) {
            if (optimizationsAndTweaks$isValidRecipe(object, item, meta)) {
                try {
                    AspectList ph = optimizationsAndTweaks$processRecipeAndGetAspects(object, history);
                    optimizationsAndTweaks$refineAndSetMinValueAspect(ph, minValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    @Unique
    private static boolean optimizationsAndTweaks$isValidRecipe(IRecipe recipe, Item item, int meta) {
        if (recipe == null || recipe.getRecipeOutput() == null || recipe.getRecipeOutput().getItem() == null) {
            return false;
        }

        ItemStack outputStack = recipe.getRecipeOutput().copy();
        ItemStack comparisonStack = new ItemStack(item, 1, meta);

        // Check if the output item and metadata match the given item and meta
        return ItemStack.areItemStacksEqual(outputStack, comparisonStack);
    }

    @Unique
    private static AspectList optimizationsAndTweaks$processRecipeAndGetAspects(IRecipe recipe, ArrayList<List> history) {
        AspectList aspectList = new AspectList();

        try {
            if (recipe instanceof ShapedRecipes) {
                optimizationsAndTweaks$processShapedRecipe((ShapedRecipes) recipe, aspectList, history);
            } else if (recipe instanceof ShapelessRecipes) {
                optimizationsAndTweaks$processShapelessRecipe((ShapelessRecipes) recipe, aspectList, history);
            } else if (recipe instanceof ShapedOreRecipe) {
                optimizationsAndTweaks$processShapedOreRecipe((ShapedOreRecipe) recipe, aspectList, history);
            } else if (recipe instanceof ShapelessOreRecipe) {
                optimizationsAndTweaks$processShapelessOreRecipe((ShapelessOreRecipe) recipe, aspectList, history);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return aspectList;
    }

    @Unique
    private static void optimizationsAndTweaks$processShapedRecipe(ShapedRecipes recipe, AspectList aspectList, ArrayList<List> history) {
        if (recipe == null || recipe.recipeItems == null || recipe.recipeItems.length == 0) {
            // Handle null/invalid recipes more gracefully
            return;
        }

        int width = recipe.recipeWidth;
        ItemStack[] items = recipe.recipeItems;

        int calculatedWidth = Math.min(width, (int) Math.sqrt(items.length));

        for (int i = 0; i < calculatedWidth; i++) {
            for (int j = 0; j < calculatedWidth; j++) {
                // Calculate the index within the item array
                int index = i + j * calculatedWidth;

                // Bounds checking to prevent accessing invalid array indexes
                if (index >= 0 && index < items.length) {
                    ItemStack stack = items[index];
                    optimizationsAndTweaks$processItemStack(stack, aspectList, history);
                }
            }
        }
    }

    @Unique
    private static void optimizationsAndTweaks$processShapelessRecipe(
        ShapelessRecipes recipe, AspectList aspectList, ArrayList<List> history) {
        List<?> items = Collections.singletonList(recipe.recipeItems);
        for (Object item : items) {
            if (item instanceof ItemStack) {
                optimizationsAndTweaks$processItemStack((ItemStack) item, aspectList, history);
            } else if (item instanceof List<?>) {
                for (Object stack : (List<?>) item) {
                    if (stack instanceof ItemStack) {
                        optimizationsAndTweaks$processItemStack((ItemStack) stack, aspectList, history);
                    }
                }
            }
        }
    }

    @Unique
    private static void optimizationsAndTweaks$processShapedOreRecipe(ShapedOreRecipe recipe, AspectList aspectList, ArrayList<List> history) {
        Object input = recipe.getInput();

        if (input != null) {
            Object[] items = (Object[]) input;

            for (Object item : items) {
                if (item instanceof ItemStack) {
                    optimizationsAndTweaks$processItemStack((ItemStack) item, aspectList, history);
                } else if (item instanceof ArrayList) {
                    for (Object o : (ArrayList<?>) item) {
                        if (o instanceof ItemStack) {
                            optimizationsAndTweaks$processItemStack((ItemStack) o, aspectList, history);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private static void optimizationsAndTweaks$processShapelessOreRecipe(ShapelessOreRecipe recipe, AspectList aspectList, ArrayList<List> history) {
        List<?> items = Collections.singletonList(recipe.getInput());

        for (Object item : items) {
            if (item instanceof ItemStack) {
                optimizationsAndTweaks$processItemStack((ItemStack) item, aspectList, history);
            } else if (item instanceof ArrayList) {
                for (Object o : (ArrayList<?>) item) {
                    if (o instanceof ItemStack) {
                        optimizationsAndTweaks$processItemStack((ItemStack) o, aspectList, history);
                    }
                }
            }
        }
    }
    @Unique
    private static void optimizationsAndTweaks$refineAndSetMinValueAspect(AspectList ph, int minValue) {
        if (ph != null && ph.visSize() > 0) {
            List<Aspect> aspectsToRemove = new ArrayList<>();

            for (Aspect aspect : ph.getAspects()) {
                if (ph.getAmount(aspect) <= 0) {
                    aspectsToRemove.add(aspect);
                }
            }

            for (Aspect aspect : aspectsToRemove) {
                ph.remove(aspect);
            }

            if (ph.visSize() < minValue) {
                ph.copy();
                ph.visSize();
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static AspectList getObjectTags(ItemStack itemstack) {
        Item item;
        int meta;
        try {
            item = itemstack.getItem();
            meta = itemstack.getItemDamage();
        } catch (Exception var8) {
            return null;
        }

        AspectList tmp = ThaumcraftApi.objectTags.get(Arrays.asList((Object) item, meta));
        if (tmp == null) {
            Collection<List> col = ThaumcraftApi.objectTags.keySet();

            for (List l : col) {
                if (l.get(0) == item && l.get(1) instanceof int[]) {
                    int[] range = (int[]) l.get(1);
                    Arrays.sort(range);
                    if (Arrays.binarySearch(range, meta) >= 0) {
                        tmp = ThaumcraftApi.objectTags.get(Arrays.asList(item, range));
                        return tmp;
                    }
                }
            }

            tmp = ThaumcraftApi.objectTags.get(Arrays.asList((Object)item, 32767));
            if (tmp == null) {
                if (meta == 32767) {
                    int index = 0;

                    do {
                        tmp = ThaumcraftApi.objectTags.get(Arrays.asList((Object)item, index));
                        ++index;
                    } while(index < 16 && tmp == null);
                }

                if (tmp == null) {
                    tmp = generateTags(item, meta);
                }
            }
        }

        if (itemstack.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting)itemstack.getItem();
            if (tmp == null) {
                tmp = new AspectList();
            }

            tmp.merge(Aspect.MAGIC, (wand.getRod(itemstack).getCraftCost() + wand.getCap(itemstack).getCraftCost()) / 2);
            tmp.merge(Aspect.TOOL, (wand.getRod(itemstack).getCraftCost() + wand.getCap(itemstack).getCraftCost()) / 3);
        }

        if (item != null && item == Items.potionitem) {
            if (tmp == null) {
                tmp = new AspectList();
            }

            tmp.merge(Aspect.WATER, 1);
            ItemPotion ip = (ItemPotion)item;
            List effects = Collections.singletonList(ip.getEffects(itemstack.getItemDamage()));
            if (ItemPotion.isSplash(itemstack.getItemDamage())) {
                tmp.merge(Aspect.ENTROPY, 2);
            }

            for (Object effect : effects) {
                PotionEffect var6 = (PotionEffect) effect;
                tmp.merge(Aspect.MAGIC, (var6.getAmplifier() + 1) * 2);
                if (var6.getPotionID() == Potion.blindness.id) {
                    tmp.merge(Aspect.DARKNESS, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.confusion.id) {
                    tmp.merge(Aspect.ELDRITCH, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.damageBoost.id) {
                    tmp.merge(Aspect.WEAPON, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.digSlowdown.id) {
                    tmp.merge(Aspect.TRAP, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.digSpeed.id) {
                    tmp.merge(Aspect.TOOL, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.fireResistance.id) {
                    tmp.merge(Aspect.ARMOR, var6.getAmplifier() + 1);
                    tmp.merge(Aspect.FIRE, (var6.getAmplifier() + 1) * 2);
                } else if (var6.getPotionID() == Potion.harm.id) {
                    tmp.merge(Aspect.DEATH, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.heal.id) {
                    tmp.merge(Aspect.HEAL, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.hunger.id) {
                    tmp.merge(Aspect.DEATH, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.invisibility.id) {
                    tmp.merge(Aspect.SENSES, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.jump.id) {
                    tmp.merge(Aspect.FLIGHT, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.moveSlowdown.id) {
                    tmp.merge(Aspect.TRAP, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.moveSpeed.id) {
                    tmp.merge(Aspect.MOTION, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.nightVision.id) {
                    tmp.merge(Aspect.SENSES, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.poison.id) {
                    tmp.merge(Aspect.POISON, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.regeneration.id) {
                    tmp.merge(Aspect.HEAL, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.resistance.id) {
                    tmp.merge(Aspect.ARMOR, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.waterBreathing.id) {
                    tmp.merge(Aspect.AIR, (var6.getAmplifier() + 1) * 3);
                } else if (var6.getPotionID() == Potion.weakness.id) {
                    tmp.merge(Aspect.DEATH, (var6.getAmplifier() + 1) * 3);
                }
            }
        }

        return capAspects(tmp, 64);
    }
    @Shadow
    private static AspectList capAspects(AspectList sourcetags, int amount) {
        if (sourcetags == null) {
            return sourcetags;
        } else {
            AspectList out = new AspectList();
            Aspect[] arr$ = sourcetags.getAspects();

            for (Aspect aspect : arr$) {
                out.merge(aspect, Math.min(amount, sourcetags.getAmount(aspect)));
            }

            return out;
        }
    }
    @Shadow
    private static AspectList generateTagsFromCrucibleRecipes(Item item, int meta, ArrayList<List> history) {
        CrucibleRecipe cr = ThaumcraftApi.getCrucibleRecipe(new ItemStack(item, 1, meta));
        if (cr != null) {
            AspectList ot = cr.aspects.copy();
            int ss = cr.getRecipeOutput().stackSize;
            ItemStack cat = null;
            if (cr.catalyst instanceof ItemStack) {
                cat = (ItemStack)cr.catalyst;
            } else if (cr.catalyst instanceof ArrayList && !((ArrayList<?>) cr.catalyst).isEmpty()) {
                cat = (ItemStack)((ArrayList<?>)cr.catalyst).get(0);
            }

            assert cat != null;
            AspectList ot2 = generateTags(cat.getItem(), cat.getItemDamage());
            AspectList out = new AspectList();
            Aspect[] arr$;
            int len$;
            int i$;
            Aspect as;
            if (ot2 != null && ot2.size() > 0) {
                arr$ = ot2.getAspects();
                len$ = arr$.length;

                for(i$ = 0; i$ < len$; ++i$) {
                    as = arr$[i$];
                    out.add(as, ot2.getAmount(as));
                }
            }

            arr$ = ot.getAspects();
            len$ = arr$.length;

            for(i$ = 0; i$ < len$; ++i$) {
                as = arr$[i$];
                int amt = (int)(Math.sqrt(ot.getAmount(as)) / ss);
                out.add(as, amt);
            }

            arr$ = out.getAspects();
            len$ = arr$.length;

            for(i$ = 0; i$ < len$; ++i$) {
                as = arr$[i$];
                if (out.getAmount(as) <= 0) {
                    out.remove(as);
                }
            }

            return out;
        } else {
            return null;
        }
    }
    @Shadow
    private static AspectList generateTagsFromArcaneRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList ret = null;
        List recipeList = ThaumcraftApi.getCraftingRecipes();

        label173:
        for (Object o : recipeList) {
            if (o instanceof IArcaneRecipe) {
                IArcaneRecipe recipe = (IArcaneRecipe) o;
                if (recipe.getRecipeOutput() != null) {
                    int idR = recipe.getRecipeOutput().getItemDamage() == 32767 ? 0 : recipe.getRecipeOutput().getItemDamage();
                    int idS = Math.max(meta, 0);
                    if (recipe.getRecipeOutput().getItem() == item && idR == idS) {
                        ArrayList<ItemStack> ingredients = new ArrayList<>();
                        new AspectList();

                        try {
                            int i;
                            ItemStack is;
                            if (o instanceof ShapedArcaneRecipe) {
                                int width = ((ShapedArcaneRecipe) o).width;
                                Object[] items = ((ShapedArcaneRecipe) o).getInput();

                                for (i = 0; i < width && i < 3; ++i) {
                                    for (int j = 0; j < i; ++j) {
                                        if (items[i + j * width] != null) {
                                            ItemStack it;
                                            if (items[i + j * width] instanceof ArrayList) {

                                                for (Object object : (ArrayList) items[i + j * width]) {
                                                    it = (ItemStack) object;
                                                    if (Utils.isEETransmutionItem(it.getItem())) {
                                                        continue label173;
                                                    }

                                                    AspectList obj = generateTags(it.getItem(), it.getItemDamage(), history);
                                                    if (obj != null && obj.size() > 0) {
                                                        is = it.copy();
                                                        is.stackSize = 1;
                                                        ingredients.add(is);
                                                        break;
                                                    }
                                                }
                                            } else {
                                                is = (ItemStack) items[i + j * width];
                                                if (Utils.isEETransmutionItem(is.getItem())) {
                                                    continue label173;
                                                }

                                                it = is.copy();
                                                it.stackSize = 1;
                                                ingredients.add(it);
                                            }
                                        }
                                    }
                                }
                            } else if (o instanceof ShapelessArcaneRecipe) {
                                ArrayList items = ((ShapelessArcaneRecipe) o).getInput();

                                for (i = 0; i < items.size() && i < 9; ++i) {
                                    if (items.get(i) != null) {
                                        ItemStack it;
                                        if (items.get(i) instanceof ArrayList) {

                                            for (Object object : (ArrayList) items.get(i)) {
                                                it = (ItemStack) object;
                                                if (Utils.isEETransmutionItem(it.getItem())) {
                                                    continue label173;
                                                }

                                                AspectList obj = generateTags(it.getItem(), it.getItemDamage(), history);
                                                if (obj != null && obj.size() > 0) {
                                                    is = it.copy();
                                                    is.stackSize = 1;
                                                    ingredients.add(is);
                                                    break;
                                                }
                                            }
                                        } else {
                                            it = (ItemStack) items.get(i);
                                            if (Utils.isEETransmutionItem(it.getItem())) {
                                                continue label173;
                                            }

                                            it = it.copy();
                                            it.stackSize = 1;
                                            ingredients.add(it);
                                        }
                                    }
                                }
                            }

                            AspectList ph = getAspectsFromIngredients(ingredients, recipe.getRecipeOutput(), history);
                            Aspect[] arr$;
                            int i$;
                            Aspect as;
                            if (recipe.getAspects() != null) {
                                arr$ = recipe.getAspects().getAspects();
                                i = arr$.length;

                                for (i$ = 0; i$ < i; ++i$) {
                                    as = arr$[i$];
                                    ph.add(as, (int) (Math.sqrt(recipe.getAspects().getAmount(as)) / ((float) recipe.getRecipeOutput().stackSize)));
                                }
                            }

                            arr$ = ph.copy().getAspects();
                            i = arr$.length;

                            for (i$ = 0; i$ < i; ++i$) {
                                as = arr$[i$];
                                if (ph.getAmount(as) <= 0) {
                                    ph.remove(as);
                                }
                            }

                            ret = ph;
                        } catch (Exception var22) {
                            var22.printStackTrace();
                        }
                    }
                }
            }
        }

        return ret;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private static AspectList generateTagsFromInfusionRecipes(Item item, int meta, ArrayList<List> history) {
        InfusionRecipe cr = ThaumcraftApi.getInfusionRecipe(new ItemStack(item, 1, meta));
        if (cr == null) {
            return null;
        } else {
            AspectList ot = cr.getAspects().copy();
            ArrayList<ItemStack> ingredients = new ArrayList<>();
            ItemStack is = cr.getRecipeInput().copy();
            is.stackSize = 1;
            ingredients.add(is);

            ItemStack[] components = cr.getComponents();
            for (ItemStack cat : components) {
                ItemStack is2 = cat.copy();
                is2.stackSize = 1;
                ingredients.add(is2);
            }

            AspectList out = new AspectList();
            AspectList ot2 = getAspectsFromIngredients(ingredients, (ItemStack) cr.getRecipeOutput(), history);

            Aspect[] aspectsFromOt2 = ot2.getAspects();
            for (Aspect as : aspectsFromOt2) {
                out.add(as, ot2.getAmount(as));
            }

            Aspect[] aspectsFromOt = ot.getAspects();
            for (Aspect as : aspectsFromOt) {
                int amt = (int) (Math.sqrt(ot.getAmount(as)) / (double) ((ItemStack) cr.getRecipeOutput()).stackSize);
                out.add(as, amt);
            }

            Aspect[] outAspects = out.getAspects();
            for (Aspect as : outAspects) {
                if (out.getAmount(as) <= 0) {
                    out.remove(as);
                }
            }

            return out;
        }
    }
}
