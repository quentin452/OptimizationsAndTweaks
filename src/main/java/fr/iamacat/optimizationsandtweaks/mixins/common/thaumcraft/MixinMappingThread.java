package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.client.gui.GuiResearchRecipe;
import thaumcraft.client.gui.MappingThread;
import thaumcraft.common.lib.research.ScanManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(MappingThread.class)
public class MixinMappingThread {

    @Shadow
    Map<String, Integer> idMappings = null;
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void run() {
        for (Integer id : this.idMappings.values()) {
            try {
                Item item = Item.getItemById(id);
                if (item != null) {
                    List<ItemStack> subItems = new ArrayList<>();
                    item.getSubItems(item, item.getCreativeTab(), subItems);
                    subItems.forEach(stack -> GuiResearchRecipe.putToCache(ScanManager.generateItemHash(item, stack.getItemDamage()), stack.copy()));
                } else {
                    Block block = Block.getBlockById(id);
                    for (int a = 0; a < 16; ++a) {
                        GuiResearchRecipe.putToCache(ScanManager.generateItemHash(Item.getItemFromBlock(block), a), new ItemStack(block, 1, a));
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
