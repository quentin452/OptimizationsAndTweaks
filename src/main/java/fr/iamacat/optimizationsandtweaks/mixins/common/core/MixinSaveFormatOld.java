package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import net.minecraft.world.storage.SaveFormatOld;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SaveFormatOld.class)
public class MixinSaveFormatOld {

    @Shadow
    private static final Logger logger = LogManager.getLogger();
    @Shadow
    public final File savesDirectory;

    public MixinSaveFormatOld(File p_i2147_1_) {
        if (!p_i2147_1_.exists()) {
            p_i2147_1_.mkdirs();
        }

        this.savesDirectory = p_i2147_1_;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean deleteWorldDirectory(String p_75802_1_) {
        File file1 = new File(this.savesDirectory, p_75802_1_);

        if (!file1.exists()) {
            return true;
        } else {
            logger.info("Deleting level " + p_75802_1_);

            for (int i = 1; i <= 5; ++i) {
                logger.info("Attempt " + i + "...");

                if (deleteFiles(Objects.requireNonNull(file1.listFiles()))) {
                    break;
                }

                logger.warn("Unsuccessful in deleting contents.");

                if (i < 5) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException interruptedexception) {}
                }
            }

            return file1.delete();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected static boolean deleteFiles(File[] p_75807_0_) {
        for (File file1 : p_75807_0_) {
            logger.debug("Deleting " + file1);

            if (file1.isDirectory() && !deleteFiles(Objects.requireNonNull(file1.listFiles()))) {
                logger.warn("Couldn't delete directory " + file1);
                return false;
            }

            if (!file1.delete()) {
                logger.warn("Couldn't delete file " + file1);
                return false;
            }
        }

        return true;
    }
}
