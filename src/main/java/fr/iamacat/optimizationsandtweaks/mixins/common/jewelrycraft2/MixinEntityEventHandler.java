package fr.iamacat.optimizationsandtweaks.mixins.common.jewelrycraft2;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import darkknight.jewelrycraft.JewelrycraftMod;
import darkknight.jewelrycraft.api.Curse;
import darkknight.jewelrycraft.config.ConfigHandler;
import darkknight.jewelrycraft.events.EntityEventHandler;
import darkknight.jewelrycraft.item.ItemList;
import darkknight.jewelrycraft.network.PacketRequestPlayerInfo;
import darkknight.jewelrycraft.util.BlockUtils;
import darkknight.jewelrycraft.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

@Mixin(EntityEventHandler.class)
public class MixinEntityEventHandler {
    @Shadow
    int updateTime = 0;
    @Shadow
    int totalUnavailableCurses = 0;
    @Shadow
    int luck = 0;
    @Shadow
    boolean addedCurses = false;
    @Shadow
    public static Random rand = new Random();
    @Shadow
    public static ArrayList<String> types = new ArrayList();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer && !(event.entity instanceof EntityPlayerMP)) {
            JewelrycraftMod.netWrapper.sendToServer(new PacketRequestPlayerInfo());
        }

        Entity entity = event.entity;
        if (!event.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            NBTTagCompound persistTag = PlayerUtils.getModPlayerPersistTag(player, "jewelrycraft2");
           /* boolean shouldGiveManual = ItemList.guide != null && !persistTag.getBoolean("givenGuide");
            if (shouldGiveManual) {
                ItemStack manual = new ItemStack(ItemList.guide);
                if (!player.inventory.addItemStackToInventory(manual)) {
                    BlockUtils.dropItemStackInWorld(player.worldObj, player.posX, player.posY, player.posZ, manual);
                }

                persistTag.setBoolean("givenGuide", true);
            }

            */

            boolean render = persistTag.getBoolean("fancyRender");
            JewelrycraftMod.fancyRender = render;
            Iterator var7;
            Curse curse;
            if (ConfigHandler.CURSES_ENABLED) {
                var7 = Curse.getCurseList().iterator();

                while(var7.hasNext()) {
                    curse = (Curse)var7.next();
                    if (curse.canCurseBeActivated(event.world) && !persistTag.hasKey(curse.getName())) {
                        persistTag.setInteger(curse.getName(), 0);
                    }
                }
            }

            var7 = Curse.getCurseList().iterator();

            while(var7.hasNext()) {
                curse = (Curse)var7.next();
                if (!curse.canCurseBeActivated(event.world)) {
                    Curse.availableCurses.remove(curse);
                    persistTag.setInteger(curse.getName(), 0);
                    ++this.totalUnavailableCurses;
                } else if (!Curse.availableCurses.contains(curse)) {
                    Curse.availableCurses.add(curse);
                }
            }

            persistTag.setBoolean("sendInfo", true);
            this.luck = this.calculateLuck((EntityPlayer)entity);
        }

        if (ConfigHandler.CAN_RED_HEARTS_SPAWN) {
            types.add("Red");
        }

        if (ConfigHandler.CAN_BLUE_HEARTS_SPAWN) {
            types.add("Blue");
        }

        if (ConfigHandler.CAN_HOLY_HEARTS_SPAWN) {
            types.add("White");
        }

        if (ConfigHandler.CAN_BLACK_HEARTS_SPAWN) {
            types.add("Black");
        }
    }
    @Shadow
    public int calculateLuck(EntityPlayer player) {
        int luck = 0;
        NBTTagCompound playerInfo = PlayerUtils.getModPlayerPersistTag(player, "jewelrycraft2");
        if (ConfigHandler.CURSES_ENABLED) {
            Iterator var4 = Curse.getCurseList().iterator();

            while(var4.hasNext()) {
                Curse curse = (Curse)var4.next();
                if (curse.canCurseBeActivated(player.worldObj) && playerInfo.getInteger(curse.getName()) > 0) {
                    luck += curse.luck();
                }
            }
        }

        return luck;
    }
}
