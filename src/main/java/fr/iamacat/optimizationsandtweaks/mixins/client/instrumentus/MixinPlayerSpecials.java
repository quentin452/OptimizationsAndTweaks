package fr.iamacat.optimizationsandtweaks.mixins.client.instrumentus;

import info.beanbot.morepaxels.client.player.PlayerSpecials;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(PlayerSpecials.class)
public class MixinPlayerSpecials {

    @Unique
    private static final Set<String> ALLOWED_NAMES = new HashSet<>(Arrays.asList("Beanxxbot", "TheDiscoCreeper", "Rajecent", "Hermyone"));

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private boolean nameIsGood(Entity entity) {
        String name = EnumChatFormatting.getTextWithoutFormattingCodes(entity.getCommandSenderName());
        return optimizationsAndTweaks$isNameAllowed(name);
    }

    @Unique
    private boolean optimizationsAndTweaks$isNameAllowed(String name) {
        return ALLOWED_NAMES.contains(name);
    }

}
