package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraftminusthaumcraft;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fox.spiteful.unthaumic.Unthaumic;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanManager;

@Mixin(Unthaumic.class)
public class MixinUnthaumic {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void join(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            NBTTagCompound tags = player.getEntityData();
            NBTTagCompound persist = tags.getCompoundTag("PlayerPersisted");

            if (!persist.getBoolean("Unthaumic")) {
                for (Aspect aspect : Aspect.getCompoundAspects()) {
                    Thaumcraft.proxy.playerKnowledge.addDiscoveredAspect(player.getCommandSenderName(), aspect);
                }

                for (Map.Entry<List, AspectList> entry : ThaumcraftApi.objectTags.entrySet()) {
                    if (entry.getValue() instanceof List && ((List<?>) entry.getValue()).size() == 2) {
                        List<?> list = (List<?>) entry.getValue();
                        if (list.get(0) instanceof Item) {
                            Item item = (Item) list.get(0);
                            if (list.get(1) instanceof Integer) {
                                int meta = (Integer) list.get(1);
                                if (meta == 32767) {
                                    for (meta = 0; meta < 16; ++meta) {
                                        ResearchManager.completeScannedObjectUnsaved(
                                            player.getCommandSenderName(),
                                            "@" + ScanManager.generateItemHash(item, meta));
                                    }
                                } else {
                                    ResearchManager.completeScannedObjectUnsaved(
                                        player.getCommandSenderName(),
                                        "@" + ScanManager.generateItemHash(item, meta));
                                }
                            } else if (list.get(1) instanceof int[]) {
                                int[] metas = (int[]) list.get(1);
                                for (int meta : metas) {
                                    ResearchManager.completeScannedObjectUnsaved(
                                        player.getCommandSenderName(),
                                        "@" + ScanManager.generateItemHash(item, meta));
                                }
                            }
                        }
                    }
                }

                ResearchManager.scheduleSave(player);
                persist.setBoolean("Unthaumic", true);
            }
        }
    }
}
