package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.*;
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

    // todo fix conflict(Object2ObjectHashMap) with crafttweaker
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    /** The key-value pairs for the tag. Each key is a UTF string, each value is a tag. */
    @Unique
    private Object2ObjectHashMap optimizationsAndTweaks$tagMap = new Object2ObjectHashMap();

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    @Redirect(
        method = "write",
        at = @At(value = "FIELD", target = "Lnet/minecraft/nbt/NBTTagCompound;tagMap:Ljava/util/Map;"))
    private java.util.Map redirectwrite(NBTTagCompound tagCompound, DataOutput output) throws IOException {
        return optimizationsAndTweaks$tagMap;
    }

    @Redirect(
        method = "func_152446_a",
        at = @At(value = "FIELD", target = "Lnet/minecraft/nbt/NBTTagCompound;tagMap:Ljava/util/Map;"))
    private java.util.Map redirectfunc_152446_a(NBTTagCompound compound, DataInput input, int depth,
        NBTSizeTracker sizeTracker) throws IOException {
        return optimizationsAndTweaks$tagMap;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Set func_150296_c() {
        return this.optimizationsAndTweaks$tagMap.keySet();
    }

    /**
     * Gets the type byte for the tag.
     */

    @Overwrite
    public byte getId() {
        return (byte) 10;
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
     */
    @Overwrite
    public void setTag(String key, NBTBase value) {
        this.optimizationsAndTweaks$tagMap.put(key, value);
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     */
    @Overwrite
    public void setByte(String key, byte value) {
        this.optimizationsAndTweaks$tagMap.put(key, new NBTTagByte(value));
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     */
    @Overwrite
    public void setShort(String key, short value) {
        this.optimizationsAndTweaks$tagMap.put(key, new NBTTagShort(value));
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     */
    @Overwrite
    public void setInteger(String key, int value) {
        this.optimizationsAndTweaks$tagMap.put(key, new NBTTagInt(value));
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     */
    @Overwrite
    public void setLong(String key, long value) {
        this.optimizationsAndTweaks$tagMap.put(key, new NBTTagLong(value));
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     */
    @Overwrite
    public void setFloat(String key, float value) {
        this.optimizationsAndTweaks$tagMap.put(key, new NBTTagFloat(value));
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     */
    @Overwrite
    public void setDouble(String key, double value) {
        this.optimizationsAndTweaks$tagMap.put(key, new NBTTagDouble(value));
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     */
    @Overwrite
    public void setString(String key, String value) {
        this.optimizationsAndTweaks$tagMap.put(key, new NBTTagString(value));
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
     */
    @Overwrite
    public void setByteArray(String key, byte[] value) {
        this.optimizationsAndTweaks$tagMap.put(key, new NBTTagByteArray(value));
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
     */
    @Overwrite
    public void setIntArray(String key, int[] value) {
        this.optimizationsAndTweaks$tagMap.put(key, new NBTTagIntArray(value));
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
     */
    @Overwrite
    public void setBoolean(String key, boolean value) {
        this.setByte(key, (byte) (value ? 1 : 0));
    }

    /**
     * gets a generic tag with the specified name
     */
    @Overwrite
    public NBTBase getTag(String key) {
        return (NBTBase) this.optimizationsAndTweaks$tagMap.get(key);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public byte func_150299_b(String key) {
        NBTBase nbtbase = (NBTBase) this.optimizationsAndTweaks$tagMap.get(key);
        return nbtbase != null ? nbtbase.getId() : 0;
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     */
    @Overwrite
    public boolean hasKey(String key) {
        return this.optimizationsAndTweaks$tagMap.containsKey(key);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean hasKey(String key, int type) {
        byte b0 = this.func_150299_b(key);
        return b0 == type ? true
            : (type != 99 ? false : b0 == 1 || b0 == 2 || b0 == 3 || b0 == 4 || b0 == 5 || b0 == 6);
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     */
    @Overwrite
    public byte getByte(String key) {
        try {
            return !this.optimizationsAndTweaks$tagMap.containsKey(key) ? 0
                : ((NBTBase.NBTPrimitive) this.optimizationsAndTweaks$tagMap.get(key)).func_150290_f();
        } catch (ClassCastException classcastexception) {
            return (byte) 0;
        }
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     */
    @Overwrite
    public short getShort(String key) {
        try {
            return !this.optimizationsAndTweaks$tagMap.containsKey(key) ? 0
                : ((NBTBase.NBTPrimitive) this.optimizationsAndTweaks$tagMap.get(key)).func_150289_e();
        } catch (ClassCastException classcastexception) {
            return (short) 0;
        }
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     */
    @Overwrite
    public int getInteger(String key) {
        try {
            return !this.optimizationsAndTweaks$tagMap.containsKey(key) ? 0
                : ((NBTBase.NBTPrimitive) this.optimizationsAndTweaks$tagMap.get(key)).func_150287_d();
        } catch (ClassCastException classcastexception) {
            return 0;
        }
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     */
    @Overwrite
    public long getLong(String key) {
        try {
            return !this.optimizationsAndTweaks$tagMap.containsKey(key) ? 0L
                : ((NBTBase.NBTPrimitive) this.optimizationsAndTweaks$tagMap.get(key)).func_150291_c();
        } catch (ClassCastException classcastexception) {
            return 0L;
        }
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     */
    @Overwrite
    public float getFloat(String key) {
        try {
            return !this.optimizationsAndTweaks$tagMap.containsKey(key) ? 0.0F
                : ((NBTBase.NBTPrimitive) this.optimizationsAndTweaks$tagMap.get(key)).func_150288_h();
        } catch (ClassCastException classcastexception) {
            return 0.0F;
        }
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     */
    @Overwrite
    public double getDouble(String key) {
        try {
            return !this.optimizationsAndTweaks$tagMap.containsKey(key) ? 0.0D
                : ((NBTBase.NBTPrimitive) this.optimizationsAndTweaks$tagMap.get(key)).func_150286_g();
        } catch (ClassCastException classcastexception) {
            return 0.0D;
        }
    }

    @Redirect(
        method = "getString",
        at = @At(value = "FIELD", target = "Lnet/minecraft/nbt/NBTTagCompound;tagMap:Ljava/util/Map;"))
    public java.util.Map redirectgetString(NBTTagCompound compound, String key) {
        return optimizationsAndTweaks$tagMap;
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
     */
    @Overwrite
    public byte[] getByteArray(String key) {
        try {
            return !this.optimizationsAndTweaks$tagMap.containsKey(key) ? new byte[0]
                : ((NBTTagByteArray) this.optimizationsAndTweaks$tagMap.get(key)).func_150292_c();
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.createCrashReport(key, 7, classcastexception));
        }
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
     */
    @Overwrite
    public int[] getIntArray(String key) {
        try {
            return !this.optimizationsAndTweaks$tagMap.containsKey(key) ? new int[0]
                : ((NBTTagIntArray) this.optimizationsAndTweaks$tagMap.get(key)).func_150302_c();
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.createCrashReport(key, 11, classcastexception));
        }
    }

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
     * stored.
     */
    @Overwrite
    public NBTTagCompound getCompoundTag(String key) {
        try {
            return !this.optimizationsAndTweaks$tagMap.containsKey(key) ? new NBTTagCompound()
                : (NBTTagCompound) this.optimizationsAndTweaks$tagMap.get(key);
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.createCrashReport(key, 10, classcastexception));
        }
    }

    /**
     * Gets the NBTTagList object with the given name. Args: name, NBTBase type
     */
    @Overwrite
    public NBTTagList getTagList(String key, int type) {
        try {
            if (this.func_150299_b(key) != 9) {
                return new NBTTagList();
            } else {
                NBTTagList nbttaglist = (NBTTagList) this.optimizationsAndTweaks$tagMap.get(key);
                return nbttaglist.tagCount() > 0 && nbttaglist.func_150303_d() != type ? new NBTTagList() : nbttaglist;
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.createCrashReport(key, 9, classcastexception));
        }
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
     * method.
     */
    @Overwrite
    public boolean getBoolean(String key) {
        return this.getByte(key) != 0;
    }

    /**
     * Remove the specified tag.
     */
    @Overwrite
    public void removeTag(String key) {
        this.optimizationsAndTweaks$tagMap.remove(key);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public String toString() {
        String s = "{";
        String s1;

        for (Iterator iterator = this.optimizationsAndTweaks$tagMap.keySet()
            .iterator(); iterator.hasNext(); s = s + s1 + ':' + this.optimizationsAndTweaks$tagMap.get(s1) + ',') {
            s1 = (String) iterator.next();
        }

        return s + "}";
    }

    /**
     * Return whether this compound has no tags.
     */
    @Overwrite
    public boolean hasNoTags() {
        return this.optimizationsAndTweaks$tagMap.isEmpty();
    }

    /**
     * Create a crash report which indicates a NBT read error.
     */
    @Overwrite
    private CrashReport createCrashReport(final String p_82581_1_, final int p_82581_2_,
        ClassCastException p_82581_3_) {
        CrashReport crashreport = CrashReport.makeCrashReport(p_82581_3_, "Reading NBT data");
        CrashReportCategory crashreportcategory = crashreport.makeCategoryDepth("Corrupt NBT tag", 1);
        crashreportcategory.addCrashSectionCallable(
            "Tag type found",
            () -> NBTBase.NBTTypes[((NBTBase) this.optimizationsAndTweaks$tagMap.get(p_82581_1_)).getId()]);
        crashreportcategory.addCrashSectionCallable("Tag type expected", () -> NBTBase.NBTTypes[p_82581_2_]);
        crashreportcategory.addCrashSection("Tag name", p_82581_1_);
        return crashreport;
    }

    /**
     * Creates a clone of the tag.
     */
    @Overwrite
    public NBTBase copy() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (Object o : this.optimizationsAndTweaks$tagMap.keySet()) {
            String s = (String) o;
            nbttagcompound.setTag(s, ((NBTBase) this.optimizationsAndTweaks$tagMap.get(s)).copy());
        }

        return nbttagcompound;
    }

    @Redirect(
        method = "equals",
        at = @At(value = "FIELD", target = "Lnet/minecraft/nbt/NBTTagCompound;tagMap:Ljava/util/Map;"))
    public java.util.Map redirectequals(NBTTagCompound compound, Object p_equals_1_) {
        return optimizationsAndTweaks$tagMap;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int hashCode() {
        return super.hashCode() ^ this.optimizationsAndTweaks$tagMap.hashCode();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static byte func_152447_a(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.func_152450_a(8);
        return input.readByte();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static String func_152448_b(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        return input.readUTF();
    }
}
