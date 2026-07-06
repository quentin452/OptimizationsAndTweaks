package fr.iamacat.optimizationsandtweaks.mixins.client.salutation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraftforge.client.event.GuiOpenEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import speiger.src.salutation.Salutation;
import speiger.src.salutation.client.ClientHandler;
import speiger.src.salutation.client.gui.chat.ChatScreen;
import speiger.src.salutation.client.gui.chat.ISaluationChat;
import speiger.src.salutation.client.gui.chat.MPChatScreen;
import speiger.src.salutation.client.gui.chat.MultilineChatScreen;

@Mixin(ClientHandler.class)
public class MixinSalutationClientHandler {

    @Shadow
    boolean replacedChat = false;

    @SubscribeEvent
    @Overwrite(remap = false)
    public void onGuiOpen(GuiOpenEvent event) {
        if (!FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient()) return;
        GuiScreen screen = event.gui;
        Minecraft mc = Minecraft.getMinecraft();
        if (Loader.isModLoaded("chunkpregen")) return;
        boolean disable = Salutation.DISABLE_OVERRIDE.get();
        if (screen instanceof GuiMainMenu) {
            if (!replacedChat && !disable) {
                ReflectionHelper.setPrivateValue(
                    GuiIngame.class,
                    mc.ingameGUI,
                    new MultilineChatScreen(),
                    "persistantChatGUI",
                    "field_73840_e");
                replacedChat = true;
            } else if (replacedChat && disable) {
                ReflectionHelper.setPrivateValue(
                    GuiIngame.class,
                    mc.ingameGUI,
                    new GuiNewChat(mc),
                    "persistantChatGUI",
                    "field_73840_e");
                replacedChat = false;
            }
        } else if (!disable && screen instanceof GuiChat && !(screen instanceof ISaluationChat)) {
            if (screen instanceof GuiSleepMP) {
                event.setCanceled(true);
                mc.displayGuiScreen(new MPChatScreen());
            } else {
                event.setCanceled(true);
                mc.displayGuiScreen(new ChatScreen((GuiChat) screen));
            }
        }
    }
}
