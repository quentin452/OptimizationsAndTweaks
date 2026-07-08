package fr.iamacat.optimizationsandtweaks.mixins.server.core;

import java.text.DecimalFormat;

import javax.swing.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.StatsComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The original {@code func_120034_a} (the 500ms stats-refresh tick) forces a full {@code System.gc()}
 * every call -- redirecting that single call to a no-op drops the forced GC stall while leaving the rest
 * of the original (untouched) method (memory/tick-avg string formatting, repaint) unmodified.
 */
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

    @Redirect(method = "func_120034_a", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"), remap = false)
    private void optimizationsAndTweaks$skipForcedGc() {
        // no-op: avoid forcing a full GC every 500ms stats-refresh tick
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
