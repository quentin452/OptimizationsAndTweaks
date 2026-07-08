package fr.iamacat.optimizationsandtweaks.mixins.common.gadomancy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import makeo.gadomancy.common.crafting.InfusionVisualDisguiseArmor;

/**
 * Original {@code getResearchKey} does {@code result == null ? null : (String) result[0]} -- if
 * {@code getCraftingRecipeKey} ever returns a non-null but EMPTY array, {@code result[0]} throws
 * {@link ArrayIndexOutOfBoundsException}. The fix only adds a {@code result.length > 0} guard, so instead
 * of copying the whole method this wraps the {@code getCraftingRecipeKey} call and coerces an empty
 * result to {@code null}, letting the original (untouched) ternary take its existing null-branch.
 *
 * @author OptimizationsAndTweaks
 * @reason Avoid an ArrayIndexOutOfBoundsException when Thaumcraft's fake research-key handler returns an
 *         empty (non-null) array.
 */
@Mixin(InfusionVisualDisguiseArmor.class)
public class MixinInfusionVisualDisguiseArmor {

    @WrapOperation(
        method = "getResearchKey",
        at = @At(
            value = "INVOKE",
            target = "Lthaumcraft/api/ThaumcraftApi;getCraftingRecipeKey(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)[Ljava/lang/Object;"),
        remap = false)
    private static Object[] optimizationsandtweaks$nullifyEmptyResult(EntityPlayer player, ItemStack stack,
        Operation<Object[]> original) {
        Object[] result = original.call(player, stack);
        return (result != null && result.length == 0) ? null : result;
    }
}
