package fr.iamacat.optimizationsandtweaks.mixins.common.nei;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import codechicken.core.ServerUtils;
import codechicken.nei.NEIActions;
import codechicken.nei.NEIServerUtils;

/**
 * Reduces TPS lags caused by NEIServerUtils from NEI.
 */
@Mixin(NEIServerUtils.class)
public class MixinNEIServerUtils {

    /**
     * @author iamacatfr
     * @reason the hardcoded {@code 4} group-count assumed exactly 4 six-hour time zone groups (24 total); derive it
     *         from {@code NEIActions.timeZones.length} instead so it stays correct if that array's size ever
     *         changes. Rest of the method (vanilla bytecode) is untouched.
     */
    @ModifyConstant(method = "advanceDisabledTimes", constant = @Constant(intValue = 4), remap = false)
    private static int optimizationsandtweaks$deriveTimeZoneGroupCount(int original) {
        return NEIActions.timeZones.length / 6;
    }

    @Shadow
    public static long getTime(World world) {
        return world.getWorldInfo()
            .getWorldTime();
    }

    @Shadow
    public static void setHourForward(World world, int hour, boolean notify) {
        long day = (getTime(world) / 24000L) * 24000L;
        long newTime = day + 24000L + hour * 1000L;
        setTime(newTime, world);
        if (notify)
            ServerUtils.sendChatToAll(new ChatComponentTranslation("nei.chat.time", getTime(world) / 24000L, hour));
    }

    @Shadow
    public static void setTime(long l, World world) {
        world.getWorldInfo()
            .setWorldTime(l);
    }
}
