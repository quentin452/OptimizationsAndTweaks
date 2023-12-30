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
        return generateTags(item, meta, new ArrayList());
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static AspectList generateTags(Item item, int meta, ArrayList<List> history) {
        int tmeta = meta;

        try {
            tmeta = !Objects.requireNonNull((new ItemStack(item, 1, meta)).getItem()).isDamageable() && (new ItemStack(item, 1, meta)).getItem().getHasSubtypes() ? meta : 32767;
        } catch (Exception var5) {
        }

        if (ThaumcraftApi.exists(item, tmeta)) {
            return getObjectTags(new ItemStack(item, 1, tmeta));
        } else if (history.contains(Arrays.asList(item, tmeta))) {
            return null;
        } else {
            history.add(Arrays.asList(item, tmeta));
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
    private static AspectList generateTagsFromRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList ret;
        ret = generateTagsFromCrucibleRecipes(item, meta, history);
        if (ret != null) {
            return ret;
        } else {
            ret = generateTagsFromArcaneRecipes(item, meta, history);
            if (ret != null) {
                return ret;
            } else {
                ret = generateTagsFromInfusionRecipes(item, meta, history);
                if (ret != null) {
                    return ret;
                } else {
                    ret = generateTagsFromCraftingRecipes(item, meta, history);
                    return ret;
                }
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private static AspectList generateTagsFromCraftingRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList ret = null;
        int value = Integer.MAX_VALUE;
        List recipeList = CraftingManager.getInstance().getRecipeList();

        label216:
        for(int q = 0; q < recipeList.size(); ++q) {
            IRecipe recipe = (IRecipe)recipeList.get(q);
            if (recipe != null && recipe.getRecipeOutput() != null && Item.getIdFromItem(recipe.getRecipeOutput().getItem()) > 0 && recipe.getRecipeOutput().getItem() != null) {
                int idR = recipe.getRecipeOutput().getItemDamage() == 32767 ? 0 : recipe.getRecipeOutput().getItemDamage();
                int idS = meta == 32767 ? 0 : meta;
                if (recipe.getRecipeOutput().getItem() == item && idR == idS) {
                    ArrayList<ItemStack> ingredients = new ArrayList();
                    new AspectList();

                    try {
                        int width;
                        int i;
                        ItemStack is;
                        if (recipeList.get(q) instanceof ShapedRecipes) {
                            width = ((ShapedRecipes)recipeList.get(q)).recipeWidth;
                            i = ((ShapedRecipes)recipeList.get(q)).recipeHeight;
                            ItemStack[] items = ((ShapedRecipes)recipeList.get(q)).recipeItems;

                            for(i = 0; i < width && i < 3; ++i) {
                                for(int j = 0; j < i && j < 3; ++j) {
                                    if (items[i + j * width] != null) {
                                        if (Utils.isEETransmutionItem(items[i + j * width].getItem())) {
                                            continue label216;
                                        }

                                        is = items[i + j * width].copy();
                                        is.stackSize = 1;
                                        ingredients.add(is);
                                    }
                                }
                            }
                        } else {
                            ItemStack it;
                            if (recipeList.get(q) instanceof ShapelessRecipes) {
                                List<ItemStack> items = ((ShapelessRecipes)recipeList.get(q)).recipeItems;

                                for(i = 0; i < items.size() && i < 9; ++i) {
                                    if (items.get(i) != null) {
                                        if (Utils.isEETransmutionItem(items.get(i).getItem())) {
                                            continue label216;
                                        }

                                        it = items.get(i).copy();
                                        it.stackSize = 1;
                                        ingredients.add(it);
                                    }
                                }
                            } else {
                                if (recipeList.get(q) instanceof ShapedOreRecipe) {
                                    width = ((ShapedOreRecipe)recipeList.get(q)).getRecipeSize();
                                    Object[] items = ((ShapedOreRecipe)recipeList.get(q)).getInput();

                                    for(i = 0; i < width && i < 9; ++i) {
                                        if (items[i] != null) {
                                            if (items[i] instanceof ArrayList) {

                                                for (Object o : (ArrayList) items[i]) {
                                                    it = (ItemStack) o;
                                                    if (Utils.isEETransmutionItem(it.getItem())) {
                                                        continue label216;
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
                                                it = (ItemStack)items[i];
                                                if (Utils.isEETransmutionItem(it.getItem())) {
                                                    continue label216;
                                                }

                                                it = it.copy();
                                                it.stackSize = 1;
                                                ingredients.add(it);
                                            }
                                        }
                                    }
                                } else if (recipeList.get(q) instanceof ShapelessOreRecipe) {
                                    ArrayList items = ((ShapelessOreRecipe)recipeList.get(q)).getInput();

                                    for(i = 0; i < items.size() && i < 9; ++i) {
                                        if (items.get(i) != null) {
                                            if (items.get(i) instanceof ArrayList) {
                                                Iterator i$ = ((ArrayList)items.get(i)).iterator();

                                                while(i$.hasNext()) {
                                                    it = (ItemStack)i$.next();
                                                    if (Utils.isEETransmutionItem(it.getItem())) {
                                                        continue label216;
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
                                                it = (ItemStack)items.get(i);
                                                if (Utils.isEETransmutionItem(it.getItem())) {
                                                    continue label216;
                                                }

                                                it = it.copy();
                                                it.stackSize = 1;
                                                ingredients.add(it);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        AspectList ph = getAspectsFromIngredients(ingredients, recipe.getRecipeOutput(), history);
                        Aspect[] arr$ = ph.copy().getAspects();

                        for(i = 0; i < i; ++i) {
                            Aspect as = arr$[i];
                            if (ph.getAmount(as) <= 0) {
                                ph.remove(as);
                            }
                        }

                        if (ph.visSize() < value && ph.visSize() > 0) {
                            ret = ph;
                            value = ph.visSize();
                        }
                    } catch (Exception var20) {
                        var20.printStackTrace();
                    }
                }
            }
        }

        return ret;
    }
    @Shadow
    public static AspectList getObjectTags(ItemStack itemstack) {
        Item item;
        int meta;
        try {
            item = itemstack.getItem();
            meta = itemstack.getItemDamage();
        } catch (Exception var8) {
            return null;
        }

        AspectList tmp = ThaumcraftApi.objectTags.get(Arrays.asList(item, meta));
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

            tmp = ThaumcraftApi.objectTags.get(Arrays.asList(item, 32767));
            if (tmp == null && tmp == null) {
                if (meta == 32767 && tmp == null) {
                    int index = 0;

                    do {
                        tmp = ThaumcraftApi.objectTags.get(Arrays.asList(item, index));
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
            if (effects != null) {
                if (ItemPotion.isSplash(itemstack.getItemDamage())) {
                    tmp.merge(Aspect.ENTROPY, 2);
                }

                Iterator var5 = effects.iterator();

                while(var5.hasNext()) {
                    PotionEffect var6 = (PotionEffect)var5.next();
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
            int len$ = arr$.length;

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
            } else if (cr.catalyst instanceof ArrayList && ((ArrayList<?>)cr.catalyst).size() > 0) {
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
                int amt = (int)(Math.sqrt((double)ot.getAmount(as)) / (double)ss);
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
        int value = 0;
        List recipeList = ThaumcraftApi.getCraftingRecipes();

        label173:
        for(int q = 0; q < recipeList.size(); ++q) {
            if (recipeList.get(q) instanceof IArcaneRecipe) {
                IArcaneRecipe recipe = (IArcaneRecipe)recipeList.get(q);
                if (recipe.getRecipeOutput() != null) {
                    int idR = recipe.getRecipeOutput().getItemDamage() == 32767 ? 0 : recipe.getRecipeOutput().getItemDamage();
                    int idS = meta < 0 ? 0 : meta;
                    if (recipe.getRecipeOutput().getItem() == item && idR == idS) {
                        ArrayList<ItemStack> ingredients = new ArrayList();
                        new AspectList();
                        int cval = 0;

                        try {
                            int i;
                            ItemStack is;
                            if (recipeList.get(q) instanceof ShapedArcaneRecipe) {
                                int width = ((ShapedArcaneRecipe)recipeList.get(q)).width;
                                i = ((ShapedArcaneRecipe)recipeList.get(q)).height;
                                Object[] items = ((ShapedArcaneRecipe)recipeList.get(q)).getInput();

                                for(i = 0; i < width && i < 3; ++i) {
                                    for(int j = 0; j < i && j < 3; ++j) {
                                        if (items[i + j * width] != null) {
                                            ItemStack it;
                                            if (items[i + j * width] instanceof ArrayList) {
                                                Iterator i$ = ((ArrayList)items[i + j * width]).iterator();

                                                while(i$.hasNext()) {
                                                    it = (ItemStack)i$.next();
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
                                                is = (ItemStack)items[i + j * width];
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
                            } else if (recipeList.get(q) instanceof ShapelessArcaneRecipe) {
                                ArrayList items = ((ShapelessArcaneRecipe)recipeList.get(q)).getInput();

                                for(i = 0; i < items.size() && i < 9; ++i) {
                                    if (items.get(i) != null) {
                                        ItemStack it;
                                        if (items.get(i) instanceof ArrayList) {
                                            Iterator i$ = ((ArrayList)items.get(i)).iterator();

                                            while(i$.hasNext()) {
                                                it = (ItemStack)i$.next();
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
                                            it = (ItemStack)items.get(i);
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

                                for(i$ = 0; i$ < i; ++i$) {
                                    as = arr$[i$];
                                    ph.add(as, (int)(Math.sqrt((double)recipe.getAspects().getAmount(as)) / (double)((float)recipe.getRecipeOutput().stackSize)));
                                }
                            }

                            arr$ = ph.copy().getAspects();
                            i = arr$.length;

                            for(i$ = 0; i$ < i; ++i$) {
                                as = arr$[i$];
                                if (ph.getAmount(as) <= 0) {
                                    ph.remove(as);
                                }
                            }

                            if (cval >= value) {
                                ret = ph;
                                value = cval;
                            }
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
