package fr.iamacat.optimizationsandtweaks.mixins.common.potionshards;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mod.posh.EventHandler;
import mod.posh.ModBlocks;
import mod.posh.ModItems;
import mod.posh.ModTools;

@Mixin(EventHandler.class)
public class MixinEventHandlerPotionShards {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester != null && event.harvester.getHeldItem() != null
            && event.harvester.getHeldItem()
                .getItem() == ModTools.cleansePickaxe) {
            Random rand = new Random();
            ItemStack drop = null;

            if (event.block == ModBlocks.slownessOre) {
                drop = new ItemStack(ModItems.speedShard, rand.nextInt(2) + 1);
            } else if (event.block == ModBlocks.miningFatigueOre) {
                drop = new ItemStack(ModItems.hasteShard, rand.nextInt(2) + 1);
            } else if (event.block == ModBlocks.instantDamageOre) {
                drop = new ItemStack(ModItems.healthShard, rand.nextInt(2) + 1);
            } else if (event.block == ModBlocks.poisonOre) {
                drop = new ItemStack(ModItems.regenerationShard, rand.nextInt(2) + 1);
            } else if (event.block == ModBlocks.witherOre) {
                drop = new ItemStack(ModItems.regenerationShard, rand.nextInt(2) + 1);
            } else if (event.block == ModBlocks.nauseaOre) {
                drop = new ItemStack(ModItems.nightVisionShard, rand.nextInt(2) + 1);
            } else if (event.block == ModBlocks.blindnessOre) {
                drop = new ItemStack(ModItems.nightVisionShard, rand.nextInt(2) + 1);
            } else if (event.block == ModBlocks.weaknessOre) {
                drop = new ItemStack(ModItems.strengthShard, rand.nextInt(2) + 1);
            } else if (event.block == ModBlocks.rainbowOre) {
                drop = new ItemStack(ModItems.rainbowShard, rand.nextInt(2) + 1);
            }

            if (drop != null) {
                event.drops.clear();
                event.drops.add(drop);
            }
        }
    }
}
