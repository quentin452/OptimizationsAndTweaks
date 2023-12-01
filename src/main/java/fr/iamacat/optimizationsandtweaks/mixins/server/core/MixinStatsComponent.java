package fr.iamacat.optimizationsandtweaks.mixins.server.core;

import java.text.DecimalFormat;

import javax.swing.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.StatsComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StatsComponent.class)
public class MixinStatsComponent extends JComponent {

    @Shadow
    private static final DecimalFormat field_120040_a = new DecimalFormat("########0.000");
    @Shadow
    private int[] field_120038_b = new int[256];
    @Shadow
    private int field_120039_c;
    @Shadow
    private String[] field_120036_d = new String[11];
    @Shadow
    private final MinecraftServer field_120037_e;

    public MixinStatsComponent(MinecraftServer field120037E) {
        field_120037_e = field120037E;
    }

    @Overwrite
    private void func_120034_a() {
        long i = Runtime.getRuntime()
            .totalMemory()
            - Runtime.getRuntime()
                .freeMemory();
        // System.gc();
        this.field_120036_d[0] = "Memory use: " + i / 1024L / 1024L
            + " mb ("
            + Runtime.getRuntime()
                .freeMemory() * 100L
                / Runtime.getRuntime()
                    .maxMemory()
            + "% free)";
        this.field_120036_d[1] = "Avg tick: "
            + field_120040_a.format(this.func_120035_a(this.field_120037_e.tickTimeArray) * 1.0E-6D)
            + " ms";
        this.repaint();
    }

    @Shadow
    private double func_120035_a(long[] p_120035_1_) {
        long i = 0L;

        for (int j = 0; j < p_120035_1_.length; ++j) {
            i += p_120035_1_[j];
        }

        return (double) i / (double) p_120035_1_.length;
    }
}
