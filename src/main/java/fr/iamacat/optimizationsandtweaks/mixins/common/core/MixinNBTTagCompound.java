package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.*;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.util.ReportedException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;

@Mixin(NBTTagCompound.class)
public abstract class MixinNBTTagCompound extends NBTBase {
    @Shadow
    private Map tagMap = new HashMap();

    @Shadow
    public boolean hasKey(String key)
    {
        return this.tagMap.containsKey(key);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean hasKey(String key, int type) {
        byte tagType = this.func_150299_b(key);

        if (type == 99) {
            return tagType == 1 || tagType == 2 || tagType == 3 || tagType == 4 || tagType == 5 || tagType == 6;
        }

        return tagType == type;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public byte func_150299_b(String key)
    {
        NBTBase nbtbase = (NBTBase)this.tagMap.get(key);
        return nbtbase != null ? nbtbase.getId() : 0;
    }

}
