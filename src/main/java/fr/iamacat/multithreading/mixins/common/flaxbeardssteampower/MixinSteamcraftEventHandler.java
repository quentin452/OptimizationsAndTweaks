package fr.iamacat.multithreading.mixins.common.flaxbeardssteampower;

import flaxbeard.steamcraft.Steamcraft;
import flaxbeard.steamcraft.SteamcraftItems;
import flaxbeard.steamcraft.api.Tuple3;
import flaxbeard.steamcraft.api.util.SPLog;
import flaxbeard.steamcraft.entity.ExtendedPropertiesVillager;
import flaxbeard.steamcraft.handler.SteamcraftEventHandler;
import flaxbeard.steamcraft.item.ItemExosuitArmor;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.*;

@Mixin(SteamcraftEventHandler.class)
public class MixinSteamcraftEventHandler {
    @Shadow

    private static final UUID uuid = UUID.fromString("bbd786a9-611f-4c31-88ad-36dc9da3e15c");
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
        if (MultithreadingandtweaksConfig.enableMixinSteamcraftEventHandler){
        EntityVillager villager;
        EntityPlayer player;
        if (event.entityLiving instanceof EntityVillager && timeUntilResetField != null && lastBuyingPlayerField != null) {
            villager = (EntityVillager)event.entityLiving;
            Integer timeUntilReset = null;
            String lastBuyingPlayer = null;

            try {
                timeUntilReset = timeUntilResetField.getInt(villager);
                lastBuyingPlayer = (String)lastBuyingPlayerField.get(villager);
            } catch (IllegalAccessException var13) {
                var13.printStackTrace();
            }

            if (!villager.isTrading() && timeUntilReset != null && timeUntilReset == 39 && lastBuyingPlayer != null) {
                player = villager.worldObj.getPlayerEntityByName(lastBuyingPlayer);
                if (player != null) {
                    ItemStack hat;
                    int level;
                    if (player.inventory.armorInventory[3] != null && player.inventory.armorInventory[3].getItem() == SteamcraftItems.tophat) {
                        hat = player.inventory.armorInventory[3];
                        if (!hat.hasTagCompound()) {
                            hat.setTagCompound(new NBTTagCompound());
                        }

                        if (!hat.stackTagCompound.hasKey("level")) {
                            hat.stackTagCompound.setInteger("level", 0);
                        }

                        level = hat.stackTagCompound.getInteger("level");
                        ++level;
                        hat.stackTagCompound.setInteger("level", level);
                    } else if (player.inventory.armorInventory[3] != null && player.inventory.armorInventory[3].getItem() == SteamcraftItems.exoArmorHead && ((ItemExosuitArmor)player.inventory.armorInventory[3].getItem()).hasUpgrade(player.inventory.armorInventory[3], SteamcraftItems.tophat)) {
                        hat = ((ItemExosuitArmor)player.inventory.armorInventory[3].getItem()).getStackInSlot(player.inventory.armorInventory[3], 3);
                        if (!hat.hasTagCompound()) {
                            hat.setTagCompound(new NBTTagCompound());
                        }

                        if (!hat.stackTagCompound.hasKey("level")) {
                            hat.stackTagCompound.setInteger("level", 0);
                        }

                        level = hat.stackTagCompound.getInteger("level");
                        ++level;
                        hat.stackTagCompound.setInteger("level", level);
                        ((ItemExosuitArmor)player.inventory.armorInventory[3].getItem()).setInventorySlotContents(player.inventory.armorInventory[3], 3, hat);
                    }
                }
            }
        }

        if (event.entityLiving instanceof EntityVillager && !event.entityLiving.worldObj.isRemote && buyingListField != null) {
            villager = (EntityVillager)event.entityLiving;
            ExtendedPropertiesVillager nbt = (ExtendedPropertiesVillager)villager.getExtendedProperties(Steamcraft.VILLAGER_PROPERTY_ID);
            if (nbt.lastHadCustomer == null) {
                nbt.lastHadCustomer = false;
            }

            boolean hasCustomer = false;
            if (villager.getCustomer() != null && villager.getCustomer().inventory.armorInventory[3] != null && (villager.getCustomer().inventory.armorInventory[3].getItem() == SteamcraftItems.tophat || villager.getCustomer().inventory.armorInventory[3].getItem() == SteamcraftItems.exoArmorHead && ((ItemExosuitArmor)villager.getCustomer().inventory.armorInventory[3].getItem()).hasUpgrade(villager.getCustomer().inventory.armorInventory[3], SteamcraftItems.tophat))) {
                player = villager.getCustomer();
                hasCustomer = true;
                if (!nbt.lastHadCustomer) {
                    MerchantRecipeList recipeList = villager.getRecipes(player);
                    Iterator var19 = recipeList.iterator();

                    while(true) {
                        while(var19.hasNext()) {
                            Object obj = var19.next();
                            MerchantRecipe recipe = (MerchantRecipe)obj;
                            if (recipe.getItemToSell().stackSize > 1 && recipe.getItemToSell().stackSize != MathHelper.floor_float((float)recipe.getItemToSell().stackSize * 1.25F)) {
                                recipe.getItemToSell().stackSize = MathHelper.floor_float((float)recipe.getItemToSell().stackSize * 1.25F);
                            } else if (recipe.getItemToBuy().stackSize > 1 && recipe.getItemToBuy().stackSize != MathHelper.ceiling_float_int((float)recipe.getItemToBuy().stackSize / 1.25F)) {
                                recipe.getItemToBuy().stackSize = MathHelper.ceiling_float_int((float)recipe.getItemToBuy().stackSize / 1.25F);
                            } else if (recipe.getSecondItemToBuy() != null && recipe.getSecondItemToBuy().stackSize > 1 && recipe.getSecondItemToBuy().stackSize != MathHelper.ceiling_float_int((float)recipe.getSecondItemToBuy().stackSize / 1.25F)) {
                                recipe.getSecondItemToBuy().stackSize = MathHelper.ceiling_float_int((float)recipe.getSecondItemToBuy().stackSize / 1.25F);
                            }
                        }

                        try {
                            buyingListField.set(villager, recipeList);
                        } catch (IllegalAccessException var12) {
                            var12.printStackTrace();
                        }
                        break;
                    }
                }
            }

            if (!hasCustomer && nbt.lastHadCustomer) {
                MerchantRecipeList recipeList = null;

                try {
                    recipeList = (MerchantRecipeList)buyingListField.get(villager);
                } catch (IllegalAccessException var11) {
                    var11.printStackTrace();
                }

                if (recipeList != null) {
                    Iterator var18 = recipeList.iterator();

                    label129:
                    while(true) {
                        while(true) {
                            if (!var18.hasNext()) {
                                break label129;
                            }

                            Object obj = var18.next();
                            MerchantRecipe recipe = (MerchantRecipe)obj;
                            if (recipe.getItemToSell().stackSize > 1 && recipe.getItemToSell().stackSize != MathHelper.ceiling_float_int((float)recipe.getItemToSell().stackSize / 1.25F)) {
                                recipe.getItemToSell().stackSize = MathHelper.ceiling_float_int((float)recipe.getItemToSell().stackSize / 1.25F);
                            } else if (recipe.getItemToBuy().stackSize > 1 && recipe.getItemToBuy().stackSize != MathHelper.floor_float((float)recipe.getItemToBuy().stackSize * 1.25F)) {
                                recipe.getItemToBuy().stackSize = MathHelper.floor_float((float)recipe.getItemToBuy().stackSize * 1.25F);
                            } else if (recipe.getSecondItemToBuy() != null && recipe.getSecondItemToBuy().stackSize > 1 && recipe.getSecondItemToBuy().stackSize != MathHelper.floor_float
                                ((float)recipe.getSecondItemToBuy().stackSize * 1.25F)) {
                                recipe.getSecondItemToBuy().stackSize = MathHelper.floor_float((float)recipe.getSecondItemToBuy().stackSize * 1.25F);
                            }
                        }
                    }
                }

                try {
                    buyingListField.set(villager, recipeList);
                } catch (IllegalAccessException var10) {
                    var10.printStackTrace();
                }
            }

            nbt.lastHadCustomer = hasCustomer;
        }
        ci.cancel();
        }
    }
}
