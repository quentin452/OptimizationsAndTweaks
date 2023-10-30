package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ReportedException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

// avoid usage of locks and use ConcurrentHashMap instead of HashMap for more tps performances
@Mixin(DataWatcher.class)
public class MixinDataWatcher {

    @Shadow
    private final Entity field_151511_a;
    /** When isBlank is true the DataWatcher is not watching any objects */
    @Shadow
    private boolean isBlank = true;
    @Unique
    private static final Object2ObjectHashMap<Class, Integer> optimizationsAndTweaks$dataTypes = new Object2ObjectHashMap<>();
    @Unique
    private final Object2ObjectHashMap<Integer, DataWatcher.WatchableObject> optimizationsAndTweaks$watchedObjects = new Object2ObjectHashMap<>();

    /** true if one or more object was changed */
    @Shadow
    private boolean objectChanged;

    public MixinDataWatcher(Entity p_i45313_1_) {
        this.field_151511_a = p_i45313_1_;
    }

    /**
     * adds a new object to dataWatcher to watch, to update an already existing object see updateObject. Arguments: data
     * Value Id, Object to add
     */
    // @Inject(method = "addObject", at = @At("HEAD"), cancellable = true)
    public void addObject(int p_75682_1_, Object p_75682_2_, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinDataWatcher) {
            Integer integer = optimizationsAndTweaks$dataTypes.get(p_75682_2_.getClass());

            if (integer == null) {
                throw new IllegalArgumentException("Unknown data type: " + p_75682_2_.getClass());
            } else if (p_75682_1_ > 31) {
                throw new IllegalArgumentException(
                    "Data value id is too big with " + p_75682_1_ + "! (Max is " + 31 + ")");
            } else if (this.optimizationsAndTweaks$watchedObjects.containsKey(p_75682_1_)) {
                throw new IllegalArgumentException("Duplicate id value for " + p_75682_1_ + "!");
            } else {
                DataWatcher.WatchableObject watchableobject = new DataWatcher.WatchableObject(
                    integer,
                    p_75682_1_,
                    p_75682_2_);
                this.optimizationsAndTweaks$watchedObjects.put(p_75682_1_, watchableobject);
                this.isBlank = false;
            }
            ci.cancel();
        }
    }

    /**
     * Add a new object for the DataWatcher to watch, using the specified data type.
     */
    // @Inject(method = "addObjectByDataType", at = @At("HEAD"), cancellable = true)
    public void addObjectByDataType(int p_82709_1_, int p_82709_2_, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinDataWatcher) {
            DataWatcher.WatchableObject watchableobject = new DataWatcher.WatchableObject(p_82709_2_, p_82709_1_, null);
            this.optimizationsAndTweaks$watchedObjects.put(p_82709_1_, watchableobject);
            this.isBlank = false;
            ci.cancel();
        }
    }

    @Inject(method = "getWatchedObject", at = @At("HEAD"), cancellable = true)
    private DataWatcher.WatchableObject getWatchedObject(int p_75691_1_, CallbackInfoReturnable<Boolean> cir) {
        if (OptimizationsandTweaksConfig.enableMixinDataWatcher) {
            DataWatcher.WatchableObject watchableobject;

            try {
                watchableobject = this.optimizationsAndTweaks$watchedObjects.get(p_75691_1_);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting synched entity data");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Synched entity data");
                crashreportcategory.addCrashSection("Data ID", p_75691_1_);
                throw new ReportedException(crashreport);
            }
            return watchableobject;

        }
        cir.setReturnValue(false);
        return null;
    }

    // @Inject(method = "getChanged", at = @At("HEAD"), cancellable = true)
    public List<DataWatcher.WatchableObject> getChanged(CallbackInfoReturnable<Boolean> cir) {
        if (OptimizationsandTweaksConfig.enableMixinDataWatcher) {
            ArrayList<DataWatcher.WatchableObject> arraylist = null;

            if (this.objectChanged) {

                for (DataWatcher.WatchableObject watchableobject : this.optimizationsAndTweaks$watchedObjects
                    .values()) {
                    if (watchableobject.isWatched()) {
                        watchableobject.setWatched(false);

                        if (arraylist == null) {
                            arraylist = new ArrayList<>();
                        }

                        arraylist.add(watchableobject);
                    }
                }

            }

            this.objectChanged = false;
            return arraylist;

        }
        cir.setReturnValue(false);
        return null;
    }

    // @Inject(method = "func_151509_a", at = @At("HEAD"), cancellable = true)
    public void func_151509_a(PacketBuffer p_151509_1_, CallbackInfo ci) throws IOException {
        if (OptimizationsandTweaksConfig.enableMixinDataWatcher) {

            for (DataWatcher.WatchableObject watchableobject : this.optimizationsAndTweaks$watchedObjects.values()) {
                optimizationsAndTweaks$writeWatchableObjectToPacketBuffer(p_151509_1_, watchableobject);
            }

            p_151509_1_.writeByte(127);
            ci.cancel();
        }
    }

    // @Inject(method = "getAllWatched", at = @At("HEAD"), cancellable = true)
    public List<DataWatcher.WatchableObject> getAllWatched(CallbackInfoReturnable<Boolean> cir) {
        if (OptimizationsandTweaksConfig.enableMixinDataWatcher) {
            ArrayList<DataWatcher.WatchableObject> arraylist = null;
            DataWatcher.WatchableObject watchableobject;

            for (Iterator<DataWatcher.WatchableObject> iterator = this.optimizationsAndTweaks$watchedObjects.values()
                .iterator(); iterator.hasNext(); arraylist.add(watchableobject)) {
                watchableobject = iterator.next();

                if (arraylist == null) {
                    arraylist = new ArrayList<>();
                }
            }
            return arraylist;

        }
        cir.setReturnValue(false);
        return null;
    }

    /**
     * Writes a watchable object (entity attribute of type {byte, short, int, float, string, ItemStack,
     * ChunkCoordinates}) to the specified PacketBuffer
     */

    @Unique
    private static void optimizationsAndTweaks$writeWatchableObjectToPacketBuffer(PacketBuffer p_151510_0_,
        DataWatcher.WatchableObject p_151510_1_) throws IOException {
        int i = (p_151510_1_.getObjectType() << 5 | p_151510_1_.getDataValueId() & 31) & 255;
        p_151510_0_.writeByte(i);

        switch (p_151510_1_.getObjectType()) {
            case 0:
                p_151510_0_.writeByte((Byte) p_151510_1_.getObject());
                break;
            case 1:
                p_151510_0_.writeShort((Short) p_151510_1_.getObject());
                break;
            case 2:
                p_151510_0_.writeInt((Integer) p_151510_1_.getObject());
                break;
            case 3:
                p_151510_0_.writeFloat((Float) p_151510_1_.getObject());
                break;
            case 4:
                p_151510_0_.writeStringToBuffer((String) p_151510_1_.getObject());
                break;
            case 5:
                ItemStack itemstack = (ItemStack) p_151510_1_.getObject();
                p_151510_0_.writeItemStackToBuffer(itemstack);
                break;
            case 6:
                ChunkCoordinates chunkcoordinates = (ChunkCoordinates) p_151510_1_.getObject();
                p_151510_0_.writeInt(chunkcoordinates.posX);
                p_151510_0_.writeInt(chunkcoordinates.posY);
                p_151510_0_.writeInt(chunkcoordinates.posZ);
        }
    }

    // @Inject(method = "updateWatchedObjectsFromList", at = @At("HEAD"), cancellable = true)
    @SideOnly(Side.CLIENT)
    public void updateWatchedObjectsFromList(List<DataWatcher.WatchableObject> p_75687_1_, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinDataWatcher) {

            for (DataWatcher.WatchableObject watchableobject : p_75687_1_) {
                DataWatcher.WatchableObject watchableobject1 = this.optimizationsAndTweaks$watchedObjects
                    .get(watchableobject.getDataValueId());

                if (watchableobject1 != null) {
                    watchableobject1.setObject(watchableobject.getObject());
                    this.field_151511_a.func_145781_i(watchableobject.getDataValueId());
                }
            }

            this.objectChanged = true;
            ci.cancel();
        }
    }

    static {
        optimizationsAndTweaks$dataTypes.put(Byte.class, 0);
        optimizationsAndTweaks$dataTypes.put(Short.class, 1);
        optimizationsAndTweaks$dataTypes.put(Integer.class, 2);
        optimizationsAndTweaks$dataTypes.put(Float.class, 3);
        optimizationsAndTweaks$dataTypes.put(String.class, 4);
        optimizationsAndTweaks$dataTypes.put(ItemStack.class, 5);
        optimizationsAndTweaks$dataTypes.put(ChunkCoordinates.class, 6);
    }
}
