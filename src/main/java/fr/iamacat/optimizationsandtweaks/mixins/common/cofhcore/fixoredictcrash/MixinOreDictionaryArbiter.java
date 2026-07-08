package fr.iamacat.optimizationsandtweaks.mixins.common.cofhcore.fixoredictcrash;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.core.util.oredict.OreDictionaryArbiterProxy;
import cofh.lib.util.ItemWrapper;
import cofh.lib.util.helpers.ItemHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

@Mixin(OreDictionaryArbiter.class)
public class MixinOreDictionaryArbiter {

    @Shadow
    private static BiMap<String, Integer> oreIDs;
    @Shadow
    private static TMap<Integer, ArrayList<ItemStack>> oreStacks;
    @Shadow
    private static TMap<ItemWrapper, ArrayList<Integer>> stackIDs;
    @Shadow
    private static TMap<ItemWrapper, ArrayList<String>> stackNames;
    @Shadow
    private static String[] oreNames;

    @Unique
    private static boolean optimizationsAndTweaks$initialized = false;

    /**
     * @reason fixing a memory leak by adding a boolean guard. HEAD-cancel instead of a full-method replace
     *         so any other transform on this method still applies.
     */
    @Inject(method = "initialize", at = @At("HEAD"), remap = false, cancellable = true)
    private static void initialize(CallbackInfo ci) {
        if (!optimizationsAndTweaks$initialized) {
            oreIDs = HashBiMap.create(32);
            oreStacks = new THashMap<>(32);
            stackIDs = new THashMap<>(32);
            stackNames = new THashMap<>(32);
            oreNames = OreDictionary.getOreNames();

            for (String oreName : oreNames) {
                ArrayList<ItemStack> var1 = OreDictionary.getOres(oreName);

                for (ItemStack itemStack : var1) {
                    OreDictionaryArbiter.registerOreDictionaryEntry(itemStack, oreName);
                }
            }

            for (ItemWrapper var4 : stackIDs.keySet()) {
                if (var4.metadata != 32767) {
                    ItemWrapper var5 = new ItemWrapper(var4.item, 32767);
                    if (stackIDs.containsKey(var5)) {
                        stackIDs.get(var4)
                            .addAll(stackIDs.get(var5));
                        stackNames.get(var4)
                            .addAll(stackNames.get(var5));
                    }
                }
            }

            ItemHelper.oreProxy = new OreDictionaryArbiterProxy();

            optimizationsAndTweaks$initialized = true;
        }
        ci.cancel();
    }

    /**
     * @reason fixing https://github.com/quentin452/privates-minecraft-modpack/issues/353 (NPE/crash guard on
     *         invalid stacks). HEAD-cancel instead of a full-method replace so any other transform on this
     *         method (e.g. Hodgepodge's own NPE guard) still applies.
     */
    @Inject(method = "registerOreDictionaryEntry", at = @At("HEAD"), remap = false, cancellable = true)
    private static void registerOreDictionaryEntry(ItemStack var0, String var1, CallbackInfo ci) {
        if (true && (var0 != null && var0.getItem() != null && !Strings.isNullOrEmpty(var1))) {
            int var2 = OreDictionary.getOreID(var1);
            oreIDs.put(var1, var2);
            if (oreStacks == null) {
                oreStacks = new THashMap<>(32);
            }
            if (stackIDs == null) {
                stackIDs = new THashMap<>(32);
            }
            if (stackNames == null) {
                stackNames = new THashMap<>(32);
            }
            try {
                if (!oreStacks.containsKey(var2)) {
                    oreStacks.put(var2, new ArrayList<>());
                }
                oreStacks.get(var2)
                    .add(var0);

                ItemWrapper var3 = ItemWrapper.fromItemStack(var0);
                if (!stackIDs.containsKey(var3)) {
                    stackIDs.put(var3, new ArrayList<>());
                    stackNames.put(var3, new ArrayList<>());
                }
                stackIDs.get(var3)
                    .add(var2);
                stackNames.get(var3)
                    .add(var1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ci.cancel();
    }
}
