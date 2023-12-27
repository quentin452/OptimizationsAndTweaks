package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(Container.class)
public class MixinContainer {
    @Shadow
    public List inventorySlots = new ArrayList();

    @Overwrite
    public List getInventory() {
        ArrayList arraylist = new ArrayList();

        int slotsCount = this.inventorySlots.size();
        for (int i = 0; i < slotsCount; ++i) {
            Slot slot = (Slot) this.inventorySlots.get(i);
            if (slot != null) {
                ItemStack stack = slot.getStack();
                if (stack != null) {
                    arraylist.add(stack);
                }
            }
        }

        return arraylist;
    }
}

