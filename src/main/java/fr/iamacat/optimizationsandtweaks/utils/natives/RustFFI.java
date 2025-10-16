package fr.iamacat.optimizationsandtweaks.utils.natives;

import java.io.File;

import cpw.mods.fml.common.FMLLog;

/**
 * Java wrapper for Rust FFI functions using JNI (Java Native Interface)
 * Provides access to native Rust functions from Java without external dependencies
 */
public class RustFFI {

    private static boolean initialized = false;
    private static boolean libraryAvailable = false;

    // Native method declarations - these will be implemented in Rust
    private static native void rust_hello_world();

    private static native String rust_get_hello_string();

    private static native void rust_print_message(String message);

    public static native void setPanicGuardEnabled(boolean enabled);

    /**
     * Initializes the Rust FFI library
     * Must be called before using any Rust functions
     * 
     * @param minecraftDir The Minecraft instance directory
     * @return true if initialization was successful
     */
    public static boolean initialize(File minecraftDir) {
        if (initialized) {
            return libraryAvailable;
        }

        initialized = true;

        try {
            // Load the native library using NativeLibraryLoader
            boolean ok = true;
            ok &= NativeLibraryLoader.loadLibrary("optimizationsandtweaks_pathfinding", minecraftDir);
            ok &= NativeLibraryLoader.loadLibrary("optimizationsandtweaks_blocksupdates", minecraftDir);
            ok &= NativeLibraryLoader.loadLibrary("optimizationsandtweaks_shared", minecraftDir);
            if (ok) {
                libraryAvailable = true;
                FMLLog.info("[OptimizationsAndTweaks] Rust FFI initialized successfully using JNI");
                return true;
            } else {
                FMLLog
                    .info("[OptimizationsAndTweaks] Rust native library not available, FFI features will be disabled");
                return false;
            }

        } catch (UnsatisfiedLinkError e) {
            FMLLog.info("[OptimizationsAndTweaks] Rust native library not found: %s", e.getMessage());
            FMLLog.info(
                "[OptimizationsAndTweaks] Rust FFI features will be disabled. This is optional and does not affect core functionality.");
            return false;
        } catch (Exception e) {
            FMLLog.info("[OptimizationsAndTweaks] Could not initialize Rust FFI: %s", e.getMessage());
            FMLLog.info(
                "[OptimizationsAndTweaks] Rust FFI features will be disabled. This is optional and does not affect core functionality.");
            return false;
        }
    }

    /**
     * Prints "Hello World from Rust!" to stdout
     */
    public static void printHelloWorld() {
        if (!libraryAvailable) {
            return;
        }

        try {
            rust_hello_world();
        } catch (UnsatisfiedLinkError e) {
            FMLLog.warning("[OptimizationsAndTweaks] Native method rust_hello_world not available");
            libraryAvailable = false;
        } catch (Exception e) {
            FMLLog.warning("[OptimizationsAndTweaks] Error calling rust_hello_world: %s", e.getMessage());
        }
    }

    /**
     * Gets "Hello World from Rust!" as a Java String
     * 
     * @return The hello string from Rust, or null if an error occurred
     */
    public static String getHelloString() {
        if (!libraryAvailable) {
            return null;
        }

        try {
            return rust_get_hello_string();
        } catch (UnsatisfiedLinkError e) {
            FMLLog.warning("[OptimizationsAndTweaks] Native method rust_get_hello_string not available");
            libraryAvailable = false;
            return null;
        } catch (Exception e) {
            FMLLog.warning("[OptimizationsAndTweaks] Error calling rust_get_hello_string: %s", e.getMessage());
            return null;
        }
    }

    /**
     * Sends a message to Rust to print
     * 
     * @param message The message to print
     */
    public static void printMessage(String message) {
        if (!libraryAvailable) {
            return;
        }

        try {
            rust_print_message(message);
        } catch (UnsatisfiedLinkError e) {
            FMLLog.warning("[OptimizationsAndTweaks] Native method rust_print_message not available");
            libraryAvailable = false;
        } catch (Exception e) {
            FMLLog.warning("[OptimizationsAndTweaks] Error calling rust_print_message: %s", e.getMessage());
        }
    }

    /**
     * Checks if the Rust FFI is initialized and ready to use
     * 
     * @return true if initialized and library is available
     */
    public static boolean isInitialized() {
        return initialized && libraryAvailable;
    }
}
