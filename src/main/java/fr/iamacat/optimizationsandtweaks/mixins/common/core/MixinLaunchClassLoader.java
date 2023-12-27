package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import net.minecraft.launchwrapper.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;
// todo fix : it seem that the mixin isn't loaded in game
@Mixin(LaunchClassLoader.class)
public abstract class MixinLaunchClassLoader extends URLClassLoader {

    @Shadow
    public static final int BUFFER_SIZE = 1 << 12;
    @Unique
    private ArrayList<URL> optimizationsAndTweaks$sources;
    @Shadow
    private ClassLoader parent = getClass().getClassLoader();
    @Shadow

    private List<IClassTransformer> transformers = new ArrayList<IClassTransformer>(2);
    @Unique
    private Object2ObjectHashMap<String, Class<?>> optimizationsAndTweaks$cachedClasses = new Object2ObjectHashMap<>();
    @Shadow
    private Set<String> invalidClasses = new HashSet<String>(1000);

    @Shadow
    private Set<String> classLoaderExceptions = new HashSet<String>();
    @Shadow
    private Set<String> transformerExceptions = new HashSet<String>();
    @Unique
    private Object2ObjectHashMap<Package, Manifest> optimizationsAndTweaks$packageManifests = new Object2ObjectHashMap<>();
    @Unique
    private Object2ObjectHashMap<String, byte[]> optimizationsAndTweaks$resourceCache = new Object2ObjectHashMap<>();
    @Unique
    private Set<String> optimizationsAndTweaks$negativeResourceCache = Collections
        .newSetFromMap(new Object2ObjectHashMap<>());
    @Shadow

    private IClassNameTransformer renameTransformer;
    @Shadow

    private static final Manifest EMPTY = new Manifest();
    @Shadow

    private final ThreadLocal<byte[]> loadBuffer = new ThreadLocal<byte[]>();
    @Shadow

    private static final String[] RESERVED_NAMES = { "CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5",
        "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9" };

    @Shadow
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("legacy.debugClassLoading", "false"));
    @Shadow
    private static final boolean DEBUG_FINER = DEBUG
        && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingFiner", "false"));
    @Shadow
    private static final boolean DEBUG_SAVE = DEBUG
        && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingSave", "false"));
    @Shadow
    private static File tempFolder = null;

    public MixinLaunchClassLoader(URL[] sources) {
        super(sources, null);
        this.optimizationsAndTweaks$sources = new ArrayList<>(Arrays.asList(sources));

        // classloader exclusions
        addClassLoaderExclusion("java.");
        addClassLoaderExclusion("sun.");
        addClassLoaderExclusion("org.lwjgl.");
        addClassLoaderExclusion("org.apache.logging.");
        addClassLoaderExclusion("net.minecraft.launchwrapper.");

        // transformer exclusions
        addTransformerExclusion("javax.");
        addTransformerExclusion("argo.");
        addTransformerExclusion("org.objectweb.asm.");
        addTransformerExclusion("com.google.common.");
        addTransformerExclusion("org.bouncycastle.");
        addTransformerExclusion("net.minecraft.launchwrapper.injector.");

        if (DEBUG_SAVE) {
            int x = 1;
            tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP");
            while (tempFolder.exists() && x <= 10) {
                tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP" + x++);
            }

            if (tempFolder.exists()) {
                LogWrapper.info("DEBUG_SAVE enabled, but 10 temp directories already exist, clean them and try again.");
                tempFolder = null;
            } else {
                LogWrapper.info(
                    "DEBUG_SAVE Enabled, saving all classes to \"%s\"",
                    tempFolder.getAbsolutePath()
                        .replace('\\', '/'));
                tempFolder.mkdirs();
            }
        }
    }

    @Overwrite(remap = false)
    public void registerTransformer(String transformerClassName) {
        try {
            IClassTransformer transformer = (IClassTransformer) loadClass(transformerClassName).newInstance();
            transformers.add(transformer);
            if (transformer instanceof IClassNameTransformer && renameTransformer == null) {
                renameTransformer = (IClassNameTransformer) transformer;
            }
        } catch (Exception e) {
            LogWrapper.log(
                Level.ERROR,
                e,
                "A critical problem occurred registering the ASM transformer class %s",
                transformerClassName);
        }
    }

    @Overwrite(remap = false)
    public Class<?> findClass(final String name) throws ClassNotFoundException {
        if (invalidClasses.contains(name)) {
            throw new ClassNotFoundException(name);
        }

        for (final String exception : classLoaderExceptions) {
            if (name.startsWith(exception)) {
                return parent.loadClass(name);
            }
        }

        if (optimizationsAndTweaks$cachedClasses.containsKey(name)) {
            return optimizationsAndTweaks$cachedClasses.get(name);
        }

        try {
            final Class<?> clazz = super.findClass(name);
            optimizationsAndTweaks$cachedClasses.put(name, clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            invalidClasses.add(name);
            throw e;
        }
    }

    @Overwrite(remap = false)
    private void saveTransformedClass(final byte[] data, final String transformedName) {
        if (tempFolder == null) {
            return;
        }

        final File outFile = new File(tempFolder, transformedName.replace('.', File.separatorChar) + ".class");
        final File outDir = outFile.getParentFile();

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            LogWrapper.fine(
                "Saving transformed class \"%s\" to \"%s\"",
                transformedName,
                outFile.getAbsolutePath()
                    .replace('\\', '/'));

            final Path outPath = outFile.toPath();
            Files.write(outPath, data);
        } catch (IOException ex) {
            LogWrapper.log(Level.WARN, ex, "Could not save transformed class \"%s\"", transformedName);
        }
    }

    @Overwrite(remap = false)
    private String untransformName(final String name) {
        if (renameTransformer != null) {
            return renameTransformer.unmapClassName(name);
        }

        return name;
    }

    @Overwrite(remap = false)
    private String transformName(final String name) {
        if (renameTransformer != null) {
            return renameTransformer.remapClassName(name);
        }

        return name;
    }

    @Overwrite(remap = false)
    private boolean isSealed(final String path, final Manifest manifest) {
        Attributes attributes = manifest.getAttributes(path);
        String sealed = null;
        if (attributes != null) {
            sealed = attributes.getValue(Attributes.Name.SEALED);
        }

        if (sealed == null) {
            attributes = manifest.getMainAttributes();
            if (attributes != null) {
                sealed = attributes.getValue(Attributes.Name.SEALED);
            }
        }
        return "true".equalsIgnoreCase(sealed);
    }

    @Overwrite(remap = false)
    private URLConnection findCodeSourceConnectionFor(final String name) {
        final URL resource = findResource(name);
        if (resource != null) {
            try {
                return resource.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Overwrite(remap = false)
    private byte[] runTransformers(final String name, final String transformedName, byte[] basicClass) {
        if (DEBUG_FINER) {
            LogWrapper.finest(
                "Beginning transform of {%s (%s)} Start Length: %d",
                name,
                transformedName,
                (basicClass == null ? 0 : basicClass.length));
            for (final IClassTransformer transformer : transformers) {
                final String transName = transformer.getClass()
                    .getName();
                LogWrapper.finest(
                    "Before Transformer {%s (%s)} %s: %d",
                    name,
                    transformedName,
                    transName,
                    (basicClass == null ? 0 : basicClass.length));
                basicClass = transformer.transform(name, transformedName, basicClass);
                LogWrapper.finest(
                    "After  Transformer {%s (%s)} %s: %d",
                    name,
                    transformedName,
                    transName,
                    (basicClass == null ? 0 : basicClass.length));
            }
            LogWrapper.finest(
                "Ending transform of {%s (%s)} Start Length: %d",
                name,
                transformedName,
                (basicClass == null ? 0 : basicClass.length));
        } else {
            for (final IClassTransformer transformer : transformers) {
                basicClass = transformer.transform(name, transformedName, basicClass);
            }
        }
        return basicClass;
    }

    @Overwrite(remap = false)
    public void addURL(final URL url) {
        super.addURL(url);
        optimizationsAndTweaks$sources.add(url);
    }

    @Overwrite(remap = false)
    public List<URL> getSources() {
        return optimizationsAndTweaks$sources;
    }

    @Overwrite(remap = false)
    private byte[] readFully(InputStream stream) {
        try {
            byte[] buffer = getOrCreateBuffer();

            int read;
            int totalLength = 0;
            while ((read = stream.read(buffer, totalLength, buffer.length - totalLength)) != -1) {
                totalLength += read;

                // Extend our buffer
                if (totalLength >= buffer.length - 1) {
                    byte[] newBuffer = new byte[buffer.length + BUFFER_SIZE];
                    System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                    buffer = newBuffer;
                }
            }

            final byte[] result = new byte[totalLength];
            System.arraycopy(buffer, 0, result, 0, totalLength);
            return result;
        } catch (Throwable t) {
            LogWrapper.log(Level.WARN, t, "Problem loading class");
            return new byte[0];
        } finally {
            loadBuffer.remove();
        }
    }

    @Overwrite(remap = false)
    private byte[] getOrCreateBuffer() {
        byte[] buffer = loadBuffer.get();
        if (buffer == null) {
            loadBuffer.set(new byte[BUFFER_SIZE]);
            buffer = loadBuffer.get();
        }
        return buffer;
    }
    @Overwrite(remap = false)
    public List<IClassTransformer> getTransformers() {
        return Collections.unmodifiableList(transformers);
    }

    @Overwrite(remap = false)
    public void addClassLoaderExclusion(String toExclude) {
        classLoaderExceptions.add(toExclude);
    }

    @Overwrite(remap = false)
    public void addTransformerExclusion(String toExclude) {
        transformerExceptions.add(toExclude);
    }
    @Overwrite(remap = false)
    public byte[] getClassBytes(String name) throws IOException {
        if (optimizationsAndTweaks$resourceCache.containsKey(name)) {
            return optimizationsAndTweaks$resourceCache.get(name);
        }
        if (name.indexOf('.') == -1) {
            for (final String reservedName : RESERVED_NAMES) {
                if (name.toUpperCase(Locale.ENGLISH).startsWith(reservedName)) {
                    final byte[] data = getClassBytes("_" + name);
                    if (data.length > 0) {
                        optimizationsAndTweaks$resourceCache.put(name, data);
                        return data;
                    }
                }
            }
        }

        InputStream classStream = null;
        try {
            final String resourcePath = name.replace('.', '/').concat(".class");
            final URL classResource = findResource(resourcePath);

            if (classResource == null) {
                if (DEBUG) LogWrapper.finest("Failed to find class resource %s", resourcePath);
                return new byte[0];
            }
            classStream = classResource.openStream();

            if (DEBUG) LogWrapper.finest("Loading class %s from resource %s", name, classResource.toString());
            final byte[] data = readFully(classStream);
            optimizationsAndTweaks$resourceCache.put(name, data);
            return data;
        } finally {
            closeSilently(classStream);
        }
    }
    @Overwrite(remap = false)
    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {}
        }
    }

    @Overwrite(remap = false)
    public void clearNegativeEntries(Set<String> entriesToClear) {
      //  optimizationsAndTweaks$negativeResourceCache.removeAll(entriesToClear);
    }
}
