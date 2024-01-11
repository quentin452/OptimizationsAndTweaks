package fr.iamacat.optimizationsandtweaks.utilsformods.recurrentcomplextrewrite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class FileInjector {

    private static ModConfig modConfig;

    public static void setModConfig(ModConfig config) {
        modConfig = config;
    }

    public static void preinit(final FMLPreInitializationEvent event) {
        // Get the current Minecraft instance directory
        File minecraftDir = event.getModConfigurationDirectory()
            .getParentFile();

        // Path to the destination directory within the Minecraft instance folder
        String structuresDirectoryPath = new File(minecraftDir, "structures").getAbsolutePath();
        String activeDirectoryPath = new File(structuresDirectoryPath, "active").getAbsolutePath();

        // Create the "structures" directory if it doesn't exist
        File structuresDirectory = new File(structuresDirectoryPath);
        if (!structuresDirectory.exists()) {
            if (structuresDirectory.mkdirs()) {
                System.out.println("Created 'structures' directory");
            } else {
                System.err.println("Failed to create 'structures' directory");
            }
        }

        // Create the "active" directory if it doesn't exist
        File activeDirectory = new File(activeDirectoryPath);
        if (!activeDirectory.exists()) {
            if (activeDirectory.mkdirs()) {
                System.out.println("Created 'active' directory");
            } else {
                System.err.println("Failed to create 'active' directory");
            }
        }

        boolean enableMYTH_AND_MONSTERS_atlantis = modConfig.isMYTH_AND_MONSTERS_atlantisisenabled();

        // Path to the destination directory within the Minecraft instance folder
        String destinationDirectoryPath = new File(minecraftDir, "structures/active").getAbsolutePath();

        // List of source file names
        String[] sourceFiles = { "MYTH_AND_MONSTERS_atlantisV1.0.rcst", };

        try {
            // Get all the files in the destination directory
            File destinationDirectory = new File(destinationDirectoryPath);
            File[] filesInDestination = destinationDirectory.listFiles();

            // Delete the files that are not in the list of allowed files.
            if (filesInDestination != null) {
                for (File file : filesInDestination) {
                    if (file.isFile() && file.getName()
                        .startsWith("MYTH_AND_MONSTERS_")) {
                        String fileName = file.getName();
                        if (!isValidFileName(fileName, sourceFiles)) {
                            if (file.delete()) {
                                System.out.println("File deleted: " + fileName + " (not in the allowed list)");
                            } else {
                                System.err.println("Failed to delete file: " + fileName);
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < sourceFiles.length; i++) {
                String sourceFileName = sourceFiles[i];
                boolean isEnabled = false;

                // Determine if the current .rcst file should be enabled based on the configuration
                if (i == 0) isEnabled = enableMYTH_AND_MONSTERS_atlantis;

                if (isEnabled) {
                    // Get an InputStream from the resource within the JAR
                    InputStream inputStream = FileInjector.class.getClassLoader()
                        .getResourceAsStream("assets/optimizationsandtweaks/structres/active/" + sourceFileName);

                    if (inputStream != null) {
                        // Copy the file from the InputStream to the destination directory
                        java.nio.file.Files.copy(
                            inputStream,
                            new File(destinationDirectoryPath, sourceFileName).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                        if (!ModConfig.loggingdisabler) {
                            System.out.println("Copy successful: " + sourceFileName);
                        }
                    }
                } else {
                    // If the config is disabled, check if the file exists and delete it if it does
                    File existingFile = new File(destinationDirectoryPath, sourceFileName);
                    if (existingFile.exists() && existingFile.isFile()) {
                        if (existingFile.delete()) {
                            if (!ModConfig.loggingdisabler) {
                                System.out.println("File deleted: " + sourceFileName + " (disabled in config)");
                            }
                        } else {
                            if (!ModConfig.loggingdisabler) {
                                System.err.println("Failed to delete file: " + sourceFileName);
                            }
                        }
                    } else {
                        if (!ModConfig.loggingdisabler) {
                            System.out.println("Skipped copying: " + sourceFileName + " (disabled in config)");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidFileName(String fileName, String[] allowedFiles) {

        if (fileName.startsWith("MYTH_AND_MONSTERS_") && !Arrays.asList(allowedFiles)
            .contains(fileName)) {
            return false;
        }

        for (String allowedFile : allowedFiles) {
            if (fileName.equals(allowedFile)) {
                return true;
            }
        }

        return false;
    }

}
