package fr.iamacat.optimizationsandtweaks.mixins.common.weathercarpet;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import mc.Mitchellbrine.anchormanMod.util.CloudChecking;

/**
 * Disables the version check from the Weather Carpet mod.
 */
@Mixin(CloudChecking.class)
public class MixinCloudChecking {

    @Overwrite
    public static boolean userValidation(EntityPlayer player) throws IOException {
        return false;
    }
}
