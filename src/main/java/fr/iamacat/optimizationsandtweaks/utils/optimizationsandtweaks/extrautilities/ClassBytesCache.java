package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.extrautilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.launchwrapper.LaunchClassLoader;

public class ClassBytesCache {

    private static final Map<String, byte[]> classBytesCache = new ConcurrentHashMap<>();

    public static byte[] getClassBytes(ClassLoader classLoader, String className) throws IOException {
        byte[] bytes = classBytesCache.get(className);
        if (bytes == null) {
            if (classLoader instanceof LaunchClassLoader) {
                bytes = ((LaunchClassLoader) classLoader).getClassBytes(className);
            } else if (classLoader != null) {
                bytes = readAllBytes(Objects.requireNonNull(classLoader.getResourceAsStream(className)));
            } else {
                throw new IOException("Class loader is null");
            }
            classBytesCache.put(className, bytes);
        }
        return bytes;
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[inputStream.available()];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
            int available = inputStream.available();
            if (available > 0) {
                byte[] newData = new byte[buffer.size() + available];
                System.arraycopy(buffer.toByteArray(), 0, newData, 0, buffer.size());
                data = newData;
            }
        }

        buffer.flush();
        return buffer.toByteArray();
    }
}
