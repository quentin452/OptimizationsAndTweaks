package fr.iamacat.optimizationsandtweaks.mixins.common.industrialupgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.denfop.IUItem;
import com.denfop.register.RegisterOreDict;

@Mixin(RegisterOreDict.class)
public class MixinRegisterOreDict {

    @Inject(method = "oredict", at = @At("RETURN"), remap = false)
    private static void oredict(CallbackInfo ci) {
        OreDictionary.registerOre(
            "oreAluminum",
            new ItemStack(
                Item.getItemFromBlock(IUItem.netherore1)
                    .setUnlocalizedName("netherore1"),
                1,
                2));
        OreDictionary.registerOre(
            "oreAluminum",
            new ItemStack(
                Item.getItemFromBlock(IUItem.endore1)
                    .setUnlocalizedName("endore1"),
                1,
                2));
    }
}
