package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.resources.GZIPOutputStream2;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import static net.minecraft.nbt.CompressedStreamTools.write;

public class CompressTask {

    private final NBTTagCompound data;

    public CompressTask(NBTTagCompound data) {
        this.data = data;
    }
    private static final Executor namedExecutor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "CompressTaskThread");
        t.setDaemon(true);
        return t;
    });

    public CompletableFuture<byte[]> compressAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return compress(data);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, namedExecutor);
    }

    public static byte[] compress(NBTTagCompound p_74798_0_) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

        try (DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream2(bytearrayoutputstream))) {
            write(p_74798_0_, dataoutputstream);
        }
        return bytearrayoutputstream.toByteArray();
    }
}
