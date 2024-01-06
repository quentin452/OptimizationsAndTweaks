package fr.iamacat.optimizationsandtweaks.mixins.common.gadomancy;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.Classers;
import makeo.gadomancy.common.crafting.InfusionVisualDisguiseArmor;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.IInternalMethodHandler;

@Mixin(InfusionVisualDisguiseArmor.class)
public class MixinInfusionVisualDisguiseArmor {

    @Unique
    private static final IInternalMethodHandler FAKE_HANDLER_2 = new Classers.FakeMethodHandler();

    /**
     * @author
     * @reason
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    private static String getResearchKey(ItemStack stack) {
        IInternalMethodHandler old = ThaumcraftApi.internalMethods;
        ThaumcraftApi.internalMethods = FAKE_HANDLER_2;
        Object[] result = ThaumcraftApi.getCraftingRecipeKey(Minecraft.getMinecraft().thePlayer, stack);
        ThaumcraftApi.internalMethods = old;

        // Check if the result is not null and has at least one element
        if (result != null && result.length > 0) {
            return (String) result[0];
        } else {
            return null;
        }
    }
}
