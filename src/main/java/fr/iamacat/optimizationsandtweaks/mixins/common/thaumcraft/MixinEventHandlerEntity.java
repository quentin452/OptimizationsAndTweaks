package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRepairableExtended;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.items.armor.ItemHoverHarness;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.WarpEvents;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.events.EventHandlerEntity;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.EntityUtils;

@Mixin(EventHandlerEntity.class)
public class MixinEventHandlerEntity {

    @Shadow
    public HashMap<Integer, Float> prevStep = new HashMap();
    @Shadow
    public static HashMap<String, ArrayList<WeakReference<Entity>>> linkedEntities = new HashMap();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void livingTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityPlayer)) {
            if (event.entity instanceof EntityMob && !event.entity.isDead) {
                EntityMob mob = (EntityMob) event.entity;
                int a = (int) mob.getEntityAttribute(EntityUtils.CHAMPION_MOD)
                    .getAttributeValue();
                if (a >= 0 && ChampionModifier.mods[a].type == 0) {
                    ChampionModifier.mods[a].effect.performEffect(mob, null, null, 0.0F);
                }
            }
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entity;
        World world = player.worldObj;

        if (world.isRemote) {
            optimizationsAndTweaks$handleStepHeight(player);
        } else {
            optimizationsAndTweaks$handleNonRemoteUpdates(player);
        }
    }

    @Unique
    private void optimizationsAndTweaks$handleNonRemoteUpdates(EntityPlayer player) {
        int ticksExisted = player.ticksExisted;

        if (!Config.wuss && ticksExisted > 0
            && ticksExisted % 2000 == 0
            && !player.isPotionActive(Config.potionWarpWardID)) {
            WarpEvents.checkWarpEvent(player);
        }

        if (ticksExisted % 10 == 0 && player.isPotionActive(Config.potionDeathGazeID)) {
            WarpEvents.checkDeathGaze(player);
        }

        if (ticksExisted % 40 == 0) {
            optimizationsAndTweaks$doItemRepairs(player);
        }
    }

    @Unique
    private void optimizationsAndTweaks$doItemRepairs(EntityPlayer player) {
        InventoryPlayer inventory = player.inventory;

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null && stack.getItemDamage() > 0
                && stack.getItem() instanceof IRepairable
                && !player.capabilities.isCreativeMode
                && (!(stack.getItem() instanceof ItemHoverHarness) || i >= InventoryPlayer.getHotbarSize())) {
                doRepair(stack, player);

            }
        }

        for (int i = 0; i < 4; ++i) {
            ItemStack armorStack = inventory.armorItemInSlot(i);

            if (armorStack != null && armorStack.getItemDamage() > 0
                && armorStack.getItem() instanceof IRepairable
                && !player.capabilities.isCreativeMode) {
                doRepair(armorStack, player);
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$handleStepHeight(EntityPlayer player) {
        if ((player.isSneaking() || player.inventory.armorItemInSlot(0) == null
            || player.inventory.armorItemInSlot(0)
                .getItem() != ConfigItems.itemBootsTraveller)
            && prevStep.containsKey(player.getEntityId())) {
            player.stepHeight = prevStep.get(player.getEntityId());
            prevStep.remove(player.getEntityId());
        }
    }

    @Shadow
    public static void doRepair(ItemStack is, EntityPlayer player) {
        int level = EnchantmentHelper.getEnchantmentLevel(Config.enchRepair.effectId, is);
        if (level > 0) {
            if (level > 2) {
                level = 2;
            }

            AspectList cost = ThaumcraftCraftingManager.getObjectTags(is);
            if (cost != null && cost.size() != 0) {
                cost = ResearchManager.reduceToPrimals(cost);
                AspectList finalCost = new AspectList();
                Aspect[] arr$ = cost.getAspects();
                int len$ = arr$.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    Aspect a = arr$[i$];
                    if (a != null) {
                        finalCost.merge(a, (int) Math.sqrt((double) (cost.getAmount(a) * 2)) * level);
                    }
                }

                if (is.getItem() instanceof IRepairableExtended) {
                    if (((IRepairableExtended) is.getItem()).doRepair(is, player, level)
                        && WandManager.consumeVisFromInventory(player, finalCost)) {
                        is.damageItem(-level, player);
                    }
                } else if (WandManager.consumeVisFromInventory(player, finalCost)) {
                    is.damageItem(-level, player);
                }

            }
        }
    }

    @Shadow
    private void updateSpeed(EntityPlayer player) {
        try {
            if (!player.capabilities.isFlying && player.inventory.armorItemInSlot(0) != null
                && player.moveForward > 0.0F) {
                int haste = EnchantmentHelper
                    .getEnchantmentLevel(Config.enchHaste.effectId, player.inventory.armorItemInSlot(0));
                if (haste > 0) {
                    float bonus = (float) haste * 0.015F;
                    if (player.isAirBorne) {
                        bonus /= 2.0F;
                    }

                    if (player.isInWater()) {
                        bonus /= 2.0F;
                    }

                    player.moveFlying(0.0F, 1.0F, bonus);
                }
            }
        } catch (Exception var4) {}

    }
}
