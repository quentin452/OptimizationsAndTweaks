package fr.iamacat.multithreading.mixins.common.core;

import cpw.mods.fml.common.FMLLog;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.logging.Logger;
@Mixin(ForgeInternalHandler.class)
public class MixinGodZillaFix {

        @Redirect(method = "onEntityJoinWorld", at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/FMLLog;warning(Ljava/lang/String;[Ljava/lang/Object;)V"), remap = false)
        private void onEntityJoinWorldRedirect(String message, Object[] params, EntityJoinWorldEvent event) {
            if (event.entity instanceof EntityItem && MultithreadingandtweaksMultithreadingConfig.enableMixinGodZillaFix) {
                EntityItem entityItem = (EntityItem) event.entity;
                ItemStack itemStack = entityItem.getEntityItem();

                if (itemStack == null || itemStack.getItem() == null) {
                    return;
                }
            } else {
                FMLLog.warning(message, params);
            }
        }
    }
