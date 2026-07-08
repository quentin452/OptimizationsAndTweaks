package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.io.File;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// checkSessionLock() @Overwrite dropped 2026-07-08: it was a byte-for-byte behavioral copy of vanilla
// (try-with-resources over Files.newInputStream instead of a manual finally-close over FileInputStream --
// same reads, same exception propagation), so the original vanilla method now applies unmodified.
@Mixin(SaveHandler.class)
public abstract class MixinSaveHandler implements ISaveHandler, IPlayerFileData {

    @Shadow
    private final File worldDirectory;
    @Shadow
    private final long initializationTime = MinecraftServer.getSystemTimeMillis();

    public MixinSaveHandler(File p_i2146_1_, String p_i2146_2_, boolean p_i2146_3_) {
        this.worldDirectory = new File(p_i2146_1_, p_i2146_2_);
    }
}
