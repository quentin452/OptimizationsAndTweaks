package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.resources.GZIPInputStream2;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NBTReadThread extends Thread {

    private final InputStream inputStream;
    private final BlockingQueue<NBTTagCompound> resultQueue;

    public NBTReadThread(InputStream inputStream) {
        this.inputStream = inputStream;
        this.resultQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        try (DataInputStream datainputstream = new DataInputStream(
            new BufferedInputStream(new GZIPInputStream2(inputStream)))) {
            NBTTagCompound nbttagcompound = CompressedStreamTools.func_152456_a(datainputstream,
                NBTSizeTracker.field_152451_a);
            resultQueue.offer(nbttagcompound);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NBTTagCompound getResult() {
        try {
            return resultQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
