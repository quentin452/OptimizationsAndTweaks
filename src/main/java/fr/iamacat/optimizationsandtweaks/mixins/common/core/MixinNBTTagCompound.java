package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(NBTTagCompound.class)
public abstract class MixinNBTTagCompound extends NBTBase {
    @Shadow
    private Map tagMap = new Object2ObjectHashMap();

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
