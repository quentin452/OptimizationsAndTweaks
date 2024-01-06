package fr.iamacat.optimizationsandtweaks.mixins.common.tconstruct;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import mantle.pulsar.pulse.Handler;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.Smeltery;
import tconstruct.plugins.gears.TinkerGears;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.util.config.PHConstruct;

@Mixin(TinkerGears.class)
public class MixinTinkerGears {

    @Shadow
    public static Item gearCast;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @Handler
    public void postInit(FMLPostInitializationEvent event) {
        ItemStack cast = new ItemStack(gearCast);
        FluidStack aluCastLiquid = new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue);
        FluidStack goldCastLiquid = null;
        if (!PHConstruct.removeGoldCastRecipes) {
            goldCastLiquid = new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2);
        }

        // find all gears in the registry
        for (String oreName : OreDictionary.getOreNames()) {
            if (!oreName.startsWith("gear")) continue;

            List<ItemStack> gears = OreDictionary.getOres(oreName);

            // register every gear besides wooden gear for creating a gear cast
            if (!oreName.equals("gearWood")) {
                for (ItemStack g : gears) {
                    TConstructRegistry.getTableCasting()
                        .addCastingRecipe(cast, aluCastLiquid, g, false, 50);
                    if (!PHConstruct.removeGoldCastRecipes) TConstructRegistry.getTableCasting()
                        .addCastingRecipe(cast, goldCastLiquid, g, false, 50);
                }
            }

            // Check if the gears list is empty before proceeding
            if (!gears.isEmpty()) {
                // find a fluid that fits the gear
                String material = oreName.substring(4);
                // try the oredict name directly
                Fluid fluid = FluidRegistry.getFluid(material);
                // or lowercased
                if (fluid == null) fluid = FluidRegistry.getFluid(material.toLowerCase());
                // or in the tinkers liquid format
                if (fluid == null) fluid = FluidRegistry.getFluid(material.toLowerCase() + ".molten");

                // found one?
                if (fluid != null) {
                    ItemStack gear = gears.get(0);
                    FluidStack liquid = new FluidStack(fluid.getID(), TConstruct.ingotLiquidValue * 4);
                    // gear casting
                    TConstructRegistry.getTableCasting()
                        .addCastingRecipe(gear, liquid, cast, 55);
                    // and melting it back
                    FluidType ft = FluidType.getFluidType(fluid);
                    if (ft != null) Smeltery.addMelting(ft, gear, 100, TConstruct.ingotLiquidValue * 4);
                    else Smeltery.addMelting(gear, TinkerSmeltery.glueBlock, 0, 100, liquid);
                }
            }
        }
    }
}
