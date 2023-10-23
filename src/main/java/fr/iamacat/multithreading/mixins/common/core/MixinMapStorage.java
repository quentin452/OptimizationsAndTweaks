package fr.iamacat.multithreading.mixins.common.core;

import java.io.File;
import java.io.FileOutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(MapStorage.class)
public class MixinMapStorage {

    @Shadow
    private ISaveHandler saveHandler;

    @Inject(method = "saveData", at = @At("HEAD"), remap = false, cancellable = true)
    private void saveData(WorldSavedData p_75747_1_, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinMapStorage) {
            if (this.saveHandler != null) {
                try {
                    File file1 = this.saveHandler.getMapFileFromName(p_75747_1_.mapName);

                    if (file1 != null) {
                        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                        NBTTagList nbtList = new NBTTagList();
                        NBTTagCompound nbttagcompound = new NBTTagCompound();
                        p_75747_1_.writeToNBT(nbttagcompound);
                        nbtList.appendTag(nbttagcompound);
                        nbttagcompound1.setTag("data", nbtList);

                        // Compress and save the data to the file
                        FileOutputStream fileoutputstream = new FileOutputStream(file1);
                        CompressedStreamTools.writeCompressed(nbttagcompound1, fileoutputstream);
                        fileoutputstream.close();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            ci.cancel();
        }
    }
}
