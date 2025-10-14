package fr.iamacat.optimizationsandtweaks.utils.natives;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cpw.mods.fml.common.FMLLog;

/**
 * Handles extraction and loading of native libraries from mod resources
 */
public class NativeLibraryLoader {

    private static final String NATIVES_DIR = "natives";
    private static boolean libraryLoaded = false;

    /**
     * Loads a native library from the mod's resources
     * Extracts it to the Minecraft instance natives folder if different
     * 
     * @param libraryName The name of the library (without platform-specific prefix/suffix)
     * @return true if the library was loaded successfully
     */
    public static boolean loadLibrary(String libraryName, File minecraftDir) {
        if (libraryLoaded) {
            return true;
        }

        try {
            // Determine OS and platform-specific library name
            String osDir = getOSDirectory();
            String platformLibName = getPlatformLibraryName(libraryName);
            String resourcePath = "/assets/optimizationsandtweaks/natives/" + osDir + "/" + platformLibName;

            // Create natives directory in Minecraft instance
            File nativesDir = new File(minecraftDir, NATIVES_DIR);
            if (!nativesDir.exists()) {
                nativesDir.mkdirs();
            }

            File targetFile = new File(nativesDir, platformLibName);

            // Extract library if it doesn't exist or is different
            if (shouldExtractLibrary(resourcePath, targetFile)) {
                extractLibrary(resourcePath, targetFile);
                FMLLog.info("[OptimizationsAndTweaks] Extracted native library: %s", platformLibName);
            } else {
                FMLLog.info("[OptimizationsAndTweaks] Native library already up to date: %s", platformLibName);
            }

            // Load the library
            System.load(targetFile.getAbsolutePath());
            libraryLoaded = true;
            FMLLog.info("[OptimizationsAndTweaks] Successfully loaded native library: %s", platformLibName);
            return true;

        } catch (Exception e) {
            FMLLog.severe("[OptimizationsAndTweaks] Failed to load native library: %s", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the OS-specific directory name for native libraries
     */
    private static String getOSDirectory() {
        String os = System.getProperty("os.name")
            .toLowerCase();

        if (os.contains("win")) {
            return "windows";
        } else if (os.contains("mac")) {
            return "macos";
        } else {
            // Linux and other Unix-like systems
            return "linux";
        }
    }

    /**
     * Gets the platform-specific library name
     */
    private static String getPlatformLibraryName(String baseName) {
        String os = System.getProperty("os.name")
            .toLowerCase();

        if (os.contains("win")) {
            return baseName + ".dll";
        } else if (os.contains("mac")) {
            return "lib" + baseName + ".dylib";
        } else {
            // Linux and other Unix-like systems
            return "lib" + baseName + ".so";
        }
    }

    /**
     * Checks if the library should be extracted
     * Returns true if the file doesn't exist or has different content
     */
    private static boolean shouldExtractLibrary(String resourcePath, File targetFile) {
        if (!targetFile.exists()) {
            return true;
        }

        try {
            // Compare checksums
            String resourceChecksum = getResourceChecksum(resourcePath);
            String fileChecksum = getFileChecksum(targetFile);

            return !resourceChecksum.equals(fileChecksum);
        } catch (Exception e) {
            // If we can't compare, extract to be safe
            FMLLog.warning("[OptimizationsAndTweaks] Could not compare checksums, will re-extract library");
            return true;
        }
    }

    /**
     * Extracts the library from resources to the target file
     */
    private static void extractLibrary(String resourcePath, File targetFile) throws IOException {
        InputStream in = NativeLibraryLoader.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IOException("Native library not found in resources: " + resourcePath);
        }

        try {
            // Create parent directories if needed
            targetFile.getParentFile()
                .mkdirs();

            // Extract the file
            FileOutputStream out = new FileOutputStream(targetFile);
            try {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }

        // Make the file executable on Unix-like systems
        targetFile.setExecutable(true);
        targetFile.setReadable(true);
    }

    /**
     * Calculates MD5 checksum of a resource
     */
    private static String getResourceChecksum(String resourcePath) throws IOException, NoSuchAlgorithmException {
        InputStream in = NativeLibraryLoader.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            return bytesToHex(md.digest());
        } finally {
            in.close();
        }
    }

    /**
     * Calculates MD5 checksum of a file
     */
    private static String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException {
        FileInputStream in = new FileInputStream(file);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            return bytesToHex(md.digest());
        } finally {
            in.close();
        }
    }

    /**
     * Converts byte array to hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Checks if the native library is loaded
     */
    public static boolean isLibraryLoaded() {
        return libraryLoaded;
    }
}
