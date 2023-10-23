package fr.iamacat.optimizationsandtweaks.mixins.common.flaxbeardssteampower;

import java.lang.reflect.Field;
import java.util.*;

import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.entity.living.LivingEvent;

import org.apache.commons.lang3.tuple.MutablePair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import flaxbeard.steamcraft.Steamcraft;
import flaxbeard.steamcraft.SteamcraftItems;
import flaxbeard.steamcraft.api.Tuple3;
import flaxbeard.steamcraft.api.util.SPLog;
import flaxbeard.steamcraft.entity.ExtendedPropertiesVillager;
import flaxbeard.steamcraft.handler.SteamcraftEventHandler;
import flaxbeard.steamcraft.item.ItemExosuitArmor;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(SteamcraftEventHandler.class)
public class MixinSteamcraftEventHandler {

    @Shadow
    public static boolean lastViewVillagerGui;
    @Shadow
    public static int use;
    @Shadow
    boolean lastWearing = false;
    @Shadow
    boolean worldStartUpdate = false;
    @Shadow
    private SPLog log;
    @Shadow
    private static boolean isShiftDown;
    @Shadow
    private static Field lastBuyingPlayerField;
    @Shadow
    private static Field timeUntilResetField;
    @Shadow
    private static Field merchantField;
    private static Field buyingListField;
    @Shadow
    List<DamageSource> invalidSources;
    @Shadow
    private int sideHit;
    @Shadow
    public static HashMap<MutablePair<Integer, Tuple3>, Integer> quickLavaBlocks;
    @Shadow
    public static HashMap<MutablePair<EntityPlayer, Tuple3>, Integer> charges;
    @Shadow
    public static final int PEACEFUL_CHARGE = 240;
    @Shadow
    public static final int EASY_CHARGE_CAP = 280;
    @Shadow
    public static final int EASY_CHARGE_MIN = 160;
    @Shadow
    public static final int NORMAL_CHARGE_CAP = 320;
    @Shadow
    public static final int NORMAL_CHARGE_MIN = 120;
    @Shadow
    public static final int HARD_CHARGE_CAP = 360;
    @Shadow
    public static final int HARD_CHARGE_MIN = 80;
    @Shadow
    private static int[][] extraBlocksSide;
    @Shadow
    private static int[][] extraBlocksForward;
    @Shadow
    private static int[][] extraBlocksVertical;
    @Shadow
    private static int[][] extraBlocks9Side;
    @Shadow
    private static int[][] extraBlocks9Forward;
    @Shadow
    private static int[][] extraBlocks9Vertical;
    @Shadow
    private ArrayList<Material> LEAF_MATERIALS;

    @Inject(method = "updateVillagers", at = @At("HEAD"), cancellable = true, remap = false)
    public void updateVillagers(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (!OptimizationsandTweaksConfig.enableMixinSteamcraftEventHandler) {
            return;
        }

        if (!(event.entityLiving instanceof EntityVillager)) {
            return;
        }

        EntityVillager villager = (EntityVillager) event.entityLiving;

        if (timeUntilResetField != null && lastBuyingPlayerField != null) {
            Integer timeUntilReset = null;
            String lastBuyingPlayer = null;

            try {
                timeUntilReset = timeUntilResetField.getInt(villager);
                lastBuyingPlayer = (String) lastBuyingPlayerField.get(villager);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (!villager.isTrading() && timeUntilReset != null && timeUntilReset == 39 && lastBuyingPlayer != null) {
                updateHatLevel(villager, lastBuyingPlayer);
            }
        }

        if (!villager.worldObj.isRemote && buyingListField != null) {
            updateMerchantRecipeList(villager);
        }

        ci.cancel();
    }

    @Unique
    private void updateHatLevel(EntityVillager villager, String playerName) {
        EntityPlayer player = villager.worldObj.getPlayerEntityByName(playerName);
        if (player == null) {
            return;
        }

        ItemStack hat = player.inventory.armorInventory[3];
        if (hat != null && (hat.getItem() == SteamcraftItems.tophat || (hat.getItem() == SteamcraftItems.exoArmorHead
            && ((ItemExosuitArmor) Objects.requireNonNull(hat.getItem())).hasUpgrade(hat, SteamcraftItems.tophat)))) {
            if (!hat.hasTagCompound()) {
                hat.setTagCompound(new NBTTagCompound());
            }

            NBTTagCompound tagCompound = hat.getTagCompound();
            if (!tagCompound.hasKey("level")) {
                tagCompound.setInteger("level", 0);
            }

            int level = tagCompound.getInteger("level");
            level++;
            tagCompound.setInteger("level", level);

            if (hat.getItem() == SteamcraftItems.exoArmorHead) {
                ((ItemExosuitArmor) Objects.requireNonNull(hat.getItem()))
                    .setInventorySlotContents(player.inventory.armorInventory[3], 3, hat);
            }
        }
    }

    @Unique
    private void updateMerchantRecipeList(EntityVillager villager) {
        ExtendedPropertiesVillager nbt = (ExtendedPropertiesVillager) villager
            .getExtendedProperties(Steamcraft.VILLAGER_PROPERTY_ID);
        if (nbt.lastHadCustomer == null) {
            nbt.lastHadCustomer = false;
        }

        boolean hasCustomer = false;
        EntityPlayer customer = villager.getCustomer();
        if (customer != null) {
            ItemStack hat = customer.inventory.armorInventory[3];
            if (hat != null
                && (hat.getItem() == SteamcraftItems.tophat || (hat.getItem() == SteamcraftItems.exoArmorHead
                    && ((ItemExosuitArmor) hat.getItem()).hasUpgrade(hat, SteamcraftItems.tophat)))) {
                hasCustomer = true;
                if (!nbt.lastHadCustomer) {
                    MerchantRecipeList recipeList = villager.getRecipes(customer);
                    for (Object obj : recipeList) {
                        MerchantRecipe recipe = (MerchantRecipe) obj;
                        if (recipe.getItemToSell().stackSize > 1) {
                            recipe.getItemToSell().stackSize = MathHelper
                                .floor_float(recipe.getItemToSell().stackSize * 1.25F);
                        }
                        if (recipe.getItemToSell().stackSize > 1 && recipe.getItemToSell().stackSize
                            != MathHelper.floor_float((float) recipe.getItemToSell().stackSize * 1.25F)) {
                            recipe.getItemToSell().stackSize = MathHelper
                                .floor_float((float) recipe.getItemToSell().stackSize * 1.25F);
                        } else if (recipe.getItemToBuy().stackSize > 1 && recipe.getItemToBuy().stackSize
                            != MathHelper.ceiling_float_int((float) recipe.getItemToBuy().stackSize / 1.25F)) {
                                recipe.getItemToBuy().stackSize = MathHelper
                                    .ceiling_float_int((float) recipe.getItemToBuy().stackSize / 1.25F);
                            } else if (recipe.getSecondItemToBuy() != null && recipe.getSecondItemToBuy().stackSize > 1
                                && recipe.getSecondItemToBuy().stackSize != MathHelper
                                    .ceiling_float_int((float) recipe.getSecondItemToBuy().stackSize / 1.25F)) {
                                        recipe.getSecondItemToBuy().stackSize = MathHelper
                                            .ceiling_float_int((float) recipe.getSecondItemToBuy().stackSize / 1.25F);
                                    }
                    }

                    try {
                        buyingListField.set(villager, recipeList);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!hasCustomer && nbt.lastHadCustomer) {
                MerchantRecipeList recipeList = getMerchantRecipeList(villager);

                if (recipeList != null) {
                    for (Object obj : recipeList) {
                        MerchantRecipe recipe = (MerchantRecipe) obj;
                        if (recipe.getItemToSell().stackSize > 1 && recipe.getItemToSell().stackSize
                            != MathHelper.floor_float((float) recipe.getItemToSell().stackSize * 1.25F)) {
                            recipe.getItemToSell().stackSize = MathHelper
                                .floor_float((float) recipe.getItemToSell().stackSize * 1.25F);
                        } else if (recipe.getItemToBuy().stackSize > 1 && recipe.getItemToBuy().stackSize
                            != MathHelper.ceiling_float_int((float) recipe.getItemToBuy().stackSize / 1.25F)) {
                                recipe.getItemToBuy().stackSize = MathHelper
                                    .ceiling_float_int((float) recipe.getItemToBuy().stackSize / 1.25F);
                            } else if (recipe.getSecondItemToBuy() != null && recipe.getSecondItemToBuy().stackSize > 1
                                && recipe.getSecondItemToBuy().stackSize != MathHelper
                                    .ceiling_float_int((float) recipe.getSecondItemToBuy().stackSize / 1.25F)) {
                                        recipe.getSecondItemToBuy().stackSize = MathHelper
                                            .ceiling_float_int((float) recipe.getSecondItemToBuy().stackSize / 1.25F);
                                    }
                    }

                    try {
                        buyingListField.set(villager, recipeList);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            nbt.lastHadCustomer = hasCustomer;
        }
    }

    @Unique
    private MerchantRecipeList getMerchantRecipeList(EntityVillager villager) {
        try {
            return (MerchantRecipeList) buyingListField.get(villager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
