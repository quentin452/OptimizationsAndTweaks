package fr.iamacat.optimizationsandtweaks.mixins.common.ppap;

import com.laureegrd.ppapmod.init.ModItems;
import com.laureegrd.ppapmod.item.ItemPPAP;
import com.laureegrd.ppapmod.util.Config;
import com.laureegrd.ppapmod.util.PPAPEventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(PPAPEventHandler.class)
public class MixinPPAPEventHandler {

    private static Random rand = new Random();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onMobDrops(LivingDropsEvent event) {
        if (Config.disableDrops || event.source == null || !(event.source.getEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.source.getEntity();
        if (player.getHeldItem() == null || !(player.getHeldItem().getItem() instanceof ItemPPAP)) {
            return;
        }

        int probability = Config.musicDiscDropChance * (event.lootingLevel + 1);
        if (event.entity instanceof EntityMob && rand.nextInt(100) < probability) {
            Item heldItem = player.getHeldItem().getItem();
            if (!(heldItem instanceof ItemPPAP)) {
                return;
            }

            ItemPPAP sword = (ItemPPAP) heldItem;
            if (sword.getToolMaterialName().equals(ModItems.ppapToolMaterial.name())) {
                event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(ModItems.record)));
            } else if (sword.getToolMaterialName().equals(ModItems.ppapToolMaterialBig.name())) {
                event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(ModItems.recordLong)));
            }
        }
    }
}
