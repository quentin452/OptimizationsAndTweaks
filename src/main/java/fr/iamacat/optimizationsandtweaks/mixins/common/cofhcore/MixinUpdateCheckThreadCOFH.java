package fr.iamacat.optimizationsandtweaks.mixins.common.cofhcore;

import cofh.mod.updater.UpdateCheckThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(UpdateCheckThread.class)
public class MixinUpdateCheckThreadCOFH {
    /**
     * @author
     * @reason disabling update checks to reduce CPU time from COFH mods
     */
    @Overwrite(remap = false)
    public void run() {

    }
}
