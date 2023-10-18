package fr.iamacat.multithreading.mixins.common.nei;

import codechicken.core.CommonUtils;
import codechicken.core.ServerUtils;
import codechicken.nei.NEIActions;
import codechicken.nei.NEIServerConfig;
import codechicken.nei.NEIServerUtils;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NEIServerUtils.class)
public class MixinNEIServerUtils {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void advanceDisabledTimes(World world) {
        int dim = CommonUtils.getDimension(world);
        int hour = (int) (getTime(world) % 24000) / 1000;
        int timeZonesLength = NEIActions.timeZones.length;

        int newhour = hour;
        while (NEIServerConfig.isActionDisabled(dim, NEIActions.timeZones[newhour / 6])) {
            newhour = ((newhour / 6 + 1) % (timeZonesLength / 6)) * 6;
        }

        if (newhour != hour) {
            setHourForward(world, newhour, false);
        }
    }

    @Unique
    private static long getTime(World world) {
        return world.getWorldInfo().getWorldTime();
    }
    @Unique
    private static void setHourForward(World world, int hour, boolean notify) {
        long day = (getTime(world) / 24000L) * 24000L;
        long newTime = day + 24000L + hour * 1000;
        setTime(newTime, world);
        if (notify)
            ServerUtils.sendChatToAll(new ChatComponentTranslation("nei.chat.time", getTime(world) / 24000L, hour));
    }

    @Unique
    private static void setTime(long l, World world) {
        world.getWorldInfo().setWorldTime(l);
    }
}
