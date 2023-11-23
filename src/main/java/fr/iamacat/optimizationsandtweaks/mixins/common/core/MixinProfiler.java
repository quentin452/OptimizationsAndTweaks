package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.*;

// disabling Profiler
@Mixin(Profiler.class)
public class MixinProfiler {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void clearProfiling()
    {
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void startSection(String name)
    {
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void endSection()
    {
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public List getProfilingData(String p_76321_1_)
    {
        return null;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void endStartSection(String name)
    {
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public String getNameOfLastSection()
    {
        return null;
    }
}
