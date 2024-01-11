package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.HashMap;
import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Map<Integer, EnchantmentData> mapEnchantmentData(int enchantmentLevel, ItemStack itemStack) {
        Item item = itemStack.getItem();
        Map<Integer, EnchantmentData> enchantmentMap = null;
        boolean isBook = item == Items.book;
        Enchantment[] enchantments = Enchantment.enchantmentsList;

        for (Enchantment enchantment : enchantments) {
            if (enchantment == null) continue;

            if (enchantment.canApplyAtEnchantingTable(itemStack) || (isBook && enchantment.isAllowedOnBooks())) {
                for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); ++level) {
                    if (enchantmentLevel >= enchantment.getMinEnchantability(level) &&
                        enchantmentLevel <= enchantment.getMaxEnchantability(level)) {
                        if (enchantmentMap == null) {
                            enchantmentMap = new HashMap<>();
                        }

                        enchantmentMap.put(enchantment.effectId, new EnchantmentData(enchantment, level));
                    }
                }
            }
        }

        return enchantmentMap;
    }
}
