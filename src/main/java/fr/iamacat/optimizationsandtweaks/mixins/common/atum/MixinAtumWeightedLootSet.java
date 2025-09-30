package fr.iamacat.optimizationsandtweaks.mixins.common.atum;

import com.teammetallurgy.atum.items.AtumWeightedLootSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(AtumWeightedLootSet.class)
public class MixinAtumWeightedLootSet {

    @Inject(method = "getRandomLoot", at = @At("HEAD"), cancellable = true, remap = false)
    private void patchNullEnchantment(CallbackInfoReturnable<ItemStack> cir) {
        AtumWeightedLootSet self = (AtumWeightedLootSet)(Object)this;
        Random rand = new Random();
        int weight = rand.nextInt(self.totalWeight);
        ItemStack stack = null;

        Integer[] keys = self.loot.keySet().toArray(new Integer[0]);
        Arrays.sort(keys);

        for (Integer key : keys) {
            if (key >= weight) {
                stack = self.loot.get(key).copy();
                int min = self.lootMin.get(key);
                int max = self.lootMax.get(key);
                int amount = rand.nextInt(max - min + 1) + min;
                stack.stackSize = amount;

                if (stack.getItem() == Items.enchanted_book) {
                    Enchantment[] pool = Enchantment.enchantmentsList;
                    Enchantment enchantment = pool[rand.nextInt(pool.length)];

                    if (enchantment != null) {
                        int level = MathHelper.getRandomIntegerInRange(rand, enchantment.getMinLevel(), enchantment.getMaxLevel());
                        ((ItemEnchantedBook) stack.getItem()).addEnchantment(stack, new EnchantmentData(enchantment, level));
                    }
                }

                break;
            }
        }

        cir.setReturnValue(stack);
    }
}
