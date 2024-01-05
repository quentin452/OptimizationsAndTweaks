package fr.iamacat.optimizationsandtweaks.mixins.common.weathercarpet;

import mc.Mitchellbrine.anchormanMod.util.CloudChecking;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.IOException;

@Mixin(CloudChecking.class)
public class MixinCloudChecking {
    @Overwrite
    public static boolean userValidation(EntityPlayer player) throws IOException {
        return false;
    }
}
